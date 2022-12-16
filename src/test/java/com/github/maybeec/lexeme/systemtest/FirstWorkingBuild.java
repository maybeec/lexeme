package com.github.maybeec.lexeme.systemtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Test;

import com.github.maybeec.lexeme.ConflictHandlingType;
import com.github.maybeec.lexeme.LeXeMerger;
import com.github.maybeec.lexeme.common.util.JDom2Util;

public class FirstWorkingBuild {

  /**
   * Path to the used resources folder
   */
  String resources = "src/test/resources/systemtests/";

  /**
   * Test for a simple use case. Used to ensure the basic implementation works as expected. Test case: First .Expecting
   *
   * <pre>
   * &lt;A> &lt;B id =''abc''/> &lt;B id =''def''/> &lt;C id =''def''/> &lt;/A> </pre
   *
   * @throws Exception when something somewhere goes wrong
   */
  @Test
  public void FirstTest() throws Exception {

    String mergeSchemaLocation = this.resources + "mergeschemas/FirstMergeSchema.xml";

    Document base = JDom2Util.getInstance().getDocument(this.resources + "bases/FirstBase.xml");
    Document patch = JDom2Util.getInstance().getDocument(this.resources + "patches/FirstPatch.xml");

    String uri = "http://www.example.org/FirstXMLSchema";

    LeXeMerger testMerger = new LeXeMerger(mergeSchemaLocation);
    Element result = testMerger.merge(base.getRootElement(), patch.getRootElement(),
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
   * Test for a more complex use case. Scenario 'Second'. Expecting
   *
   * <pre>
   * &lt;A> &lt;B id="abc;def"/> &lt;C> &lt;Ca>0&lt;/> &lt;Cb>0&lt;/> &lt;Cb>1&lt;/> &lt;Cb>2&lt;/> &lt;/> &lt;/> </>
   *
   * @throws Exception when something somewhere goes wrong
   */
  @Test
  public void SecondTest() throws Exception {

    String mergeSchemaLocation = this.resources + "mergeschemas/SecondMergeSchema.xml";

    String uri = "http://www.example.org/SecondSchema";

    Document base = JDom2Util.getInstance().getDocument(this.resources + "bases/SecondBase.xml");
    Document patch = JDom2Util.getInstance().getDocument(this.resources + "patches/SecondPatch.xml");
    LeXeMerger testMerger = new LeXeMerger(mergeSchemaLocation);
    Element result = testMerger.merge(base.getRootElement(), patch.getRootElement(),
        ConflictHandlingType.PATCHATTACHOROVERWRITE);
    assertTrue("No B Element found", result.getChildren("B", Namespace.getNamespace(uri)).size() == 1);
    Element b = result.getChildren("B", Namespace.getNamespace(uri)).get(0);
    assertTrue("Wrong id at B element", b.getAttribute("id").getValue().equals("abc;def"));
    assertTrue("No B Element found", result.getChildren("C", Namespace.getNamespace(uri)).size() == 1);
    Element c = result.getChildren("C", Namespace.getNamespace(uri)).get(0);
    assertTrue("Wrong number of Ca elements", c.getChildren("Ca", Namespace.getNamespace(uri)).size() == 1);
    assertTrue("Wrong content of Ca", JDom2Util.getInstance()
        .getTextNodes(c.getChildren("Ca", Namespace.getNamespace(uri)).get(0)).get(0).getText().equals("0"));
    int numberOfCb = c.getChildren("Cb", Namespace.getNamespace(uri)).size();
    assertEquals("Wrong number of Cb elements. Found " + numberOfCb, 3, numberOfCb);

  }

  /**
   * Test case as in {@link FirstWorkingBuild#SecondTest()}. Patch uses another prefix for its namespace. The prefix
   * used in the result should be the same as in the base
   *
   * @throws Exception when something somewhere goes wrong
   */
  @Test
  public void SecondTestWithDifferentPrefixes() throws Exception {

    String mergeSchemaLocation = this.resources + "mergeschemas/SecondMergeSchema.xml";
    String uri = "http://www.example.org/SecondSchema";
    Document base = JDom2Util.getInstance().getDocument(this.resources + "bases/SecondBase.xml");
    Document patch = JDom2Util.getInstance().getDocument(this.resources + "patches/SecondPatchWithOtherPrefixes.xml");
    LeXeMerger testMerger = new LeXeMerger(mergeSchemaLocation);
    Element result = testMerger.merge(base.getRootElement(), patch.getRootElement(),
        ConflictHandlingType.PATCHATTACHOROVERWRITE);
    assertTrue("No B Element found", result.getChildren("B", Namespace.getNamespace(uri)).size() == 1);
    Element b = result.getChildren("B", Namespace.getNamespace(uri)).get(0);
    assertTrue("Wrong id at B element", b.getAttribute("id").getValue().equals("abc;def"));
    assertTrue("No B Element found", (result.getChildren("C", Namespace.getNamespace(uri))).size() == 1);
    Element c = result.getChildren("C", Namespace.getNamespace(uri)).get(0);
    assertTrue("Wrong number of Ca elements", (c.getChildren("Ca", Namespace.getNamespace(uri))).size() == 1);
    assertTrue("Wrong content of Ca", JDom2Util.getInstance()
        .getTextNodes(c.getChildren("Ca", Namespace.getNamespace(uri)).get(0)).get(0).getText().equals("0"));
    int numberOfCb = (c.getChildren("Cb", Namespace.getNamespace(uri))).size();
    assertTrue("Wrong number of Cb elements. Found " + numberOfCb, numberOfCb == 3);

  }

  /**
   * Simple Spring Beans test case.
   *
   * @throws Exception when something somewhere goes wrong
   */
  @Test
  public void FirstBeanTest() throws Exception {

    String mergeSchemaLocation = this.resources + "mergeschemas/BeansMergeSchema.xml";

    Document base = JDom2Util.getInstance().getDocument(this.resources + "bases/Beans1.xml");
    Document patch = JDom2Util.getInstance().getDocument(this.resources + "patches/Beans1.xml");
    LeXeMerger testMerger = new LeXeMerger(mergeSchemaLocation);
    Element result = testMerger.merge(base.getRootElement(), patch.getRootElement(),
        ConflictHandlingType.PATCHOVERWRITE);
    assertEquals("Not all bean elements found", 3,
        result.getChildren("bean", Namespace.getNamespace("http://www.springframework.org/schema/beans")).size());
    List<Element> beanNodeList = result.getChildren("bean",
        Namespace.getNamespace("http://www.springframework.org/schema/beans"));
    assertEquals("Not all properties at bean1 found", 2, beanNodeList.get(0)
        .getChildren("property", Namespace.getNamespace("http://www.springframework.org/schema/beans")).size());

  }

  /**
   * Simple Spring Beans test case
   *
   * @throws Exception when something somewhere goes wrong
   */
  @Test
  public void SecondBeanTest() throws Exception {

    String mergeSchemaLocation = this.resources + "mergeschemas/BeansMergeSchema.xml";
    String namespaceUri = "http://www.springframework.org/schema/beans";

    Document base = JDom2Util.getInstance().getDocument(this.resources + "bases/Beans2.xml");
    Document patch = JDom2Util.getInstance().getDocument(this.resources + "patches/Beans2.xml");
    LeXeMerger testMerger = new LeXeMerger(mergeSchemaLocation);
    Element result = testMerger.merge(base.getRootElement(), patch.getRootElement(),
        ConflictHandlingType.PATCHOVERWRITE);
    assertEquals("Not all bean elements found", 2,
        result.getChildren("bean", Namespace.getNamespace("http://www.springframework.org/schema/beans")).size());
    List<Element> beanNodeList = result.getChildren("bean", Namespace.getNamespace(namespaceUri));
    Element bean1 = beanNodeList.get(0);

    assertEquals("wrong parent for bean1", "bean2", bean1.getAttribute("parent").getValue());
    assertEquals("No properties found", 1, bean1.getChildren("property", Namespace.getNamespace(namespaceUri)).size());

  }
}
