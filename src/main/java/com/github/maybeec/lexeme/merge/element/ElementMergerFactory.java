package com.github.maybeec.lexeme.merge.element;

import java.util.List;

import com.github.maybeec.lexeme.mergeschema.Handling;
import com.github.maybeec.lexeme.schemaprovider.MergeSchemaProvider;

public final class ElementMergerFactory {

  /**
   * The ElementMergerBuilder to be used
   */
  private static ElementMergerBuilder builder = null;

  /**
   * private constructor to prevent instances
   */
  private ElementMergerFactory() {

  }

  /**
   * Sets the used {@link ElementMergerBuilder}
   *
   * @param b the builder to be used. Passing null leads to the usage of a {@link GenericElementMergerBuilder}
   */
  public static void setBuilder(ElementMergerBuilder b) {

    builder = b;
  }

  /**
   * Builds and returns an ElementMerger instance
   *
   * @param handling the Handling to be used
   * @param provider the MergeSchemaprovider that provides other MergeSchemas
   * @return ElementMerger
   */
  public static ElementMerger build(Handling handling, MergeSchemaProvider provider) {

    if (builder == null) {
      builder = new GenericElementMergerBuilder();
    }
    return builder.build(handling, provider);
  }

  /**
   * Builds and returns a nested ElementMerger instance
   *
   * @param scope list of Handling object to be considered during the merge
   * @param handling the Handling object to instantiate this merger
   * @param provider the schema provider
   * @return ElementMerger
   */
  public static ElementMerger build(List<Handling> scope, Handling handling, MergeSchemaProvider provider) {

    if (builder == null) {
      builder = new GenericElementMergerBuilder();
    }
    return builder.build(scope, handling, provider);
  }

  /**
   * Generic builder
   */
  static final class GenericElementMergerBuilder implements ElementMergerBuilder {

    @Override
    public ElementMerger build(Handling handling, MergeSchemaProvider provider) {

      return new ElementMergerImpl(handling, provider);
    }

    @Override
    public ElementMerger build(List<Handling> scope, Handling handling, MergeSchemaProvider provider) {

      return new ElementMergerImpl(handling, scope, provider);
    }

  }
}
