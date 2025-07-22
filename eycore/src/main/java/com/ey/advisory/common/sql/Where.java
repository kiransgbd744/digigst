package com.ey.advisory.common.sql;

public class Where implements SQLPredicateBuilder {

    public SQLPredicateBuilder eq(String field, String param) {
        return this;
    }

    public SQLPredicateBuilder lt(String field, String param) {
        return this;
    }

    public SQLPredicateBuilder gt(String field, String param) {
        return this;
    }

    public SQLPredicateBuilder le(String field, String param) {
        return this;
    }

    public SQLPredicateBuilder ge(String field, String param) {
        return this;
    }

    public SQLPredicateBuilder ne(String field, String param) {
        return this;
    }

    public SQLPredicateBuilder in(String field, String param) {
        return this;
    }

    public SQLPredicateBuilder like(String field, String param) {
        return this;
    }

    public SQLPredicateBuilder btw(String field, String param1, String param2) {
        return this;
    }

    public SQLPredicateBuilder isNull(String field, String param) {
        return this;
    }

    public SQLPredicateBuilder isNotNull(String field, String param) {
        return this;
    }

    public SQLPredicateBuilder brack(SQLPredicateBuilder innerPredicate) {
        return this;
    }
    public SQLPredicateBuilder plus(SQLPredicateBuilder predToAppend) {
        return this;
    }

    public SQLPredicateBuilder custom(String sqlSnippet) {
        return this;
    }

    public SQLPredicateBuilder and() {
        return this;
    }
    public SQLPredicateBuilder or() {
        return this;
    }

    public String toSql() {
        return "";
    }
}
