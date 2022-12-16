package com.github.maybeec.lexeme.schemaprovider;

import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.maybeec.lexeme.common.util.JDom2Util;
import com.github.maybeec.lexeme.merge.element.matcher.CriterionSet;
import com.github.maybeec.lexeme.mergeschema.AdditionalNamespace;
import com.github.maybeec.lexeme.mergeschema.Criterion;
import com.github.maybeec.lexeme.mergeschema.DefaultCriterionType;
import com.github.maybeec.lexeme.mergeschema.Handling;
import com.github.maybeec.lexeme.mergeschema.MergeSchema;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;

public class MergeSchemaProviderImpl implements MergeSchemaProvider {
  /**
  *
  */
  private final Logger logger = LoggerFactory.getLogger(MergeSchemaProviderImpl.class);

  /**
   * path to the folder in which the MergeSchema files are located
   */
  private Path path;

  /**
   * last modification time
   */
  private long lastModification = 0;

  /**
   * List of found MergeSchema objects
   */
  private List<MergeSchema> mergeSchemaList;

  /**
   * The default MergeSchema
   */
  private MergeSchema defaultMergeSchema = null;

  /**
   * Identifies the default MergeSchema
   */
  static final String defaultMergeSchemaIdentifier = "$DEFAULT$";

  /**
   * Map to store already created MergeSchemaProviders. identification over their Paths
   */
  private static Map<Path, MergeSchemaProvider> providers = new HashMap<>();

  /**
   * Returns a MergeSchemaProvider for the given path
   *
   * @param path {@link Path} to the folder or file the MergeSchemas are stored
   * @return MergeSchemaProvider
   */
  public static MergeSchemaProvider getProviderForPath(Path path) {

    MergeSchemaProvider cached = providers.get(path);
    if (cached == null) {
      cached = new MergeSchemaProviderImpl(path);
      providers.put(path, cached);
    }
    return cached;
  }

  /**
   * Returns a MergeSchemaProvider for the given path
   *
   * @param string {@link String} representation of the path to the folder or file the MergeSchemas are stored
   * @return MergeSchemaProvider
   */
  public static MergeSchemaProvider getProviderForPath(String string) {

    return getProviderForPath(Paths.get(string));
  }

  /**
   * Maps a CriterionSet to its MergeSchema
   */
  private Map<String, CriterionSet> criterionSets = new HashMap<>();

  /**
   * Creates a new MergeSchemaProviderImplementation
   *
   * @param path path to the folder containing MergeSchemas
   */
  private MergeSchemaProviderImpl(Path path) {

    this.path = path;
    refreshMergeSchemaList();
  }

  @Override
  public MergeSchema getMergeSchemaForNamespaceURI(String namespaceURI) {

    if (namespaceURI != null) {
      for (MergeSchema schema : this.mergeSchemaList) {
        if (schema.getDefinition().getNamespace().equals(namespaceURI)) {
          this.logger.debug("Returned merge schema for namespace {}", namespaceURI);
          return schema;
        }
        for (AdditionalNamespace ans : schema.getDefinition().getAdditionalNamespace()) {
          if (ans.getNamespace().equals(namespaceURI)) {
            this.logger.debug("Returned merge schema for namespace {}", namespaceURI);
            return schema;
          }
        }
      }
      this.logger.warn("No MergeSchema found for Namespace {}", namespaceURI);
    }
    if (this.defaultMergeSchema != null) {
      this.logger.debug("Returned default merge schema for namespace {}", namespaceURI);
      return this.defaultMergeSchema;
    }
    this.logger.debug("Returned empty merge schema for namespace {}", namespaceURI);
    return JDom2Util.getInstance().initializeMergeSchema(new MergeSchema());
  }

  /**
   * Sets the path
   *
   * @param path string
   */
  public void setPath(String path) {

    this.path = Paths.get(path);
    this.logger.debug("Provider pathString set to {}", path);
    refreshMergeSchemaList();
  }

  /**
   * Empties current MergeSchemaList and loads all the MergeSchema objects from pathString
   */
  private void refreshMergeSchemaList() {

    this.mergeSchemaList = new LinkedList<>();
    long modification = this.lastModification;
    try {
      if (Files.isDirectory(this.path)) {
        DirectoryStream<Path> dir = Files.newDirectoryStream(this.path);
        for (Path p : dir) {
          if (!Files.isDirectory(p) && p.toFile().lastModified() > this.lastModification) {
            modification = (modification <= p.toFile().lastModified() ? p.toFile().lastModified() : modification);
            MergeSchema loadedSchema = loadMergeSchema(p);
            if (loadedSchema.getDefinition().getNamespace().equals(defaultMergeSchemaIdentifier)) {
              this.defaultMergeSchema = loadedSchema;
              this.logger.debug("Found default MergeSchema @ {}", p.toString());
              continue;
            }
            this.mergeSchemaList.add(loadedSchema);
            this.logger.debug("Found MergeSchema for {} @ {}", loadedSchema.getDefinition().getNamespace(),
                p.toString());
          }
        }
      } else {
        if (this.path.toFile().lastModified() > this.lastModification) {
          modification = (modification <= this.path.toFile().lastModified() ? this.path.toFile().lastModified()
              : modification);
          MergeSchema loadedSchema = loadMergeSchema(this.path);
          this.mergeSchemaList.add(loadedSchema);
          this.logger.debug("Found MergeSchema for {} @ {}", loadedSchema.getDefinition().getNamespace(),
              this.path.toString());
        }
      }
      this.lastModification = modification;
    } catch (Exception e) {
      this.logger.warn("Unexpected exception {}:{}", e.getClass().getName(), e.getMessage());
      if (this.logger.isDebugEnabled()) {
        e.printStackTrace();
      }
    } finally {
      this.logger.debug("Files in {} are last modified at {}", this.path.toString(),
          new java.util.Date(this.lastModification));
    }

  }

  /**
   * Loads a MergeSchema object from the given pathString
   *
   * @param path to the MergeSchema XML document
   * @return {@link MergeSchema} not null
   * @throws Exception when an error occurs during parsing (i.e. {@link java.io.FileNotFoundException})
   */
  private MergeSchema loadMergeSchema(Path path) throws Exception {

    Unmarshaller unmarshaller = JAXBContext.newInstance("com.github.maybeec.lexeme.mergeschema").createUnmarshaller();
    Object object = unmarshaller.unmarshal(Files.newBufferedReader(path, StandardCharsets.UTF_8));
    if (object instanceof JAXBElement && ((JAXBElement<?>) object).getValue() instanceof MergeSchema) {
      return (MergeSchema) ((JAXBElement<?>) object).getValue();
    }

    throw new Exception(String.format("%s is not a MergeSchema", path.toString()));

  }

  @Override
  public String getPath() {

    return this.path.toString();
  }

  @Override
  public Criterion getDefaultCriterion(String namespaceUri) {

    Criterion result = new Criterion();
    MergeSchema ms = getMergeSchemaForNamespaceURI(namespaceUri);
    DefaultCriterionType defaultCriterionType = ms.getDefaultCriterion();
    if (defaultCriterionType == null) {
      if (this.defaultMergeSchema != null && this.defaultMergeSchema.getDefaultCriterion() != null) {
        defaultCriterionType = this.defaultMergeSchema.getDefaultCriterion();
      } else {
        return result;
      }
    }
    result.setXpath(defaultCriterionType.getXpath());
    result.setOrdered(defaultCriterionType.isOrdered());
    return result;
  }

  @Override
  public List<Criterion> getCriterionFor(String elementName, String namespaceUri) {

    List<Criterion> result = new LinkedList<>();
    MergeSchema ms = getMergeSchemaForNamespaceURI(namespaceUri);
    if (ms.isRoot()) {
      Criterion criterion = new Criterion();
      criterion.setXpath("true()");
      result.add(criterion);
      return result;
    } else {
      for (Handling h : ms.getHandling()) {
        if (h.getFor().equals(elementName)) {
          return h.getCriterion();
        }
      }
      result.add(getDefaultCriterion(namespaceUri));
      return result;
    }

  }

  @Override
  public void setPath(Path path) {

    this.path = path;

  }

  @Override
  public List<Criterion> getDeepCriterion(String name, String namespace) {

    CriterionSet retrievedCriterionSet = this.criterionSets.get(namespace);
    if (retrievedCriterionSet == null) {
      MergeSchema retrievedMergeSchema = getMergeSchemaForNamespaceURI(namespace);
      retrievedCriterionSet = makeCriterionTree(retrievedMergeSchema);
      this.criterionSets.put(namespace, retrievedCriterionSet);
    }

    List<Criterion> result = walkCriterionSetFor(name, retrievedCriterionSet);
    if (result == null) {
      result = new LinkedList<>();
      result.add(getDefaultCriterion(namespace));
    }
    return result;

  }

  /**
   * traverses the CriterionSet tree recursive to find a list of criteria for a specified element
   *
   * @param name of the element
   * @param criterionSet root of the subtree
   * @return List of Criterion objects
   */
  public List<Criterion> walkCriterionSetFor(String name, CriterionSet criterionSet) {

    if (criterionSet.getName().equals(name)) {
      return criterionSet.getCriteria();
    }
    for (CriterionSet cs : criterionSet.getChildren()) {
      List<Criterion> deeperList = walkCriterionSetFor(name, cs);
      if (deeperList != null) {
        return deeperList;
      }
    }
    return null;
  }

  /**
   * Makes a CriterionSet tree from a MergeSchema
   *
   * @param schema the MergeSchema
   * @return CriterionSet
   */
  public CriterionSet makeCriterionTree(Handling schema) {

    CriterionSet currentNode = new CriterionSet(schema.getFor(), schema.getCriterion());
    for (Handling h : schema.getHandling()) {
      currentNode.addChild(makeCriterionTree(h));
    }
    return currentNode;
  }

}
