package com.stuartcullen.Stockopediatestv2.database;

import com.stuartcullen.Stockopediatestv2.database.parser.FromCSV;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.sqlite.SQLiteDataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;


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
     * Here for the purposes of the assignment (following feedback).  SQL statements should never be embedded like this!
     */
    @Language("SQLite")
    private static final String SQL_CREATION_STATEMENT =

            "   BEGIN TRANSACTION;                                          " +
            "   CREATE TABLE IF NOT EXISTS `Security` (                     " +
            "   	`id`	INTEGER NOT NULL,                               " +
            "   	`symbol`	TEXT,                                       " +
            "   	PRIMARY KEY(`id` AUTOINCREMENT)                         " +
            "   );                                                          " +
            "   CREATE TABLE IF NOT EXISTS `Attribute` (                    " +
            "   	`id`	INTEGER NOT NULL,                               " +
            "   	`name`	TEXT,                                           " +
            "   	PRIMARY KEY(`id` AUTOINCREMENT)                         " +
            "   );                                                          " +
            "   CREATE TABLE IF NOT EXISTS `Fact` (                         " +
            "   	`security_id`	INTEGER NOT NULL,                       " +
            "   	`attribute_id`	INTEGER NOT NULL,                       " +
            "   	`value`	REAL,                                           " +
            "   	PRIMARY KEY(`security_id`,`attribute_id`)               " +
            "   );                                                          " +
            "   CREATE VIEW JoinedFacts as                                  " +
            "   SELECT                                                      " +
            "   (security_id || '-' || attribute_id) as 'fact_key',         " +
            "   Security.symbol,                                            " +
            "   Attribute.name,                                             " +
            "   value                                                       " +
            "    FROM Fact                                                  " +
            "     LEFT JOIN Security ON Fact.security_id = Security.id      " +
            "     LEFT JOIN Attribute ON Fact.attribute_id = Attribute.id   " +
            "     ORDER BY Security.symbol, Attribute.name;                 " +
            "   COMMIT;                                                     ";


    /**
     * The query for finding a fact from the joined view
     */
    @Language("SQLite")
    private static final String SQL_QUERY_FIND_FACT = "SELECT value FROM JoinedFacts where symbol = ? and name = ?;";


    /**
     * The name of the value column on the join view query output rows
     */
    private static final String VALUE_COLUMN_NAME = "value";


    /**
     * Gets the connection to the database, fills with data and prepares the statement for searching
     *
     * @param creationSQL The creation statement to create the database in memory
     *
     * @throws SQLException if things are not going so well
     */
    public static void createDatabase(String creationSQL, JdbcTemplate template) throws SQLException {
        template.execute(creationSQL);

        //Attempt to retrieve a count of the created rows
        final Integer count = template.queryForObject(SQL_QUERY_CONNECTION, Integer.class, (Object[]) null);
        if (null == count)
            throw new RuntimeException("The database creation has failed!");
    }


    /**
     * Create the database using the default creation statement
     */
    public static void createDatabase(JdbcTemplate template) throws SQLException {
        createDatabase(SQL_CREATION_STATEMENT, template);
    }


    /**
     * Find a value for a given fact
     *
     * @param out The value(s) to be filled, in this case an array of length 1 should be available
     *
     * @return The query status (row count)
     *
     * @throws SQLException if things are not going so well
     */
    @SuppressWarnings("UnusedReturnValue")
    public static int findFactValueBySymbolAndAttribute(String securitySymbol, String attributeName, BigDecimal... out)
            throws SQLException {

        int rows;

        throw new RuntimeException("NOT IMPLEMENTED YET!");
    }

}
