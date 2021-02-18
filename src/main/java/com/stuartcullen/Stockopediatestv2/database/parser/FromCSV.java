package com.stuartcullen.Stockopediatestv2.database.parser;

import org.intellij.lang.annotations.Language;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;


import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseUnsignedInt;

/**
 * Stuart Cullen - 2021-02-13
 *
 * A naive CSV parser to take the provided data and place it into an in-memory database
 */
@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve"}) //There is no data source until we make it
public class FromCSV {

    /**
     * Column ordinals for facts
     */
    private enum FactColumn {
        SECURITY_ID,
        ATTRIBUTE_ID,
        VALUE
    }


    /**
     * Column ordinals for attributes
     */
    private enum AttributeColumn {
        ID,
        NAME
    }


    /**
     * Column ordinals for securities
     */
    private enum SecurityColumn {
        ID,
        SYMBOL
    }

    /**
     * The offset of the column ordinals in the table to be explicit
     */
    private final static int INDEX_OFFSET = 1;


    /**
     * Thankfully the delimiter in this case is a single character which isn't ".$|()[{^?*+\\"  so it will not trigger
     * the creation a regex pattern under the hood when performing a split since JDK7.
     */
    private final static String DELIMITER = ",";


    /**
     * Hard skip the first line of a buffered reader
     */
    public static void skipHeader(BufferedReader reader) throws IOException {
        reader.readLine();
    }


    /**
     * Parse the provided Fact CSV directly into the database via a stream
     *
     * @param source The source CSV as a buffered reader
     */
    public static void parseFacts(Reader source, JdbcTemplate template) {

        @Language("SQLite")
        final String query="INSERT INTO Fact values(?,?,?)";

        try (LineNumberReader reader = new LineNumberReader(source)) {
            skipHeader(reader);
            reader.lines()
                    .map(each -> each.split(DELIMITER))
                    .forEach(args -> {
                        template.execute(query, (PreparedStatementCallback<Boolean>) statement -> {

                            //Security ID
                            statement.setInt(
                                    FactColumn.SECURITY_ID.ordinal() + INDEX_OFFSET,
                                    parseUnsignedInt(args[FactColumn.SECURITY_ID.ordinal()]));

                            //Attribute ID
                            statement.setInt(
                                    FactColumn.ATTRIBUTE_ID.ordinal() + INDEX_OFFSET,
                                    parseUnsignedInt(args[FactColumn.ATTRIBUTE_ID.ordinal()]));

                            //Value
                            statement.setFloat(
                                    FactColumn.VALUE.ordinal() + INDEX_OFFSET,
                                    parseFloat(args[FactColumn.VALUE.ordinal()]));

                            return statement.execute();
                        });
                    });
        } catch (IOException exception) {
            throw new RuntimeException(exception);
            //since there are no provisions for a "user" to rectify this problem
        }
    }


    /**
     * Parse the provided Attribute CSV directly into the database via a stream
     *
     * @param source The source CSV as a buffered reader
     */
    public static void parseAttributes(Reader source, JdbcTemplate template) {

        @Language("SQLite")
        final String query="INSERT INTO Attribute values(?,?)";

        try (LineNumberReader reader = new LineNumberReader(source)) {
            skipHeader(reader);
            reader.lines()
                    .map(each -> each.split(DELIMITER))
                    .forEach(args -> {
                        template.execute(query, (PreparedStatementCallback<Boolean>) statement -> {

                            //ID
                            statement.setInt(
                                    AttributeColumn.ID.ordinal() + INDEX_OFFSET,
                                    parseUnsignedInt(args[AttributeColumn.ID.ordinal()]));

                            //Name
                            statement.setString(
                                    AttributeColumn.NAME.ordinal() + INDEX_OFFSET,
                                    args[AttributeColumn.NAME.ordinal()]);

                            return statement.execute();
                        });
                    });
        } catch (IOException exception) {
            throw new RuntimeException(exception);
            //since there are no provisions for a "user" to rectify this problem
        }
    }


    /**
     * Parse the provided Security CSV directly into the database via a stream
     *
     * @param source The source CSV as a buffered reader
     */
    public static void parseSecurities(Reader source, JdbcTemplate template) {

        @Language("SQLite")
        final String query="INSERT INTO Security values(?,?)";

        try (LineNumberReader reader = new LineNumberReader(source)) {
            skipHeader(reader);
            reader.lines()
                    .map(each -> each.split(DELIMITER))
                    .forEach(args -> {
                        template.execute(query, (PreparedStatementCallback<Boolean>) statement -> {

                            //ID
                            statement.setInt(
                                    SecurityColumn.ID.ordinal() + INDEX_OFFSET,
                                    parseUnsignedInt(args[SecurityColumn.ID.ordinal()]));

                            //Name
                            statement.setString(
                                    SecurityColumn.SYMBOL.ordinal() + INDEX_OFFSET,
                                    args[SecurityColumn.SYMBOL.ordinal()]);

                            return statement.execute();
                        });
                    });
        } catch (IOException exception) {
            throw new RuntimeException(exception);
            //since there are no provisions for a "user" to rectify this problem
        }
    }

}
