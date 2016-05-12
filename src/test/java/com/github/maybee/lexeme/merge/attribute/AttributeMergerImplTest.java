package com.github.maybee.lexeme.merge.attribute;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.maybee.lexeme.ConflictHandlingType;
import com.github.maybee.lexeme.mergeschema.Attribute;

/**
 *
 * @author sholzer (02.03.2015)
 */
public class AttributeMergerImplTest {

    /**
     * Test method for
     * {@link com.github.maybee.lexeme.merge.attribute.AttributeMergerImpl#merge(java.lang.String, java.lang.String, ConflictHandlingType)}
     * <p>
     * Tests the Attach-Last configuration without separation String
     */
    @Test
    public void testMergeAttachLastWithoutString() {
        Attribute testConfig = new Attribute();
        testConfig.setAttachable(true);
        String value1 = "abc";
        String value2 = "def";

        AttributeMergerImpl test = new AttributeMergerImpl(testConfig);

        String mergedValue = test.merge(value1, value2, ConflictHandlingType.PATCHATTACHOROVERWRITE);
        assertTrue("Expected " + value1 + value2 + " but returned " + mergedValue,
            mergedValue.equals(value1 + value2));
    }

    /**
     * Test method for
     * {@link com.github.maybee.lexeme.merge.attribute.AttributeMergerImpl#merge(java.lang.String, java.lang.String, ConflictHandlingType)}
     * <p>
     * Tests the Attach-Last configuration with a separation String
     */
    @Test
    public void testMergeAttachLastWithString() {
        Attribute testConfig = new Attribute();
        testConfig.setAttachable(true);
        String value1 = "abc";
        String value2 = "def";
        String separation = ";";
        testConfig.setSeparationString(separation);

        AttributeMergerImpl test = new AttributeMergerImpl(testConfig);

        String mergedValue = test.merge(value1, value2, ConflictHandlingType.PATCHATTACHOROVERWRITE);
        assertTrue("Expected " + value1 + separation + value2 + " but returned " + mergedValue,
            mergedValue.equals(value1 + separation + value2));
    }

    /**
     * Test method for
     * {@link com.github.maybee.lexeme.merge.attribute.AttributeMergerImpl#merge(java.lang.String, java.lang.String, ConflictHandlingType)}
     * <p>
     * Tests the Attach-First configuration without separation String
     */
    // Attaching element BEVOR the base elements is deprecated
    public void testMergeAttachFirstWithoutString() {
        Attribute testConfig = new Attribute();
        testConfig.setAttachable(true);
        String value1 = "abc";
        String value2 = "def";

        AttributeMergerImpl test = new AttributeMergerImpl(testConfig);

        String mergedValue = test.merge(value1, value2, ConflictHandlingType.PATCHATTACHOROVERWRITE);
        assertTrue("Expected " + value2 + value1 + " but returned " + mergedValue,
            mergedValue.equals(value2 + value1));
    }

    /**
     * Test method for
     * {@link com.github.maybee.lexeme.merge.attribute.AttributeMergerImpl#merge(java.lang.String, java.lang.String, ConflictHandlingType)}
     * <p>
     * Tests the Attach-First configuration with a separation String
     */
    // Attaching element BEVOR the base elements is deprecated
    public void testMergeAttachFirstWithString() {
        Attribute testConfig = new Attribute();
        testConfig.setAttachable(true);
        String value1 = "abc";
        String value2 = "def";
        String separation = ";";
        testConfig.setSeparationString(separation);

        AttributeMergerImpl test = new AttributeMergerImpl(testConfig);

        String mergedValue = test.merge(value1, value2, ConflictHandlingType.PATCHATTACHOROVERWRITE);
        assertTrue("Expected " + value2 + separation + value1 + " but returned " + mergedValue,
            mergedValue.equals(value2 + separation + value1));
    }

    /**
     * Test method for
     * {@link com.github.maybee.lexeme.merge.attribute.AttributeMergerImpl#merge(java.lang.String, java.lang.String, ConflictHandlingType)}
     * <p>
     * Tests the Use-First configuration
     */
    @Test
    public void testMergeUseFirst() {
        Attribute testConfig = new Attribute();
        testConfig.setAttachable(false);
        String value1 = "abc";
        String value2 = "def";

        AttributeMergerImpl test = new AttributeMergerImpl(testConfig);

        String mergedValue = test.merge(value1, value2, ConflictHandlingType.BASEOVERWRITE);
        assertTrue("Expected " + value1 + " but returned " + mergedValue, mergedValue.equals(value1));
    }

    /**
     * Test method for
     * {@link com.github.maybee.lexeme.merge.attribute.AttributeMergerImpl#merge(java.lang.String, java.lang.String, ConflictHandlingType)}
     * <p>
     * Tests the Use-Last configuration with a separation String
     */
    @Test
    public void testMergeUseLast() {
        Attribute testConfig = new Attribute();
        testConfig.setAttachable(false);
        String value1 = "abc";
        String value2 = "def";

        AttributeMergerImpl test = new AttributeMergerImpl(testConfig);

        String mergedValue = test.merge(value1, value2, ConflictHandlingType.PATCHOVERWRITE);
        assertTrue("Expected " + value2 + " but returned " + mergedValue, mergedValue.equals(value2));
    }

    /**
     * Test method for
     * {@link com.github.maybee.lexeme.merge.attribute.AttributeMergerImpl#merge(java.lang.String, java.lang.String, ConflictHandlingType)}
     * <p>
     * Tests the Ranked configuration with both parameters contained in the ranked list
     */
    // functionality not used anymore
    public void testMergeRankedBothInList() {/*
                                              * Attribute testConfiguration = new Attribute();
                                              * testConfiguration.setType(AttributeType.RANKED); String value1
                                              * = "abc"; String value2 = "def"; String value3 = "ghi";
                                              *
                                              * String ranked = value1 + ";" + value2 + ";" + value3;
                                              * testConfiguration.setContent(ranked);
                                              *
                                              * AttributeMergerImpl test = new
                                              * AttributeMergerImpl(testConfiguration); String result =
                                              * test.merge(value2, value3);
                                              *
                                              * assertTrue("Expected " + value2 + " but returned " + result,
                                              * result.equals(value2));
                                              */

    }

    /**
     * Test method for
     * {@link com.github.maybee.lexeme.merge.attribute.AttributeMergerImpl#merge(java.lang.String, java.lang.String, ConflictHandlingType)}
     * <p>
     * Tests the Ranked configuration with one parameter contained in the ranked list
     */
    public void testMergeRankedOneInList() {/*
                                             * Attribute testConfiguration = new Attribute();
                                             * testConfiguration.setType(AttributeType.RANKED); String value1
                                             * = "abc"; String value2 = "def"; String value3 = "ghi";
                                             *
                                             * String ranked = value1 + ";" + value2 + ";" + value3;
                                             * testConfiguration.setContent(ranked);
                                             *
                                             * AttributeMergerImpl test = new
                                             * AttributeMergerImpl(testConfiguration); String result =
                                             * test.merge("jkl", value3);
                                             *
                                             * assertTrue("Expected " + value3 + " but returned " + result,
                                             * result.equals(value3));
                                             */
    }

    /**
     * Test method for
     * {@link com.github.maybee.lexeme.merge.attribute.AttributeMergerImpl#merge(java.lang.String, java.lang.String, ConflictHandlingType)}
     * <p>
     * Tests the Ranked configuration with no parameters contained in the ranked list. Expect fall back to
     * default use-first
     */
    public void testMergeRankedNotInList() {/*
                                             * Attribute testConfiguration = new Attribute();
                                             * testConfiguration.setType(AttributeType.RANKED); String value1
                                             * = "abc"; String value2 = "def"; String value3 = "ghi";
                                             *
                                             * String value4 = "jkl"; String value5 = "mno";
                                             *
                                             * String ranked = value1 + ";" + value2 + ";" + value3;
                                             * testConfiguration.setContent(ranked);
                                             *
                                             * AttributeMergerImpl test = new
                                             * AttributeMergerImpl(testConfiguration); String result =
                                             * test.merge(value4, value5);
                                             *
                                             * assertTrue("Expected " + value4 + " but returned " + result,
                                             * result.equals(value4));
                                             */
    }

}
