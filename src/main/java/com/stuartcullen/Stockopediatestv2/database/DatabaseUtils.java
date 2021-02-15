package com.stuartcullen.Stockopediatestv2.database;

import org.intellij.lang.annotations.Language;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;


/**
 * Stuart Cullen - 2021-02-10
 *
 * A basic sqlite database in memory.
 *
 * V2 UPDATE (2021-02-13): It came to pass that the test did indeed want to see CSV parsing.  So this is now included
 * instead of using a fully pre-made import sql statement.
 *
 * DISCLAIMER:
 * There are now no external sources involved in creating this database schema in memory.  Everything is within this
 * class.  I've done this based on the feedback of the first attempt of the assignment.  However I don't think it's good
 * practice to have lengthy SQL statements embedded within a Java class as strings!  By using SQL resource files you can
 * write and test them properly as you go along with the correct syntax highlighting etc...
 * this would also be maintenance headache as a project grows.
 * (In spring this is expected with a schema.sql file in the resources)
 *
 * For simplicity, this also contains a join view based on the README.
 */
@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve"}) //There is no data source until we make it
public class DatabaseUtils {

    /**
     * The query to test the initial database schema
     */
    @Language("SQLite")
    private static final String SQL_QUERY_CONNECTION = "SELECT COUNT(*) AS n FROM \"sqlite_master\"";


    /**
     * A test that the join has worked
     */
    @Language("SQLite")
    private static final String SQL_QUERY_JOINED_FACTS_COUNT = "SELECT COUNT(*) AS n FROM `JoinedFacts`";


    /**
     * Create the securities table
     */
    @Language("SQLite")
    private static final String SQL_CREATION_STATEMENT_SECURITY =
            "   CREATE TABLE IF NOT EXISTS `Security` (									" +
            "   	`id`	INTEGER NOT NULL,											" +
            "   	`symbol`	TEXT,													" +
            "   	PRIMARY KEY(`id` AUTOINCREMENT)										" +
            "   );																		";


    /**
     * Create the attributes table
     */
    @Language("SQLite")
    private static final String SQL_CREATION_STATEMENT_ATTRIBUTE =
            "   CREATE TABLE IF NOT EXISTS `Attribute` (								" +
            "   	`id`	INTEGER NOT NULL,											" +
            "   	`name`	TEXT,														" +
            "   	PRIMARY KEY(`id` AUTOINCREMENT)										" +
            "   );																		";

    /**
     *
     * Create the facts table
     */
    @Language("SQLite")
    private static final String SQL_CREATION_STATEMENT_FACT =
            "   CREATE TABLE IF NOT EXISTS `Fact` (										" +
            "   	`security_id`	INTEGER NOT NULL,									" +
            "   	`attribute_id`	INTEGER NOT NULL,									" +
            "   	`value`	REAL,														" +
            "   	PRIMARY KEY(`security_id`,`attribute_id`)							" +
            "   );																		";


    /**
     * Create the joined view as prescribed in the readme
     */
    @Language("SQLite")
    private static final String SQL_CREATION_STATEMENT_JOIN_VIEW =
            "   CREATE VIEW JoinedFacts as												" +
            "   SELECT																	" +
            "   (`security_id` || '-' || `attribute_id`) as 'fact_key',					" +
            "   `Security`.`symbol`,													" +
            "   `Attribute`.`name`,														" +
            "   `value`																	" +
            "   FROM `Fact`																" +
            "   	LEFT JOIN `Security` ON `Fact`.`security_id` = `Security`.`id`		" +
            "   	LEFT JOIN `Attribute` ON `Fact`.`attribute_id` = `Attribute`.`id`	" +
            "   	ORDER BY `Security`.`symbol`, `Attribute`.`name`;					";


    /**
     * The name of the symbol column on the join view query output rows
     */
    private static final String SYMBOL_COLUMN_NAME = "symbol";


    /**
     * The name of the attribute name column on the join view query output rows
     */
    private static final String ATTRIBUTE_NAME_COLUMN_NAME = "name";


    /**
     * The query for finding a fact from the joined view
     */
    @Language("SQLite")
    private static final String SQL_QUERY_FIND_FACT =
            "SELECT value FROM JoinedFacts where " +
                    SYMBOL_COLUMN_NAME          + " = ?  and " +
                    ATTRIBUTE_NAME_COLUMN_NAME  + " = ?;";


    /**
     * Get the number of rows in the master table
     *
     * @param template The jdbc template to use for the operation
     *
     * @return The number of rows in the master table
     */
    public static int getRowCount(JdbcTemplate template) {
        //Attempt to retrieve a count of the created rows
        final Integer count = template.queryForObject(SQL_QUERY_CONNECTION, Integer.class, (Object[]) null);
        if (null == count)
            throw new RuntimeException("The database connection is broken!");

        return count;
    }


    /**
     * Get the number of rows in JoinedFacts
     *
     * @param template The jdbc template to use for the operation
     *
     * @return The number of rows in the JoinedFacts view
     */
    public static int getJoinedFactsRowCount(JdbcTemplate template) {
        //Attempt to retrieve a count of the created rows
        final Integer count = template.queryForObject(SQL_QUERY_JOINED_FACTS_COUNT, Integer.class, (Object[]) null);
        if (null == count)
            throw new RuntimeException("The database connection is broken!");

        return count;
    }


    /**
     * Execute one of the creation statements
     *
     * @param creationStatementSQL The creation statement to create the database in memory
     *
     * @throws SQLException if things are not going so well
     *
     * @return The number of rows ikn the database
     */
    @SuppressWarnings("UnusedReturnValue")
    public static int executeCreationStatement(String creationStatementSQL, JdbcTemplate template) throws SQLException {
        template.execute(creationStatementSQL);
        return getRowCount(template);
    }


    /**
     * Create the database using the default creation statements
     *
     * @return The number of rows in the database
     */
    @SuppressWarnings("UnusedReturnValue")
    public static int createDatabase(JdbcTemplate template) throws SQLException {
        executeCreationStatement(
                SQL_CREATION_STATEMENT_ATTRIBUTE.replaceAll("\\t", "").replaceAll("\\n", ""),
                template
        );
        executeCreationStatement(
                SQL_CREATION_STATEMENT_SECURITY.replaceAll("\\t", "").replaceAll("\\n", ""),
                template
        );
        executeCreationStatement(
                SQL_CREATION_STATEMENT_FACT.replaceAll("\\t", "").replaceAll("\\n", ""),
                template
        );
        executeCreationStatement(
                SQL_CREATION_STATEMENT_JOIN_VIEW.replaceAll("\\t", "").replaceAll("\\n", ""),
                template
        );

        return getRowCount(template);
    }


    /**
     * Find a value for a given fact
     *
     * @param out The value(s) to be filled, in this case an array of length 1 should be available
     *
     * @throws SQLException if things are not going so well
     */
    public static void findFactValueBySymbolAndAttribute(JdbcTemplate template, String symbol, String name, float... out)
            throws SQLException, EmptyResultDataAccessException {

        final Float result = template.queryForObject(SQL_QUERY_FIND_FACT, Float.class, symbol, name);

        if (null == result)
            throw new RuntimeException("Internal error - query or connection failed!");

        out[0] = result;
    }



}
