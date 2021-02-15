package com.stuartcullen.Stockopediatestv2.evaluation;


import java.math.BigDecimal;

/**
 * Stuart Cullen - 2020-02-10
 *
 * A termination of a potential tree of expressions with a resolved number
 * ("float" just because of the implementation constraints, it could be anything)
 */
public class TerminalNumberExpression extends FlatTraversalExpression {

    /**
     * A resolved number at which the expression terminates
     */
    float number;

    public float getNumber() {
        return number;
    }

    public void setNumber(float number) {
        this.number = number;
    }


    /**
     * Construct with static number provided
     * This is also overwrites the display type and description for the diagram
     *
     * @param number The number at which this expression now terminates
     */
    public TerminalNumberExpression(float number) {
        this();
        this.number = number;
        setDThreeNodeDisplayType("number");
        setDThreeNodeDisplayDescription(String.valueOf(number));
    }


    /**
     * Construct with default function to relay the number via a lambda
     */
    protected TerminalNumberExpression() {
        this.function = new BigDecimalOperation() {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getUIDescription() {
                throw new RuntimeException("Not applicable for a terminal function!");
            }


            /**
             * {@inheritDoc}
             */
            @Override
            public BigDecimal apply(BigDecimal bigDecimal, BigDecimal bigDecimal2) {
                return BigDecimal.valueOf(number);
            }
        };
    }

}
