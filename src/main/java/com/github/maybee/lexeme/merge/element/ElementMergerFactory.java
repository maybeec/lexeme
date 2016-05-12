package com.github.maybee.lexeme.merge.element;

import java.util.List;

import com.github.maybee.lexeme.mergeschema.Handling;
import com.github.maybee.lexeme.schemaprovider.MergeSchemaProvider;

/**
 *
 * @author sholzer (Jul 3, 2015)
 */
public final class ElementMergerFactory {

    /**
     * The ElementMergerBuilder to be used
     */
    private static ElementMergerBuilder builder = null;

    /**
     * private constructor to prevent instances
     *
     * @author sholzer (Jul 7, 2015)
     */
    private ElementMergerFactory() {
    }

    /**
     * Sets the used {@link ElementMergerBuilder}
     * @param b
     *            the builder to be used. Passing null leads to the usage of a
     *            {@link GenericElementMergerBuilder}
     * @author sholzer (Jul 7, 2015)
     */
    public static void setBuilder(ElementMergerBuilder b) {
        builder = b;
    }

    /**
     * Builds and returns an ElementMerger instance
     * @param handling
     *            the Handling to be used
     * @param provider
     *            the MergeSchemaprovider that provides other MergeSchemas
     * @return ElementMerger
     * @author sholzer (Jul 3, 2015)
     */
    public static ElementMerger build(Handling handling, MergeSchemaProvider provider) {
        if (builder == null) {
            builder = new GenericElementMergerBuilder();
        }
        return builder.build(handling, provider);
    }

    /**
     * Builds and returns a nested ElementMerger instance
     * @param scope
     *            list of Handling object to be considered during the merge
     * @param handling
     *            the Handling object to instanciate this merger
     * @param provider
     *            the schema provider
     * @return ElementMerger
     * @author sholzer (Jan 20, 2016)
     */
    public static ElementMerger build(List<Handling> scope, Handling handling, MergeSchemaProvider provider) {
        if (builder == null) {
            builder = new GenericElementMergerBuilder();
        }
        return builder.build(scope, handling, provider);
    }

    /**
     * Generic builder
     *
     * @author sholzer (Sep 17, 2015)
     */
    static final class GenericElementMergerBuilder implements ElementMergerBuilder {

        @Override
        public ElementMerger build(Handling handling, MergeSchemaProvider provider) {
            return new ElementMergerImpl(handling, provider);
        }

        @Override
        public ElementMerger build(List<Handling> scope, Handling handling, MergeSchemaProvider provider) {
            return new ElementMergerImpl(handling, scope, provider);
        }

    }
}
