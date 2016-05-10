package io.github.maybee.lexeme.merge.attribute;

import io.github.maybee.lexeme.mergeschema.Attribute;

/**
 *
 * @author sholzer (Jul 7, 2015)
 */
public final class GenericAttributeMergerBuilder implements AttributeMergerBuilder {

    @Override
    public AttributeMerger build(Attribute attribute) {
        return new AttributeMergerImpl(attribute);
    }

}
