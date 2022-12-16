package com.github.maybeec.lexeme.systemtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.After;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.github.maybeec.lexeme.ConflictHandlingType;
import com.github.maybeec.lexeme.LeXeMeBuilder;
import com.github.maybeec.lexeme.LeXeMeFactory;
import com.github.maybeec.lexeme.LeXeMerger;
import com.github.maybeec.lexeme.common.util.JDom2Util;
import com.github.maybeec.lexeme.schemaprovider.MergeSchemaProvider;
import com.github.maybeec.lexeme.schemaprovider.MergeSchemaProviderImpl;

/**
 *
 */
public class CobigenIntegrationTest {

  /**
   * The path to the system tests resources roots
   */
  final String pathRoot = "src/test/resources/systemtests/";

  // --- Issue #36 ---
  /**
   * Tests the merge process if the merge schema has multiple namespace definitions. The setup of
   * {@link FirstWorkingBuild#FirstTest()} is used with a modified MergeSchema
   *
   * @throws Exception when something unexpected happens
   */
  @Test
  public void testAdditionalNamespaceDefinition() throws Exception {

    final String mergeSchemaLocation = this.pathRoot + "mergeschemas/CobiGenIntegration/";
    final String uri = "http://www.example.org/FirstXMLSchema";
    Document base = JDom2Util.getInstance().getDocument(this.pathRoot + "bases/FirstBase.xml");
    Document patch = JDom2Util.getInstance().getDocument(this.pathRoot + "patches/FirstPatch.xml");

    LeXeMerger testObject = LeXeMeFactory.build(mergeSchemaLocation);
    Element result = testObject.merge(base.getRootElement(), patch.getRootElement(),
        ConflictHandlingType.PATCHATTACHOROVERWRITE);

    List<Element> bList = result.getChildren("B", Namespace.getNamespace(uri));
    assertTrue("Wrong number of B elements found. Excpected 2, found " + bList.size(), bList.size() == 2);
    assertTrue("Wrong id attribute at first B element", bList.get(0).getAttribute("id").getValue().equals("abc"));
    assertTrue("Wrong id attribute at first B element", bList.get(1).getAttribute("id").getValue().equals("def"));
    List<Element> cList = result.getChildren("C", Namespace.getNamespace(uri));
    assertTrue("Wrong number of C elements found. Excpected 1, found " + cList.size(), cList.size() == 1);
    assertTrue("Wrong id attribute at first C element", cList.get(0).getAttribute("id").getValue().equals("def"));
  }

  /**
   * Tests the behavior if a handling referes to another in another namespace.
   * <p/>
   * Tests if the merge rules from the base namespace will be applied to an element instead of the default handling
   *
   * @throws Exception when something unexpected happens
   */
  @Test
  public void testExtendedMergeSchema() throws Exception {

    final String mergeSchemaLocation = this.pathRoot + "mergeschemas/CobiGenIntegration/";
    final String baseURI = "http://someBase.com";
    final String extURI = "http://someExtension.com";

    Document base = JDom2Util.getInstance().getDocument(this.pathRoot + "bases/37base1.xml");
    Document patch = JDom2Util.getInstance().getDocument(this.pathRoot + "patches/37patch1.xml");

    LeXeMeBuilder builder = Mockito.mock(LeXeMeBuilder.class);
    Mockito.when(builder.build(mergeSchemaLocation)).thenReturn(new LeXeMerger(mergeSchemaLocation));
    Mockito.when(builder.build(ArgumentMatchers.any(MergeSchemaProvider.class)))
        .thenReturn(new LeXeMerger(MergeSchemaProviderImpl.getProviderForPath(mergeSchemaLocation)));

    LeXeMeFactory.setBuilder(builder);

    LeXeMerger testObject = LeXeMeFactory.build(mergeSchemaLocation);
    testObject.setValidation(false);
    Element result = testObject.merge(base.getRootElement(), patch.getRootElement(),
        ConflictHandlingType.PATCHOVERWRITE);

    List<Element> aList = result.getChildren("a", Namespace.getNamespace(extURI));
    assertEquals(1, aList.size());
    List<Element> bList = aList.get(0).getChildren("b", Namespace.getNamespace(baseURI));
    assertEquals(1, bList.size());

    Mockito.verify(builder).build(mergeSchemaLocation);
    // Mockito.verify(builder, Mockito.times(0)).build(Matchers.any(MergeSchemaProvider.class));

  }

  /**
   * Tests if the often used mergeSchemas are correct. Corresponds to
   * <a href="https://github.com/may-bee/xml-law-merge/issues/30">Issue #30</a>
   *
   * @throws Exception which should never happen
   */
  @Test
  public void testIntegrityOfMergeSchemas() throws Exception {

    final String mergeSchemaLocation = "src/main/resources/mergeschemas/";
    LeXeMeFactory.build(mergeSchemaLocation);
  }

  /**
   * Restores the default builders to prevent interferences with other tests (which will eventually happen)
   */
  @After
  public void after() {

    LeXeMeFactory.setBuilder(null);
  }

}
