package com.github.maybeec.lexeme;

public enum ConflictHandlingType {

  /**
   * In case of a conflict the patch value will be used
   */
  PATCHOVERWRITE(false, false, false),
  /**
   * In case of a conflict the patch value will be used. If attributes or contents are marked as attachable the patch
   * values will be attached with the specified separation string.
   */
  PATCHATTACHOROVERWRITE(true, false, false),
  /**
   * In case of a conflict the base value will be used
   */
  BASEOVERWRITE(false, true, false),
  /**
   * In case of a conflict the base value will be used. If attributes or contents are marked as attachable the patch
   * values will be attached with the specified separation string.
   */
  BASEATTACHOROVERWRITE(true, true, false),
  /**
   * In case of a conflict the patch value will be used. Validation will be enabled.
   */
  PATCHOVERWRITEVALIDATE(false, false, true),
  /**
   * In case of a conflict the patch value will be used. If attributes or contents are marked as attachable the patch
   * values will be attached with the specified separation string. Validation will be enabled.
   */
  PATCHATTACHOROVERWRITEVALIDATE(true, false, true),
  /**
   * In case of a conflict the base value will be used. Validation will be enabled.
   */
  BASEOVERWRITEVALIDATE(false, true, true),
  /**
   * In case of a conflict the base value will be used. If attributes or contents are marked as attachable the patch
   * values will be attached with the specified separation string. Validation will be enabled.
   */
  BASEATTACHOROVERWRITEVALIDATE(true, true, true);

  /**
   * if the conflict handling allows texts from both documents
   */
  private boolean attachable;

  /**
   * if the conflict handling favors the bases text nodes
   */
  private boolean basePrefering;

  /**
   * if the conflict handling should enable validation
   */
  private boolean validating;

  /**
   * constructor
   *
   * @param attachable if the conflict handling allows texts from both documents
   * @param basePrefering if the conflict handling favors base nodes
   * @param validating if validation should be enabled
   */
  ConflictHandlingType(boolean attachable, boolean basePrefering, boolean validating) {

    this.attachable = attachable;
    this.basePrefering = basePrefering;
    this.validating = validating;
  }

  public boolean isAttachable() {

    return this.attachable;
  }

  public boolean isBasePrefering() {

    return this.basePrefering;
  }

  public boolean isValidating() {

    return this.validating;
  }

}
