package com.github.maybeec.lexeme.merge.attribute;

import com.github.maybeec.lexeme.mergeschema.Attribute;

public final class AttributeMergerFactory {

  private static AttributeMergerBuilder builder = null;

  /**
   * Sets the field 'builder'.
   *
   * @param builder new value of builder
   */
  public static void setBuilder(AttributeMergerBuilder builder) {

    AttributeMergerFactory.builder = builder;
  }

  private AttributeMergerFactory() {

  }

  /**
   * Initializes a new AttributeMerger implementation.
   *
   * @param attribute Attribute object containing the merge rules for a given attribute
   * @return AttributeMerger
   */
  public static AttributeMerger build(Attribute attribute) {

    if (builder == null) {
      builder = new GenericAttributeMergerBuilder();
    }

    return builder.build(attribute);
  }

}
