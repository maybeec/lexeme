package com.github.maybeec.lexeme.merge.element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.junit.After;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.github.maybeec.lexeme.ConflictHandlingType;
import com.github.maybeec.lexeme.LeXeMeBuilder;
import com.github.maybeec.lexeme.LeXeMeFactory;
import com.github.maybeec.lexeme.LeXeMerger;
import com.github.maybeec.lexeme.common.exception.MultipleInstancesOfUniqueElementException;
import com.github.maybeec.lexeme.common.exception.XMLMergeException;
import com.github.maybeec.lexeme.common.util.JDom2Util;
import com.github.maybeec.lexeme.merge.element.matcher.ElementComparator;
import com.github.maybeec.lexeme.merge.element.matcher.ElementComparatorBuilder;
import com.github.maybeec.lexeme.merge.element.matcher.ElementComparatorFactory;
import com.github.maybeec.lexeme.mergeschema.Attribute;
import com.github.maybeec.lexeme.mergeschema.Criterion;
import com.github.maybeec.lexeme.mergeschema.Definition;
import com.github.maybeec.lexeme.mergeschema.Handling;
import com.github.maybeec.lexeme.mergeschema.MergeSchema;
import com.github.maybeec.lexeme.schemaprovider.MergeSchemaProvider;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;

/**
 *
 */
public class ElementMergerImplTest {

  private String path = "src/test/resources/elementmerge/elementmerger/";

  /**
   * Test method for
   * {@link com.github.maybeec.lexeme.merge.element.ElementMergerImpl#merge(Element, Element, ConflictHandlingType)}
   * <P>
   * tests the merging of two simple Element objects (content, no attributes)
   *
   * @throws Exception if something goes wrong
   */
  @Test
  public void testMergeSimpleElementOnlyContent() throws Exception {

    Document firstDoc = JDom2Util.getInstance().getDocument(this.path + "Base/SimpleElementOnlyContent.xml", false);
    Document secondDoc = JDom2Util.getInstance().getDocument(this.path + "Patch/SimpleElementOnlyContent.xml", false);
    Handling testConfig = new Handling();
    // testConfig.setUnique(HandlingType.FULL_ACCU);
    testConfig.setFor("A");

    MergeSchemaProvider mergeSchemaProvider = mock(MergeSchemaProvider.class);
    when(mergeSchemaProvider.getMergeSchemaForNamespaceURI(ArgumentMatchers.anyString())).thenReturn(new MergeSchema());

    ElementMergerImpl test = new ElementMergerImpl(testConfig, mergeSchemaProvider);

    Element element1 = firstDoc.getRootElement();

    Element element2 = secondDoc.getRootElement();

    Element result = test.merge(element1, element2, ConflictHandlingType.BASEOVERWRITE);

    assertTrue("Wrong tag name: " + result.getName(), result.getName().equals("A"));
    assertEquals("0", result.getText());

  }

  /**
   * Test method for
   * {@link com.github.maybeec.lexeme.merge.element.ElementMergerImpl#merge(Element, Element, ConflictHandlingType)}
   * <P>
   * tests the merging of two simple Element objects (no content, attributes)
   *
   * @throws IOException when file not found
   * @throws JDOMException when an exception occurs
   * @throws XMLMergeException shouldn't happen
   */
  @Test
  public void testMergeSimpleElementOnlyAttributes() throws JDOMException, IOException, XMLMergeException {

    Document firstDoc = JDom2Util.getInstance().getDocument(this.path + "Base/SimpleElementOnlyAttributes.xml", false);
    Document secondDoc = JDom2Util.getInstance().getDocument(this.path + "Patch/SimpleElementOnlyAttributes.xml",
        false);
    Handling testConfig = new Handling();
    // testConfig.setType(HandlingType.FULL_ACCU);
    testConfig.setFor("A");
    Attribute testAttribute = new Attribute();
    testAttribute.setFor("a");
    // testAttribute.setType(AttributeType.USE_FIRST);
    testConfig.getAttribute().add(testAttribute);

    MergeSchemaProvider mergeSchemaProvider = mock(MergeSchemaProvider.class);
    when(mergeSchemaProvider.getMergeSchemaForNamespaceURI(ArgumentMatchers.anyString())).thenReturn(new MergeSchema());

    ElementMergerImpl test = new ElementMergerImpl(testConfig, mergeSchemaProvider);
    Element element1 = firstDoc.getRootElement();
    Element element2 = secondDoc.getRootElement();
    Element result = test.merge(element1, element2, ConflictHandlingType.BASEOVERWRITE);
    org.jdom2.Attribute resultAttr = result.getAttribute("a");
    assertTrue("Wrong attribute value: " + resultAttr.getValue(),
        resultAttr.getValue().equals(element1.getAttribute("a").getValue()));

  }

  /**
   * Test method for
   * {@link com.github.maybeec.lexeme.merge.element.ElementMergerImpl#merge(Element, Element, ConflictHandlingType)}
   * <P>
   * tests the merging of two simple Element objects (content and attributes)
   *
   * @throws Exception if something goes wrong
   */
  @Test
  public void testMergeSimpleElementContentAndAttributes() throws Exception {

    Document firstDoc = JDom2Util.getInstance().getDocument(this.path + "Base/SimpleElementContentAndAttributes.xml",
        false);
    Document secondDoc = JDom2Util.getInstance().getDocument(this.path + "Patch/SimpleElementContentAndAttributes.xml",
        false);
    Handling testConfig = new Handling();
    // testConfig.setType(HandlingType.FULL_ACCU);
    testConfig.setFor("A");

    Attribute testAttribute = new Attribute();
    testAttribute.setFor("a");
    testAttribute.setAttachable(false);
    testConfig.getAttribute().add(testAttribute);

    MergeSchemaProvider mergeSchemaProvider = mock(MergeSchemaProvider.class);
    when(mergeSchemaProvider.getMergeSchemaForNamespaceURI(ArgumentMatchers.anyString())).thenReturn(new MergeSchema());

    ElementMergerImpl test = new ElementMergerImpl(testConfig, mergeSchemaProvider);
    Element element1 = firstDoc.getRootElement();
    Element element2 = secondDoc.getRootElement();
    Element result = test.merge(element1, element2, ConflictHandlingType.PATCHATTACHOROVERWRITE);
    assertTrue("Wrong attribute value: " + result.getAttribute("a").getValue(),
        result.getAttribute("a").getValue().equals(element2.getAttribute("a").getValue()));

    assertTrue("Wrong text content: " + result.getText(),
        result.getText().equals(element1.getText() + element2.getText()));

  }

  /**
   * Test method for
   * {@link com.github.maybeec.lexeme.merge.element.ElementMergerImpl#merge(Element, Element, ConflictHandlingType)}
   * <P>
   * tests the merging of two Element objects with one child element each
   *
   * @throws Exception if something goes wrong
   */
  @Test
  public void testMergeElementWithChildElement() throws Exception {

    Document firstDoc = JDom2Util.getInstance().getDocument(this.path + "Base/ElementWithChildElement.xml", false);
    Document secondDoc = JDom2Util.getInstance().getDocument(this.path + "Patch/ElementWithChildElement.xml", false);
    Handling testRootConfig = new Handling();
    testRootConfig.setUnique(true);
    testRootConfig.setFor("A");

    Handling testChildConfig = new Handling();
    testChildConfig.setFor("B");
    Criterion firstChildCriterion = new Criterion();
    firstChildCriterion.setXpath("true()");
    testChildConfig.getCriterion().add(firstChildCriterion);
    testChildConfig.setAttachableText(true);
    testChildConfig.setAttachableText(true);
    testRootConfig.getHandling().add(testChildConfig);

    MergeSchemaProvider mergeSchemaProvider = mock(MergeSchemaProvider.class);
    when(mergeSchemaProvider.getMergeSchemaForNamespaceURI(ArgumentMatchers.anyString())).thenReturn(new MergeSchema());

    ElementMergerImpl test = new ElementMergerImpl(testRootConfig, mergeSchemaProvider);
    Element element1 = firstDoc.getRootElement();
    Element element2 = secondDoc.getRootElement();
    Element result = test.merge(element1, element2, ConflictHandlingType.PATCHATTACHOROVERWRITE);
    assertTrue("No child element found", result.getChildren("B").size() > 0);
    Element childResult = result.getChildren("B").get(0);
    System.out.println(JDom2Util.getInstance().parseString(result));
    assertEquals("Wrong text content: " + childResult.getText(), "abc" + "def", childResult.getText());

  }

  /**
   * Test method for
   * {@link com.github.maybeec.lexeme.merge.element.ElementMergerImpl#merge(Element, Element, ConflictHandlingType)}
   * <P>
   * tests the merging of two Element objects with child elements
   *
   * @throws Exception if something goes wrong
   */
  @Test
  public void testMergeElementWithChildElements() throws Exception {

    Document firstDoc = JDom2Util.getInstance().getDocument(this.path + "Base/ElementWithChildElements.xml", false);
    Document secondDoc = JDom2Util.getInstance().getDocument(this.path + "Patch/ElementWithChildElements.xml", false);
    Handling testRootConfig = new Handling();
    testRootConfig.setUnique(true);
    testRootConfig.setFor("A");

    Handling testFirstChildConfig = new Handling();
    // testFirstChildConfig.setType(HandlingType.FULL_ACCU);
    testFirstChildConfig.setFor("B");
    testFirstChildConfig.setAttachableText(true);
    Criterion firstChildCriterion = new Criterion();
    firstChildCriterion.setXpath("true()");
    testFirstChildConfig.getCriterion().add(firstChildCriterion);
    testRootConfig.getHandling().add(testFirstChildConfig);

    Handling testSecondChildConfig = new Handling();
    testSecondChildConfig.setFor("C");
    // testSecondChildConfig.setType(HandlingType.FULL_ACCU);
    testRootConfig.getHandling().add(testSecondChildConfig);

    testFirstChildConfig.setAttachableText(true);

    MergeSchemaProvider mergeSchemaProvider = mock(MergeSchemaProvider.class);
    when(mergeSchemaProvider.getMergeSchemaForNamespaceURI(ArgumentMatchers.anyString())).thenReturn(new MergeSchema());

    ElementMergerImpl test = new ElementMergerImpl(testRootConfig, mergeSchemaProvider);
    Element element1 = firstDoc.getRootElement();
    Element element2 = secondDoc.getRootElement();
    Element result = test.merge(element1, element2, ConflictHandlingType.PATCHATTACHOROVERWRITE);

    assertTrue("No child for tag " + "B" + " found", result.getChildren("B").size() > 0);
    Element childResult = result.getChildren("B").get(0);

    assertEquals("Wrong text content: " + childResult.getText(), "abc" + "def", childResult.getText());

    assertTrue("No child for tag " + "C" + " found", result.getChildren("C").size() > 0);
    childResult = result.getChildren("C").get(0);

    assertEquals("Wrong text content: " + childResult.getText(), "abc", childResult.getText());
  }

  /**
   * Test method for
   * {@link com.github.maybeec.lexeme.merge.element.ElementMergerImpl#merge(Element, Element, ConflictHandlingType)}
   * <P>
   * tests the merging of two Element objects with unique child elements
   *
   * @throws Exception if something goes wrong
   */
  @Test(expected = MultipleInstancesOfUniqueElementException.class)
  public void testMergeUniqueElements() throws Exception {

    Document firstDoc = JDom2Util.getInstance().getDocument(this.path + "Base/UniqueElements.xml", false);
    Document secondDoc = JDom2Util.getInstance().getDocument(this.path + "Patch/UniqueElements.xml", false);
    Handling testRootConfig = new Handling();
    testRootConfig.setUnique(true);
    testRootConfig.setFor("A");

    Handling testFirstChildConfig = new Handling();
    testFirstChildConfig.setUnique(true);
    testFirstChildConfig.setFor("B");
    testRootConfig.getHandling().add(testFirstChildConfig);

    testFirstChildConfig.setAttachableText(true);

    MergeSchemaProvider mergeSchemaProvider = mock(MergeSchemaProvider.class);
    when(mergeSchemaProvider.getMergeSchemaForNamespaceURI(ArgumentMatchers.anyString())).thenReturn(new MergeSchema());

    ElementMergerImpl test = new ElementMergerImpl(testRootConfig, mergeSchemaProvider);
    Element element1 = firstDoc.getRootElement();
    Element element2 = secondDoc.getRootElement();
    test.merge(element1, element2, ConflictHandlingType.PATCHATTACHOROVERWRITE);

  }

  /**
   * Test method for
   * {@link com.github.maybeec.lexeme.merge.element.ElementMergerImpl#merge(Element, Element, ConflictHandlingType)}
   * <P>
   * tests the merging of two Element objects with unmatching elements
   *
   * @throws Exception if something goes wrong
   */
  @Test
  public void testMergeAttachElements() throws Exception {

    String testRootTag = "A";
    String testFirstChildTag = "B";
    String testSecondChildTag = "C";
    Document firstDoc = JDom2Util.getInstance().getDocument(this.path + "Base/AttachElements.xml", false);
    Document secondDoc = JDom2Util.getInstance().getDocument(this.path + "Patch/AttachElements.xml", false);
    Handling testRootConfig = new Handling();
    testRootConfig.setUnique(true);
    testRootConfig.setFor(testRootTag);

    Handling testFirstChildConfig = new Handling();
    testFirstChildConfig.setUnique(true);
    testFirstChildConfig.setFor(testFirstChildTag);
    testRootConfig.getHandling().add(testFirstChildConfig);

    Handling testSecondChildConfig = new Handling();
    testSecondChildConfig.setFor(testSecondChildTag);
    testSecondChildConfig.setUnique(true);
    testRootConfig.getHandling().add(testSecondChildConfig);

    testFirstChildConfig.setAttachableText(true);

    MergeSchemaProvider mergeSchemaProvider = mock(MergeSchemaProvider.class);
    when(mergeSchemaProvider.getMergeSchemaForNamespaceURI(ArgumentMatchers.anyString())).thenReturn(new MergeSchema());

    ElementMergerImpl test = new ElementMergerImpl(testRootConfig, mergeSchemaProvider);
    Element element1 = firstDoc.getRootElement();
    Element element2 = secondDoc.getRootElement();
    Element result = test.merge(element1, element2, ConflictHandlingType.PATCHATTACHOROVERWRITE);

    assertTrue("Nodes with tag " + testFirstChildTag + " should be present",
        result.getChildren(testFirstChildTag).size() == 1);
    assertTrue("Nodes with tag " + testSecondChildTag + " should be present",
        result.getChildren(testSecondChildTag).size() == 1);

  }

  /**
   * tests {@link ElementMergerImpl#merge(Element, Element, ConflictHandlingType)}
   * <p>
   * Test method for merging two elements with multiple child elements with the same name tag.
   *
   * @throws Exception when something somewhere goes wrong
   */
  @Test
  public void testMergeMultipleChildElements() throws Exception {

    Document doc1 = JDom2Util.getInstance().getDocument(this.path + "mergeMultipleChildElements_Base.xml");
    Document doc2 = JDom2Util.getInstance().getDocument(this.path + "mergeMultipleChildElements_Patch.xml");
    MergeSchema config = loadMergeSchema(this.path + "mergeMultipleChildElements_MergeSchema.xml");

    MergeSchemaProvider mergeSchemaProvider = mock(MergeSchemaProvider.class);
    when(mergeSchemaProvider.getMergeSchemaForNamespaceURI(ArgumentMatchers.anyString())).thenReturn(config);

    ElementMerger test = new ElementMergerImpl(config, mergeSchemaProvider);
    test.merge(doc1.getRootElement(), doc2.getRootElement(), ConflictHandlingType.PATCHOVERWRITE);
  }

  /**
   * Tests if an ElementMerger instantiates a new LeXeMerger in the correct way when encountering a new namespace
   *
   * @throws Exception when something goes wrong
   */
  @Test
  public void testNewNonRootNamespace() throws Exception {

    Element root1 = new Element("foo", "thisNamespace");
    Element child1 = new Element("baa", "thatNamespace");
    root1.addContent(child1);

    Element root2 = new Element("foo", "thisNamespace");
    Element child2 = new Element("baa", "thatNamespace");
    root1.addContent(child2);

    MergeSchema schema = new MergeSchema();
    schema.setRoot(false);
    Definition def = new Definition();
    def.setNamespace("thatNamespace");
    schema.setDefinition(def);

    final ElementComparator comparator = mock(ElementComparator.class);
    when(comparator.compare(ArgumentMatchers.any(Element.class), ArgumentMatchers.any(Element.class))).thenReturn(true);
    final MergeSchemaProvider provider = mock(MergeSchemaProvider.class);
    ElementComparatorFactory.setBuilder(new ElementComparatorBuilder() {

      @Override
      public ElementComparator build(List<Criterion> criterionList, MergeSchemaProvider provider) {

        return comparator;
      }

    });

    when(provider.getMergeSchemaForNamespaceURI("thatNamespace")).thenReturn(schema);
    when(provider.getCriterionFor(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
        .thenReturn(new LinkedList<Criterion>());

    final LeXeMerger LeXeMerger = Mockito.mock(LeXeMerger.class);
    when(LeXeMerger.merge(ArgumentMatchers.any(Element.class), ArgumentMatchers.any(Element.class),
        ArgumentMatchers.any(ConflictHandlingType.class))).thenReturn(new Element("foobar", "thatNamespace"));
    LeXeMeFactory.setBuilder(new LeXeMeBuilder() {

      @Override
      public LeXeMerger build(MergeSchemaProvider provider) {

        return LeXeMerger;
      }

      @Override
      public LeXeMerger build(String pathToMergeSchema) {

        throw new RuntimeException("Should not have been called");
      }

    });

    ElementMerger test = ElementMergerFactory.build(new Handling(), provider);
    test.merge(root1, root2, ConflictHandlingType.BASEOVERWRITE);

  }

  /*---------------------------evaluateWhereString--------------------------------*/

  /**
   * Test for {@link ElementMergerImpl#evaluateWhereString(Element, String)}
   * <p>
   * Evaluates a positive attribute
   *
   * @throws ParserConfigurationException document problems
   * @throws ReflectiveOperationException reflections
   * @throws XPathExpressionException xpath problems
   */
  @Test
  public void testEvaluateWhereStringTrue()
      throws ParserConfigurationException, ReflectiveOperationException, XPathExpressionException {

    String elementName = "A";
    String attName = "a";
    String attValue = "1";
    String where = "./@" + attName + " >0";

    Element element = new Element(elementName);
    element.setAttribute(attName, attValue);

    assertTrue("Excpected true", ElementMergerImpl.evaluateWhereString(element, where));

  }

  /**
   * Test for {@link ElementMergerImpl#evaluateWhereString(Element, String)}
   * <p>
   * Evaluates a positive attribute. Expects false
   *
   * @throws ParserConfigurationException document problems
   * @throws ReflectiveOperationException reflections
   * @throws XPathExpressionException xpath problems
   */
  @Test
  public void testEvaluateWhereStringFalse()
      throws ParserConfigurationException, ReflectiveOperationException, XPathExpressionException {

    String elementName = "A";
    String attName = "a";
    String attValue = "1";
    String where = "./@" + attName + " <0";

    Element element = new Element(elementName);
    element.setAttribute(attName, attValue);

    assertFalse("Excpected false", ElementMergerImpl.evaluateWhereString(element, where));

  }

  /**
   * Test for {@link ElementMergerImpl#evaluateWhereString(Element, String)}
   * <p>
   * Evaluates default true()
   *
   * @throws ParserConfigurationException document problems
   * @throws ReflectiveOperationException reflections
   * @throws XPathExpressionException xpath problems
   */
  @Test
  public void testEvaluateWhereStringDefault()
      throws ParserConfigurationException, ReflectiveOperationException, XPathExpressionException {

    String elementName = "A";
    String attName = "a";
    String attValue = "1";
    String where = "true()";

    Element element = new Element(elementName);
    element.setAttribute(attName, attValue);

    assertTrue("Excpected true", ElementMergerImpl.evaluateWhereString(element, where));

  }

  /**
   * Tests if the Attribute order is preserved while parsing
   *
   * @throws IOException when file not found (i guess...)
   * @throws JDOMException when something else goes wrong
   */
  @Test
  public void testAttributeOrdering() throws JDOMException, IOException {

    String path = "src/test/resources/";
    String filePath = path + "ConceptTest.xml";
    SAXBuilder builder = new SAXBuilder();
    Document doc = builder.build(filePath);

    Element element = doc.getRootElement();
    List<org.jdom2.Attribute> attributes = element.getAttributes();
    System.out.println(attributes);
    assertTrue(attributes.toString(), attributes.get(0).getName().equals("b"));
    assertTrue(attributes.toString(), attributes.get(1).getName().equals("c"));
    assertTrue(attributes.toString(), attributes.get(2).getName().equals("a"));
  }

  /**
   * Tests the default behavior of merging extended elements.
   *
   * @throws Exception Shouldn't happen
   */
  @Test
  public void testDefaultExtendedOtherNamespace() throws Exception {

    String path = "src/test/resources/elementmerge/elementmerger/extendedNamespace/";

    MergeSchemaProvider provider = mock(MergeSchemaProvider.class);
    final ElementComparator genericComparator = mock(ElementComparator.class);
    ElementComparatorBuilder comparatorBuilder = new ElementComparatorBuilder() {

      @Override
      public ElementComparator build(List<Criterion> criterionList, MergeSchemaProvider provider) {

        return genericComparator;
      }

    };
    ElementComparatorFactory.setBuilder(comparatorBuilder);
    LeXeMeBuilder leXeMeBuilder = mock(LeXeMeBuilder.class);
    LeXeMeFactory.setBuilder(leXeMeBuilder);
    when(leXeMeBuilder.build(ArgumentMatchers.any(MergeSchemaProvider.class)))
        .thenThrow(new RuntimeException("Invalid method call: XmlLawMergeFactory.build"));

    Document firstDoc = JDom2Util.getInstance().getDocument(path + "extOne_Base.xml", false);
    Document secondDoc = JDom2Util.getInstance().getDocument(path + "extOne_Patch.xml", false);
    Element base = firstDoc.getRootElement();
    Element patch = secondDoc.getRootElement();

    Handling rootHandling = new Handling();
    rootHandling.setScopeRef("ref");
    rootHandling.setNamespaceRef("http://bar.com");

    MergeSchema referedHandling = new MergeSchema();
    referedHandling.setLabel("ref");
    referedHandling.setFor("baseRoot");
    Handling bhandling = new ExtHandling();
    bhandling.setFor("b");
    referedHandling.getHandling().add(bhandling);

    when(provider.getMergeSchemaForNamespaceURI("http://bar.com")).thenReturn(referedHandling);
    // prevents the match of the <ns:b/> elements
    when(genericComparator.compare(ArgumentMatchers.any(Element.class), ArgumentMatchers.any(Element.class)))
        .thenReturn(false);

    ElementMergerImpl test = new ElementMergerImpl(rootHandling, provider);
    Element result = test.merge(base, patch, ConflictHandlingType.PATCHOVERWRITE);

    assertTrue(((ExtHandling) bhandling).criterionCalled);
    assertEquals(2, result.getChildren("b", Namespace.getNamespace("http://bar.com")).size());

  }

  /**
   * Tests the default behavior of merging extended elements with a deep referred Handling
   *
   * @throws Exception Shouldn't happen
   */
  @Test
  public void testExtendedOtherNamespaceDeepLabel() throws Exception {

    String path = "src/test/resources/elementmerge/elementmerger/extendedNamespace/";

    MergeSchemaProvider provider = mock(MergeSchemaProvider.class);
    final ElementComparator genericComparator = mock(ElementComparator.class);
    ElementComparatorBuilder comparatorBuilder = new ElementComparatorBuilder() {

      @Override
      public ElementComparator build(List<Criterion> criterionList, MergeSchemaProvider provider) {

        return genericComparator;
      }

    };
    ElementComparatorFactory.setBuilder(comparatorBuilder);
    LeXeMeBuilder leXeMeBuilder = mock(LeXeMeBuilder.class);
    LeXeMeFactory.setBuilder(leXeMeBuilder);
    when(leXeMeBuilder.build(ArgumentMatchers.any(MergeSchemaProvider.class)))
        .thenThrow(new RuntimeException("Invalid method call: XmlLawMergeFactory.build"));

    Document firstDoc = JDom2Util.getInstance().getDocument(path + "extOne_Base.xml", false);
    Document secondDoc = JDom2Util.getInstance().getDocument(path + "extOne_Patch.xml", false);
    Element base = firstDoc.getRootElement();
    Element patch = secondDoc.getRootElement();

    Handling rootHandling = new Handling();
    rootHandling.setScopeRef("ref");
    rootHandling.setNamespaceRef("http://bar.com");

    MergeSchema otherRootHandling = new MergeSchema();
    MergeSchema referedHandling = new MergeSchema();
    referedHandling.setFor("x");
    referedHandling.setLabel("ref");
    referedHandling.setFor("baseRoot");
    Handling bhandling = new ExtHandling();
    bhandling.setFor("b");
    referedHandling.getHandling().add(bhandling);
    otherRootHandling.getHandling().add(referedHandling);

    when(provider.getMergeSchemaForNamespaceURI("http://bar.com")).thenReturn(referedHandling);
    // prevents the match of the <ns:b/> elements
    when(genericComparator.compare(ArgumentMatchers.any(Element.class), ArgumentMatchers.any(Element.class)))
        .thenReturn(false);

    ElementMergerImpl test = new ElementMergerImpl(rootHandling, provider);
    Element result = test.merge(base, patch, ConflictHandlingType.PATCHOVERWRITE);

    assertTrue(((ExtHandling) bhandling).criterionCalled);
    assertEquals(2, result.getChildren("b", Namespace.getNamespace("http://bar.com")).size());

  }

  /*---------------------Helper----------------------------*/

  /**
   * Loads a MergeSchema from specified XML document file
   *
   * @param path to the file
   * @return MergeSchema
   * @throws Exception when something somewhere goes not as expected
   */
  MergeSchema loadMergeSchema(String path) throws Exception {

    Unmarshaller unmarshaller = JAXBContext.newInstance("com.github.maybeec.lexeme.mergeschema").createUnmarshaller();

    Object object = unmarshaller.unmarshal(new File(path));
    if (object instanceof JAXBElement && ((JAXBElement<?>) object).getValue() instanceof MergeSchema) {
      return (MergeSchema) ((JAXBElement<?>) object).getValue();
    }

    throw new Exception("Unmarshalled object is not a MergeSchema");
  }

  /**
   * Resets the factories
   */
  @After
  public void after() {

    LeXeMeFactory.setBuilder(null);
    ElementMergerFactory.setBuilder(null);
    ElementComparatorFactory.setBuilder(null);
  }

  /**
   * Extended Handling. Allows to check if the {@link ExtHandling#getCriterion()} was called
   */
  class ExtHandling extends Handling {

    /**
     * true if {@link #getCriterion() was called}
     */
    boolean criterionCalled = false;

    @Override
    public List<Criterion> getCriterion() {

      this.criterionCalled = true;
      return super.getCriterion();
    }

  }

}
