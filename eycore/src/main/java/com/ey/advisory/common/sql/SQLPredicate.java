package com.ey.advisory.common.sql;

import java.util.Map;
import java.util.Objects;

public abstract class SQLPredicate extends SQLElement {

    /**
     * This represents the table field on which this predicate applies to.
     * Usually a where clause in an SQL consists of several individual
     * predicates separated by 'and' or 'or' operators. each predicate will
     * be a condition based on a single field. This data member represents a
     * field of a table/view on which the predicate is based on.
     */
    protected String field;

    public SQLPredicate(String field) {
        this.field = Objects.requireNonNull(field);
    }

    /**
     * Usually, while constructing a dynamic SQL statement in an application,
     * a predicate will be generated only if there are user provided values to
     * be operated on the field using an operator. For example, an '=' condition
     * will be generated in an SQL, for a field, only if there is a user input
     * to compare with. Otherwise the predicate will be ignored. The 'paramMap'
     * of this method consists of all the parameter names and the corresponding
     * user provided values, if any. The sql predicate is valid, only if the
     * paramMap contains a paramName associated with a non-null value (i.e. only
     * if a user provided value exists to operate on the field associated with
     * this predicate). This is an abstract function, as there can be multiple
     * parameters and values associated with certain predicates. Each predicate
     * class is responsible to use the paramMap and evaluate whether the
     * instance is valid.
     *
     * @param paramMap The parameter names and the corresponding user provided
     *                 values.
     * @return true if the predicate is valid, based on the rules mentioned
     *         above; otherwise false.
     */
    public abstract boolean isValid(Map<String, Object> paramMap);
}
