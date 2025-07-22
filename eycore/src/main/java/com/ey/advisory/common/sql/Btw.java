package com.ey.advisory.common.sql;

import java.util.Map;
import java.util.Objects;

public class Btw extends  SQLPredicate{

    private String param1;
    private String param2;

    public Btw(String field, String param1, String param2) {
        super(field);
        this.param1 = Objects.requireNonNull(param1);
        this.param2 = Objects.requireNonNull(param2);
    }

    @Override
    public boolean isValid(Map<String, Object> paramMap) {
        return paramMap.get(param1) != null &&
                paramMap.get(param2) != null;
    }

    @Override
    public String getName() {
        return "Btw";
    }

    @Override
    public String toSql() {
        return String.format("%s BETWEEN :%s AND :%s",
                field, param1, param2);
    }
}
