package com.github.maybeec.lexeme.merge.element.matcher;

import java.util.List;

import com.github.maybeec.lexeme.mergeschema.Criterion;
import com.github.maybeec.lexeme.schemaprovider.MergeSchemaProvider;

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
