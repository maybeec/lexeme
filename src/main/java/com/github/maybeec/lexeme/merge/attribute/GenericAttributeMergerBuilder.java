package com.github.maybeec.lexeme.merge.attribute;

import com.github.maybeec.lexeme.mergeschema.Attribute;

public final class GenericAttributeMergerBuilder implements AttributeMergerBuilder {

  @Override
  public AttributeMerger build(Attribute attribute) {

    return new AttributeMergerImpl(attribute);
  }

}
