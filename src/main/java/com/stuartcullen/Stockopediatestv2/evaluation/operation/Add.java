package com.stuartcullen.Stockopediatestv2.evaluation.operation;

import com.stuartcullen.Stockopediatestv2.evaluation.BigDecimalOperation;

import java.math.BigDecimal;

/**
 * Stuart Cullen 2021-02-14
 *
 * Add 2 big decimals
 */
public class Add implements BigDecimalOperation {

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal apply(BigDecimal a, BigDecimal b) {
        return a.add(b);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getUIDescription() {
        return "+";
    }

}
