package com.stuartcullen.Stockopediatestv2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.stuartcullen.Stockopediatestv2.evaluation.*;
import com.stuartcullen.Stockopediatestv2.exceptions.LiveDemoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Stuart Cullen - 2021-02-14
 *
 * The web controller for this demo project
 */
@Controller
public class SoleController {

    /**
     * The template for database operations
     */
    @Autowired
    JdbcTemplate template;

    @Value("classpath:data/default.json")
    private Resource defaultJson;

    @Value("classpath:data/basic.json")
    private Resource basicJson;

    @Value("classpath:data/divide.json")
    private Resource divideJson;

    @Value("classpath:data/nested.json")
    private Resource nestedJson;

    /** the resource strings */
    String defaultJsonString, basicJsonString, divideJsonString, nestedJsonString;


    @PostConstruct
    public void init() {
        try {
            defaultJsonString = stringFromResource(defaultJson);
            basicJsonString = stringFromResource(basicJson);
            divideJsonString = stringFromResource(divideJson);
            nestedJsonString = stringFromResource(nestedJson);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * The post data object
     */
    public class DSL {

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        private String content;
    }


    /**
     * Form to send custom DSL
     *
     * @param model The model that will be supplied
     *
     * @return The page view to load
     */
    @GetMapping("/dsl")
    public String customDSLForm(Model model) {
        final DSL dsl = new DSL();
        dsl.setContent(getPreset("nested"));
        model.addAttribute("dsl", dsl);
        return "dsl";
    }


    /**
     * Submission of the DSL to test
     *
     * @param dsl The raw DSL
     * @param model The model that will be supplied
     *
     * @return The page view to load
     */
    @PostMapping("/dsl")
    public String customDSLSubmit(@ModelAttribute DSL dsl, Model model) {
        model.addAttribute("dsl", dsl);
        return processDSL(dsl,model);
    }


    /**
     * General processing of the DSL
     * This is the meat of the operation
     *
     * @param dsl The raw DSL
     * @param model The model that will be supplied
     *
     * @return The page view to load
     */
    private String processDSL (@ModelAttribute DSL dsl, Model model) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();

        try {
            //first test that the json is valid and prettify it
            final String newJson = gson.toJson(jp.parse(dsl.getContent()));
            dsl.setContent(newJson);

            final Evaluation evaluation;
            final String d3JsonFromEvaluationTree;

            //process the DSL input provided by the user (or the sample)
            evaluation = EvaluationProcessor.createEvaluationTreeFromJson(newJson);

            //run all of the calculations within the binary tree
            final BigDecimal total = ((FlatTraversalExpression) evaluation.getExpression()).apply(template, new TreeData());
            evaluation.setDThreeNodeDisplayDescription("Total\n(" + evaluation.getSecurity() + "): " + total.stripTrailingZeros().toPlainString());

            //Create an output for the diagram to use
            d3JsonFromEvaluationTree = DThreeJsonEvaluationSerializer.createD3JsonFromEvaluationTree(evaluation);
            model.addAttribute("dsl_json", d3JsonFromEvaluationTree);

        } catch (Exception e) {
            if (e instanceof LiveDemoException)
                model.addAttribute("error", "An error occurred:\n" + ((LiveDemoException)e).getMessageForWebUI());
            else
                model.addAttribute("error", "An error occurred trying to parse the input DSL");
            return "error";
        }

        return "result";
    }


    /**
     * Convenience method to grab the string from a resource
     *
     * @return The string obtained from the resource
     */
    private String stringFromResource(Resource resource) throws IOException {
        return new BufferedReader(new InputStreamReader(resource.getInputStream()))
                .lines().collect(Collectors.joining("\n"));
    }


    /**
     * Select a sample DSL
     *
     * @param presetChoice The choice
     *
     * @return The sample data from resources
     */
    private String getPreset(String presetChoice) {
        switch (presetChoice.trim()) {
            case("basic"): return basicJsonString;
            case("divide"): return divideJsonString;
            case("nested"): return nestedJsonString;
            default: return defaultJsonString;
        }
    }

}
