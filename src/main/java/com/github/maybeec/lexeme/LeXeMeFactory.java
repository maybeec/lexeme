package com.github.maybeec.lexeme;

import com.github.maybeec.lexeme.schemaprovider.MergeSchemaProvider;

public final class LeXeMeFactory {

  private static LeXeMeBuilder builder = null;

  /**
   * Sets the field 'builder'.
   *
   * @param builder new value of builder
   */
  public static void setBuilder(LeXeMeBuilder builder) {

    LeXeMeFactory.builder = builder;
  }

  /**
   * Private constructor to prevent instances
   */
  private LeXeMeFactory() {

  }

  /**
   * Special constructor to be used in nested merge processes. Won't perform a renaming of the nodes prefixes
   *
   * @param provider {@link MergeSchemaProvider} providing MergeSchemas for namespaces
   * @return a LeXeMerger
   */
  public static LeXeMerger build(MergeSchemaProvider provider) {

    if (builder == null) {
      builder = new GenericLexemeBuilder();
    }
    return builder.build(provider);
  }

  /**
   * @param pathToMergeSchema String path to the MergeSchema to be used on this document. Note: Will in future builds
   *        replaced by pathToMergeSchemas which leads to a directory containing MergeSchemas for every used namespace.
   *
   *        If <b>null</b> the default MergeSchemas will be used
   * @return a LeXeMerger
   */
  public static LeXeMerger build(String pathToMergeSchema) {

    if (builder == null) {
      builder = new GenericLexemeBuilder();
    }
    return builder.build(pathToMergeSchema);
  }

  /**
   * A generic builder
   */
  static final class GenericLexemeBuilder implements LeXeMeBuilder {

    @Override
    public LeXeMerger build(MergeSchemaProvider provider) {

      return new LeXeMerger(provider);
    }

    @Override
    public LeXeMerger build(String pathToMergeSchema) {

      return new LeXeMerger(pathToMergeSchema);
    }

  }

}
