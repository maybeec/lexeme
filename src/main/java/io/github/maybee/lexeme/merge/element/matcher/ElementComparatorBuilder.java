package io.github.maybee.lexeme.merge.element.matcher;

import io.github.maybee.lexeme.schemaprovider.MergeSchemaProvider;

import java.util.List;

import io.github.maybee.lexeme.mergeschema.Criterion;

/**
 *
 * @author sholzer (Jul 7, 2015)
 */
public interface ElementComparatorBuilder {

    /**
     * Returns an implementation of an {@link ElementComparator}
     * @param criterionList
     *            list of Criterion objects to be checked against
     * @return ElementComparator
     * @author sholzer (Jul 7, 2015)
     * @param provider
     *            MergeSchemaProvider
     */
    ElementComparator build(List<Criterion> criterionList, MergeSchemaProvider provider);

}
