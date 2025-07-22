package com.ey.advisory.common.sql;

import java.util.Map;
import java.util.Objects;

public class Ge extends SQLPredicate {

    private String param;

    public Ge(String field, String param) {
        super(field);
        this.param = Objects.requireNonNull(param);
    }

    @Override
    public boolean isValid(Map<String, Object> paramMap) {
        return paramMap.get(param) != null;
    }

    @Override
    public String getName() {
        return "Ge";
    }

    @Override
    public String toSql() {
        return String.format("%s >= :%s", field, param);
    }
}
