package com.github.maybeec.lexeme;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.Element;
import org.junit.After;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.github.maybeec.lexeme.common.exception.UnmatchingNamespacesException;
import com.github.maybeec.lexeme.common.exception.XMLMergeException;
import com.github.maybeec.lexeme.common.util.JDom2Util;
import com.github.maybeec.lexeme.merge.element.ElementMerger;
import com.github.maybeec.lexeme.merge.element.ElementMergerBuilder;
import com.github.maybeec.lexeme.merge.element.ElementMergerFactory;
import com.github.maybeec.lexeme.mergeschema.Criterion;
import com.github.maybeec.lexeme.mergeschema.Handling;
import com.github.maybeec.lexeme.mergeschema.MergeSchema;
import com.github.maybeec.lexeme.schemaprovider.MergeSchemaProvider;

import jakarta.xml.bind.JAXBException;

/**
 *
 * @author sholzer (10.03.2015)
 */
public class XMLMergerTest {

    /**
     * Test method for {@link LeXeMerger#merge(Element, Element, ConflictHandlingType)} Tests if an Exception
     * is thrown by different namespaces
     * @author sholzer (10.03.2015)
     * @throws Exception
     *             when something happens
     */
    @Test(expected = UnmatchingNamespacesException.class)
    public void testMergeUnmatchingNamespaces() throws Exception {
        Element element1 = new Element("root", "com.github.maybeec.lexeme");
        Element element2 = new Element("root", "com.github.maybeec.lexeme.mergeschema");

        new LeXeMerger(new MergeSchemaProvider() {

            @Override
            public MergeSchema getMergeSchemaForNamespaceURI(String namespaceURI) {
                return new MergeSchema();
            }

            @Override
            public String getPath() {
                return "./";
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

        }).merge(element1, element2, ConflictHandlingType.PATCHOVERWRITE);
        fail("Expected UnmatchingNamespacesException");
    }

    /**
     * Test method for {@link LeXeMerger#XMLMerger(String)}
     * <p>
     * Tests behaviour with a valid MergeSchema document and the correct path to it
     * @throws FileNotFoundException
     *             when the file is not present at the specified location
     * @throws ParserConfigurationException
     *             thrown by DOMHelper
     * @throws JAXBException
     *             when the file can't be parsed into a MergeSchema
     * @author sholzer (14.03.2015)
     */
    @SuppressWarnings("javadoc")
    @Test
    public void testPublicConstructor() throws FileNotFoundException, ParserConfigurationException, JAXBException {

        String path = "src/test/resources/xmlmerger/validMergeSchema.xml";
        new LeXeMerger(path);
    }

    /**
     * Tests {@link LeXeMerger#merge(Element, Element, ConflictHandlingType)} when the retrieved MergeSchema
     * is non root
     * @throws Exception
     *             shouldn't happen
     * @author sholzer (Jul 7, 2015)
     */
    @Test
    public void testNonRootMerge() throws Exception {
        MergeSchemaProvider provider = Mockito.mock(MergeSchemaProvider.class);
        final ElementMerger elementMerger = Mockito.mock(ElementMerger.class);

        ElementMergerFactory.setBuilder(new ElementMergerBuilder() {

            @Override
            public ElementMerger build(Handling handling, MergeSchemaProvider provider) {
                return elementMerger;
            }

            @Override
            public ElementMerger build(List<Handling> scope, Handling handling, MergeSchemaProvider provider) {
                return elementMerger;
            }

        });

        LeXeMerger test = LeXeMeFactory.build(provider);

        MergeSchema schema = new MergeSchema();
        schema.setRoot(false);
        Handling h1 = new Handling();
        h1.setFor("foo");
        Handling h2 = new Handling();
        h2.setFor("bar");

        schema.getHandling().add(h1);
        schema.getHandling().add(h2);

        Element e = new Element("foo");

        Mockito.when(provider.getMergeSchemaForNamespaceURI(Matchers.anyString())).thenReturn(schema);
        Mockito.when(elementMerger.merge(e, e, ConflictHandlingType.BASEOVERWRITE)).thenReturn(new Element("foo"));

        test.merge(e, e, ConflictHandlingType.BASEOVERWRITE);

    }

    /**
     * tests the cobigen api
     * @throws Exception
     *             during test
     * @author sholzer (Aug 27, 2015)
     */
    @Test(expected = EarlyExitRuntimeException.class)
    public void apiTest() throws Exception {
        Charset charSet = StandardCharsets.UTF_8;
        File baseFile = new File("src/test/resources/systemtests/bases/FirstBase.xml");
        String patchString =
            JDom2Util.getInstance().readFile("src/test/resources/systemtests/patches/FirstPatch.xml", charSet.name());

        LeXeMerger test = new LeXeMerger(new MergeSchemaProvider() {

            @Override
            public MergeSchema getMergeSchemaForNamespaceURI(String namespaceURI) {
                return new MergeSchema();
            }

            @Override
            public String getPath() {
                return "./";
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

        ElementMergerFactory.setBuilder(new ElementMergerBuilder() {

            @Override
            public ElementMerger build(Handling handling, MergeSchemaProvider provider) {
                ElementMergerStub stub = new ElementMergerStub();
                stub.earlyExit = true;
                return stub;
            }

            @Override
            public ElementMerger build(List<Handling> scope, Handling handling, MergeSchemaProvider provider) {
                //
                return null;
            }

        });

        test.mergeInString(baseFile, patchString, charSet.name(), ConflictHandlingType.PATCHOVERWRITE);

    }

    /**
     * Resets the used factories
     *
     * @author sholzer (Jul 9, 2015)
     */
    @After
    public void after() {
        ElementMergerFactory.setBuilder(null);
    }
}

/**
 * simple ElementMerger implementation without any real function
 * @author sholzer (14.03.2015)
 */
class ElementMergerStub implements ElementMerger {

    /**
     * {@inheritDoc}
     * @author sholzer (May 31, 2016)
     */
    @Override
    public void setRoot(boolean b) {

    }

    /**
     *
     */
    boolean earlyExit = false;

    /**
     * {@inheritDoc}
     * @author sholzer (May 31, 2016)
     */
    @Override
    public org.jdom2.Element merge(org.jdom2.Element element1, org.jdom2.Element element2, ConflictHandlingType type)
        throws XMLMergeException {
        if (earlyExit) {
            throw new EarlyExitRuntimeException();
        }
        return null;
    }

}

/**
 * Allows an early exit
 *
 * @author sholzer (Aug 27, 2015)
 */
class EarlyExitRuntimeException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -4941896411485879326L;

}
