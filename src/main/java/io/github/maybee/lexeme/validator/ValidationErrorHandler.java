package io.github.maybee.lexeme.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * The ErrorHandler used in the DocumentValidator
 * @author sholzer (20.04.2015)
 */
class ValidationErrorHandler implements ErrorHandler {

    /**
     * Used for logging events of this object
     */
    private final Logger logger = LoggerFactory.getLogger(ValidationErrorHandler.class);

    @Override
    public void error(SAXParseException arg0) throws SAXException {
        if (arg0.getCause() != null) {
            throw (SAXParseException) arg0.getCause();
        }
        throw arg0;
    }

    @Override
    public void fatalError(SAXParseException arg0) throws SAXException {
        if (arg0.getCause() != null) {
            throw (SAXParseException) arg0.getCause();
        }
        throw arg0;
    }

    @Override
    public void warning(SAXParseException arg0) throws SAXException {
        logger.warn(arg0.getMessage());
    }

}
