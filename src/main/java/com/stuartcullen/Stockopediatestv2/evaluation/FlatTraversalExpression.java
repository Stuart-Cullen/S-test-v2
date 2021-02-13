package com.stuartcullen.Stockopediatestv2.evaluation;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Stuart Cullen - 2020-02-10
 *
 * The concrete class for an expression in this assignment
 */
public class FlatTraversalExpression implements Expression {

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


    /**
     * Apply all math operations recursively
     *
     * Requires all expressions in the tree to be of the same type!
     * This could be made more type safe in a more mature project.
     *
     * @return The result for this node (the final result at the root)
     */
    BigDecimal apply() {
        return getFunction().apply(
                (null == parameterA) ? null: ((FlatTraversalExpression)getParameterA()).apply(),
                (null == parameterB) ? null: ((FlatTraversalExpression)getParameterB()).apply()
        );
    }

}
