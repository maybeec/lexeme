package com.github.maybeec.lexeme;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.github.maybeec.lexeme.common.exception.UnmatchingNamespacesException;
import com.github.maybeec.lexeme.common.exception.XMLMergeException;
import com.github.maybeec.lexeme.common.util.JDom2Util;
import com.github.maybeec.lexeme.merge.element.ElementMerger;
import com.github.maybeec.lexeme.merge.element.ElementMergerFactory;
import com.github.maybeec.lexeme.mergeschema.Handling;
import com.github.maybeec.lexeme.mergeschema.MergeSchema;
import com.github.maybeec.lexeme.schemaprovider.MergeSchemaProvider;
import com.github.maybeec.lexeme.schemaprovider.MergeSchemaProviderImpl;
import com.github.maybeec.lexeme.validator.DocumentValidator;
import com.github.maybeec.lexeme.validator.DocumentValidatorFactory;

/**
 * The API of the language-aware XML Merger. Merges two XML documents of the same XML based language into one.
 */
public class LeXeMerger {

    /**
     * Logger instance
     */
    private final Logger logger = LoggerFactory.getLogger(LeXeMerger.class);

    /**
     * the MergeSchema this XMLMerger works with
     */
    private MergeSchema mergeSchema;

    /**
     * The root ElementMerger.
     */
    private ElementMerger rootMerger;

    /**
     * the mergeSchema provider used to retrieve the mergeSchema
     */
    private MergeSchemaProvider provider;

    /**
     * Specifies if the merge result will be validated
     */
    private boolean validation = true;

    /**
     * Default ConflictHandlingType
     */
    private ConflictHandlingType conflictHandlingType = ConflictHandlingType.PATCHOVERWRITE;

    /**
     * @param pathToMergeSchema
     *            String representing the path to a folder in which merge schemas are stored. This merge
     *            schemas will be used during the merge process.
     *            
     *            If <b>null</b> the execution path will be used
     * @author sholzer (12.03.2015)
     */
    public LeXeMerger(String pathToMergeSchema) {
        if (pathToMergeSchema == null) {
            pathToMergeSchema = "./";
        }
        provider = MergeSchemaProviderImpl.getProviderForPath(Paths.get(pathToMergeSchema));

    }

    /**
     * @param pathToMergeSchema
     *            the path to a folder in which merge schemas are stored. This merge schemas will be used
     *            during the merge process.
     *            
     *            If <b>null</b> the execution path will be used
     * @author sholzer (12.03.2015)
     */
    public LeXeMerger(Path pathToMergeSchema) {
        new Document();
        if (pathToMergeSchema == null) {
            Path path = Paths.get("./");
            provider = MergeSchemaProviderImpl.getProviderForPath(path);
        } else {
            provider = MergeSchemaProviderImpl.getProviderForPath(pathToMergeSchema);
        }

    }

    /**
     * @param pathToMergeSchema
     *            String representing the path to a folder in which merge schemas are stored. This merge
     *            schemas will be used during the merge process.
     *            
     *            If <b>null</b> the execution path will be used
     * @author sholzer (12.03.2015)
     * @param ct
     *            The default ConflictHandling
     */
    public LeXeMerger(Path pathToMergeSchema, ConflictHandlingType ct) {
        conflictHandlingType = ct;
        new Document();
        if (pathToMergeSchema == null) {
            Path path = Paths.get("./");
            provider = MergeSchemaProviderImpl.getProviderForPath(path);
        } else {
            provider = MergeSchemaProviderImpl.getProviderForPath(pathToMergeSchema);
        }

    }

    /**
     * @param pathToMergeSchema
     *            String representing the path to a folder in which merge schemas are stored. This merge
     *            schemas will be used during the merge process.
     *            
     *            If <b>null</b> the execution path will be used
     * @author sholzer (12.03.2015)
     * @param cht
     *            The default ConflictHandling
     */
    public LeXeMerger(String pathToMergeSchema, ConflictHandlingType cht) {
        conflictHandlingType = cht;
        if (pathToMergeSchema == null) {
            pathToMergeSchema = "./";
        }
        Path path = Paths.get(pathToMergeSchema);
        provider = MergeSchemaProviderImpl.getProviderForPath(path);

    }

    /**
     * Special constructor to be used in nested merge processes. Won't perform a renaming of the nodes
     * prefixes
     * @param provider
     *            {@link MergeSchemaProvider} providing MergeSchemas for namespaces
     * @author sholzer (12.03.2015)
     */
    public LeXeMerger(MergeSchemaProvider provider) {
        new Document();
        this.provider = provider;
        validation = false;
    }

    /**
     * Returns the field 'validation'
     * @return boolean value of validation
     * @author sholzer (20.04.2015)
     */
    public boolean isValidation() {
        return validation;
    }

    /**
     * Sets the field 'validation'.
     * @param validation
     *            boolean new value of validation
     * @author sholzer (20.04.2015)
     */
    public void setValidation(boolean validation) {
        this.validation = validation;
    }

    /**
     * Top level method. Merges two given XML documents represented by two {@link Element} object according to
     * the merge rules stored in the mergeSchema that is also given as an Element object
     * @param element1
     *            {@link Element} root Element of the first xml document
     * @param element2
     *            {@link Element} root Element of the second xml document
     * @param conflictHandling
     *            {@link ConflictHandlingType} specifying how conflicts will be handled during the merge
     *            process
     *
     * @return {@link Element}
     * @throws XMLMergeException
     *             if the elements can't be merged
     */
    public Element merge(Element element1, Element element2, ConflictHandlingType conflictHandling)
        throws XMLMergeException {
        logger.debug("Starting merge process");
        if (element1.getNamespaceURI() != null || element2.getNamespaceURI() != null) {
            if (!element1.getNamespaceURI().equals(element2.getNamespaceURI())) {
                throw new UnmatchingNamespacesException(String.format(
                    "URI of elements doesn't match. Found %s and %s ", element1.getNamespaceURI(),
                    element2.getNamespaceURI()));
            }
        }

        String mergedSchemaLocation =
            JDom2Util.getInstance().getSchemaLocationString(element1, element2, conflictHandling);
        if (!mergedSchemaLocation.isEmpty()) {
            element1.setAttribute("schemaLocation", mergedSchemaLocation, JDom2Util.XSI);
            element2.setAttribute("schemaLocation", mergedSchemaLocation, JDom2Util.XSI);
        }
        mergeSchema = provider.getMergeSchemaForNamespaceURI(element1.getNamespaceURI());
        if (mergeSchema.isRoot()) {
            rootMerger = ElementMergerFactory.build(mergeSchema, provider);
        } else {
            for (Handling h : mergeSchema.getHandling()) {
                if (h.getFor().equals(element1.getName())) {
                    rootMerger = ElementMergerFactory.build(h, provider);
                }
            }
        }
        //
        rootMerger.setRoot(true);
        Element result = rootMerger.merge(element1, element2, conflictHandling);
        DocumentValidator docVal = DocumentValidatorFactory.build(provider);
        if (validation) {
            docVal.setStrict(true);
        }
        docVal.validate(result);
        return result;
    }

    /**
     * @see #merge(Element, Element, ConflictHandlingType)
     * @param doc1
     *            {@link Document} to be merged
     * @param doc2
     *            {@link Document} to be merged
     * @return {@link Document}
     * @author sholzer (09.03.2015)
     * @param conflictHandling
     *            {@link ConflictHandlingType} specifying how conflicts will be handled during the merge
     *            process
     * @throws XMLMergeException
     *             if the elements can't be merged
     */
    public Document merge(Document doc1, Document doc2, ConflictHandlingType conflictHandling)
        throws XMLMergeException {
        if (doc1.getDocType() != null || doc2.getDocType() != null) {
            logger.debug("found doctypes");
            validation = false;
            if (doc1.getRootElement().getNamespace().equals(Namespace.NO_NAMESPACE)
                && !doc2.getRootElement().getNamespace().equals(Namespace.NO_NAMESPACE)) {
                logger.debug("base doc is missing namespace. using {} from patch", doc2.getRootElement()
                    .getNamespaceURI());
                JDom2Util.getInstance().replaceNamespaceDeep(doc1.getRootElement(), Namespace.NO_NAMESPACE,
                    doc2.getRootElement().getNamespace());
            }
            if (doc2.getRootElement().getNamespace().equals(Namespace.NO_NAMESPACE)
                && !doc1.getRootElement().getNamespace().equals(Namespace.NO_NAMESPACE)) {
                logger.debug("patch doc is missing namespace. using {} from base", doc1.getRootElement()
                    .getNamespaceURI());
                JDom2Util.getInstance().replaceNamespaceDeep(doc2.getRootElement(), Namespace.NO_NAMESPACE,
                    doc1.getRootElement().getNamespace());
            }
            if (doc1.getRootElement().getNamespace().equals(Namespace.NO_NAMESPACE)
                && doc2.getRootElement().getNamespace().equals(Namespace.NO_NAMESPACE)) {
                Namespace publicId = JDom2Util.getInstance().getDtdNamespace(doc1.getDocType().getPublicID());
                logger.debug("no namepace found in base and doc. Create artifical namespace: {}", publicId);
                JDom2Util.getInstance().replaceNamespaceDeep(doc1.getRootElement(), Namespace.NO_NAMESPACE,
                    publicId);
                JDom2Util.getInstance().replaceNamespaceDeep(doc2.getRootElement(), Namespace.NO_NAMESPACE,
                    publicId);
            }
        }
        Element mergeResult = merge(doc1.getRootElement(), doc2.getRootElement(), conflictHandling);
        if (mergeResult.getDocument() == null) {
            Document newRoot = new Document();

            newRoot.setRootElement(mergeResult);
            return newRoot;
        }
        return merge(doc1.getRootElement(), doc2.getRootElement(), conflictHandling).getDocument();
    }

    /**
     * Merges a given XML file with a patch. This API is designed for CobiGen
     * @see com.github.maybeec.lexeme.LeXeMerger#merge(Element, Element, ConflictHandlingType)
     * @param base
     *            File
     * @param patch
     *            String
     * @param charSet
     *            target charset of the file to be read and write
     * @param conflictHandling
     *            {@link ConflictHandlingType} specifying how conflicts will be handled during the merge
     *            process. If null the default for this LeXeMerger will be used
     * @return Document the merged result of base and patch
     * @throws XMLMergeException
     *             when the Documents can't be properly merged
     * @author sholzer (23.04.2015)
     */
    public Document merge(File base, String patch, String charSet, ConflictHandlingType conflictHandling)
        throws XMLMergeException {
        if (conflictHandling == null) {
            conflictHandling = conflictHandlingType;
        }
        try {
            SAXBuilder builder = new SAXBuilder(XMLReaders.NONVALIDATING);
            builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            String baseString = JDom2Util.getInstance().readFile(base.getPath(), charSet);
            Document baseDoc =
                builder.build(new InputSource(new BufferedReader(new StringReader(baseString))));
            Document patchDoc = builder.build(new InputSource(new BufferedReader(new StringReader(patch))));
            return merge(baseDoc, patchDoc, conflictHandling);
        } catch (IOException | JDOMException e) {
            logger.error("Caught unexcpected {}:{}", e.getClass().getName(), e.getMessage());
            throw new XMLMergeException(e.getMessage());
        }

    }

    /**
     * Merges a given XML file with a patch. This API is designed for CobiGen
     * @see com.github.maybeec.lexeme.LeXeMerger#merge(Element, Element, ConflictHandlingType)
     * @param base
     *            File
     * @param patch
     *            String
     * @param charSet
     *            target charset of the file to be read and write
     * @param conflictHandling
     *            {@link ConflictHandlingType} specifying how conflicts will be handled during the merge
     *            process. If null the default for this LeXeMerger will be used
     * @return String the merged result of base and patch as String
     * @throws XMLMergeException
     *             when the Documents can't be properly merged
     * @author sholzer (23.04.2015)
     */
    public String mergeInString(File base, String patch, String charSet, ConflictHandlingType conflictHandling)
        throws XMLMergeException {
        Document resultDoc = this.merge(base, patch, charSet, conflictHandling);
        String resultString = JDom2Util.getInstance().parseString(resultDoc);
        return resultString;
    }

}
