package com.github.maybeec.lexeme.merge.element.matcher;

import java.util.List;

import com.github.maybeec.lexeme.mergeschema.Criterion;
import com.github.maybeec.lexeme.schemaprovider.MergeSchemaProvider;

public final class ElementComparatorFactory {

  /**
   * used builder
   */
  static ElementComparatorBuilder builder = null;

  /**
   * Sets the field 'builder'.
   *
   * @param builder new value of builder
   */
  public static void setBuilder(ElementComparatorBuilder builder) {

    ElementComparatorFactory.builder = builder;
  }

  /**
   * Private constructor to prevent instances
   */
  private ElementComparatorFactory() {

  }

  /**
   * Returns an implementation of an {@link ElementComparator}
   *
   * @param criterionList list of Criterion objects to be checked against
   * @return ElementComparator
   * @param provider MergeSchemaProvider
   */
  public static ElementComparator build(List<Criterion> criterionList, MergeSchemaProvider provider) {

    if (builder == null) {
      builder = new GenericElementComparatorBuilder();
    }
    return builder.build(criterionList, provider);
  }

  /**
   * Default builder
   */
  static final class GenericElementComparatorBuilder implements ElementComparatorBuilder {

    @Override
    public ElementComparator build(List<Criterion> criterionList, MergeSchemaProvider provider) {

      return new ElementComparatorImpl(criterionList, provider);
    }

  }

}
