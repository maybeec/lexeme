package com.github.maybee.lexeme.validator;

import com.github.maybee.lexeme.schemaprovider.MergeSchemaProvider;

/**
 *
 * @author sholzer (Jul 7, 2015)
 */
public final class DocumentValidatorFactory {

    /**
     *
     */
    private static DocumentValidatorBuilder builder = null;

    /**
     * Sets the field 'builder'.
     * @param builder
     *            new value of builder
     * @author sholzer (Jul 7, 2015)
     */
    public static void setBuilder(DocumentValidatorBuilder builder) {
        DocumentValidatorFactory.builder = builder;
    }

    /**
     *
     *
     * @author sholzer (Jul 7, 2015)
     */
    private DocumentValidatorFactory() {
    }

    /**
     * Builds a DocumentValidator implementation
     * @param provider
     *            MergeSchemaProvider
     * @return DocumentValidator
     * @author sholzer (Jul 7, 2015)
     */
    public static DocumentValidator build(MergeSchemaProvider provider) {
        if (builder == null) {
            builder = new GenericDocumentValidatorBuilder();
        }

        return builder.build(provider);
    }

    /**
     * Default builder
     *
     * @author sholzer (Sep 18, 2015)
     */
    static final class GenericDocumentValidatorBuilder implements DocumentValidatorBuilder {

        @Override
        public DocumentValidator build(MergeSchemaProvider provider) {
            return new DocumentValidatorImpl(provider);
        }

    }

}
