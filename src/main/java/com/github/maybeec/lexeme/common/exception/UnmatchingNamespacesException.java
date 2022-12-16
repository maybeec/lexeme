package com.github.maybeec.lexeme.common.exception;

public class UnmatchingNamespacesException extends XMLMergeException {

  /**
   * Create a new UnmatchingNamespacesException.
   */
  public UnmatchingNamespacesException() {

    super();
  }

  /**
   * Create a new UnmatchingNamespacesException wrapping an existing exception.
   *
   * <p>
   * The existing exception will be embedded in the new one, and its message will become the default message for the
   * UnmatchingNamespacesException.
   * </p>
   *
   * @param e The exception to be wrapped in a UnmatchingNamespacesException.
   */
  public UnmatchingNamespacesException(Exception e) {

    super(e);
  }

  /**
   * Create a new UnmatchingNamespacesException.
   *
   * @param message The error or warning message.
   */
  public UnmatchingNamespacesException(String message) {

    super(message);
  }

  /**
   * Create a new UnmatchingNamespacesException from an existing exception.
   *
   * <p>
   * The existing exception will be embedded in the new one, but the new exception will have its own message.
   * </p>
   *
   * @param message The detail message.
   * @param e The exception to be wrapped in a UnmatchingNamespacesException.
   */
  public UnmatchingNamespacesException(String message, Exception e) {

    super(message, e);
  }

  /**
   * by eclipse
   */
  private static final long serialVersionUID = 5804526835726941033L;

}
