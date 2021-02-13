package com.stuartcullen.Stockopediatestv2.database;

import org.hibernate.MappingException;
import org.hibernate.dialect.identity.IdentityColumnSupportImpl;

/**
 * Stuart Cullen - 2021-02-13
 *
 * Adding support for SQLite style id column
 */
public class SQLiteIdentityColumnSupport extends IdentityColumnSupportImpl {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsIdentityColumns() {
        return true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentitySelectString(String table, String column, int type)
            throws MappingException {
        return "select last_insert_rowid()";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentityColumnString(int type) throws MappingException {
        return "integer";
    }

}