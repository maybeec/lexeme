package com.github.maybeec.lexeme.merge.element.matcher;

import java.util.List;

import com.github.maybeec.lexeme.mergeschema.Criterion;
import com.github.maybeec.lexeme.schemaprovider.MergeSchemaProvider;

public interface ElementComparatorBuilder {

  /**
   * Returns an implementation of an {@link ElementComparator}
   *
   * @param criterionList list of Criterion objects to be checked against
   * @return ElementComparator
   * @param provider MergeSchemaProvider
   */
  ElementComparator build(List<Criterion> criterionList, MergeSchemaProvider provider);

}
