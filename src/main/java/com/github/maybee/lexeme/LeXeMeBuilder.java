package com.github.maybee.lexeme;

import com.github.maybee.lexeme.schemaprovider.MergeSchemaProvider;

/**
 *
 * @author sholzer (Jul 7, 2015)
 */
public interface LeXeMeBuilder {

    /**
     * Special constructor to be used in nested merge processes. Won't perform a renaming of the nodes
     * prefixes
     * @param provider
     *            {@link MergeSchemaProvider} providing MergeSchemas for namespaces
     * @return a LeXeMerger
     * @author sholzer (12.03.2015)
     */
    public LeXeMerger build(MergeSchemaProvider provider);

    /**
     * @param pathToMergeSchema
     *            String path to the MergeSchema to be used on this document. Note: Will in future builds
     *            replaced by pathToMergeSchemas which leads to a directory containing MergeSchemas for every
     *            used namespace.
     *            <p/>
     *            If <b>null</b> the default MergeSchemas will be used
     * @author sholzer (12.03.2015)
     * @return a LeXeMerger
     */
    public LeXeMerger build(String pathToMergeSchema);

}
