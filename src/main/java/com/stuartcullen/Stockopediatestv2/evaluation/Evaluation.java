package com.stuartcullen.Stockopediatestv2.evaluation;

/**
 * Stuart Cullen - 2020-02-10
 *
 * An evaluation serves as the top level to any expression/tree of expressions pertaining to a particular security.
 */
public class Evaluation {

    /**
     * The root expression
     */
    Expression expression;


    /**
     * The security symbol
     */
    String security;


    public Expression getExpression() {
        return expression;
    }


    public String getSecurity() {
        return security;
    }

}
