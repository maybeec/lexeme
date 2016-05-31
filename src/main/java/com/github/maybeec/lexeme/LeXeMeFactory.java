package com.github.maybeec.lexeme;

import com.github.maybeec.lexeme.schemaprovider.MergeSchemaProvider;

/**
 *
 * @author sholzer (Jul 7, 2015)
 */
public final class LeXeMeFactory {

    /**
     *
     */
    private static LeXeMeBuilder builder = null;

    /**
     * Sets the field 'builder'.
     * @param builder
     *            new value of builder
     * @author sholzer (Jul 7, 2015)
     */
    public static void setBuilder(LeXeMeBuilder builder) {
        LeXeMeFactory.builder = builder;
    }

    /**
     * Private constructor to prevent instances
     *
     * @author sholzer (Jul 7, 2015)
     */
    private LeXeMeFactory() {
    }

    /**
     * Special constructor to be used in nested merge processes. Won't perform a renaming of the nodes
     * prefixes
     * @param provider
     *            {@link MergeSchemaProvider} providing MergeSchemas for namespaces
     * @return a LeXeMerger
     * @author sholzer (12.03.2015)
     */
    public static LeXeMerger build(MergeSchemaProvider provider) {
        if (builder == null) {
            builder = new GenericLexemeBuilder();
        }
        return builder.build(provider);
    }

    /**
     * @param pathToMergeSchema
     *            String path to the MergeSchema to be used on this document. Note: Will in future builds
     *            replaced by pathToMergeSchemas which leads to a directory containing MergeSchemas for every
     *            used namespace.
     *            <p/>
     *            If <b>null</b> the default MergeSchemas will be used
     * @author sholzer (12.03.2015)
     * @return a LeXeMerger
     */
    public static LeXeMerger build(String pathToMergeSchema) {
        if (builder == null) {
            builder = new GenericLexemeBuilder();
        }
        return builder.build(pathToMergeSchema);
    }

    /**
     * A generic builder
     *
     * @author sholzer (Sep 17, 2015)
     */
    static final class GenericLexemeBuilder implements LeXeMeBuilder {

        /**
         * {@inheritDoc}
         * @author sholzer (May 31, 2016)
         */
        @Override
        public LeXeMerger build(MergeSchemaProvider provider) {
            return new LeXeMerger(provider);
        }

        /**
         * {@inheritDoc}
         * @author sholzer (May 31, 2016)
         */
        @Override
        public LeXeMerger build(String pathToMergeSchema) {
            return new LeXeMerger(pathToMergeSchema);
        }

    }

}
