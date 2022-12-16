package com.github.maybeec.lexeme.merge.element.matcher;

import java.util.LinkedList;
import java.util.List;

import com.github.maybeec.lexeme.mergeschema.Criterion;

/**
 * Node of a tree structure. The tree resembles the Handling tree of a MergeSchema. Each CriterionSet has child nodes, a
 * name for which element it applies and the Criterion elements for an Element with that name
 */
public class CriterionSet {

  /**
   * the children
   */
  private List<CriterionSet> children = new LinkedList<>();

  /**
   * name of the elements the criteria applies
   */
  private final String name;

  /**
   * The criteria for elements with the specified name
   */
  private final List<Criterion> criteria;

  /**
   * Constructor
   *
   * @param name of the element for the criteria to apply
   * @param criteria List of Criterion objects
   */
  public CriterionSet(String name, List<Criterion> criteria) {

    this.name = name;
    this.criteria = criteria;
  }

  /**
   * Returns the field 'name'
   *
   * @return value of name
   */
  public String getName() {

    return this.name;
  }

  /**
   * Returns the field 'criteria'
   *
   * @return value of criteria
   */
  public List<Criterion> getCriteria() {

    return this.criteria;
  }

  /**
   * Returns the field 'children'
   *
   * @return value of children
   */
  public List<CriterionSet> getChildren() {

    return this.children;
  }

  /**
   * add a CriterionSet as child
   *
   * @param c the CriterionSet to add as child
   */
  public void addChild(CriterionSet c) {

    this.children.add(c);
  }

}
