package com.stuartcullen.Stockopediatestv2.database;

import com.stuartcullen.Stockopediatestv2.database.parser.FromCSV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Stuart Cullen - 2021-02-13
 *
 * A simple way of getting the in-memory database ready for use in this project
 */
@Component
public class Starter implements ApplicationRunner {

    /**
     * The jdbc template that will be used for all database operations.
     */
    @Autowired
    private JdbcTemplate template;


    /**
     * The attributes data
     */
    @Value("classpath:data/attributes.csv")
    Resource attributesCSV;


    /**
     * The facts data
     */
    @Value("classpath:data/facts.csv")
    Resource factsCSV;


    /**
     * The securities data
     */
    @Value("classpath:data/securities.csv")
    Resource securitiesCSV;


    /**
     * {@inheritDoc}
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        DatabaseUtils.createDatabase(template);
        populateDatabase(template);
    }


    /**
     * Parse the CSV files and fill the database with the provided data
     */
    public void populateDatabase(JdbcTemplate _template) {
        try {
            FromCSV.parseAttributes(new InputStreamReader(attributesCSV.getInputStream()), template);
            FromCSV.parseSecurities(new InputStreamReader(securitiesCSV.getInputStream()), template);
            FromCSV.parseFacts(new InputStreamReader(factsCSV.getInputStream()), template);
            int rowCount = DatabaseUtils.getRowCount(template);
            System.out.println("Database ready." + rowCount + " rows were created in master.");
            System.out.println("The join view has :" + DatabaseUtils.getJoinedFactsRowCount(template) + "rows.");
        } catch (IOException e) {
            //Fail quickly and loudly
            throw new RuntimeException(e);
        }
    }

}