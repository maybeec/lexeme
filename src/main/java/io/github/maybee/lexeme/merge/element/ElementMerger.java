package io.github.maybee.lexeme.merge.element;

import io.github.maybee.lexeme.ConflictHandlingType;
import io.github.maybee.lexeme.common.exception.XMLMergeException;

import org.jdom2.Element;

/**
 * Merges two {@link Element} XML elements into one.
 */
public interface ElementMerger {

    /**
     * Merges two {@link Element} objects into one
     * @param element1
     *            {@link Element} the first element
     * @param element2
     *            {@link Element} the second element
     * @param conflictHandling
     *            {@link ConflictHandlingType} the way conflicts should be handled.
     * @return {@link Element} not null.
     * @throws XMLMergeException
     *             if the elements can't be merged
     */
    public Element merge(org.jdom2.Element element1, org.jdom2.Element element2,
        ConflictHandlingType conflictHandling) throws XMLMergeException;

    /**
     * @param b
     *            boolean true if the elements to be merged are root elements
     * @author sholzer (24.03.2015)
     */
    public void setRoot(boolean b);

}