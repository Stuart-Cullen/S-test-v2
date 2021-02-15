package com.stuartcullen.Stockopediatestv2.evaluation.operation;

import com.stuartcullen.Stockopediatestv2.evaluation.BigDecimalOperation;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;

/**
 * Stuart Cullen 2021-02-14
 *
 * Add one big decimal by another
 */
public class Divide implements BigDecimalOperation {

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal apply(BigDecimal a, BigDecimal b) {
        return a.setScale(10, HALF_UP)
                .divide(b.setScale(10, HALF_UP), HALF_UP);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getUIDescription() {
        return "÷";
    }

}
