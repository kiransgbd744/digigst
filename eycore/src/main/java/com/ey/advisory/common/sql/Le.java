package com.ey.advisory.common.sql;

import java.util.Map;
import java.util.Objects;

public class Le extends SQLPredicate {

    private String param;

    public Le(String field, String param) {
        super(field);
        this.param = Objects.requireNonNull(param);
    }

    @Override
    public boolean isValid(Map<String, Object> paramMap) {
        return paramMap.get(param) != null;
    }

    @Override
    public String getName() {
        return "Le";
    }

    @Override
    public String toSql() {
        return String.format("%s <= :%s", field, param);
    }
}
