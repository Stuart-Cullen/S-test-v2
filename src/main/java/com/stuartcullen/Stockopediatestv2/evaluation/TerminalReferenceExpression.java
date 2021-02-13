package com.stuartcullen.Stockopediatestv2.evaluation;

/**
 * Stuart Cullen - 2020-02-10
 *
 * With some modifications to the traversal method this architecture could
 * also be extended to allow for "infinitely" introspective expressions
 */
public class TerminalReferenceExpression extends TerminalNumberExpression {

    /**
     * The security symbol to search for in the fact view
     */
    private String securitySymbol;


    /**
     * The attribute name to search for in the fact view
     */
    private String attributeName;


    public String getSecuritySymbol() {
        return securitySymbol;
    }

    public void setSecuritySymbol(String securitySymbol) {
        this.securitySymbol = securitySymbol;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }


    /**
     * Sole Constructor
     *
     * Calls upon the super constructor to ensure that the terminating number is presented if resolved correctly
     *
     * @param securitySymbol The security symbol to search for in the fact view
     * @param attributeName The attribute name to search for in the fact view
     */
    public TerminalReferenceExpression(String securitySymbol, String attributeName) {
        super();
        this.securitySymbol = securitySymbol;
        this.attributeName = attributeName;
    }


    /**
     * @return A human readable string for the reference expression
     */
    @Override
    public String toString() {
        return "TerminalReferenceExpression{" +
                "securitySymbol='" + securitySymbol + '\'' +
                ", attributeName='" + attributeName + '\'' +
                '}';
    }

}
