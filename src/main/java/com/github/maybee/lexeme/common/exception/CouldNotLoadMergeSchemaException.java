package com.github.maybee.lexeme.common.exception;


/**
 *
 * @author sholzer (23.03.2015)
 */
public class CouldNotLoadMergeSchemaException extends XMLMergeException {

    /**
     *
     */
    private static final long serialVersionUID = 7192506601030137110L;

    /**
     *
     * @author sholzer (23.03.2015)
     */
    public CouldNotLoadMergeSchemaException() {

    }

    /**
     * @param msg
     *            {@link String}
     * @author sholzer (23.03.2015)
     */
    public CouldNotLoadMergeSchemaException(String msg) {
        super(msg);
    }

}
