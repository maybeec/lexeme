package com.github.maybeec.lexeme.merge.element;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.xml.xpath.XPathExpressionException;

import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Text;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.maybeec.lexeme.ConflictHandlingType;
import com.github.maybeec.lexeme.LeXeMeFactory;
import com.github.maybeec.lexeme.LeXeMerger;
import com.github.maybeec.lexeme.common.exception.ElementsCantBeMergedException;
import com.github.maybeec.lexeme.common.exception.MultipleInstancesOfUniqueElementException;
import com.github.maybeec.lexeme.common.exception.XMLMergeException;
import com.github.maybeec.lexeme.common.util.JDom2Util;
import com.github.maybeec.lexeme.merge.attribute.AttributeMergerFactory;
import com.github.maybeec.lexeme.merge.element.matcher.ElementComparator;
import com.github.maybeec.lexeme.merge.element.matcher.ElementComparatorFactory;
import com.github.maybeec.lexeme.mergeschema.Attribute;
import com.github.maybeec.lexeme.mergeschema.Handling;
import com.github.maybeec.lexeme.schemaprovider.MergeSchemaProvider;

/**
 * Implementation of {@link ElementMerger}. Merges two {@link Element} objects into one according to the
 * handling given at construction.
 * @author sholzer (11.02.2015)
 */
public class ElementMergerImpl implements ElementMerger {

    /**
     * Used to log errors, warnings or information about this object or its processes
     */
    private static Logger logger = LoggerFactory.getLogger(ElementMergerImpl.class);

    /**
     * Id to identify this object in the logger output
     */
    private String logId = "";

    /**
     * The Handling object containing the structural information if the elements to be merged
     */
    private Handling handling;

    /**
     * Contains the current local Handling object scope. An handling object is added to the scope if it
     * describes the rules for a new Element or if it's overriding the rules of an Handling object already in
     * the scope. The scope represents the node set of /ancestor-or-self::/sibling-or-self:: of the current
     * Handling tree
     */
    private List<Handling> handlingScopeList;

    /**
     * The MergeSchemaProvider used to provide MergeSchema for given namespaces
     */
    private MergeSchemaProvider provider;

    /**
     * The current namespaceURI
     */
    private String myNamespace;

    /**
     * The namespace of an referenced MergeSchema. Can be null if not used
     */
    private String referencedNamespace = "";

    /**
     * List of already used unique handlings. will refer to a new list on every
     * {@link #merge(Element, Element, ConflictHandlingType)} call
     */
    private List<Handling> usedUniqueHandlingList;

    /**
     * Initializes the ElementMergerImpl object with an Handling object specifying the merge rules for an
     * element
     * @param handling
     *            Handling object containing the merge rules for a specific element
     * @author sholzer (07.04.2015)
     * @param provider
     *            {@link MergeSchemaProvider} to provide the merge process of different namespaces with the
     *            used mergeSchema object
     */
    public ElementMergerImpl(Handling handling, MergeSchemaProvider provider) {
        logger.debug("Instanciate Handling for {}", handling.getFor());
        this.handling = handling;
        handlingScopeList = new LinkedList<>();
        handlingScopeList.addAll(handling.getHandling()); // getHandling() returns a live list
        this.provider = provider;
        // if a reference exists
        if (!handling.getScopeRef().equals("")) {
            logger.debug("Found MergeSchema reference: {}@{}", handling.getScopeRef(), (handling
                .getNamespaceRef().equals("") ? "local" : handling.getNamespaceRef()));
            referencedNamespace = handling.getNamespaceRef();
            handlingScopeList.addAll(getScopeFromRef(handling.getScopeRef(), handling.getNamespaceRef()));
        }
    }

    /**
     * Initializes the ElementMergerImpl object with an Handling object and the Handling scope from it's
     * creating object.
     * @param handling
     *            Handling object containing the merge rules for a specific element
     * @param scope
     *            List&lt;Handling> List of Handling objects that can be used on child elements.
     * @param provider
     *            a {@link MergeSchemaProvider}
     */
    ElementMergerImpl(Handling handling, List<Handling> scope, MergeSchemaProvider provider) {
        logger.debug("Instanciate Handling for {}", handling.getFor());
        this.handling = handling;
        handlingScopeList = createScope(scope, handling);
        this.provider = provider;
        // if a reference exists
        if (!handling.getScopeRef().equals("")) {
            logger.debug("Found MergeSchema reference: {}@{}", handling.getScopeRef(), (handling
                .getNamespaceRef().equals("") ? "local" : handling.getNamespaceRef()));
            referencedNamespace = handling.getNamespaceRef();
            handlingScopeList.addAll(getScopeFromRef(handling.getScopeRef(), handling.getNamespaceRef()));
        }
    }

    @Override
    public Element merge(Element element1, Element element2, ConflictHandlingType conflictHandling)
        throws XMLMergeException {

        myNamespace = element1.getNamespaceURI();
        logId = handling.getFor() + "@" + myNamespace + ": ";

        // Compute the text processing flags
        final boolean attachableText = (conflictHandling.isAttachable() && handling.isAttachableText());
        final boolean useBaseText = conflictHandling.isBasePrefering() || attachableText;
        final boolean usePatchText = !conflictHandling.isBasePrefering() || attachableText;

        logger.debug("{}:Text nodes from base will be {}used", logId, (useBaseText ? "" : "not "));
        logger.debug("{}:Text nodes from patch will be {}used", logId, (usePatchText ? "" : "not "));

        if (!useBaseText) {
            for (Iterator<Content> iterator = element1.getContent().iterator(); iterator.hasNext();) {
                Content c = iterator.next();
                if (c instanceof Text) {
                    iterator.remove();
                }
            }
        }
        if (!usePatchText) {
            for (Iterator<Content> iterator = element2.getContent().iterator(); iterator.hasNext();) {
                Content c = iterator.next();
                if (c instanceof Text) {
                    iterator.remove();
                }
            }
        }

        // Setup
        Element mergedElement = new Element(element1.getName(), element1.getNamespace());
        usedUniqueHandlingList = new LinkedList<>(); // stores Handling objects with
                                                     // .isUnique=true to keep track of
                                                     // elements declared as unique
        mergeAttributes(element1, element2, mergedElement, conflictHandling);

        // adding all base elements and texts (depending on the conflict handling) to the merge result as a
        // ,skeleton'
        for (Content node : element1.getContent()) {
            if (node instanceof Text) {
                if (useBaseText) {
                    mergedElement.addContent(node.clone());
                }
            } else {
                mergedElement.addContent(node.clone());
            }
        }
        // computing the relative ,,position'' of the documents to each other
        String firstMatchableElementFromPatch = null;
        RelativeState state = RelativeState.FIRSTELEMENTFROMBASE;
        if (!element1.getChildren().isEmpty()) {
            Element firstBaseElement = element1.getChildren().get(0);
            Element firstPatchElement =
                element2.getChild(firstBaseElement.getName(), firstBaseElement.getNamespace());
            int patchIndex = element2.getChildren().indexOf(firstPatchElement);
            if (patchIndex > 0) {
                state = RelativeState.FIRSTELEMENTFROMPATCH;
                firstMatchableElementFromPatch = firstPatchElement.getName();
            }
        } else {
            state = RelativeState.EMPTYBASE;
        }
        logger.debug("{} is in state {}", logId, state.name());
        // If the base is empty // TODO what if the base contains Texts
        if (state.equals(RelativeState.EMPTYBASE)) {
            for (Content node : element2.getContent()) {
                if (node instanceof Text) {
                    if (usePatchText) {
                        mergedElement.addContent(node.clone());
                    }
                } else {
                    mergedElement.addContent(node.clone());
                }
            }
            return mergedElement;
        }

        List<Content> patchContent = new LinkedList<>();
        // add the patch content too only if the conflictHandling is not Baseoverwrite
        for (Content c : element2.getContent()) {
            if (c instanceof Text) {
                if (usePatchText) {
                    patchContent.add(c);
                }
            } else {
                patchContent.add(c);
            }
        }
        // Finds a match for base nodes from the patch nodes and merges them if possible
        matchAndMergeNodes(mergedElement.getContent().listIterator(), patchContent, usePatchText,
            conflictHandling);

        ListIterator<Content> resultIterator = mergedElement.getContent().listIterator();
        ListIterator<Content> patchIterator = patchContent.listIterator();
        switch (state) {
        case FIRSTELEMENTFROMPATCH:
            // Align all nodes ,,above'' the element in the patch those tag is present in base and patch in
            // the result element.
            // firstMatchableElementFromPatch isn't null at this point
            while (patchIterator.hasNext()) {
                Content currentPatchNode = patchIterator.next();
                if (currentPatchNode instanceof Element
                    && ((Element) currentPatchNode).getName().equals(firstMatchableElementFromPatch)) {
                    // leave the while loop and undo the last next() call in patch
                    patchIterator.previous();
                    break;
                } else {
                    // add to the result and remove from the patch
                    resultIterator.add(currentPatchNode.clone());
                    patchIterator.remove();
                }
            }
            //$FALL-THROUGH$
        case FIRSTELEMENTFROMBASE:
            // Now both trees are ,,aligned'' and the accumulation of patch elements below base elements
            // will start
            while (resultIterator.hasNext()) {
                // Patch nodes will be listed BELOW base nodes
                Content lastGroupNode = getLastNodeFromGroup(resultIterator);
                while (patchIterator.hasNext()) {
                    Content currentPatchNode = patchIterator.next();
                    if ((lastGroupNode instanceof Element) && (currentPatchNode instanceof Element)) {
                        Element lastGroupElement = (Element) lastGroupNode;
                        Element currentPatchElement = (Element) currentPatchNode;
                        boolean sameName = lastGroupElement.getName().equals(currentPatchElement.getName());
                        /*
                         * The second condition covers the follwing case: <base> <a/> <c/> </base> <patch>
                         * <a/> <b/> <c/> </patch>
                         */
                        if (sameName
                            || (peekFor(lastGroupElement.getName(), patchIterator) && lastGroupElement
                                .getName().equals(currentPatchElement.getName()))) {
                            resultIterator.add(currentPatchElement.clone());
                            patchIterator.remove();
                            continue;
                        } else {
                            break;
                        }
                    } else {
                        resultIterator.previous(); // added as a bug fix. I have absolutely no idea why I
                                                   // even need this since the javadoc of add() clearly states
                                                   // that the node will be inserted BEFORE the current cursor

                        resultIterator.add(currentPatchNode.clone());
                        patchIterator.remove();
                    }
                }
            }
            //$FALL-THROUGH$
        case EMPTYBASE:
        default:
        }

        // add the left over patch nodes
        if (!patchContent.isEmpty()) {
            for (Content c : patchContent) {
                mergedElement.addContent(c.clone());
            }
        }

        // #25 end
        verifyUniqueness(mergedElement);
        return mergedElement;
    }

    /**
     * Evaluates if or if not an Element suffices the XPath expression of an Handling object
     * @param element
     *            to be tested
     * @param where
     *            String XPath expression
     * @return boolean true if the element suffices the XPath expression, false otherwise
     * @throws XPathExpressionException
     *             if where String can't be compiled (probably invalid XPath)
     */
    public static boolean evaluateWhereString(Element element, String where) throws XPathExpressionException {
        XPathFactory factory = XPathFactory.instance();
        XPathExpression<Object> xpath = factory.compile(where);
        List<Object> result = xpath.evaluate(element);
        if (result.contains(false)) {
            return false;
        }
        return true;
    }

    /**
     * Creates a list of Handling objects to be used on the child elements of the element this Handling is
     * describing
     * @param parentHandlingScope
     *            List<Handling> handling List of the parent Handling object
     * @param ownHandling
     *            the Handling object of this object
     * @return List containing Handling objects
     * @author sholzer (11.02.2015)
     */
    private List<Handling> createScope(List<Handling> parentHandlingScope, Handling ownHandling) {

        // Identify all Handlings that will be replaced
        List<Handling> replacement = new LinkedList<>();
        for (Handling parent : parentHandlingScope) {
            for (Handling child : ownHandling.getHandling()) {
                if (parent.getFor().equals(child.getFor()) && parent.getWhere().equals(child.getWhere())) {
                    replacement.add(parent);
                    break;
                }
            }
        }

        // The new scope is based on the old one
        List<Handling> scope = new LinkedList<>();
        scope.addAll(parentHandlingScope);

        // Remove the replaced handling objects from the return list
        for (Handling parent : replacement) {
            scope.remove(parent);
        }

        scope.addAll(ownHandling.getHandling());
        return scope;
    }

    /**
     * Returns the Handling object to be used on a given Element
     * @param element
     *            Element for which the Handling object should be returned
     * @return Handling
     * @author sholzer (17.02.2015)
     * @throws ElementsCantBeMergedException
     *             when the where-statement can't be parsed
     */
    private Handling getHandlingFromScopeForElement(Element element) throws ElementsCantBeMergedException {
        try {
            logger.debug("{}: Searching for {}", logId, element.getName());
            for (Handling handling : handlingScopeList) {
                if (handling.getFor().equals(element.getName())
                    && evaluateWhereString(element, handling.getWhere())) {
                    if (handling.isUnique()) {
                        usedUniqueHandlingList.add(handling);
                    }
                    logger.debug("{}: Found handling for {}", logId, handling.getFor());
                    return handling;
                }
            }
            logger.debug("{}: Found no Handling object for {}. Default used", logId, element.getName());
            Handling defaultHandling = getDefaultHandlingFor(element.getName(), myNamespace);

            if (defaultHandling.isUnique()) {
                usedUniqueHandlingList.add(defaultHandling);
            }
            return defaultHandling;
        } catch (XPathExpressionException e) {
            String message =
                String
                    .format(
                        "%s: Caught unexpected XPathExpressionException. Probaply the 'where'-statement is corrupt:%s",
                        logId, e.getMessage());

            logger.error(message);
            throw new ElementsCantBeMergedException(message);
        }
    }

    /**
     * Returns the Attribute object from the Handling object of this object for a given attribute name
     * @param name
     *            String
     * @return Attribute object
     * @author sholzer (17.02.2015)
     */
    private Attribute getAttributeForName(String name) {
        for (Attribute attribute : handling.getAttribute()) {
            if (attribute.getFor().equals(name)) {
                return attribute;
            }
        }
        logger.debug("{}: Found no Attribute object for {}. Default used", logId, name);
        return getDefaultAttributeFor(name);
    }

    /**
     * Returns the default Handling object for the given name
     * @param elementName
     *            String name of the element the Handling object will describe
     * @return Handling &lt;Handling for="$name" unique="false">&lt;DefaultCriterion.../>&lt;/>
     * @author sholzer (24.02.2015)
     * @param namespace
     *            {@link String} the namespace URI
     */
    private Handling getDefaultHandlingFor(String elementName, String namespace) {
        Handling handling = new Handling();
        handling.getCriterion().add(provider.getDefaultCriterion(namespace));
        handling.setFor(elementName);
        return handling;

    }

    /**
     * Returns the default Attribute object for the given name.
     * @param attributeName
     *            String name of the attribute the Attribute object will describe
     * @return Attribute &lt;Attribute for="$name" attachable="false"/>
     * @author sholzer (24.02.2015)
     */
    private Attribute getDefaultAttributeFor(String attributeName) {
        Attribute attribute = new Attribute();
        attribute.setFor(attributeName);
        return attribute;
    }

    /**
     * Sets the field 'root'.
     * @param root
     *            new value of root
     * @author sholzer (24.03.2015)
     */
    @Override
    public void setRoot(boolean root) {
    }

    /**
     * Creates an Handling scope from another MergeSchema
     * @param scopeRef
     *            the referenced Handling in another MergeSchema
     * @param namespaceRef
     *            the namespace of the other MergeSchema
     * @return {@link List}&lt;Handling>
     * @author sholzer (Jul 24, 2015)
     */
    List<Handling> getScopeFromRef(String scopeRef, String namespaceRef) {
        List<Handling> result = new LinkedList<>();
        // If no namespace is provided it'll be assumed that the reference is located in this MergeSchema
        if (namespaceRef.equals("")) {
            namespaceRef = myNamespace;
        }

        List<Handling> pathToLabeledHandling =
            recursiveHandlingPath(provider.getMergeSchemaForNamespaceURI(namespaceRef), scopeRef);
        // follow the Handling path and create the scope as in the algorithm would
        for (Handling h : pathToLabeledHandling) {
            result = createScope(result, h);
        }
        return result;
    }

    /**
     * Searches a given Handling tree for an Handling with the given label and return the path to it
     * @param handling
     *            Handling to be started at
     * @param label
     *            of the required Handling
     * @return {@link List}&lt;Handling> with the input Handling on top and the required Handling as last
     *         element
     * @author sholzer (Jul 24, 2015)
     */
    private List<Handling> recursiveHandlingPath(Handling handling, String label) {
        List<Handling> result = new LinkedList<>();
        if (handling.getLabel().equals(label)) { // recursion end
            result.add(handling);
            return result;
        }
        for (Handling h : handling.getHandling()) {// Recursion
            List<Handling> recursiveResult = recursiveHandlingPath(h, label);
            if (recursiveResult.isEmpty()) {
                continue;
            }
            // If the labeled Handling is in the current path add this Handling to the result and append the
            // recursionResult (which isn't empty since the labeled Handling is in it's path)
            result.add(h);
            result.addAll(recursiveResult);
        }

        return result;

    }

    /**
     * Finds for each node retrieved by the Iterator a match in the patchContent (if there is one) and merges
     * the two elements afterwards. Textnodes AFTER a match in the patchContent will be added below the merged
     * result. The match will be removed from the patchContent and the merge result will replace the match in
     * the resultIterators list.
     * @param resultIterator
     *            ListIterator of the list in that the patchContent will be patched
     * @param patchContent
     *            List of nodes in the patch. Can be altered.
     * @param conflictHandlingType
     *            for the nested merge process
     * @param usePatchText
     *            boolean if the text nodes from the patch document can be used
     * @throws XMLMergeException
     *             when something somewhere goes wrong. Can be thrown by the recursive call of
     *             {@link #merge(Element, Element, ConflictHandlingType)} or a new {@link LeXeMerger} instance
     * @author sholzer (Aug 24, 2015)
     */
    private void matchAndMergeNodes(ListIterator<Content> resultIterator, List<Content> patchContent,
        boolean usePatchText, ConflictHandlingType conflictHandlingType) throws XMLMergeException {
        // For each node in the base
        for (; resultIterator.hasNext();) {
            // result of the search. Will be a merged element or a text node
            Content nestedMergeResult = null;
            // List containing text nodes following a potential match element from the patch
            List<Text> followingTexts = new LinkedList<>();
            // current base node
            Content resultNode = resultIterator.next();
            // for each node in the patch
            for (ListIterator<Content> patchIterator = patchContent.listIterator(); patchIterator.hasNext();) {
                // current patch node
                Content patchNode = patchIterator.next();
                boolean matchFound = false;
                // if the base node is an element and the patch node is of the same type
                if (resultNode instanceof Element && patchNode instanceof Element) {
                    // casts to Elements
                    Element resultElement = (Element) resultNode;
                    Element patchElement = (Element) patchNode;
                    boolean foreignNamespace =
                        !(resultElement.getNamespaceURI().equals(myNamespace) || resultElement
                            .getNamespaceURI().equals(referencedNamespace));
                    // if they share the same namespace continue. Otherwise go to the next patch node
                    if (resultElement.getNamespace().equals(patchElement.getNamespace())) {
                        // retrieve a fitting Handling object
                        Handling usedHandling = getHandlingFromScopeForElement(resultElement);
                        // make a Comparator from the Handling
                        ElementComparator comparator =
                            ElementComparatorFactory.build(usedHandling.getCriterion(), provider);
                        // If they match continue. Otherwise go to the next patch node
                        if (comparator.compare(resultElement, patchElement) || foreignNamespace) {
                            // remove the found match from the patchContent
                            patchIterator.remove();
                            matchFound = true;
                            // Decide if the elements are in the current or inherited namespace OR from
                            // another namespace
                            if (!foreignNamespace) {
                                // If yes: Create a new ElementMerger element and invoke merge()
                                ElementMerger nestedMerger =
                                    ElementMergerFactory.build(handlingScopeList, usedHandling, provider);
                                nestedMergeResult =
                                    nestedMerger.merge(resultElement, patchElement, conflictHandlingType);
                            } else {
                                // If no: Create a new LeXeMerger instance and invoce merge()
                                LeXeMerger nestedMerger = LeXeMeFactory.build(provider);
                                nestedMergeResult =
                                    nestedMerger.merge(resultElement, patchElement, conflictHandlingType);
                            }
                        }
                    }
                }
                // If the base and the patch node are Text objects
                if (resultNode instanceof Text && patchNode instanceof Text) {
                    // cast to Text objects
                    Text resultText = (Text) resultNode;
                    Text patchText = (Text) patchNode;
                    // If they have the same normalised value they're considered the same
                    if (resultText.getTextNormalize().equals(patchText.getTextNormalize())) {
                        // Normalise the text node for a prettier output
                        resultText.setText(resultText.getTextNormalize());
                        patchIterator.remove();
                        matchFound = true;
                    }
                }
                // If a match has been found add all the text nodes below the match to the result list
                while (usePatchText && patchIterator.hasNext() && matchFound) {
                    Content nextPatchNode = patchIterator.next();
                    if (nextPatchNode instanceof Text) {
                        followingTexts.add((Text) nextPatchNode.clone());
                        patchIterator.remove();
                    } else {
                        break;
                    }
                }
                // if a match has been found leave the patches for loop
                if (matchFound) {
                    break;
                }
            }
            // if some merge has been performed replace the base node with the merge result.
            if (nestedMergeResult != null) {
                resultIterator.set(nestedMergeResult);
            }
            // If a merge/match has been performed add the following text nodes. If no node is contained in
            // followingTexts or the ConflictHandlingType is BASEOVERWRITE nothing happens here.
            if (usePatchText) {
                for (Text t : followingTexts) {
                    resultIterator.add(t);
                }
            }
        }

    }

    /**
     * Merges the attributes from base and patch and sets the result in the result element
     * @param base
     *            base element
     * @param patch
     *            patch element
     * @param result
     *            result element. Attributes will be altered
     * @param conflictHandling
     *            the conflictHandlingtype
     * @author sholzer (Aug 24, 2015)
     */
    private void mergeAttributes(Element base, Element patch, Element result,
        ConflictHandlingType conflictHandling) {
        for (org.jdom2.Attribute attribute : JDom2Util.getInstance().getUniqueAttributes(base, patch)) {

            org.jdom2.Attribute baseAttribute =
                base.getAttribute(attribute.getName(), attribute.getNamespace());
            org.jdom2.Attribute patchAttribute =
                patch.getAttribute(attribute.getName(), attribute.getNamespace());
            String baseAttributeValue = (baseAttribute == null ? null : baseAttribute.getValue());
            String patchAttributeValue = (patchAttribute == null ? null : patchAttribute.getValue());
            String mergedAttributeValue =
                AttributeMergerFactory.build(getAttributeForName(attribute.getName())).merge(
                    baseAttributeValue, patchAttributeValue, conflictHandling);
            result.setAttribute(attribute.getName(), mergedAttributeValue, attribute.getNamespace());
        }
    }

    /**
     *
     * @param iterator
     *            with hasNext() == true. Iterator position will change
     * @return the last node from the current group. i.e. the last <a> element from a consecutive group of <a>
     *         elements
     * @author sholzer (Aug 25, 2015)
     */
    private Content getLastNodeFromGroup(ListIterator<Content> iterator) {
        Content currentNode = iterator.next();
        if (iterator.hasNext()) {
            Content nextNode = iterator.next();
            if (nextNode.getClass().equals(currentNode.getClass())) {
                if (nextNode instanceof Element) {
                    Element currentElement = (Element) currentNode;
                    Element nextElement = (Element) nextNode;
                    if (!nextElement.getName().equals(currentElement.getName())) {
                        return currentNode;
                    }
                }
                iterator.previous();
                return getLastNodeFromGroup(iterator);
            } else {
                return currentNode;
            }
        } else {
            return currentNode;
        }
    }

    /**
     * Peeks into the list provided by the iterator if an element with the tag ,,name'' exists.
     * @param name
     *            the name to be looking for
     * @param iterator
     *            list iterator of a list to be looked at. next() and previous() will be called multiple
     *            times. Position of the iterator will be the same as before
     * @return true if an element with tag ,,name'' exists in the list of iterator
     * @author sholzer (Aug 25, 2015)
     */
    private boolean peekFor(String name, ListIterator<Content> iterator) {
        if (iterator.hasNext()) {
            // take a step forwards
            Content currentNode = iterator.next();
            boolean result;
            // if the current node is an element with the searched name return true
            if (currentNode instanceof Element && ((Element) currentNode).getName().equals(name)) {
                result = true;
            } else { // otherwise look at the next node
                result = peekFor(name, iterator);
            }
            // take a step backwards
            iterator.previous();
            return result;
        } else {// at the end of the list obviously there was no such element
            return false;
        }

    }

    /**
     * Checks if unique elements occur at most once in the merge result
     * @param mergedElement
     *            the element to be checked
     * @throws MultipleInstancesOfUniqueElementException
     *             if an unique element occurs more than once
     * @author sholzer (Aug 25, 2015)
     */
    private void verifyUniqueness(Element mergedElement) throws MultipleInstancesOfUniqueElementException {
        for (Handling h : usedUniqueHandlingList) {
            int counter = 0;
            if (!h.isUnique()) {
                continue;
            }
            for (Element child : mergedElement.getChildren()) {
                if (child.getName().equals(h.getFor())) {
                    counter++;
                }
            }
            if (counter > 1) {
                throw new MultipleInstancesOfUniqueElementException("Found " + counter + " instances of "
                    + h.getFor());
            }
        }
    }

    /**
     * describes the state the two input elements are in relation to each other
     *
     * @author sholzer (Aug 21, 2015)
     */
    private enum RelativeState {
        /**
         * Describes the case of both base and patch having the same beginning element tag or base and patch
         * being totally exclusive
         */
        FIRSTELEMENTFROMBASE,
        /**
         * Describes the case of the bases first element tag occurring in the patch farther below
         */
        FIRSTELEMENTFROMPATCH,
        /**
         * literally
         */
        EMPTYBASE;
    }
}
