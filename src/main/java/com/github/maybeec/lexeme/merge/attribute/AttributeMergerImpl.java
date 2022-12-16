package com.github.maybeec.lexeme.merge.attribute;

import com.github.maybeec.lexeme.ConflictHandlingType;
import com.github.maybeec.lexeme.mergeschema.Attribute;

//import xmlmerge.dataStructure.Attribute;

/**
 * Merges two org.w3c.dom.Attr XML element attributes into one considering the information given by the XML schema and
 * the merge rules provided by an Attribute object
 */
public class AttributeMergerImpl implements AttributeMerger {

  /**
   * The Attribute object containing the merge rules
   */
  private Attribute attribute;

  /**
   * Initializes a new AttributeMergerImplementation.
   *
   * @param attribute Attribute object containing the merge rules for a given attribute
   */
  public AttributeMergerImpl(Attribute attribute) {

    this.attribute = attribute;
    if (this.attribute.getSeparationString() == null) {
      this.attribute.setSeparationString("");
    }
  }

  /**
   * Returns the field 'attribute'
   *
   * @return value of attribute
   */
  @Override
  public Attribute getAttribute() {

    return this.attribute;
  }

  @Override
  public String merge(String base, String patch, ConflictHandlingType conflictHandling) {

    if (base == null) {
      return patch;
    }
    if (patch == null) {
      return base;
    }

    switch (conflictHandling) {
      case BASEATTACHOROVERWRITEVALIDATE:
      case BASEATTACHOROVERWRITE:
        if (this.attribute.isAttachable()) {
          return base + this.attribute.getSeparationString() + patch;
        }
        //$FALL-THROUGH$
      case BASEOVERWRITEVALIDATE:
      case BASEOVERWRITE:
        return base;
      case PATCHATTACHOROVERWRITEVALIDATE:
      case PATCHATTACHOROVERWRITE:
        if (this.attribute.isAttachable()) {
          return base + this.attribute.getSeparationString() + patch;
        }
        //$FALL-THROUGH$
      case PATCHOVERWRITEVALIDATE:
      case PATCHOVERWRITE:
        return patch;
      default:
        return patch;

    }
  }
}
