package com.stuartcullen.Stockopediatestv2.exceptions;

/**
 * Stuart Cullen - 2021-02-14
 *
 * An exception that can be raised specifically when we cannot retrieve a fact from the database for a given reference
 */
public class NoFactReferenceException extends LiveDemoException {

    /**
     * The security symbol we were searching for
     */
    private String securitySymbol;


    /**
     * The attribute name we were searching for
     */
    private String attributeName;


    /**
     * Basic constructor for this specific problem
     *
     * @param cause The underlying throwable cause
     *
     * @param securitySymbol The security symbol we were searching for
     * @param attributeName The attribute name we were searching for
     */
    public NoFactReferenceException(Throwable cause, String securitySymbol, String attributeName) {
        super(cause);
        this.securitySymbol = securitySymbol;
        this.attributeName = attributeName;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageForWebUI() {
        return "Couldn't retrieve fact: " + securitySymbol + "," + attributeName + "\n" + super.getMessageForWebUI();
    }

}
