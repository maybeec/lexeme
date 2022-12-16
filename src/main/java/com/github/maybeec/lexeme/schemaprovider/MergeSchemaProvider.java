package com.github.maybeec.lexeme.schemaprovider;

import java.nio.file.Path;
import java.util.List;

import com.github.maybeec.lexeme.mergeschema.Criterion;
import com.github.maybeec.lexeme.mergeschema.MergeSchema;

/**
 * Provides the merge process with the merge schema documents for each namespace.
 */
public interface MergeSchemaProvider {

  /**
   * Returns the merge schema Document for a given NamespaceURI
   *
   * @param namespaceURI {@link String}
   * @return {@link MergeSchema} if a MergeSchema for the given namespace is found. If not a default will be returned
   */
  public MergeSchema getMergeSchemaForNamespaceURI(String namespaceURI);

  /**
   * Specifies the path to the MergeSchema Documents
   *
   * @param path {@link String} Source path to a folder
   */
  public void setPath(Path path);

  /**
   * Returns the specified path
   *
   * @return {@link String}
   */
  public String getPath();

  /**
   * Returns the default Criterion for the given namespace URI
   *
   * @param namespaceUri {@link String}
   * @return {@link Criterion}
   */
  Criterion getDefaultCriterion(String namespaceUri);

  /**
   * Returns the first Criterion for an element of a namespace
   *
   * @param elementName the local name of the element
   * @param namespaceUri the namespaceURI
   * @return {@link Criterion}
   */
  public List<Criterion> getCriterionFor(String elementName, String namespaceUri);

  /**
   * Walks the Handling tree of a MergeSchema and returns the first List of Criterion objects for an Element with the
   * specified name
   *
   * @param name of the Element a List of Criterion objects is requested
   * @param namespace of the Element to find a suitable MergeSchema
   * @return List of Criterion for the specified element name in the specified MergeSchema. Can be empty or contains
   *         only the default Criterion for the found MergeSchema
   */
  public List<Criterion> getDeepCriterion(String name, String namespace);

}
