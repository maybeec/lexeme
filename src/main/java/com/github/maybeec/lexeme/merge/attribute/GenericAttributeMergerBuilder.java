package com.github.maybeec.lexeme.merge.attribute;

import com.github.maybeec.lexeme.mergeschema.Attribute;

/**
 *
 * @author sholzer (Jul 7, 2015)
 */
public final class GenericAttributeMergerBuilder implements AttributeMergerBuilder {

    /**
     * {@inheritDoc}
     * @author sholzer (May 31, 2016)
     */
    @Override
    public AttributeMerger build(Attribute attribute) {
        return new AttributeMergerImpl(attribute);
    }

}
