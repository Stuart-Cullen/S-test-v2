package com.stuartcullen.Stockopediatestv2.exceptions;

/**
 * Stuart Cullen - 2021-02-14
 *
 * An exception that can be raised specifically when the inbound json syntax is broken (for example)
 */
public class JsonDeserializationException extends LiveDemoException {


    /**
     * Basic constructor for this specific problem
     *
     * @param cause The underlying throwable cause
     */
    public JsonDeserializationException(Throwable cause) {
        super(cause);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageForWebUI() {
        return "Couldn't understand input DSL: \n" + super.getMessageForWebUI();
    }

}
