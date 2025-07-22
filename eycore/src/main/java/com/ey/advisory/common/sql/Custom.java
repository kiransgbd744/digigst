package com.ey.advisory.common.sql;

import java.util.Map;
import java.util.Objects;

public class Custom extends SQLPredicate {

    private String snippet;

    public Custom(String snippet) {
        super("");
        this.snippet = Objects.requireNonNull(snippet);
    }

    @Override
    public boolean isValid(Map<String, Object> paramMap) {
        return true;
    }

    @Override
    public String getName() {
        return "Custom";
    }

    @Override
    public String toSql() {
        return this.snippet;
    }
}
