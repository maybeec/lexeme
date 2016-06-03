package com.github.maybeec.lexeme.merge.element.matcher;

import java.util.LinkedList;
import java.util.List;

import com.github.maybeec.lexeme.mergeschema.Criterion;

/**
 * Node of a tree structure. The tree resembles the Handling tree of a MergeSchema. Each CriterionSet has
 * child nodes, a name for which element it applies and the Criterion elements for an Element with that name
 * @author sholzer (Sep 15, 2015)
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
     * @param name
     *            of the element for the criteria to apply
     * @param criteria
     *            List of Criterion objects
     * @author sholzer (Sep 17, 2015)
     */
    public CriterionSet(String name, List<Criterion> criteria) {
        this.name = name;
        this.criteria = criteria;
    }

    /**
     * Returns the field 'name'
     * @return value of name
     * @author sholzer (16.09.2015)
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the field 'criteria'
     * @return value of criteria
     * @author sholzer (16.09.2015)
     */
    public List<Criterion> getCriteria() {
        return criteria;
    }

    /**
     * Returns the field 'children'
     * @return value of children
     * @author sholzer (Sep 16, 2015)
     */
    public List<CriterionSet> getChildren() {
        return children;
    }

    /**
     * add a CriterionSet as child
     * @param c
     *            the CriterionSet to add as child
     * @author sholzer (Sep 16, 2015)
     */
    public void addChild(CriterionSet c) {
        children.add(c);
    }

}
