package io.github.maybee.lexeme;

import io.github.maybee.lexeme.schemaprovider.MergeSchemaProvider;
import io.github.maybee.lexeme.schemaprovider.MergeSchemaProviderImpl;

import org.junit.Test;

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
