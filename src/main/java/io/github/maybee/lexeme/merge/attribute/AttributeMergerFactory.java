package io.github.maybee.lexeme.merge.attribute;

import io.github.maybee.lexeme.mergeschema.Attribute;

/**
 *
 * @author sholzer (Jul 7, 2015)
 */
public final class AttributeMergerFactory {

    /**
     *
     */
    private static AttributeMergerBuilder builder = null;

    /**
     * Sets the field 'builder'.
     * @param builder
     *            new value of builder
     * @author sholzer (Jul 7, 2015)
     */
    public static void setBuilder(AttributeMergerBuilder builder) {
        AttributeMergerFactory.builder = builder;
    }

    /**
     * @author sholzer (Jul 7, 2015)
     */
    private AttributeMergerFactory() {
    }

    /**
     * Initializes a new AttributeMerger implementation.
     * @param attribute
     *            Attribute object containing the merge rules for a given attribute
     * @return AttributeMerger
     */
    public static AttributeMerger build(Attribute attribute) {
        if (builder == null) {
            builder = new GenericAttributeMergerBuilder();
        }

        return builder.build(attribute);
    }

}
