package com.github.maybeec.lexeme.common.exception;

public class MultipleInstancesOfUniqueElementException extends ElementsCantBeMergedException {

  /**
   * Create a new MultipleInstancesOfUniqueElementException.
   */
  public MultipleInstancesOfUniqueElementException() {

    super();
  }

  /**
   * Create a new MultipleInstancesOfUniqueElementException wrapping an existing exception.
   *
   * <p>
   * The existing exception will be embedded in the new one, and its message will become the default message for the
   * MultipleInstancesOfUniqueElementException.
   * </p>
   *
   * @param e The exception to be wrapped in a MultipleInstancesOfUniqueElementException.
   */
  public MultipleInstancesOfUniqueElementException(Exception e) {

    super(e);
  }

  /**
   * Create a new MultipleInstancesOfUniqueElementException.
   *
   * @param message The error or warning message.
   */
  public MultipleInstancesOfUniqueElementException(String message) {

    super(message);
  }

  /**
   * Create a new MultipleInstancesOfUniqueElementException from an existing exception.
   *
   * <p>
   * The existing exception will be embedded in the new one, but the new exception will have its own message.
   * </p>
   *
   * @param message The detail message.
   * @param e The exception to be wrapped in a MultipleInstancesOfUniqueElementException.
   */
  public MultipleInstancesOfUniqueElementException(String message, Exception e) {

    super(message, e);
  }

  /**
   * Generated by eclipse
   */
  private static final long serialVersionUID = -1346925895199808914L;

}
