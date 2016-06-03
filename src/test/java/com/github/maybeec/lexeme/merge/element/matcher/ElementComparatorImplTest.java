package com.github.maybeec.lexeme.merge.element.matcher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Text;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.maybeec.lexeme.common.exception.ElementsCantBeMergedException;
import com.github.maybeec.lexeme.common.util.JDom2Util;
import com.github.maybeec.lexeme.merge.element.matcher.ElementComparatorImpl;
import com.github.maybeec.lexeme.mergeschema.Criterion;
import com.github.maybeec.lexeme.schemaprovider.MergeSchemaProvider;

/**
 *
 * @author sholzer (13.02.2015)
 */
public class ElementComparatorImplTest {

    /**
     *
     */
    MergeSchemaProvider provider;

    /**
     * Build up method
     *
     * @author sholzer (Sep 17, 2015)
     */
    @Before
    public void before() {
        provider = Mockito.mock(MergeSchemaProvider.class);
    }

    /**
     * Test method for
     * {@link com.github.maybeec.lexeme.merge.element.matcher.ElementComparatorImpl#compare(Element,Element)}
     * <p>
     * Compares one Element object with itself through the id attribute
     * @author sholzer (13.02.2015)
     * @throws XPathExpressionException
     *             thrown by compare
     * @throws ParserConfigurationException
     *             thrown by Document construction
     * @throws ElementsCantBeMergedException
     *             thrown by compare()
     */
    @Test
    public void testCompareElementWithItselfThroughAttribute() throws XPathExpressionException,
        ParserConfigurationException, ElementsCantBeMergedException {
        String rootName = "A";
        String attName = "a";
        String attValue = "1";

        String xpath = "./@" + attName;
        Element root = new Element(rootName);
        // doc.appendChild(root);
        root.setAttribute(attName, attValue);
        assertTrue("Expected true", getComparatorFromXpath(xpath).compare(root, root));
    }

    /**
     * Test method for
     * {@link com.github.maybeec.lexeme.merge.element.matcher.ElementComparatorImpl#compare(Element, Element)}
     * <p>
     * Compares two Element objects through the id attribute. Expecting true
     * @author sholzer (13.02.2015)
     * @throws XPathExpressionException
     *             thrown by compare
     * @throws ParserConfigurationException
     *             thrown by Document construction
     * @throws ElementsCantBeMergedException
     *             thrown by compare()
     */
    @Test
    public void testCompareElementWithMatchThroughAttribute() throws XPathExpressionException,
        ParserConfigurationException, ElementsCantBeMergedException {
        String rootName = "A";
        String attName = "a";
        String attValue = "1";

        String xpath = "./@" + attName;

        Element root1 = new Element(rootName);
        root1.setAttribute(attName, attValue);
        Element root2 = new Element(rootName);
        root2.setAttribute(attName, attValue);

        assertTrue("Expected true", getComparatorFromXpath(xpath).compare(root1, root2));
    }

    /**
     * Test method for
     * {@link com.github.maybeec.lexeme.merge.element.matcher.ElementComparatorImpl#compare(Element, Element)}
     * <p>
     * Compares two Element objects through the id attribute. Excpecting false
     * @author sholzer (13.02.2015)
     * @throws XPathExpressionException
     *             thrown by compare
     * @throws ParserConfigurationException
     *             thrown by Document construction
     * @throws ElementsCantBeMergedException
     *             thrown by compare()
     */
    @Test
    public void testCompareElementWithMisMatchThroughAttribute() throws XPathExpressionException,
        ParserConfigurationException, ElementsCantBeMergedException {
        String rootName = "A";
        String attName = "a";
        String attValue1 = "1";
        String attValue2 = "0";
        String xpath = "./@" + attName;

        Element root1 = new Element(rootName);
        root1.setAttribute(attName, attValue1);
        Element root2 = new Element(rootName);
        root2.setAttribute(attName, attValue2);

        ElementComparatorImpl test = getComparatorFromXpath(xpath);

        assertFalse("Expected false", test.compare(root1, root2));
    }

    /**
     * Test method for
     * {@link com.github.maybeec.lexeme.merge.element.matcher.ElementComparatorImpl#compare(Element, Element)}
     * <p>
     * Compares two Element objects through the deeper attributes
     * @author sholzer (13.02.2015)
     * @throws XPathExpressionException
     *             thrown by compare
     * @throws ParserConfigurationException
     *             thrown by Document construction
     * @throws ElementsCantBeMergedException
     *             thrown by compare()
     */
    @Test
    public void testCompareElementWithDeepMatch() throws ParserConfigurationException,
        XPathExpressionException, ElementsCantBeMergedException {
        String rootName = "A";
        String attName = "a";
        String childName = "B";
        String attValue = "1";

        String xpath = "./" + childName + "/@" + attName;

        Element root1 = new Element(rootName);
        Element child1 = new Element(childName);
        root1.addContent(child1);
        child1.setAttribute(attName, attValue);
        Element root2 = new Element(rootName);
        Element child2 = new Element(childName);
        root2.addContent(child2);
        child2.setAttribute(attName, attValue);
        assertTrue("Expected true", getComparatorFromXpath(xpath).compare(root1, root2));
    }

    /**
     * Test method for
     * {@link com.github.maybeec.lexeme.merge.element.matcher.ElementComparatorImpl#compare(Element,Element)}
     * <p>
     * Compares two Element objects through the deeper attributes. Expected false
     * @author sholzer (13.02.2015)
     * @throws XPathExpressionException
     *             thrown by compare
     * @throws ParserConfigurationException
     *             thrown by Document construction
     * @throws ElementsCantBeMergedException
     *             thrown by compare()
     */
    @Test
    public void testCompareElementWithDeepMisMatch() throws ParserConfigurationException,
        XPathExpressionException, ElementsCantBeMergedException {
        String rootName = "A";
        String attName = "a";
        String childName = "B";
        String attValue1 = "1";
        String attValue2 = "0";

        String xpath = "./" + childName + "/@" + attName;

        Element root1 = new Element(rootName);
        Element child1 = new Element(childName);
        root1.addContent(child1);
        child1.setAttribute(attName, attValue1);
        Element root2 = new Element(rootName);
        Element child2 = new Element(childName);
        root2.addContent(child2);
        child2.setAttribute(attName, attValue2);
        assertFalse("Expected false", getComparatorFromXpath(xpath).compare(root1, root2));
    }

    /**
     * Test method for {@link ElementComparatorImpl#compare(Element, Element)}
     * <p>
     * Compares two Elements through their text content. Expects mismatch
     * @throws Exception
     *             when something somewhere goes wrong
     * @author sholzer (23.03.2015)
     */
    @Test
    public void testCompareElementWithTextNodeMismatch() throws Exception {
        String rootName = "A";
        String textValue1 = "0";
        String textValue2 = "1";

        String xpath = "./text()";

        Element root1 = new Element(rootName);
        Text child1 = new Text(textValue1);
        root1.addContent(child1);
        Element root2 = new Element(rootName);
        Text child2 = new Text(textValue2);
        root2.addContent(child2);
        assertFalse("Expected false", getComparatorFromXpath(xpath).compare(root1, root2));

    }

    /**
     * Returns an ElementComparatorImpl with a Criterion object containing the given XPath expressoin
     * @param xpath
     *            String valid XPath expression
     * @return ElementComparatorImpl
     * @author sholzer (13.02.2015)
     */
    private ElementComparatorImpl getComparatorFromXpath(String xpath) {
        Criterion testCriterion = new Criterion();
        testCriterion.setXpath(xpath);
        List<Criterion> list = new LinkedList<>();
        list.add(testCriterion);
        return new ElementComparatorImpl(list, provider);
    }

    /*-------Node awareness---------*/

    /**
     *
     */
    String pathRoot = "src/test/resources/elementmerge/elementcomparator/";

    /**
     * Test method for {@link ElementComparatorImpl#nodeBasedCompare(Element, Element)}
     * <p>
     * Symmetric
     * @throws Exception
     *             when something somewhere goes wrong
     * @author sholzer (23.03.2015)
     */
    @Test
    public void testNodeCompareSymmetric() throws Exception {
        Document doc1 = JDom2Util.getInstance().getDocument(pathRoot + "ElementOne.xml", false);
        Document doc2 = JDom2Util.getInstance().getDocument(pathRoot + "ElementOne.xml", false);

        String xpath = "./@id";

        Criterion crit = new Criterion();
        crit.setOrdered(true);
        crit.setXpath(xpath);

        List<Criterion> critList = new LinkedList<>();
        critList.add(crit);

        ElementComparatorImpl test = new ElementComparatorImpl(critList, provider);
        assertTrue(test.nodeBasedCompare(doc1.getRootElement(), doc2.getRootElement()));

    }

    /**
     * Test method for {@link ElementComparatorImpl#nodeBasedCompare(Element, Element)}
     * <p>
     * Mismatch
     * @throws Exception
     *             when something somewhere goes wrong
     * @author sholzer (23.03.2015)
     */
    @Test
    public void testNodeCompareMismatch() throws Exception {
        Document doc1 = JDom2Util.getInstance().getDocument(pathRoot + "ElementOne.xml", false);
        Document doc2 = JDom2Util.getInstance().getDocument(pathRoot + "ElementThree.xml", false);

        String xpath = "./@id";

        Criterion crit = new Criterion();
        crit.setOrdered(true);
        crit.setXpath(xpath);

        List<Criterion> critList = new LinkedList<>();
        critList.add(crit);

        ElementComparatorImpl test = new ElementComparatorImpl(critList, provider);
        assertFalse(test.nodeBasedCompare(doc1.getRootElement(), doc2.getRootElement()));

    }

    /**
     * Test method for {@link ElementComparatorImpl#nodeBasedCompare(Element, Element)}
     * <p>
     * Unordered match
     * @throws Exception
     *             when something somewhere goes wrong
     * @author sholzer (23.03.2015)
     */
    @Test
    public void testNodeCompareUnorderedMatch() throws Exception {
        Document doc1 = JDom2Util.getInstance().getDocument(pathRoot + "ElementFour.xml", false);
        Document doc2 = JDom2Util.getInstance().getDocument(pathRoot + "ElementTwo.xml", false);

        String xpath = "./*";

        Criterion crit = new Criterion();
        crit.setOrdered(false);
        crit.setXpath(xpath);

        List<Criterion> critList = new LinkedList<>();
        critList.add(crit);

        ElementComparatorImpl test = new ElementComparatorImpl(critList, provider);
        assertTrue(test.nodeBasedCompare(doc1.getRootElement(), doc2.getRootElement()));

    }
}
