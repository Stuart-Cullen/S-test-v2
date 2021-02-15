package com.stuartcullen.Stockopediatestv2.evaluation;

import com.google.gson.*;
import com.stuartcullen.Stockopediatestv2.exceptions.DThreeJsonSerializationException;

import java.lang.reflect.Type;


/**
 * Stuart Cullen 2021-02-14
 *
 * A serializer to produce the diagram output
 */
public class DThreeJsonEvaluationSerializer {


    /**
     * Google's finest json interpreter
     */
    private final static Gson gson;
    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder
                .registerTypeAdapter(FlatTraversalExpression.class, new DThreeFTESerializer())
                .registerTypeAdapter(TerminalNumberExpression.class, new DThreeFTETerminalSerializer())
                .registerTypeHierarchyAdapter(Evaluation.class, new EvaluationSerializer())
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create();
    }


    /**
     * The top level just needs a pseudo list of children where it will always be one child
     */
    private static class EvaluationSerializer implements JsonSerializer<Evaluation> {

        /**
         * {@inheritDoc}
         */
        @Override
        public JsonElement serialize(Evaluation evaluation, Type type, JsonSerializationContext context) {
            JsonArray jsonArray = new JsonArray();
            JsonObject obj = new JsonObject();
            JsonElement topExpression = null;

            if (null != evaluation.expression) {
                topExpression = context.serialize(evaluation.expression);
                jsonArray.add(topExpression);
            }

            if (jsonArray.size() != 0){
                obj.add("children", jsonArray);
            }

            obj.addProperty("name", evaluation.dThreeNodeDisplayName);
            obj.addProperty("parent", evaluation.dThreeParentDisplayName);
            obj.addProperty("security", evaluation.security);
            obj.addProperty("display_type", evaluation.dThreeNodeDisplayType);
            obj.addProperty("description", evaluation.dThreeNodeDisplayDescription);

            return obj;
        }
    }


    /**
     * A custom serializer to write flat traversal expressions to the d3 format
     * There are probably more fluent/elegant ways of achieving this but for now I've just assumed direct control...
     */
    private static class DThreeFTESerializer implements JsonSerializer<FlatTraversalExpression> {

        /**
         * {@inheritDoc}
         */
        @Override
        public JsonElement serialize(FlatTraversalExpression expression, Type type, JsonSerializationContext context) {

            System.out.println(expression.dThreeNodeDisplayName);
            JsonObject obj = new JsonObject();

            JsonElement paramA, paramB;
            JsonArray jsonArray = new JsonArray();

            if (null != expression.parameterA) {
                paramA = context.serialize(expression.getParameterA());
                jsonArray.add(paramA);
            }

            if (null != expression.parameterB) {
                paramB = context.serialize(expression.getParameterB());
                jsonArray.add(paramB);
            }

            obj.addProperty("name", expression.dThreeNodeDisplayName);
            obj.addProperty("parent", expression.dThreeParentDisplayName);
            obj.addProperty("display_type", expression.dThreeNodeDisplayType);
            obj.addProperty("description",expression.dThreeNodeDisplayDescription);
            obj.addProperty("function_description",expression.function.getUIDescription());

            if (jsonArray.size() != 0){
                obj.add("children", jsonArray);
            }
            return obj;
        }
    }


    /**
     * Now the terminal statements are just done manually
     */
    private static class DThreeFTETerminalSerializer implements JsonSerializer<TerminalNumberExpression> {

        /**
         * {@inheritDoc}
         */
        @Override
        public JsonElement serialize(TerminalNumberExpression terminalNumberExpression, Type type, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", terminalNumberExpression.dThreeNodeDisplayName);
            obj.addProperty("parent", terminalNumberExpression.dThreeParentDisplayName);
            obj.addProperty("display_type", terminalNumberExpression.dThreeNodeDisplayType);
            obj.addProperty("description", terminalNumberExpression.dThreeNodeDisplayDescription);
            return obj;
        }
    }


    /**
     * Create the output of nodes for the diagram to use
     *
     * @param evaluation The binary tree of expressions
     *
     * @return The string that the d3 diagram can use to populate its nodes
     *
     * @throws DThreeJsonSerializationException Although there's really no coming back from that!
     */
    public static String createD3JsonFromEvaluationTree(Evaluation evaluation) throws DThreeJsonSerializationException {
        try {
            return gson.toJson(evaluation);
        } catch (JsonSyntaxException e) {
            throw new DThreeJsonSerializationException(e);
        }
    }

}
