package com.stuartcullen.Stockopediatestv2.evaluation;

import com.stuartcullen.Stockopediatestv2.database.DatabaseUtils;
import com.stuartcullen.Stockopediatestv2.exceptions.LiveDemoException;
import com.stuartcullen.Stockopediatestv2.exceptions.NoFactReferenceException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.sql.SQLException;

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
        setDThreeNodeDisplayType("reference");
        setDThreeNodeDisplayDescription(attributeName);
    }


    /**
     * We also want to display the attribute name on this particular type of expression
     *
     * @param dThreeNodeDisplayDescription The base description
     */
    @Override
    public void setDThreeNodeDisplayDescription(String dThreeNodeDisplayDescription) {
        this.dThreeNodeDisplayDescription = attributeName + "(" + dThreeNodeDisplayDescription + ")";
    }


    /**
     * Attempt to find the value for a given attribute of a security
     *
     * @param template The JDBC template to use for database operations
     *
     * @throws SQLException If the database is having issues
     */
    public void resolveReference(JdbcTemplate template) throws LiveDemoException  {
        var out = new float[1];
        try {
            DatabaseUtils.findFactValueBySymbolAndAttribute(template, securitySymbol, attributeName, out);
        } catch (SQLException | EmptyResultDataAccessException e) {
            throw new NoFactReferenceException(e, securitySymbol, attributeName);
        }
        this.number = out[0];
        setDThreeNodeDisplayDescription(attributeName + "\n(" + number + ")");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal apply(JdbcTemplate template, TreeData treeData) throws LiveDemoException {

        //attempt to resolve number from database
        resolveReference(template);
        return super.apply(template, treeData);
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
