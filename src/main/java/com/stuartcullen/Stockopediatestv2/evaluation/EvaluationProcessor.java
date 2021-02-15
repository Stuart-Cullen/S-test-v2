package com.stuartcullen.Stockopediatestv2.evaluation;

import com.google.gson.*;
import com.stuartcullen.Stockopediatestv2.evaluation.operation.Add;
import com.stuartcullen.Stockopediatestv2.evaluation.operation.Divide;
import com.stuartcullen.Stockopediatestv2.evaluation.operation.Multiply;
import com.stuartcullen.Stockopediatestv2.evaluation.operation.Subtract;
import com.stuartcullen.Stockopediatestv2.exceptions.JsonDeserializationException;

import java.lang.reflect.Type;
import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;

/**
 * Stuart Cullen - 2021-02-10
 *
 * Creates a tree of expressions based on the json which can then be processed recursively, following retrieval of the
 * reference components.
 *
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
            if (jsonElement.isJsonObject()) {
                final FlatTraversalExpression obj = context.deserialize(jsonElement.getAsJsonObject(), FlatTraversalExpression.class);

                return obj;
            }


            //A terminating number
            if (jsonElement.getAsJsonPrimitive().isNumber())
                return new TerminalNumberExpression(jsonElement.getAsFloat());

            //Otherwise we assume it's a terminating reference for which a number will be collected
            return new TerminalReferenceExpression(securitySymbol, jsonElement.getAsString());
        }
    }


    /**
     * Return a base mathematical operation for a given character
     *
     * @param character The maths operation character
     */
    public static BigDecimalOperation chooseOperationFromChar(char character) {
        switch (character) {
            case('+'): return new Add();
            case('-'): return new Subtract();
            case('*'): return new Multiply();
            case('/'): return new Divide();
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
     * @throws JsonSyntaxException If the json could not be parsed which can be relayed to the user
     */
    public static Evaluation createEvaluationTreeFromJson(final String inputJson) throws JsonDeserializationException {
        securitySymbol = null;
        final Evaluation evaluation;
        try {
            evaluation = gson.fromJson(inputJson, Evaluation.class);
        } catch (JsonSyntaxException e) {
            throw new JsonDeserializationException(e);
        }
        return evaluation;
    }

}
