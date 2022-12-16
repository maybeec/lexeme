package com.github.maybeec.lexeme.validator;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.github.maybeec.lexeme.common.exception.ValidationException;
import com.github.maybeec.lexeme.common.util.JDom2Util;
import com.github.maybeec.lexeme.mergeschema.AdditionalNamespace;
import com.github.maybeec.lexeme.mergeschema.MergeSchema;
import com.github.maybeec.lexeme.schemaprovider.MergeSchemaProvider;

public class DocumentValidatorImpl implements DocumentValidator {

  /**
   * Flag if validation problems should throw exceptions or not
   */
  private boolean strict = false;

  /**
   * Sets a flag for the validation
   *
   * @param strict true if {@link ValidationException} should be thrown when the validation fails. False if an logger
   *        warning is sufficient.
   */
  @Override
  public void setStrict(boolean strict) {

    this.strict = strict;
  }

  /**
   * Used for logging events of this object
   */
  private final Logger logger = LoggerFactory.getLogger(DocumentValidatorImpl.class);

  /**
   * The used MergeSchemaProvider
   */
  private MergeSchemaProvider provider;

  /**
   * Creates a instance of a DocumentValidatorImplementation
   *
   * @param provider {@link MergeSchemaProvider}
   */
  public DocumentValidatorImpl(MergeSchemaProvider provider) {

    this.provider = provider;
  }

  @Override
  public void validate(Element node) throws ValidationException {

    final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    dbf.setNamespaceAware(true);
    dbf.setValidating(true);

    try {
      dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
    } catch (IllegalArgumentException x) {
      throw new ValidationException(String.format("%s: JAXP DocumentBuilderFactory attribute not recognized: %s",
          x.getClass().getName(), JAXP_SCHEMA_LANGUAGE), x);
    }

    final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    List<String> schemas = new LinkedList<>();
    for (String namespace : getNameSpaces(node)) {
      MergeSchema ms = this.provider.getMergeSchemaForNamespaceURI(namespace);
      if (ms.getDefinition() == null) {
        continue;
      }
      String schemaLocation = "";
      if (node.getNamespace().getURI().equals(ms.getDefinition().getNamespace())) {
        schemaLocation = ms.getDefinition().getLocation();
      } else {
        for (AdditionalNamespace ans : ms.getDefinition().getAdditionalNamespace()) {
          if (node.getNamespace().getURI().equals(ans.getNamespace())) {
            schemaLocation = ans.getLocation();
            break;
          }
        }
      }
      if (!schemaLocation.equals("")) {
        schemas.add(ms.getDefinition().getLocation());
      }

    }
    if (!schemas.isEmpty()) {
      dbf.setAttribute(JAXP_SCHEMA_SOURCE, schemas.toArray());
    }
    try (StringReader stringReader = new StringReader(JDom2Util.getInstance().parseString(node))) {
      InputSource inputSource = new InputSource(stringReader);
      DocumentBuilder db = dbf.newDocumentBuilder();
      db.setErrorHandler(new ValidationErrorHandler());
      db.parse(inputSource);
    } catch (SAXException e) {
      this.logger.warn("Validation failed due to: {}\n{}", e.getMessage(), node.toString());
      if (this.strict) {
        throw new ValidationException("Validation failed due to: " + e.getMessage(), e);
      }
    } catch (ParserConfigurationException e) {
      throw new ValidationException("Unexpected ParserConfigurationException caught while creating a DocumentBuilder."
          + " This should not have happened: %s" + e.getMessage(), e);
    } catch (IOException e) {
      throw new ValidationException(
          "Caught IOException while parsing the to a String transformed Node. This should not have happened: %s"
              + e.getMessage(),
          e);
    }

  }

  /**
   * Returns a List of NamespaceURIs used in the subtree of node
   *
   * @param element {@link Element}
   * @return {@link java.util.List}&lt;{@link String}>
   */
  private List<String> getNameSpaces(Element element) {

    List<String> namespaces = new LinkedList<>();
    for (Namespace ns : element.getNamespacesInScope()) {
      boolean flag = true;
      for (String s : namespaces) {
        if (ns.getURI().equals(s)) {
          flag = false;
          break;
        }
      }
      if (flag) {
        namespaces.add(ns.getURI());
      }
    }

    return namespaces;
  }

}
