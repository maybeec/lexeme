package io.github.maybee.lexeme.validator;

import io.github.maybee.lexeme.common.exception.ValidationException;

import org.jdom2.Element;

/**
 *
 * @author sholzer (15.04.2015)
 */
public interface DocumentValidator {

    /**
     * Validates an {@link Element} against it's schemas defined in the node
     * @param result
     *            {@link Element}
     * @author sholzer (20.04.2015)
     * @throws ValidationException
     *             when the validation fails
     */
    public void validate(Element result) throws ValidationException;

    /**
     * Sets a flag for the validation
     * @param strict
     *            true if {@link ValidationException} should be thrown when the validation fails. False if an
     *            logger warning is sufficient.
     * @author sholzer (Sep 9, 2015)
     */
    public void setStrict(boolean strict);

}
