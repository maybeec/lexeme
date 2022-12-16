package com.github.maybeec.lexeme.validator;

import com.github.maybeec.lexeme.schemaprovider.MergeSchemaProvider;

public final class DocumentValidatorFactory {

  private static DocumentValidatorBuilder builder = null;

  /**
   * Sets the field 'builder'.
   *
   * @param builder new value of builder
   */
  public static void setBuilder(DocumentValidatorBuilder builder) {

    DocumentValidatorFactory.builder = builder;
  }

  private DocumentValidatorFactory() {

  }

  /**
   * Builds a DocumentValidator implementation
   *
   * @param provider MergeSchemaProvider
   * @return DocumentValidator
   */
  public static DocumentValidator build(MergeSchemaProvider provider) {

    if (builder == null) {
      builder = new GenericDocumentValidatorBuilder();
    }

    return builder.build(provider);
  }

  /**
   * Default builder
   */
  static final class GenericDocumentValidatorBuilder implements DocumentValidatorBuilder {

    @Override
    public DocumentValidator build(MergeSchemaProvider provider) {

      return new DocumentValidatorImpl(provider);
    }

  }

}
