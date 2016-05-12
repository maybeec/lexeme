package com.github.maybee.lexeme.schemaprovider;

import java.nio.file.Path;
import java.util.List;

import com.github.maybee.lexeme.mergeschema.Criterion;
import com.github.maybee.lexeme.mergeschema.MergeSchema;

/**
 * Provides the merge process with the merge schema documents for each namespace.
 * @author sholzer (03.04.2015)
 */
public interface MergeSchemaProvider {

    /**
     * Returns the merge schema Document for a given NamespaceURI
     * @param namespaceURI
     *            {@link String}
     * @return {@link MergeSchema} if a MergeSchema for the given namespace is found. If not a default will be
     *         returned
     * @author sholzer (03.04.2015)
     */
    public MergeSchema getMergeSchemaForNamespaceURI(String namespaceURI);

    /**
     * Specifies the path to the MergeSchema Documents
     * @param path
     *            {@link String} Source path to a folder
     * @author sholzer (03.04.2015)
     */
    public void setPath(Path path);

    /**
     * Returns the specified path
     * @return {@link String}
     * @author sholzer (07.04.2015)
     */
    public String getPath();

    /**
     * Returns the default Criterion for the given namespace URI
     * @param namespaceUri
     *            {@link String}
     * @return {@link Criterion}
     */
    Criterion getDefaultCriterion(String namespaceUri);

    /**
     * Returns the first Criterion for an element of a namespace
     * @param elementName
     *            the local name of the element
     * @param namespaceUri
     *            the namespaceURI
     * @return {@link Criterion}
     * @author sholzer (Jun 30, 2015)
     */
    public List<Criterion> getCriterionFor(String elementName, String namespaceUri);

    /**
     * Walks the Handling tree of a MergeSchema and returns the first List of Criterion objects for an Element
     * with the specified name
     * @param name
     *            of the Element a List of Criterion objects is requested
     * @param namespace
     *            of the Element to find a suitable MergeSchema
     * @return List of Criterion for the specified element name in the specified MergeSchema. Can be empty or
     *         contains only the default Criterion for the found MergeSchema
     * @author sholzer (Sep 17, 2015)
     */
    public List<Criterion> getDeepCriterion(String name, String namespace);

}
