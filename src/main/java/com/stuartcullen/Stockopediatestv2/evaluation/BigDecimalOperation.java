package com.stuartcullen.Stockopediatestv2.evaluation;

import java.math.BigDecimal;
import java.util.function.BiFunction;

/**
 * Stuart Cullen - 2020-02-14
 *
 * A basic template for the mathematical operations
 */
public interface BigDecimalOperation extends BiFunction<BigDecimal, BigDecimal, BigDecimal> {

    /**
     * @return A string to use in the UI to describe the operation
     */
    String getUIDescription();

}
