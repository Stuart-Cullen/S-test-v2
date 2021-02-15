package com.stuartcullen.Stockopediatestv2.evaluation;

/**
 * Stuart Cullen - 2021-02-14
 *
 * Data which can be passed through the tree recursively as it is processed
 */
public class TreeData {

    /**
     * The current depth of node
     */
    public int depth;


    /**
     * The lateral node position
     */
    public int strafe;

    public TreeData() { }


    /**
     * A general event that could be useful which should be raised when applying an expression
     *
     * @param expression The expression that is being applied
     */
    protected void onExpressionApplied(FlatTraversalExpression expression) {}

}
