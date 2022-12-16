package com.github.maybeec.lexeme.validator;

import org.jdom2.Element;

import com.github.maybeec.lexeme.common.exception.ValidationException;

public interface DocumentValidator {

  /**
   * Validates an {@link Element} against it's schemas defined in the node
   *
   * @param result {@link Element}
   * @throws ValidationException when the validation fails
   */
  public void validate(Element result) throws ValidationException;

  /**
   * Sets a flag for the validation
   *
   * @param strict true if {@link ValidationException} should be thrown when the validation fails. False if an logger
   *        warning is sufficient.
   */
  public void setStrict(boolean strict);

}
