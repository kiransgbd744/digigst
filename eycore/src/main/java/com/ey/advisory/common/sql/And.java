package com.ey.advisory.common.sql;

public class And extends SQLOperator {
    @Override
    public String getName() {
        return "and";
    }

    @Override
    public String toSql() {
        return "AND";
    }
}
