package com.stuartcullen.Stockopediatestv2.exceptions;

/**
 * Stuart Cullen - 2021-02-14
 *
 * An exception that can be raised specifically when the outbound json syntax is broken (for example)
 * This would be a very serious error, and there are no user friendly extras for this one...
 */
public class DThreeJsonSerializationException extends LiveDemoException {


    /**
     * Basic constructor for this specific problem
     *
     * @param cause The underlying throwable cause
     */
    public DThreeJsonSerializationException(Throwable cause) {
        super(cause);
    }

}
