package com.github.maybeec.lexeme.validator;

import java.nio.file.Path;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Test;

import com.github.maybeec.lexeme.common.exception.ValidationException;
import com.github.maybeec.lexeme.common.util.JDom2Util;
import com.github.maybeec.lexeme.mergeschema.Criterion;
import com.github.maybeec.lexeme.mergeschema.Definition;
import com.github.maybeec.lexeme.mergeschema.MergeSchema;
import com.github.maybeec.lexeme.schemaprovider.MergeSchemaProvider;

/**
 *
 * @author sholzer (15.04.2015)
 */
public class DocValImplTest {

    /**
     * path to files
     */
    String pathRoot = "src/test/resources/validator/";

    /**
     * Test method for
     * {@link com.github.maybeec.lexeme.validator.DocumentValidatorImpl#validate(org.jdom2.Element)}.
     * @throws Exception
     *             when something somewhere goes wrong
     */
    @Test
    public void testValidateNodeAgainstSingleXSDFirstTest() throws Exception {
        // Get Node from File
        String pathToDoc = pathRoot + "FirstTest.xml";
        Document doc = JDom2Util.getInstance().getDocument(pathToDoc, false);
        Element validNode = doc.getRootElement();
        assert validNode != null;
        DocumentValidator test = new DocumentValidatorImpl(new MergeSchemaProvider() {

            @Override
            public MergeSchema getMergeSchemaForNamespaceURI(String namespaceURI) {
                return new MergeSchema();
            }

            @Override
            public String getPath() {
                return null;
            }

            @Override
            public Criterion getDefaultCriterion(String namespaceUri) {
                return new Criterion();
            }

            @Override
            public List<Criterion> getCriterionFor(String elementName, String namespaceUri) {
                //
                return null;
            }

            @Override
            public void setPath(Path path) {
                //

            }

            @Override
            public List<Criterion> getDeepCriterion(String name, String namespace) {
                //
                return null;
            }

        });

        test.validate(validNode);

    }

    /**
     * Test method for {@link com.github.maybeec.lexeme.validator.DocumentValidatorImpl#validate(Element)}.
     * @throws Exception
     *             when something somewhere goes wrong
     */
    @Test(expected = ValidationException.class)
    public void testValidateNodeAgainstSingleXSDSecondTest() throws Exception {
        // Get Node from File
        String pathToDoc = pathRoot + "SecondTest.xml";
        Document doc = JDom2Util.getInstance().getDocument(pathToDoc, false);
        Element validNode = doc.getRootElement();
        assert validNode != null;
        DocumentValidator test = new DocumentValidatorImpl(new MergeSchemaProvider() {

            @Override
            public MergeSchema getMergeSchemaForNamespaceURI(String namespaceURI) {
                return new MergeSchema();
            }

            @Override
            public String getPath() {
                return null;
            }

            @Override
            public Criterion getDefaultCriterion(String namespaceUri) {
                return new Criterion();
            }

            @Override
            public List<Criterion> getCriterionFor(String elementName, String namespaceUri) {
                return null;
            }

            @Override
            public void setPath(Path path) {
                //

            }

            @Override
            public List<Criterion> getDeepCriterion(String name, String namespace) {
                //
                return null;
            }

        });
        test.setStrict(true);
        test.validate(validNode);

    }

    /**
     * Test method for {@link com.github.maybeec.lexeme.validator.DocumentValidatorImpl#validate(Element)}.
     * @throws Exception
     *             when something somewhere goes wrong
     */
    @Test
    public void testValidateNodeAgainstMultipleXSDThirdTest() throws Exception {
        // Get Node from File
        String pathToDoc = pathRoot + "ThirdTest.xml";
        Document doc = JDom2Util.getInstance().getDocument(pathToDoc, false);
        Element validNode = doc.getRootElement();
        assert validNode != null;
        DocumentValidator test = new DocumentValidatorImpl(new MergeSchemaProvider() {

            @Override
            public MergeSchema getMergeSchemaForNamespaceURI(String namespaceURI) {
                return new MergeSchema();
            }

            @Override
            public String getPath() {
                return null;
            }

            @Override
            public Criterion getDefaultCriterion(String namespaceUri) {
                return new Criterion();
            }

            @Override
            public List<Criterion> getCriterionFor(String elementName, String namespaceUri) {
                //
                return null;
            }

            @Override
            public void setPath(Path path) {
                //

            }

            @Override
            public List<Criterion> getDeepCriterion(String name, String namespace) {
                //
                return null;
            }

        });
        test.setStrict(true);
        test.validate(validNode);

    }

    /**
     * Test method for {@link com.github.maybeec.lexeme.validator.DocumentValidatorImpl#validate(Element)}.
     * @throws Exception
     *             when something somewhere goes wrong
     */
    @Test(expected = ValidationException.class)
    public void testValidateNodeAgainstMultipleXSDFourthTest() throws Exception {
        // Get Node from File
        String pathToDoc = pathRoot + "FourthTest.xml";
        Document doc = JDom2Util.getInstance().getDocument(pathToDoc, false);
        Element validNode = doc.getRootElement();
        assert validNode != null;
        DocumentValidator test = new DocumentValidatorImpl(new MergeSchemaProvider() {

            @Override
            public MergeSchema getMergeSchemaForNamespaceURI(String namespaceURI) {
                return new MergeSchema();
            }

            @Override
            public String getPath() {
                return null;
            }

            @Override
            public Criterion getDefaultCriterion(String namespaceUri) {
                return new Criterion();
            }

            @Override
            public List<Criterion> getCriterionFor(String elementName, String namespaceUri) {
                //
                return null;
            }

            @Override
            public void setPath(Path path) {

            }

            @Override
            public List<Criterion> getDeepCriterion(String name, String namespace) {
                //
                return null;
            }

        });
        test.setStrict(true);
        test.validate(validNode);

    }

    /**
     * Test method for {@link com.github.maybeec.lexeme.validator.DocumentValidatorImpl#validate(Element)}.
     * @throws Exception
     *             when something somewhere goes wrong
     */
    @Test
    public void testValidateAgainstAlienXSD() throws Exception {
        String pathToDoc = pathRoot + "SecondTest.xml";
        Document doc = JDom2Util.getInstance().getDocument(pathToDoc, false);
        Element validNode = doc.getRootElement();
        assert validNode != null;
        DocumentValidator test = new DocumentValidatorImpl(new MergeSchemaProvider() {

            @Override
            public MergeSchema getMergeSchemaForNamespaceURI(String namespaceURI) {

                MergeSchema ms = new MergeSchema();
                if (namespaceURI.equals("http://www.example.org/FirstSchema")) {
                    Definition d = new Definition();
                    d.setLocation("src/test/resources/validator/AlienSchema.xsd");
                    d.setNamespace("http://www.example.org/FirstSchema");
                    ms.setDefinition(d);
                }
                return ms;
            }

            @Override
            public String getPath() {
                return null;
            }

            @Override
            public Criterion getDefaultCriterion(String namespaceUri) {
                return new Criterion();
            }

            @Override
            public List<Criterion> getCriterionFor(String elementName, String namespaceUri) {
                //
                return null;
            }

            @Override
            public void setPath(Path path) {
                //

            }

            @Override
            public List<Criterion> getDeepCriterion(String name, String namespace) {
                //
                return null;
            }

        });
        test.validate(validNode);

    }

}
