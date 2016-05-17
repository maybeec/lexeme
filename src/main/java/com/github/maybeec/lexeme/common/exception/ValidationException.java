package com.github.maybeec.lexeme.common.exception;

/**
 *
 * @author sholzer (19.02.2015)
 */
public class ValidationException extends XMLMergeException {

    /**
     * @param string
     *            Message containing information
     * @author sholzer (20.04.2015)
     */
    public ValidationException(String string) {
        super(string);
    }

    /**
     *
     * @author sholzer (20.04.2015)
     */
    public ValidationException() {
    }

    /**
     * generated by eclipse
     */
    private static final long serialVersionUID = -8025892189475915159L;

}