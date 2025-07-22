package com.ey.advisory.common.sql;

public interface SQLPredicateBuilder {

    SQLPredicateBuilder eq(String field, String param);
    SQLPredicateBuilder lt(String field, String param);
    SQLPredicateBuilder gt(String field, String param);
    SQLPredicateBuilder le(String field, String param);
    SQLPredicateBuilder ge(String field, String param);
    SQLPredicateBuilder ne(String field, String param);

    SQLPredicateBuilder in(String field, String param);

    SQLPredicateBuilder like(String field, String param);

    SQLPredicateBuilder btw(String field, String param1, String param2);

    SQLPredicateBuilder isNull(String field, String param);
    SQLPredicateBuilder isNotNull(String field, String param);

    SQLPredicateBuilder brack(SQLPredicateBuilder innerPredicate);
    SQLPredicateBuilder plus(SQLPredicateBuilder predToAppend);

    SQLPredicateBuilder custom(String sqlSnippet);

    SQLPredicateBuilder and();
    SQLPredicateBuilder or();

    String toSql();

}
