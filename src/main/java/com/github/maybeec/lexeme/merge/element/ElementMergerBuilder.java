package com.github.maybeec.lexeme.merge.element;

import java.util.List;

import com.github.maybeec.lexeme.mergeschema.Handling;
import com.github.maybeec.lexeme.schemaprovider.MergeSchemaProvider;

/**
 *
 * @author sholzer (Jul 3, 2015)
 */
public interface ElementMergerBuilder {

    /**
     * Builds and returns an ElementMerger instance
     * @param handling
     *            the Handling to be used
     * @param provider
     *            the MergeSchemaprovider that provides other MergeSchemas
     * @return ElementMerger
     * @author sholzer (Jul 3, 2015)
     */
    ElementMerger build(Handling handling, MergeSchemaProvider provider);

    /**
     * Builds and returns a nested ElementMerger instance
     * @param scope
     *            list of Handling object to be considered during the merge
     * @param handling
     *            the Handling object to instanciate this merger
     * @param provider
     *            the schema provider
     * @return ElementMerger
     * @author sholzer (Jan 20, 2016)
     */
    ElementMerger build(List<Handling> scope, Handling handling, MergeSchemaProvider provider);

}
