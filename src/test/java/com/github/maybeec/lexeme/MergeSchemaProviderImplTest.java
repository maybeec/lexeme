package com.github.maybeec.lexeme;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.maybeec.lexeme.mergeschema.Criterion;
import com.github.maybeec.lexeme.mergeschema.MergeSchema;
import com.github.maybeec.lexeme.schemaprovider.MergeSchemaProvider;
import com.github.maybeec.lexeme.schemaprovider.MergeSchemaProviderImpl;

/**
 *
 * @author sholzer (07.04.2015)
 */
public class MergeSchemaProviderImplTest {

    /**
     * Path the provider should look at
     */
    private String path = "src/test/resources/provider";

    /**
     * namespace
     */
    private String namespaceA = "a";

    /**
     * default namespace
     */
    private String namespaceDefault = "$DEFAULT$";

    /**
     * Tests the retrieval of an existing merge schema. <br/>
     * Folder: src/test/resources/provider<br/>
     * Retrieve: ./namespace_a.xml
     * @author sholzer (May 13, 2016)
     */
    @Test
    public void testRetrieveA() {
        MergeSchemaProvider provider = MergeSchemaProviderImpl.getProviderForPath(path);
        MergeSchema schema = provider.getMergeSchemaForNamespaceURI(namespaceA);
        assertEquals("Wrong merge schema returned", namespaceA, schema.getDefinition().getNamespace());
    }

    /**
     * Tests if an empty MergeSchema is returned when no default is given<br/>
     * Folder: src/test/resources/provider/testEmptyMergeSchema
     * @author sholzer (May 13, 2016)
     */
    @Test
    public void testRetrieveEmpty() {
        MergeSchemaProvider provider =
            MergeSchemaProviderImpl.getProviderForPath(path + "/testEmptyMergeSchema");
        MergeSchema schema = provider.getMergeSchemaForNamespaceURI(namespaceA);
        assertEquals("Didn't return empty merge schema", null, schema.getDefinition().getNamespace());
    }

    /**
     * Tests if the provider returns the default merge schema if one is present<br/>
     * Folder: src/test/resources/provider<br/>
     * Retrieve ./default.xml
     * @author sholzer (May 13, 2016)
     */
    @Test
    public void testDefault() {
        MergeSchemaProvider provider = MergeSchemaProviderImpl.getProviderForPath(path);
        MergeSchema retrievedDefault = provider.getMergeSchemaForNamespaceURI("nonExisting");
        assertEquals("Didn't return default merge schema", namespaceDefault, retrievedDefault.getDefinition()
            .getNamespace());
    }

    /**
     * Tests if the provider accesses the 'additional-namespace' field during it's search for namespaces.<br/>
     * Folder: src/test/resources/provider/testAdditionalNamespace Retrieve: namespace_a.xml
     * @author sholzer (May 13, 2016)
     */
    @Test
    public void testAdditionalNamespace() {
        MergeSchemaProvider provider =
            MergeSchemaProviderImpl.getProviderForPath(path + "/testAdditionalNamespace");
        MergeSchema schema = provider.getMergeSchemaForNamespaceURI("b");
        assertEquals("Wrong merge schema returned", namespaceA, schema.getDefinition().getNamespace());
    }

    /**
     * Tests the retrieval of an existing default criterion in an existing merge schema<br/>
     * Folder: src/test/resources/provider<br/>
     * Retrieve: namespace_a.xml
     * @author sholzer (May 13, 2016)
     */
    @Test
    public void testGetDefaultCrit() {
        MergeSchemaProvider provider = MergeSchemaProviderImpl.getProviderForPath(path);
        Criterion crit = provider.getDefaultCriterion(namespaceA);
        assertEquals("Wrong default criterion returned", "abc", crit.getXpath());
    }

    /**
     * Tests the retrieval of an existing default criterion from the default schema<br/>
     * Folder: src/test/resources/provider<br/>
     * Retrieve: default.xml
     * @author sholzer (May 13, 2016)
     */
    @Test
    public void testGetDefaultCritFromDefault() {
        MergeSchemaProvider provider = MergeSchemaProviderImpl.getProviderForPath(path);
        Criterion crit = provider.getDefaultCriterion("nonExisting");
        assertEquals("Wrong default criterion returned", "def", crit.getXpath());
    }

    /**
     * Tests the retrieval of an existing default criterion from the default schema<br/>
     * Folder: src/test/resources/provider<br/>
     * Retrieve: default.xml
     * @author sholzer (May 13, 2016)
     */
    @Test
    public void testGetDefaultCritFromEmpty() {
        MergeSchemaProvider provider =
            MergeSchemaProviderImpl.getProviderForPath(path + "/testEmptyMergeSchema");
        Criterion crit = provider.getDefaultCriterion("nonExisting");
        assertEquals("Wrong default criterion returned", "./*", crit.getXpath());
    }

}
