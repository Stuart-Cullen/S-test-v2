package com.stuartcullen.Stockopediatestv2.evaluation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stuartcullen.Stockopediatestv2.exceptions.LiveDemoException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

/**
 * Stuart Cullen - 2020-02-10
 *
 * The concrete class for an expression in this assignment
 *
 * I've added a few extra fields for d3 display.
 */
public class FlatTraversalExpression implements Expression {

    /**
     * A node name that will be generated for d3 display convenience
     */
    @Expose
    @SerializedName("name")
    String dThreeNodeDisplayName;


    /**
     * A node description that will be generated for d3 display convenience
     */
    @Expose
    @SerializedName("description")
    String dThreeNodeDisplayDescription;


    /**
     * A node type that will be generated for d3 display convenience
     */
    @Expose
    @SerializedName("display_type")
    String dThreeNodeDisplayType = "function";


    /**
     * The name of the parent node
     * Explicitly "top" by default as the evaluation node is technically not part of the same recursion tree
     */
    @Expose
    @SerializedName("parent")
    String dThreeParentDisplayName = "top";

    @SerializedName("fn")
    BigDecimalOperation function;

    @SerializedName("a")
    Expression parameterA;

    @SerializedName("b")
    Expression parameterB;


    @Override
    public BigDecimalOperation getFunction() {
        return function;
    }

    @Override
    public Expression getParameterA() {
        return parameterA;
    }

    @Override
    public Expression getParameterB() {
        return parameterB;
    }


    public String getDThreeNodeDisplayDescription() {
        return dThreeNodeDisplayDescription;
    }

    public void setDThreeNodeDisplayDescription(String dThreeNodeDisplayDescription) {
        this.dThreeNodeDisplayDescription = dThreeNodeDisplayDescription;
    }

    public String getDThreeNodeDisplayType() {
        return dThreeNodeDisplayType;
    }

    public void setDThreeNodeDisplayType(String dThreeNodeDisplayType) {
        this.dThreeNodeDisplayType = dThreeNodeDisplayType;
    }


    /**
     * Apply all math operations recursively
     *
     * V2: Now with inline data querying.
     * @see TerminalReferenceExpression
     *
     * The tree data is a way of keeping track of the tree position for non recursive outsiders.  A morris traversal is
     * always hard to follow.  Using the tree a function can be plugged in, but there are other more comprehensive ways
     * of doing this for sure.
     *
     * @return The result for this node (the final result at the root)
     */
    public BigDecimal apply(JdbcTemplate template, TreeData treeData) throws LiveDemoException {

        BigDecimal resultA = null;
        BigDecimal resultB = null;

        dThreeNodeDisplayName = "" + treeData.depth + "X" + treeData.strafe;

        if (null != parameterA) {
            ((FlatTraversalExpression)parameterA).dThreeParentDisplayName = dThreeNodeDisplayName;
            treeData.depth += 1;
            treeData.strafe -= 1;
            resultA = ((FlatTraversalExpression)parameterA).apply(template, treeData);
            treeData.depth -= 1;
            treeData.strafe +=1;
        }

        if (null != parameterB) {
            ((FlatTraversalExpression)parameterB).dThreeParentDisplayName = dThreeNodeDisplayName;
            treeData.depth += 1;
            treeData.strafe += 1;
            resultB = ((FlatTraversalExpression)parameterB).apply(template, treeData);
            treeData.depth -= 1;
            treeData.strafe -=1;
        }

        BigDecimal topResult = getFunction().apply(resultA, resultB);
        setDThreeNodeDisplayDescription(topResult.stripTrailingZeros().toPlainString());
        treeData.onExpressionApplied(this);
        return topResult;
    }

}
