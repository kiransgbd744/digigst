package com.ey.advisory.common.sql;

import java.util.Map;

public class IsNull extends SQLPredicate {

    public IsNull(String field) {
        super(field);
    }

    @Override
    public boolean isValid(Map<String, Object> paramMap) {
        return true;
    }

    @Override
    public String getName() {
        return "IsNull";
    }

    @Override
    public String toSql() {
        return String.format("%s IS NULL", field);
    }
}
