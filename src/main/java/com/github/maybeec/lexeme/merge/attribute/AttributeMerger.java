package com.github.maybeec.lexeme.merge.attribute;

import com.github.maybeec.lexeme.ConflictHandlingType;
import com.github.maybeec.lexeme.mergeschema.Attribute;

/**
 * Merges an attribute of two xml elements
 */
public interface AttributeMerger {

  /**
   * Merges the values of two attributes of a given name into one.
   *
   * @param attribute1 String value of the attribute of the first element
   * @param attribute2 String value of the attribute of the second element
   * @param conflictHandling {@link ConflictHandlingType} specifying how conflicts will be handled during the merge
   *        process
   * @return String merged value
   */
  public String merge(String attribute1, String attribute2, ConflictHandlingType conflictHandling);

  /**
   * Returns the stored Attribute object
   *
   * @return Attribute object
   */
  public Attribute getAttribute();

}
