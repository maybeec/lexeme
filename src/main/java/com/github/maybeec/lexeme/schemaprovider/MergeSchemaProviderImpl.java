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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.maybeec.lexeme.common.util.JDom2Util;
import com.github.maybeec.lexeme.merge.element.matcher.CriterionSet;
import com.github.maybeec.lexeme.mergeschema.AdditionalNamespace;
import com.github.maybeec.lexeme.mergeschema.Criterion;
import com.github.maybeec.lexeme.mergeschema.DefaultCriterionType;
import com.github.maybeec.lexeme.mergeschema.Handling;
import com.github.maybeec.lexeme.mergeschema.MergeSchema;

/**
 *
 * @author sholzer (03.04.2015)
 */
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
     * @param path
     *            {@link Path} to the folder or file the MergeSchemas are stored
     * @return MergeSchemaProvider
     * @author sholzer (Jan 12, 2016)
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
     * @param string
     *            {@link String} representation of the path to the folder or file the MergeSchemas are stored
     * @return MergeSchemaProvider
     * @author sholzer (Jan 12, 2016)
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
     * @param path
     *            path to the folder containing MergeSchemas
     * @author sholzer (03.04.2015)
     */
    private MergeSchemaProviderImpl(Path path) {
        this.path = path;
        refreshMergeSchemaList();
    }

    /**
     * {@inheritDoc}
     * @author sholzer (May 31, 2016)
     */
    @Override
    public MergeSchema getMergeSchemaForNamespaceURI(String namespaceURI) {
        if (namespaceURI != null) {
            for (MergeSchema schema : mergeSchemaList) {
                if (schema.getDefinition().getNamespace().equals(namespaceURI)) {
                    logger.debug("Returned merge schema for namespace {}", namespaceURI);
                    return schema;
                }
                for (AdditionalNamespace ans : schema.getDefinition().getAdditionalNamespace()) {
                    if (ans.getNamespace().equals(namespaceURI)) {
                        logger.debug("Returned merge schema for namespace {}", namespaceURI);
                        return schema;
                    }
                }
            }
            logger.warn("No MergeSchema found for Namespace {}", namespaceURI);
        }
        if (defaultMergeSchema != null) {
            logger.debug("Returned default merge schema for namespace {}", namespaceURI);
            return defaultMergeSchema;
        }
        logger.debug("Returned empty merge schema for namespace {}", namespaceURI);
        return JDom2Util.getInstance().initializeMergeSchema(new MergeSchema());
    }

    /**
     * Sets the path
     * @param path
     *            string
     * @author sholzer (03.04.2015)
     */
    public void setPath(String path) {
        this.path = Paths.get(path);
        logger.debug("Provider pathString set to {}", path);
        refreshMergeSchemaList();
    }

    /**
     * Empties current MergeSchemaList and loads all the MergeSchema objects from pathString
     * @author sholzer (03.04.2015)
     */
    private void refreshMergeSchemaList() {
        mergeSchemaList = new LinkedList<>();
        long modification = lastModification;
        try {
            if (Files.isDirectory(path)) {
                DirectoryStream<Path> dir = Files.newDirectoryStream(path);
                for (Path p : dir) {
                    if (!Files.isDirectory(p) && p.toFile().lastModified() > lastModification) {
                        modification =
                            (modification <= p.toFile().lastModified() ? p.toFile().lastModified() : modification);
                        MergeSchema loadedSchema = loadMergeSchema(p);
                        if (loadedSchema.getDefinition().getNamespace().equals(defaultMergeSchemaIdentifier)) {
                            defaultMergeSchema = loadedSchema;
                            logger.debug("Found default MergeSchema @ {}", p.toString());
                            continue;
                        }
                        mergeSchemaList.add(loadedSchema);
                        logger.debug("Found MergeSchema for {} @ {}", loadedSchema.getDefinition().getNamespace(),
                            p.toString());
                    }
                }
            } else {
                if (path.toFile().lastModified() > lastModification) {
                    modification =
                        (modification <= path.toFile().lastModified() ? path.toFile().lastModified() : modification);
                    MergeSchema loadedSchema = loadMergeSchema(path);
                    mergeSchemaList.add(loadedSchema);
                    logger.debug("Found MergeSchema for {} @ {}", loadedSchema.getDefinition().getNamespace(),
                        path.toString());
                }
            }
            lastModification = modification;
        } catch (Exception e) {
            logger.warn("Unexpected exception {}:{}", e.getClass().getName(), e.getMessage());
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        } finally {
            logger.debug("Files in {} are last modified at {}", path.toString(), new java.util.Date(lastModification));
        }

    }

    /**
     * Loads a MergeSchema object from the given pathString
     * @param path
     *            to the MergeSchema XML document
     * @return {@link MergeSchema} not null
     * @throws Exception
     *             when an error occurs during parsing (i.e. {@link java.io.FileNotFoundException})
     * @author sholzer (03.04.2015)
     */
    private MergeSchema loadMergeSchema(Path path) throws Exception {
        Unmarshaller unmarshaller =
            JAXBContext.newInstance("com.github.maybeec.lexeme.mergeschema").createUnmarshaller();
        Object object = unmarshaller.unmarshal(Files.newBufferedReader(path, StandardCharsets.UTF_8));
        if (object instanceof JAXBElement && ((JAXBElement<?>) object).getValue() instanceof MergeSchema) {
            return (MergeSchema) ((JAXBElement<?>) object).getValue();
        }

        throw new Exception(String.format("%s is not a MergeSchema", path.toString()));

    }

    /**
     * {@inheritDoc}
     * @author sholzer (May 31, 2016)
     */
    @Override
    public String getPath() {
        return path.toString();
    }

    /**
     * {@inheritDoc}
     * @author sholzer (May 31, 2016)
     */
    @Override
    public Criterion getDefaultCriterion(String namespaceUri) {
        Criterion result = new Criterion();
        MergeSchema ms = getMergeSchemaForNamespaceURI(namespaceUri);
        DefaultCriterionType defaultCriterionType = ms.getDefaultCriterion();
        if (defaultCriterionType == null) {
            if (defaultMergeSchema != null && defaultMergeSchema.getDefaultCriterion() != null) {
                defaultCriterionType = defaultMergeSchema.getDefaultCriterion();
            } else {
                return result;
            }
        }
        result.setXpath(defaultCriterionType.getXpath());
        result.setOrdered(defaultCriterionType.isOrdered());
        return result;
    }

    /**
     * {@inheritDoc}
     * @author sholzer (May 31, 2016)
     */
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

    /**
     * {@inheritDoc}
     * @author sholzer (May 31, 2016)
     */
    @Override
    public void setPath(Path path) {
        this.path = path;

    }

    /**
     * {@inheritDoc}
     * @author sholzer (May 31, 2016)
     */
    @Override
    public List<Criterion> getDeepCriterion(String name, String namespace) {
        CriterionSet retrievedCriterionSet = criterionSets.get(namespace);
        if (retrievedCriterionSet == null) {
            MergeSchema retrievedMergeSchema = getMergeSchemaForNamespaceURI(namespace);
            retrievedCriterionSet = makeCriterionTree(retrievedMergeSchema);
            criterionSets.put(namespace, retrievedCriterionSet);
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
     * @param name
     *            of the element
     * @param criterionSet
     *            root of the subtree
     * @return List of Criterion objects
     * @author sholzer (Sep 17, 2015)
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
     * @param schema
     *            the MergeSchema
     * @return CriterionSet
     * @author sholzer (Sep 17, 2015)
     */
    public CriterionSet makeCriterionTree(Handling schema) {
        CriterionSet currentNode = new CriterionSet(schema.getFor(), schema.getCriterion());
        for (Handling h : schema.getHandling()) {
            currentNode.addChild(makeCriterionTree(h));
        }
        return currentNode;
    }

}
