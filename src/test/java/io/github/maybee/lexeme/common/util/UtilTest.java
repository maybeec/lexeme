package io.github.maybee.lexeme.common.util;

import static org.junit.Assert.assertEquals;
import io.github.maybee.lexeme.ConflictHandlingType;
import io.github.maybee.lexeme.common.util.JDom2Util;

import java.util.Map;

import org.jdom2.Document;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author sholzer (Sep 2, 2015)
 */
public class UtilTest {

    /**
     * instance under test
     */
    JDom2Util util;

    /**
     * @author sholzer (Sep 2, 2015)
     */
    @Before
    public void setUp() {
        util = JDom2Util.getInstance();
    }

    /**
     * Tests if the getNamespaceLocation method returns the correct set of uri location pairs
     * @throws Exception
     *             test fails
     * @author sholzer (Sep 2, 2015)
     */
    @Test
    public void getNamespaceLocation() throws Exception {
        String baseFile = "src/test/resources/systemtests/bases/Beans3.xml";
        String patchFile = "src/test/resources/systemtests/patches/Beans3.xml";

        Document base = util.getDocument(baseFile);
        Document patch = util.getDocument(patchFile);

        Map<String, String> testResult =
            util.getNamespaceLocations(base.getRootElement(), patch.getRootElement(),
                ConflictHandlingType.BASEOVERWRITE);
        assertEquals(2, testResult.keySet().size());
    }
}
