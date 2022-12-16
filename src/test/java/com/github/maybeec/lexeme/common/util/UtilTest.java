package com.github.maybeec.lexeme.common.util;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.jdom2.Document;
import org.junit.Before;
import org.junit.Test;

import com.github.maybeec.lexeme.ConflictHandlingType;

public class UtilTest {

  /**
   * instance under test
   */
  JDom2Util util;

  @Before
  public void setUp() {

    this.util = JDom2Util.getInstance();
  }

  /**
   * Tests if the getNamespaceLocation method returns the correct set of uri location pairs
   *
   * @throws Exception test fails
   */
  @Test
  public void getNamespaceLocation() throws Exception {

    String baseFile = "src/test/resources/systemtests/bases/Beans3.xml";
    String patchFile = "src/test/resources/systemtests/patches/Beans3.xml";

    Document base = this.util.getDocument(baseFile);
    Document patch = this.util.getDocument(patchFile);

    Map<String, String> testResult = this.util.getNamespaceLocations(base.getRootElement(), patch.getRootElement(),
        ConflictHandlingType.BASEOVERWRITE);
    assertEquals(2, testResult.keySet().size());
  }
}
