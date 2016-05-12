package com.github.maybee.lexeme;

import org.junit.Test;

import com.github.maybee.lexeme.schemaprovider.MergeSchemaProvider;
import com.github.maybee.lexeme.schemaprovider.MergeSchemaProviderImpl;

/**
 *
 * @author sholzer (07.04.2015)
 */
public class MergeSchemaProviderImplTest {

    @Test
    public void testDefault() {
        MergeSchemaProvider provider =
            MergeSchemaProviderImpl.getProviderForPath("src/test/resources/provider");

    }
}
