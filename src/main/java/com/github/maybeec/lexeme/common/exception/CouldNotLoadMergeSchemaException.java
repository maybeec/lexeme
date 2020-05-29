package com.github.maybeec.lexeme.common.exception;


/**
 *
 * @author sholzer (23.03.2015)
 */
public class CouldNotLoadMergeSchemaException extends XMLMergeException {

    /**
     * Create a new CouldNotLoadMergeSchemaException.
     */
    public CouldNotLoadMergeSchemaException() {
        super();
    }

    /**
     * Create a new CouldNotLoadMergeSchemaException wrapping an existing exception.
     *
     * <p>
     * The existing exception will be embedded in the new one, and its message will become the default message
     * for the CouldNotLoadMergeSchemaException.
     * </p>
     *
     * @param e
     *            The exception to be wrapped in a CouldNotLoadMergeSchemaException.
     */
    public CouldNotLoadMergeSchemaException(Exception e) {
        super(e);
    }

    /**
     * Create a new CouldNotLoadMergeSchemaException.
     *
     * @param message
     *            The error or warning message.
     */
    public CouldNotLoadMergeSchemaException(String message) {
        super(message);
    }

    /**
     * Create a new CouldNotLoadMergeSchemaException from an existing exception.
     *
     * <p>
     * The existing exception will be embedded in the new one, but the new exception will have its own
     * message.
     * </p>
     *
     * @param message
     *            The detail message.
     * @param e
     *            The exception to be wrapped in a CouldNotLoadMergeSchemaException.
     */
    public CouldNotLoadMergeSchemaException(String message, Exception e) {
        super(message, e);
    }

    /**
     *
     */
    private static final long serialVersionUID = 7192506601030137110L;

}
