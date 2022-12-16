package com.github.maybeec.lexeme.validator;

import com.github.maybeec.lexeme.schemaprovider.MergeSchemaProvider;

public interface DocumentValidatorBuilder {

  /**
   * Builds a DocumentValidator implementation
   *
   * @param provider MergeSchemaProvider
   * @return DocumentValidator
   */
  public DocumentValidator build(MergeSchemaProvider provider);

}
