package com.stuartcullen.Stockopediatestv2.evaluation;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.math.RoundingMode.HALF_UP;

/**
 * Stuart Cullen - 2021-02-10
 *
 * Creates a tree of expressions based on the json which can then be processed recursively, following retrieval of the
 * reference components.
 *
 * Originally I had planned to do a non recursive walk through the tree, but since it is highly unlikely that operations
 * would be that deeply nested it is outside of the scope.
 * (and the json parser would have already overflown the stack...)
 *
 * All database functions are grouped as an example of an architecture which could be later optimised.
 *
 * Considerations:
 * It is possible to perform all calculations and lookups whilst parsing... but I don't think that's a good idea.
 * A broken json parser is obscure enough to debug at the best of times to name one reason.
 *
 * All operations could have been loaded into a database instead of creating objects... and I have done something
 * similar in the past with dynamic polynomials... but I don't think it's in the spirit of the test.
 *
 * I designed this with some introspective extensibility in mind.  All it would take is to provide a hierarchy to
 * the reference retrieval and then walk through the tree in the right order and you could self reference within the
 * tree as needed.
 */
public class EvaluationProcessor {

    /**
     * The labels in json
     */
    private final static String
            JSON_LABEL_FUNCTION = "fn",
            JSON_LABEL_PARAM_A = "a",
            JSON_LABEL_PARAM_B = "b",
            JSON_LABEL_SECURITY = "security";

    /**
     * An extended root type so that a distinction can be made easily within the deserializer.
     * (gson design choices...)
     */
    private static class RootProcessed extends Evaluation {}


    /**
     * The security symbol is gleaned at any point the json parses an evaluation root
     */
    private static String securitySymbol;


    /**
     * Since there is no introspection within the hierarchy, a flat list of references can be compiled for the database
     * to fetch before resolving the operations.  A tree could instead be used with a more intelligent traversal method
     * enabling the fetching of required references at the required time even inside an introspective setup.
     *
     * This is a very basic solution
     */
    private static List<TerminalReferenceExpression> requiredReferences;


    /**
     * Google's finest json interpreter
     */
    private final static Gson gson;
    static {
        GsonBuilder gsonBuilder = new GsonBuilder();

        //assign a custom maths operator deserializer to gson
        gsonBuilder.registerTypeAdapter(BigDecimalOperation.class, (JsonDeserializer<BigDecimalOperation>)
                (element, type, context) -> chooseOperationFromChar(element.getAsCharacter()));

        //assign the deserializer to deal with the polymorphic parameters
        gsonBuilder.registerTypeAdapter(Expression.class, new TraverseAfterDeserializer());

        //we also need to store the symbol locally during parsing the root node
        gsonBuilder.registerTypeAdapter(Evaluation.class, (JsonDeserializer<Evaluation>)
                (element, type, context) -> {
                    JsonObject object = element.getAsJsonObject().getAsJsonObject();
                    securitySymbol = object.get(JSON_LABEL_SECURITY).getAsString();
                    return context.deserialize(object, RootProcessed.class);
                });

        gson = gsonBuilder.create();
    }


    /**
     * A custom deserializer to deal with the polymorphic parameter types of the DSL
     */
    private static class TraverseAfterDeserializer implements JsonDeserializer<Expression> {

        /**
         * {@inheritDoc}
         */
        @Override
        public Expression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
                throws JsonParseException {

            //A nested function
            if (jsonElement.isJsonObject())
                return context.deserialize(jsonElement.getAsJsonObject(), FlatTraversalExpression.class);

            //A terminating number
            if (jsonElement.getAsJsonPrimitive().isNumber())
                return new TerminalNumberExpression(jsonElement.getAsFloat());

            //Otherwise we assume it's a terminating reference for which a number will be collected
            var reference = new TerminalReferenceExpression(securitySymbol, jsonElement.getAsString());
            requiredReferences.add(reference);
            return reference;
        }
    }


    /**
     * Return a base mathematical operation for a given character
     *
     * @param character The maths operation character
     */
    public static BigDecimalOperation chooseOperationFromChar(char character) {
        switch (character) {

            //ADDITION
            case('+'): return (a, b) -> {
                final BigDecimal result = a.add(b);
                printOperation(character, a, b, result);
                return result;
            };

            //SUBTRACTION
            case('-'): return (a, b) -> {
                final BigDecimal result = a.subtract(b);
                printOperation(character, a, b, result);
                return result;
            };

            //MULTIPLICATION
            case('*'): return (a, b) -> {
                final BigDecimal result = a.multiply(b);
                printOperation(character, a, b, result);
                return result;
            };

            //DIVISION
            case('/'): return (a, b) -> {
                final BigDecimal result = a.setScale(10, HALF_UP)
                        .divide(b.setScale(10, HALF_UP), HALF_UP);
                printOperation(character, a, b, result);
                return result;
            };

            default: throw new RuntimeException("Unknown operation! (" + character + ")");
        }
    }


    /**
     * A quick utility to report the math operation to the user
     *
     * @param character The character representing the operation
     * @param a The left side of the equation
     * @param b The right side of the equation
     */
    public static void printOperation(char character, BigDecimal a, BigDecimal b, BigDecimal result) {
        System.out.println("Sum:" + a.toPlainString() + character + b.toPlainString() + " = " +result.toPlainString());
    }


    /**
     * A basic utility function to grab the "evaluation" from the json
     *
     * @param inputJson The input json string
     *
     * @throws JsonSyntaxException If the json could not be parsed
     */
    public static Evaluation createEvaluationTreeFromJson(final String inputJson)
    throws JsonSyntaxException {
        requiredReferences = new ArrayList<>();
        securitySymbol = null;
        return gson.fromJson(inputJson, Evaluation.class);
    }

/*
    *//**
     * Send the required references off to be filled by the database query
     *
     * This could be heavily optimised but optimising specifically for SQLite would not be within the scope of the
     * test.  Hence this simply grabs each reference one by one.
     *//*
    public static void resolveReferences() {
        requiredReferences.forEach(Database::applyValueBySymbolAndAttribute);
    }


    *//**
     * Run all of the DSL prescribed operations to obtain the final score
     *
     * @param inputJson The input json string
     * @param databaseCreationStatement The sql creation statement to make the database in memory
     *
     *//*
    public static BigDecimal calculateEvaluation(final String inputJson, String databaseCreationStatement) {

        //build tree
        final Evaluation evaluationTree = createEvaluationTreeFromJson(inputJson);

        //create a database in memory based on the provided data
        try {
            Database.createDatabase(databaseCreationStatement);
        } catch (SQLException e) {
            System.out.println("Unable to create database from creation statement!");
            throw new RuntimeException(e);
        }

        //perform all database operations
        resolveReferences();

        try {
            Database.closeDatabase();
        } catch (SQLException e) {
            System.out.println("Unable to close the database!");
            throw new RuntimeException(e);
        }

        //recursively perform all operations
        return ((FlatTraversalExpression) evaluationTree.getExpression()).apply();
    }*/



}
