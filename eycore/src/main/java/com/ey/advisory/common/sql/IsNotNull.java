package com.ey.advisory.common.sql;

import java.util.Map;

public class IsNotNull extends SQLPredicate {

    public IsNotNull(String field) {
        super(field);
    }

    @Override
    public boolean isValid(Map<String, Object> paramMap) {
        return true;
    }

    @Override
    public String getName() {
        return "IsNotNull";
    }

    @Override
    public String toSql() {
        return String.format("%s IS NOT NULL", field);
    }
}
