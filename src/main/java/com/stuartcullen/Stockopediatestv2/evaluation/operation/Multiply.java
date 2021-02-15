package com.stuartcullen.Stockopediatestv2.evaluation.operation;

import com.stuartcullen.Stockopediatestv2.evaluation.BigDecimalOperation;

import java.math.BigDecimal;


/**
 * Stuart Cullen 2021-02-14
 *
 * Multiply one big decimal by another
 */
public class Multiply implements BigDecimalOperation {

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal apply(BigDecimal a, BigDecimal b) {
        return a.multiply(b);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getUIDescription() {
        return "×";
    }

}
