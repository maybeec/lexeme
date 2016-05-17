package com.github.maybeec.lexeme.systemtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Test;

import com.github.maybeec.lexeme.ConflictHandlingType;
import com.github.maybeec.lexeme.LeXeMerger;
import com.github.maybeec.lexeme.common.util.JDom2Util;

/**
 *
 * @author sholzer (01.04.2015)
 */
public class FullFunctionalityTest {

    /**
     * Path to the used resources folder
     */
    String resources = "src/test/resources/systemtests/";

    /**
     * Simple Spring Beans test case.
     * @throws Exception
     *             when something somewhere goes wrong
     * @author sholzer (31.03.2015)
     */
    @Test
    public void ThirdBeanTest() throws Exception {
        String mergeSchemaLocation = resources + "MergeSchemas";

        String bean = "http://www.springframework.org/schema/beans";
        String aop = "http://www.springframework.org/schema/aop";

        Document base = JDom2Util.getInstance().getDocument(resources + "bases/Beans3.xml");
        Document patch = JDom2Util.getInstance().getDocument(resources + "patches/Beans3.xml");
        LeXeMerger testMerger = new LeXeMerger(mergeSchemaLocation);
        Element result =
            testMerger.merge(base.getRootElement(), patch.getRootElement(),
                ConflictHandlingType.PATCHOVERWRITE);
        assertEquals("Not as much bean as expected", result.getChildren("bean", Namespace.getNamespace(bean))
            .size(), 2);
        assertTrue("no aop:config found",
            result.getChildren("config", Namespace.getNamespace(aop)).size() == 1);
        Element config = result.getChild("config", Namespace.getNamespace(aop));
        assertEquals("Not as much pointcut as expected", 2,
            config.getChildren("pointcut", Namespace.getNamespace(aop)).size());
        assertEquals("Not as much advisor as expected", 2,
            config.getChildren("advisor", Namespace.getNamespace(aop)).size());

    }

    /**
     * Tests the behavior with NonUniqueRoot documents. Only the processing will be tested
     * @throws Exception
     *             when something goes wrong
     * @author sholzer (Jul 9, 2015)
     */
    @Test
    public void ThirdSystemTest() throws Exception {

        String mergeSchemaLocation = resources + "MergeSchemas";

        Document base = JDom2Util.getInstance().getDocument(resources + "bases/ThirdBase.xml");
        Document patch = JDom2Util.getInstance().getDocument(resources + "bases/ThirdBase.xml");
        LeXeMerger testMerger = new LeXeMerger(mergeSchemaLocation);
        Element result =
            testMerger.merge(base.getRootElement(), patch.getRootElement(),
                ConflictHandlingType.PATCHOVERWRITE);
    }

}
