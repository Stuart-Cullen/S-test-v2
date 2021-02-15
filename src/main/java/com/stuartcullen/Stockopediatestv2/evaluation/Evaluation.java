package com.stuartcullen.Stockopediatestv2.evaluation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;

/**
 * Stuart Cullen - 2020-02-10
 *
 * An evaluation serves as the top level to any expression/tree of expressions pertaining to a particular security.
 */
public class Evaluation {

    /**
     * A node name that will be generated for d3 display convenience
     */
    @SerializedName("name")
    String dThreeNodeDisplayName = "top";

    /**
     * The name of the parent node
     * Explicitly "null" by default which indicates an orphan or the root of the tree in js
     */
    @SerializedName("parent")
    String dThreeParentDisplayName = "null";


    /**
     * A node description that will be generated for d3 display convenience
     */
    @Expose
    @SerializedName("description")
    String dThreeNodeDisplayDescription;


    /**
     * A node type that will be generated for d3 display convenience
     */
    @Expose
    @SerializedName("display_type")
    String dThreeNodeDisplayType = "evaluation";


    /**
     * The root expression
     */
    Expression expression;


    /**
     * The security symbol
     */
    String security;


    public Expression getExpression() {
        return expression;
    }


    public String getSecurity() {
        return security;
    }


    public String getDThreeNodeDisplayDescription() {
        return dThreeNodeDisplayDescription;
    }

    public void setDThreeNodeDisplayDescription(String dThreeNodeDisplayDescription) {
        this.dThreeNodeDisplayDescription = dThreeNodeDisplayDescription;
    }

    public String getDThreeNodeDisplayType() {
        return dThreeNodeDisplayType;
    }

    public void setDThreeNodeDisplayType(String dThreeNodeDisplayType) {
        this.dThreeNodeDisplayType = dThreeNodeDisplayType;
    }

}
