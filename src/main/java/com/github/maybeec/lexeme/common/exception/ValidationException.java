package com.github.maybeec.lexeme.common.exception;

public class ValidationException extends XMLMergeException {

  /**
   * Create a new ValidationException.
   */
  public ValidationException() {

    super();
  }

  /**
   * Create a new ValidationException wrapping an existing exception.
   *
   * <p>
   * The existing exception will be embedded in the new one, and its message will become the default message for the
   * ValidationException.
   * </p>
   *
   * @param e The exception to be wrapped in a ValidationException.
   */
  public ValidationException(Exception e) {

    super(e);
  }

  /**
   * Create a new ValidationException.
   *
   * @param message The error or warning message.
   */
  public ValidationException(String message) {

    super(message);
  }

  /**
   * Create a new ValidationException from an existing exception.
   *
   * <p>
   * The existing exception will be embedded in the new one, but the new exception will have its own message.
   * </p>
   *
   * @param message The detail message.
   * @param e The exception to be wrapped in a ValidationException.
   */
  public ValidationException(String message, Exception e) {

    super(message, e);
  }

  /**
   * generated by eclipse
   */
  private static final long serialVersionUID = -8025892189475915159L;

}
