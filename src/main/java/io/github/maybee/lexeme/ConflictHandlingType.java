package io.github.maybee.lexeme;

/**
 *
 * @author sholzer (Jun 18, 2015)
 */
public enum ConflictHandlingType {

    /**
     * In case of a conflict the patch value will be used
     */
    PATCHOVERWRITE(false, false),
    /**
     * In case of a conflict the patch value will be used. If attributes or contents are marked as attachable
     * the patch values will be attached with the specified separation string.
     */
    PATCHATTACHOROVERWRITE(true, false),
    /**
     * In case of a conflict the base value will be used
     */
    BASEOVERWRITE(false, true),
    /**
     * In case of a conflict the base value will be used. If attributes or contents are marked as attachable
     * the patch values will be attached with the specified separation string.
     */
    BASEATTACHOROVERWRITE(true, true);

    /**
     * if the conflict handling allows texts from both documents
     */
    private boolean attachable;

    /**
     * if the conflict handling favors the bases text nodes
     */
    private boolean basePrefering;

    /**
     * constructor
     * @param attachable
     *            if the conflict handling allows texts from both documents
     * @param basePrefering
     *            if the conflict handling favors base nodes
     * @author sholzer (Sep 11, 2015)
     */
    ConflictHandlingType(boolean attachable, boolean basePrefering) {
        this.attachable = attachable;
        this.basePrefering = basePrefering;
    }

    /**
     *
     * @return boolean
     * @author sholzer (Sep 11, 2015)
     */
    public boolean isAttachable() {
        return attachable;
    }

    /**
     *
     * @return boolean
     * @author sholzer (Sep 11, 2015)
     */
    public boolean isBasePrefering() {
        return basePrefering;
    }

}
