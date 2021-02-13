package com.stuartcullen.Stockopediatestv2.database;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.identity.IdentityColumnSupport;

import java.sql.Types;

/**
 * Stuart Cullen - 2021-02-13
 *
 * A new dialect for SQLite as it doesn't come out of the box with spring boot
 */
public class SQLiteDialect extends Dialect {

    public SQLiteDialect() {
        registerColumnType(Types.BIT, "integer");
        registerColumnType(Types.TINYINT, "tinyint");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.INTEGER, "integer");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new SQLiteIdentityColumnSupport();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasAlterTable() {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean dropConstraints() {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDropForeignKeyString() {
        return "";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getAddForeignKeyConstraintString(String cn, String[] fk, String t, String[] pk, boolean rpk) {
        return "";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getAddPrimaryKeyConstraintString(String constraintName) {
        return "";
    }

}