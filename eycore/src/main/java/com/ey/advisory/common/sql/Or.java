package com.ey.advisory.common.sql;

public class Or extends SQLOperator {

    @Override
    public String getName() {
        return "or";
    }

    @Override
    public String toSql() {
        return "OR";
    }
}
