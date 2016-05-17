package com.github.maybeec.lexeme.common.exception;


/**
 *
 * @author sholzer (10.03.2015)
 */
public class UnmatchingNamespacesException extends XMLMergeException {

    /**
     * by eclipse
     */
    private static final long serialVersionUID = 5804526835726941033L;

    /**
     *
     * @author sholzer (10.03.2015)
     */
    public UnmatchingNamespacesException() {

    }

    /**
     * @param message
     *            String
     * @author sholzer (10.03.2015)
     */
    public UnmatchingNamespacesException(String message) {
        super(message);
    }
}
