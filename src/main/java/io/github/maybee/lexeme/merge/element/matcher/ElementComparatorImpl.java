package io.github.maybee.lexeme.merge.element.matcher;

import io.github.maybee.lexeme.common.exception.ElementsCantBeMergedException;
import io.github.maybee.lexeme.common.util.JDom2Util;
import io.github.maybee.lexeme.schemaprovider.MergeSchemaProvider;

import java.util.LinkedList;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.maybee.lexeme.mergeschema.Criterion;

/**
 * @author Steffen Holzer
 */
public class ElementComparatorImpl implements ElementComparator {

    /**
     * Used to log errors, warnings or information about this object or its processes
     */
    private final Logger logger = LoggerFactory.getLogger(ElementComparatorImpl.class);

    /**
     * List containing the Criterion objects
     */
    private List<Criterion> criterionList;

    /**
     *
     */
    private MergeSchemaProvider provider;

    /**
     * Stores the XPathExpression objects created during creation of this object
     */
    // private List<XPathExpression<Object>> xpathList;

    /**
     * Initializes the object with a List of Criterion objects used to compare two elements
     * @param criterionList
     *            {@link List}&lt;{@link Criterion}>
     * @param provider
     *            provides the matching with namespace dependent information
     */
    public ElementComparatorImpl(List<Criterion> criterionList, MergeSchemaProvider provider) {
        this.criterionList = criterionList;
        this.provider = provider;

    }

    @Override
    public boolean compare(Element element1, Element element2) throws ElementsCantBeMergedException {
        if (!element1.getName().equals(element2.getName())) {
            return false;
        }
        try {
            return nodeBasedCompare(element1, element2);
        } catch (Exception e) {
            logger.debug("Comparison of {} elements failed due to {}:{} | assuming false",
                element1.getName(), e.getClass().getName(), e.getMessage());
            return false;
        }
    }

    /**
     * Compares two nodes through their subnodes considering the Criterion list of this Comparator
     * @param element1
     *            {@link Element}
     * @param element2
     *            {@link Element}
     * @return boolean true if all conditions are met
     * @throws ElementsCantBeMergedException
     *             when the elements can't be compared
     * @author sholzer (27.04.2015)
     */
    boolean nodeBasedCompare(Element element1, Element element2) throws ElementsCantBeMergedException {
        logger.debug("comparing {} with {} via nodes", element1.toString(), element2.toString());
        XPathFactory factory = XPathFactory.instance();
        for (Criterion criterion : criterionList) {
            logger.debug("Criterion:{} ordered:{}", criterion.getXpath(), criterion.isOrdered());
            XPathExpression<Object> xpathExpression = null;
            xpathExpression = factory.compile(criterion.getXpath());

            // evaluate the xpath expression for base and patch
            List<Object> evaluationList1 = xpathExpression.evaluate(element1);
            List<Object> evaluationList2 = xpathExpression.evaluate(element2);

            if (evaluationList1.size() != evaluationList2.size()) {
                return false;
            }
            if (criterion.isOrdered()) {
                for (int i = 0; i < evaluationList1.size(); i++) {
                    Object node1 = evaluationList1.get(i);
                    Object node2 = evaluationList2.get(i);
                    if (!nodeCompare(node1, node2) && !stringCompare(node1, node2)) {
                        return false;
                    }
                }
            } else { // check if every node in list1 has a match in list2
                List<Object> matches = new LinkedList<>();
                for (int i = 0; i < evaluationList1.size(); i++) {
                    Object baseNode = evaluationList1.get(i);
                    for (int j = 0; j < evaluationList2.size(); j++) {
                        Object patchNode = evaluationList2.get(j);
                        if (nodeCompare(baseNode, patchNode) || stringCompare(baseNode, patchNode)) {
                            if (!matches.contains(patchNode)) {
                                matches.add(patchNode);
                                break;
                            }
                        }
                    }
                }
                if (matches.size() != evaluationList2.size()) {
                    logger.debug("Not enough matches found");
                    return false;
                }
            }
        }
        // if all conditions are met return true
        logger.debug("All criteria match");
        return true;

    }

    /**
     * Compares the String representation of either an Element, Text, Document or Attribute
     * @param base
     *            {@link Object} (Element, Text, Document or Attribute)
     * @param patch
     *            {@link Object} (Element, Text, Document or Attribute)
     * @return true iff the String representation of both objects are equal. false otherwise
     * @author sholzer (15.05.2015)
     */
    boolean stringCompare(Object base, Object patch) {
        String baseString = JDom2Util.getInstance().parseString(base);
        String patchString = JDom2Util.getInstance().parseString(patch);
        logger.debug("{}=?{}:{}", baseString, patchString, baseString.equals(patchString));
        return baseString.equals(patchString);
    }

    /**
     * Compares two nodes by retrieved criteria from the corresponding merge schema
     * @param base
     *            result node from the base
     * @param patch
     *            result node from the patch
     * @return true if and only if base and patch are Element objects and they match during a comparison
     * @throws ElementsCantBeMergedException
     *             when base or patch isn't an Element object or they doesn't match in the comparison
     * @author sholzer (Sep 17, 2015)
     */
    boolean nodeCompare(Object base, Object patch) throws ElementsCantBeMergedException {
        if ((base instanceof Element) && (patch instanceof Element)) {
            Element baseElement = (Element) base;
            Element patchElement = (Element) patch;
            List<Criterion> criteria =
                provider.getDeepCriterion(baseElement.getName(), baseElement.getNamespaceURI());
            ElementComparator nestedComparator = ElementComparatorFactory.build(criteria, provider);
            boolean nestedResult = nestedComparator.compare(baseElement, patchElement);
            logger.debug("Comparing {} with {} as elements: {}", baseElement.getName(),
                patchElement.getName(), nestedResult);
            return nestedResult;
        }
        return false;
    }
}
