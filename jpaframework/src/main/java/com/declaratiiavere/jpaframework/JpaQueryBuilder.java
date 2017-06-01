package com.declaratiiavere.jpaframework;

import java.util.List;

/**
 * Utility class that is used for building JPA query strings, making the process easier and safer. It handles
 * through the different constructors, building of both standard one entity query (where info from the whole entity
 * and its relationships are fetched) as well as JPA constructor queries.
 *
 * @author Razvan Dani
 */
public class JpaQueryBuilder extends QueryBuilder {
    private Class constructorQueryClass; // in case of constructor queries, the class of the result
    private String entityAlias; // in case of sandard JPA queries, the alias of the entity

    private static final String NEW = "NEW";
    private static final String JOIN_FETCH = "JOIN FETCH";
    private static final String LEFT_JOIN_FETCH = "LEFT JOIN FETCH";

    /**
     * Constructs a builder that handles JPA constructor queries, i.e. queries that contains the construct
     * SELECT NEW packagename.classname(list_of_fields). The resultClass represents the class that will be used
     * in the query.
     *
     * @param constructorQueryClass The Class used in the constructor query
     */
    public JpaQueryBuilder(Class constructorQueryClass) {
        this.constructorQueryClass = constructorQueryClass;
    }

    /**
     * Constructs a builder used for standard JPA queries where information is fetched from a single entity (and any
     * of its relations).
     *
     * @param entityName  The name of the JPA entity
     * @param entityAlias The alias of the entity, as it appears in the SELECT statement (and in WHERE, GROUP BY, ORDER BY and
     *                    HAVING statements)
     */
    public JpaQueryBuilder(String entityName, String entityAlias) {
        this.entityAlias = entityAlias;

        addSelectStatement(entityAlias);
        addFromStatement(entityName);
        addFromStatement(" ");
        addFromStatement(entityAlias);
    }

    public JpaQueryBuilder(String statement) {

    }

    /**
     * Adds an (inner) JOIN FETCH to the FROM statement for the specified join field. Fetch joins are very useful for
     * improving performance of JPA queries as it ensures that the relation specified by the joinField is loaded in
     * the same select and avoid the 1+N issue. Explicit fetch joins are recommended rather than marking the relations
     * as EAGER as in this way only those relations can be fetched that are actually needed for the particualar query.
     * <p/>
     * When adding an inner fetch join, the entity on the left side of the relation will be loaded only if the entity on
     * the right side of the relation is present.
     * <p/>
     * The method addLeftFetchJoin should be used instead of this method when the entity on the "left side" of the join should be
     * loaded regardless if the right side of the relation is present or not.
     *
     * @param joinField The join field
     */
    public void addInnerFetchJoin(String joinField) {
        addFromStatement(JOIN_FETCH);
        addFromStatement(" ");
        addFromStatement(joinField);
        addFromStatement("\n");
    }

    /**
     * Adds an LEFT JOIN FETCH to the FROM statement for the specified join field. Left join fetches are very useful for
     * improving performance of JPA queries as it ensures that the relation specified by the joinField is loaded in the
     * same select and avoids the 1+N issue. Explicit left join fetch is recommended rather than marking the relations as
     * EAGER as in this way only those relations can be fetched that are actually needed for the particular query.
     * <p/>
     * When adding a left fetch join, the entity on the left side of the relation will be loaded regardless if the entity on
     * the right side of the relation is present or not.
     * <p/>
     * The method addInnerFetchJoin should be used instead of this method when the entity on the "left side" of the join should be
     * loaded only if the entity on right side of the relation is present.
     *
     * @param joinField The join field
     */
    public void addLeftFetchJoin(String joinField) {
        addFromStatement(LEFT_JOIN_FETCH);
        addFromStatement(" ");
        addFromStatement(joinField);
        addFromStatement("\n");
    }


    /**
     * Overrides default implementation from superclass to handle the JPA constructor queries, when applicable.
     *
     * @return The StringBuilder for the SELLECT part
     */
    protected StringBuilder getSelectStatementBuilder() {
        StringBuilder selectStatementBuilder = null;

        if (super.getSelectStatementBuilder() != null) {
            if (constructorQueryClass != null) {
                selectStatementBuilder = new StringBuilder(super.getSelectStatementBuilder());
                selectStatementBuilder.insert(0, NEW + " " + constructorQueryClass.getName() + "(");
                selectStatementBuilder.append(")");
            } else {
                selectStatementBuilder = super.getSelectStatementBuilder();
            }
        }

        return selectStatementBuilder;
    }

    /**
     * Adds the order by statements to the query. The fields are added in order by statements in the same order that order by field infos
     * are present in the list.
     *
     * @param orderByInfoList The order by field infos list for the ORDER BY part of the select statement
     */
    public void addOrderByStatements(List<OrderByInfo> orderByInfoList) {
        if (orderByInfoList != null && !orderByInfoList.isEmpty()) {
            String entityAliasPrefix = (entityAlias != null ? entityAlias + "." : "");

            for (OrderByInfo orderByInfo : orderByInfoList) {
                if(orderByInfo.getFieldName().contains(".")){
                    addOrderByStatement(orderByInfo.getFieldName() + " " + orderByInfo.getOrderType().getText());
                }
                else{
                    addOrderByStatement(entityAliasPrefix + orderByInfo.getFieldName() + " " + orderByInfo.getOrderType().getText());
                }
            }
        }
    }
}
