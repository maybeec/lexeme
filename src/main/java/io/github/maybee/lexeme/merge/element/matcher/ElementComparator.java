package io.github.maybee.lexeme.merge.element.matcher;

import io.github.maybee.lexeme.common.exception.ElementsCantBeMergedException;

import org.jdom2.Element;

/**
 * Compares two elements to decide if or if not they're considered to be mergeable
 * @author Steffen Holzer
 */
public interface ElementComparator {

    /**
     * Compares two {@link Element} objects to decide if they can be merged.
     * @param firstElement
     *            {@link Element} first element
     * @param secondElement
     *            {@link Element} second element
     * @return boolean true if they're considered mergeable, false otherwise
     * @throws ElementsCantBeMergedException
     *             when an Exception occurs
     */
    public boolean compare(Element firstElement, Element secondElement) throws ElementsCantBeMergedException;

}