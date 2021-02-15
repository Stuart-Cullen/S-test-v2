package com.stuartcullen.Stockopediatestv2.evaluation.operation;

import com.stuartcullen.Stockopediatestv2.evaluation.BigDecimalOperation;

import java.math.BigDecimal;


/**
 * Stuart Cullen 2021-02-14
 *
 * Subtract one big decimal from another
 */
public class Subtract implements BigDecimalOperation {

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal apply(BigDecimal a, BigDecimal b) {
        return a.subtract(b);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getUIDescription() {
        return "−";
    }

}
