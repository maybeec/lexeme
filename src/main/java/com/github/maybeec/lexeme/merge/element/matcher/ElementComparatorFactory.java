package com.github.maybeec.lexeme.merge.element.matcher;

import java.util.List;

import com.github.maybeec.lexeme.mergeschema.Criterion;
import com.github.maybeec.lexeme.schemaprovider.MergeSchemaProvider;

/**
 *
 * @author sholzer (Jul 7, 2015)
 */
public final class ElementComparatorFactory {

    /**
     * used builder
     */
    static ElementComparatorBuilder builder = null;

    /**
     * Sets the field 'builder'.
     * @param builder
     *            new value of builder
     * @author sholzer (Jul 7, 2015)
     */
    public static void setBuilder(ElementComparatorBuilder builder) {
        ElementComparatorFactory.builder = builder;
    }

    /**
     * Private constructor to prevent instances
     *
     * @author sholzer (Jul 7, 2015)
     */
    private ElementComparatorFactory() {

    }

    /**
     * Returns an implementation of an {@link ElementComparator}
     * @param criterionList
     *            list of Criterion objects to be checked against
     * @return ElementComparator
     * @author sholzer (Jul 7, 2015)
     * @param provider
     *            MergeSchemaProvider
     */
    public static ElementComparator build(List<Criterion> criterionList, MergeSchemaProvider provider) {
        if (builder == null) {
            builder = new GenericElementComparatorBuilder();
        }
        return builder.build(criterionList, provider);
    }

    /**
     * Default builder
     *
     * @author sholzer (Sep 18, 2015)
     */
    static final class GenericElementComparatorBuilder implements ElementComparatorBuilder {

        /**
         * {@inheritDoc}
         * @author sholzer (May 31, 2016)
         */
        @Override
        public ElementComparator build(List<Criterion> criterionList, MergeSchemaProvider provider) {
            return new ElementComparatorImpl(criterionList, provider);
        }

    }

}
