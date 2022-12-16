package com.github.maybeec.lexeme.common.exception;

/**
 * A generic exception thrown during the merge process of this library
 */
public class XMLMergeException extends Exception {

  /**
   * Create a new XMLMergeException.
   */
  public XMLMergeException() {

    super();
  }

  /**
   * Create a new XMLMergeException wrapping an existing exception.
   *
   * <p>
   * The existing exception will be embedded in the new one, and its message will become the default message for the
   * XMLMergeException.
   * </p>
   *
   * @param e The exception to be wrapped in a XMLMergeException.
   */
  public XMLMergeException(Exception e) {

    super(e);
  }

  /**
   * Create a new XMLMergeException.
   *
   * @param message The error or warning message.
   */
  public XMLMergeException(String message) {

    super(message);
  }

  /**
   * Create a new XMLMergeException from an existing exception.
   *
   * <p>
   * The existing exception will be embedded in the new one, but the new exception will have its own message.
   * </p>
   *
   * @param message The detail message.
   * @param e The exception to be wrapped in a XMLMergeException.
   */
  public XMLMergeException(String message, Exception e) {

    super(message, e);
  }

  /**
   * generated by Eclipse
   */
  private static final long serialVersionUID = 2939508045103474155L;

}
