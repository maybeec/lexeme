package com.github.maybeec.lexeme.common.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.maybeec.lexeme.ConflictHandlingType;
import com.github.maybeec.lexeme.mergeschema.DefaultCriterionType;
import com.github.maybeec.lexeme.mergeschema.Definition;
import com.github.maybeec.lexeme.mergeschema.MergeSchema;

/**
 * Singleton class
 */
public class JDom2Util {

  /**
   * Convenient constant
   */
  public static final Namespace XSI = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

  /**
   * Used for logging events of this object
   */
  private final static Logger logger = LoggerFactory.getLogger(JDom2Util.class);

  /**
   * Singleton instance
   */
  private static JDom2Util instance = null;

  /**
   * Singleton constructor
   */
  private JDom2Util() {

  }

  /**
   * Getter to the used JDom2Helper instance
   *
   * @return {@link JDom2Util}
   */
  public static JDom2Util getInstance() {

    if (instance == null) {
      instance = new JDom2Util();
    }
    return instance;
  }

  /**
   * @param element {@link org.jdom2.Element}
   * @return {@link List}&lt;{@link String}&gt;
   */
  public List<Text> getTextNodes(Element element) {

    List<Text> result = new LinkedList<>();

    for (Content content : element.getContent()) {
      if (content instanceof Text) {
        result.add((Text) content);
      }
    }

    return result;
  }

  /**
   * Parses a given {@link org.jdom2.Document} from the xml at the given path
   *
   * @param path {@link String} path to the XML
   * @param validation boolean
   * @return {@link Document}
   * @throws JDOMException when validation fails
   * @throws IOException when the file is not found
   *
   */
  public Document getDocument(String path, boolean validation) throws JDOMException, IOException {

    SAXBuilder builder;
    if (validation) {
      builder = new SAXBuilder(XMLReaders.XSDVALIDATING);
    } else {
      builder = new SAXBuilder();
    }

    return builder.build(path);
  }

  /**
   * Parses a JDom2 Object into a string
   *
   * @param e Object (Element, Text, Document or Attribute)
   * @return String
   */
  public String parseString(Object e) {

    XMLOutputter element2String = new XMLOutputter();
    if (e instanceof Element) {
      return element2String.outputString((Element) e);
    }
    if (e instanceof Text) {
      return element2String.outputString((Text) e);
    }
    if (e instanceof Document) {
      return element2String.outputString((Document) e);
    }
    if (e instanceof org.jdom2.Attribute) {
      Attribute a = (org.jdom2.Attribute) e;
      return a.getName() + "=\"" + a.getValue() + "\"";
    }
    return e.toString();
  }

  /**
   * Gets a Document from XML-file without validation
   *
   * @param path path
   * @return Document
   * @throws IOException when the file is not found
   * @throws JDOMException when something unexpected happens while parsing
   * @see #getDocument(String, boolean)
   */
  public Document getDocument(String path) throws JDOMException, IOException {

    return this.getDocument(path, false);
  }

  /**
   * Returns a list of unique Elements contained in the subtree of base and patch
   *
   * @param base {@link Element}
   * @param patch {@link Element}
   * @return {@link List}&lt;{@link Element} with unique element tags (tags from different namespaces are considered
   *         different). The elements are ordered as in the input elements
   */
  public List<Element> getUniqueElements(Element base, Element patch) {

    List<Element> result = new LinkedList<>();
    List<Element> list1 = getChildrenFromElement(base);
    List<Element> list2 = getChildrenFromElement(patch);

    for (int i = 0; i < (list1.size() >= list2.size() ? list1.size() : list2.size()); i++) {
      if (i < list1.size()) {
        Element element = list1.get(i);
        boolean newTag = true;
        for (Element e : result) {
          if (element.getName().equals(e.getName())) {
            newTag = false;
            break;
          }
        }
        if (newTag) {
          result.add(element);
        }
      }
      if (i < list2.size()) {
        Element element = list2.get(i);
        boolean newTag = true;
        for (Element e : result) {
          if (element.getName().equals(e.getName())) {
            newTag = false;
            break;
          }
        }
        if (newTag) {
          result.add(element);
        }
      }
    }

    return result;
  }

  /**
   * Returns a list of unique Attributes contained in the subtree of base and patch
   *
   * @param base {@link Attribute}
   * @param patch {@link Attribute}
   * @return {@link List}&lt;{@link Attribute} with unique attribute names (names from different namespaces are
   *         considered different). The attributes are ordered as in the input elements
   * @see #getUniqueElements(Element, Element)
   */
  public List<Attribute> getUniqueAttributes(Element base, Element patch) {

    List<Attribute> result = new LinkedList<>();
    List<Attribute> list1 = base.getAttributes();
    List<Attribute> list2 = patch.getAttributes();

    for (int i = 0; i < (list1.size() >= list2.size() ? list1.size() : list2.size()); i++) {
      if (i < list1.size()) {
        Attribute attribute = list1.get(i);
        boolean newTag = true;
        for (Attribute e : result) {
          if (attribute.getName().equals(e.getName())) {
            newTag = false;
            break;
          }
        }
        if (newTag) {
          result.add(attribute);
        }
      }
      if (i < list2.size()) {
        Attribute attribute = list2.get(i);
        boolean newTag = true;
        for (Attribute e : result) {
          if (attribute.getName().equals(e.getName())) {
            newTag = false;
            break;
          }
        }
        if (newTag) {
          result.add(attribute);
        }
      }
    }

    return result;
  }

  /**
   * Reads a file into a string
   *
   * @param path to the file
   * @param charset the name of the charset to be used
   * @return String
   * @throws IOException when the file can't be read
   */
  public String readFile(String path, String charset) throws IOException {

    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return new String(encoded, charset);
  }

  /**
   * Returns all child elements of the given Element regardless of their Namespaces
   *
   * @param element {@link Element}
   * @return {@link List}&lt;{@link Element}&gt;
   */
  public List<Element> getChildrenFromElement(Element element) {

    List<Element> result = new LinkedList<>();

    for (Content c : element.getContent()) {
      if (c instanceof Element) {
        result.add((Element) c);
      }
    }
    return result;

  }

  /**
   * Returns a map of namespaces and their schema locations extracted from a base and a patch element
   *
   * @param base not null root of the base document
   * @param patch not null root of the patch document
   * @param conflictHandling decides which values will be preferred in case of different locations in base and patch
   * @return Map&lt;String, String&gt; with the namespace uris as keys and the schema locations as values
   */
  public Map<String, String> getNamespaceLocations(Element base, Element patch, ConflictHandlingType conflictHandling) {

    String attName = "schemaLocation";
    Map<String, String> resultMap = new HashMap<>();
    String fullBaseLocationString = base.getAttributeValue(attName, XSI);
    String fullPatchLocationString = patch.getAttributeValue(attName, XSI);

    LinkedList<String> baseList = new LinkedList<>();
    if (fullBaseLocationString != null) {
      for (String s : fullBaseLocationString.split("\n")) {
        if (s.equals("")) {
          continue;
        }
        baseList.addAll(Arrays.asList(s.split(" ")));
      }
      baseList.removeAll(Arrays.asList(""));
    }
    LinkedList<String> patchList = new LinkedList<>();
    if (fullPatchLocationString != null) {
      for (String s : fullPatchLocationString.split("\n")) {
        if (s.equals("")) {
          continue;
        }
        patchList.addAll(Arrays.asList(s.split(" ")));
      }
      patchList.removeAll(Arrays.asList(""));
    }
    // lists should now be of the form (uri, location, uri, location.....)
    List<String> firstList;
    List<String> secondList;
    switch (conflictHandling) {
      default:
      case BASEATTACHOROVERWRITEVALIDATE:
      case BASEATTACHOROVERWRITE:
      case BASEOVERWRITEVALIDATE:
      case BASEOVERWRITE:
        firstList = patchList;
        secondList = baseList;
        break;
      case PATCHATTACHOROVERWRITEVALIDATE:
      case PATCHATTACHOROVERWRITE:
      case PATCHOVERWRITEVALIDATE:
      case PATCHOVERWRITE:
        firstList = baseList;
        secondList = patchList;
    }
    for (ListIterator<String> iterator = firstList.listIterator(); iterator.hasNext();) {
      String key = iterator.next();
      String value = (iterator.hasNext() ? iterator.next() : null);
      if (value == null) {
        logger.warn("The xsi:schemaLocation may be corrupt. Found no location for uri {}", key);
      }
      resultMap.put(key, value);
    }
    // adds pairs from the second list and replaces values for keys found in this list
    for (ListIterator<String> iterator = secondList.listIterator(); iterator.hasNext();) {
      String key = iterator.next();
      String value = (iterator.hasNext() ? iterator.next() : null);
      if (value == null) {
        logger.warn("The xsi:schemaLocation may be corrupt. Found no location for uri {}", key);
      }
      resultMap.put(key, value);
    }
    return resultMap;
  }

  /**
   * Returns the attribute value String for the merged schema locations
   *
   * @param base base documents root, not null
   * @param patch patch documents root, not null
   * @param conflictHandling decides which values will be preferred in case of different locations in base and patch
   * @return String containing every uri exactly once and a location for each uri of the form uri location \n uri
   *         location ...
   */
  public String getSchemaLocationString(Element base, Element patch, ConflictHandlingType conflictHandling) {

    Map<String, String> map = getNamespaceLocations(base, patch, conflictHandling);
    if (map.isEmpty()) {
      return "";
    }
    String result = "";
    for (String key : map.keySet()) {
      result = result + key + " " + map.get(key) + "  ";
    }
    result = result.trim();
    return result;
  }

  /**
   * Replaces a namespace through the document
   *
   * @param root root element of xml tree. Replacement will occur on ./descendant-or-self::*
   * @param toReplace the namespace currently in the document
   * @param replacement the namespace that should replace toReplace
   */
  public void replaceNamespaceDeep(Element root, Namespace toReplace, Namespace replacement) {

    if (root.getNamespace().equals(toReplace)) {
      root.setNamespace(replacement);
    }
    for (Element e : root.getChildren()) {
      replaceNamespaceDeep(e, toReplace, replacement);
    }
  }

  /**
   * Creates a Namespace for a dtd defined document
   *
   * @param publicId the publicId from the &lt;!-Doctype&gt; node
   * @return Namespace with an URI missing the leading '-' from the publicId
   */
  public Namespace getDtdNamespace(String publicId) {

    String arg = publicId;

    if (publicId.indexOf("-") == 0) {
      arg = publicId.replaceFirst("-", "");
    }

    return Namespace.getNamespace(arg);
  }

  /**
   * Initializes the specified MergeSchema, i.e. replaces NullPointers with clean instantiated objects
   *
   * @param schema MergeSchema to be initialized
   * @return the specified MergeSchema with initialized field
   */
  public MergeSchema initializeMergeSchema(MergeSchema schema) {

    if (schema.getDefinition() == null) {
      Definition def = new Definition();
      schema.setDefinition(def);
    }
    if (schema.getDefaultCriterion() == null) {
      DefaultCriterionType crit = new DefaultCriterionType();
      schema.setDefaultCriterion(crit);
    }

    return schema;
  }

}
