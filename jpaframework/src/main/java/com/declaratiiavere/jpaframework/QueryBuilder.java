package com.declaratiiavere.jpaframework;

/**
 * Utility class that makes building of queries (SQL or JPA) easier. For JPA queries a separate JPaQueryBuilder that
 * extends this class, should be used.
 *
 * @author Razvan Dani
 */
public class QueryBuilder {
    private static final String SELECT_STATEMENT = "SELECT";
    private static final String DELETE_STATEMENT = "DELETE";
    private static final String FROM_STATEMENT = "FROM";
    private static final String WHERE_STATEMENT = "WHERE";
    private static final String GROUP_BY_STATEMENT = "GROUP BY";
    private static final String ORDER_BY_STATEMENT = "ORDER BY";
    private static final String HAVING_STATEMENT = "HAVING";

    private StringBuilder selectStatementBuilder; // initially null as not all queries have select
    private StringBuilder deleteStatementBuilder; // initially null as not all queries have delete
    private StringBuilder fromStatementBuilder = new StringBuilder();
    private StringBuilder whereStatementBuilder; // initially null as not all queries have where
    private StringBuilder groupByStatementBuilder; // initially null as not all queries have group by
    private StringBuilder orderByStatementBuilder; // initially null as not all queries have order by
    private StringBuilder havingStatementBuilder; // initially null as not all queries have "HAVING" statement


    /**
     * Adds the specified String representing a list of fields (E.g. "alias1.field1, .., alias2.field10") to the
     * SELECT part of the query. There should be no commas at the beginning or the end of the string.
     * Multiple calls to addToSelectStatement can be made and the builder will handle putting
     * the fields together.
     *
     * @param selectFields The String representing the select fields.
     */
    public void addSelectStatement(String selectFields) {
        if (selectStatementBuilder == null) {
            selectStatementBuilder = new StringBuilder();
        }

        if (selectStatementBuilder.length() > 0) {
            selectStatementBuilder.append(", ");
        }

        selectStatementBuilder.append(selectFields);
    }

    /**
     * Resets the select statement to an empty value.
     */
    public void resetSelectStatement() {
        selectStatementBuilder = new StringBuilder();
    }

    /**
     * Adds the specified String representing a list of fields (E.g. "alias1.field1, .., alias2.field10") to the
     * DELETE part of the query. There should be no commas at the beginning or the end of the string.
     * Multiple calls to addDeleteStatement can be made and the builder will handle putting
     * the fields together.
     *
     * @param deleteFields The String representing the delete fields.
     */
    public void addDeleteStatement(String deleteFields) {
        if (deleteStatementBuilder == null) {
            deleteStatementBuilder = new StringBuilder();
        }

        if (deleteStatementBuilder.length() > 0) {
            deleteStatementBuilder.append(", ");
        }

        deleteStatementBuilder.append(deleteFields);
    }

    /**
     * Resets the delete statement to an empty value.
     */
    public void resetDeleteStatement() {
        deleteStatementBuilder = new StringBuilder();
    }

    /**
     * Adds the specified String to the FROM part of the query. Two consecutive calls can be made to this method; the
     * first call defines the table and the second call will define the alias (e.g. addFromStatement("languagePair) and
     * addFromStatement(lp) will return "languagePair lp". One part can contain any number of tables and/or joins.
     * There is no need to put spaces at the beginning or the end of the string, the builder takes care of adding
     * necessary spaces for syntax correctness.
     *
     * @param fromStatementPart One part of the FROM statement
     */
    public void addFromStatement(String fromStatementPart) {
        if (fromStatementBuilder.length() > 0) {
            fromStatementBuilder.append(" ");
        }

        fromStatementBuilder.append(fromStatementPart);
    }

    /**
     * Adds the specified condition to the WHERE part of the query. Multiple calls can be made and the builder will
     * concatenate the AND between the conditions automatically, so do not manually add the AND at the beginning or the
     * end of the condition string. Each condition string can be a full expression with as many OR's and AND's as wanted,
     * as long as there is no AND at the beginning and end of the string.
     *
     * @param condition The condition string
     */
    public void addCondition(String condition) {
        if (whereStatementBuilder == null) {
            whereStatementBuilder = new StringBuilder();
        }

        if (whereStatementBuilder.length() > 0) {
            whereStatementBuilder.append(" AND ");
        }

        whereStatementBuilder.append("(").append(condition).append(")");
    }

    /**
     * Adds the specified String representing a list of fields (E.g. "alias1.field1, .., alias2.field10") to the
     * GROUP BY part of the query. There should be no commas at the beginning or the end of the string.
     * Multiple calls to addGroupByStatement can be made and the builder will handle putting
     * the fields together.
     *
     * @param groupByFields The String representing the select fields.
     */
    public void addGroupByStatement(String groupByFields) {
        if (groupByStatementBuilder == null) {
            groupByStatementBuilder = new StringBuilder();
        }

        if (groupByStatementBuilder.length() > 0) {
            groupByStatementBuilder.append(", ");
        }

        groupByStatementBuilder.append(groupByFields);
    }

    /**
     * Adds the specified String representing a list of fields (E.g. "alias1.field1 desc, .., alias2.field10 asc") to the
     * ORDER BY part of the query. There should be no commas at the beginning or the end of the string.
     * Multiple calls to addOrderByStatement can be made and the builder will handle putting
     * the fields together.
     *
     * @param orderByFields The String representing the select fields.
     */
    public void addOrderByStatement(String orderByFields) {
        if (orderByStatementBuilder == null) {
            orderByStatementBuilder = new StringBuilder();
        }

        if (orderByStatementBuilder.length() > 0) {
            orderByStatementBuilder.append(", ");
        }

        orderByStatementBuilder.append(orderByFields);
    }

    /**
     * Adds the specified condition to the HAVING part of the query. Multiple calls can be made and the builder will
     * concatenate the AND between the conditions automatically, so do not manually add the AND at the beginning or the
     * end of the condition string. Each condition string can be a full expression with as many OR's and AND's as wanted,
     * as long as there is no AND at the beginning and end of the string.
     *
     * @param condition The condition string
     */
    public void addConditionToHavingStatement(String condition) {
        if (havingStatementBuilder == null) {
            havingStatementBuilder = new StringBuilder();
        }

        if (havingStatementBuilder.length() > 0) {
            havingStatementBuilder.append(" AND ");
        }

        havingStatementBuilder.append("(").append(condition).append(")");
    }

    /**
     * Returns the full content of the constructed query.
     *
     * @return The query string
     */
    public String getQuery() {
        if ((selectStatementBuilder == null || selectStatementBuilder.length() == 0)
                && (deleteStatementBuilder == null || deleteStatementBuilder.length() == 0)) {
            throw new QueryBuilderException("SELECT/DELETE statement cannot be empty");
        }

        if (fromStatementBuilder.length() == 0) {
            throw new QueryBuilderException("FROM statement cannot be empty");
        }

        if (havingStatementBuilder != null && havingStatementBuilder.length() > 0
                && (groupByStatementBuilder == null || groupByStatementBuilder.length() == 0)) {
            throw new QueryBuilderException("GROUP BY statement cannot be empty when HAVING statement is present");
        }

        StringBuilder query = new StringBuilder();

        if (selectStatementBuilder != null && selectStatementBuilder.length() != 0) {
            query.append(SELECT_STATEMENT).append(" ").append(getSelectStatementBuilder()).append("\n");
        } else {
            query.append(DELETE_STATEMENT).append(" ").append(getDeleteStatementBuilder()).append("\n");
        }

        query.append(FROM_STATEMENT).append(" ").append(fromStatementBuilder).append("\n");

        if (whereStatementBuilder != null && whereStatementBuilder.length() > 0) {
            query.append(WHERE_STATEMENT).append(" ").append(whereStatementBuilder).append("\n");
        }

        if (groupByStatementBuilder != null && groupByStatementBuilder.length() > 0) {
            query.append(GROUP_BY_STATEMENT).append(" ").append(groupByStatementBuilder).append("\n");
        }

        if (orderByStatementBuilder != null && orderByStatementBuilder.length() > 0) {
            query.append(ORDER_BY_STATEMENT).append(" ").append(orderByStatementBuilder).append("\n");
        }

        if (havingStatementBuilder != null && havingStatementBuilder.length() > 0) {
            query.append(HAVING_STATEMENT).append(" ").append(havingStatementBuilder).append("\n");
        }

        return query.toString();
    }

    public boolean conditionsExist() {
        return (whereStatementBuilder != null && whereStatementBuilder.length() > 0);
    }

    /**
     * Returns the StringBuilder for the SELECT statement, made protected so that subclasses can override it if need be.
     *
     * @return The StringBuilder for the SELECT statement
     */
    protected StringBuilder getSelectStatementBuilder() {
        return selectStatementBuilder;
    }

    /**
     * Returns the StringBuilder for the DELETE statement, made protected so that subclasses can override it if need be.
     *
     * @return The StringBuilder for the DELETE statement
     */
    protected StringBuilder getDeleteStatementBuilder() {
        return deleteStatementBuilder;
    }
}
