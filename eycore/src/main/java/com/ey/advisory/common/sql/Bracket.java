package com.ey.advisory.common.sql;

import java.util.Map;
import java.util.Objects;

public class Bracket extends SQLPredicate {

    private SQLPredicateBuilder builder;

    public Bracket(SQLPredicateBuilder builder) {
        super("");
        this.builder = Objects.requireNonNull(builder);
    }

    @Override
    public boolean isValid(Map<String, Object> paramMap) {
        return true;
    }

    @Override
    public String getName() {
        return "Bracket";
    }

    @Override
    public String toSql() {
        return String.format("(%s)", builder.toSql());
    }
}
