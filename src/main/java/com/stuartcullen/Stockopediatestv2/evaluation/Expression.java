package com.stuartcullen.Stockopediatestv2.evaluation;

import java.io.Serializable;

/**
 * Stuart Cullen - 2020-02-10
 *
 * An interface for a recursive expression.  Different traversal strategies could sit on top of this.
 */
public interface Expression extends Serializable {

    /**
     * @return The mathematical function to apply
     */
    BigDecimalOperation getFunction();


    /**
     * @return The "sub" expression for parameter A or null if not applicable
     */
    Expression getParameterA();


    /**
     * @return The "sub" expression for parameter B or null if not applicable
     */
    Expression getParameterB();

}

