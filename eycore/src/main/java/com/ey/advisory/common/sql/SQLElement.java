package com.ey.advisory.common.sql;

public abstract class SQLElement {
    /**
     * The string representation of the SQL operation.
     *
     * @return
     */
    public abstract String getName();

    /**
     * Generate an sql representation of this predicate (i.e. the SQL snippet
     * that represents this predicate)
     *
     * @return
     */
    public abstract String toSql();
}
