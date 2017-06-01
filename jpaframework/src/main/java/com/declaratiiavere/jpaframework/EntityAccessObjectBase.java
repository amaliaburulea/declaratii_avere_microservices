package com.declaratiiavere.jpaframework;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base class for all entity access objects. It provides several internal
 * methods that are supposed to be used only by the framework, as well the
 * following methods that can be used by subclasses:
 * <p/>
 * storeEntity - inserts or updates an Entity removeEntity - removes an entity
 * getEntity - find an Entity by its primary key findEntities - executes a JPA
 * query and returns the results executeBulkOperation - executes a bulk JPA
 * UPDATE or DELETE createQuery - creates and returns a JPA Query, should be
 * used directly only when doing bulk upate or remove operations or when
 * constructing and executing "special" finder queries that don't fit into what
 * findEntities and getEntity offers
 * <p/>
 * More detailed description is available for this methods in their respective
 * JavaDoc's.
 *
 * @author Razvan Dani
 */
public abstract class EntityAccessObjectBase {
    private static final Pattern STATEMENT_PARAM_END_PATTERN = Pattern
            .compile("[^A-Za-z0-9_]");

    // thread local for master db entity manager
    private static final ThreadLocal<EntityManager> entityManagerThreadLocal = new ThreadLocal<>();

    private static ConcurrentHashMap<String, Field> idFieldByEntityClassNameMap = new ConcurrentHashMap<>();

    /**
     * Sets the specified EntityManager into a ThreadLocal so
     * that it can be access by the current thread. It should not be used
     * directly by subclasses or any other classes but those from the framework.
     *
     * @param entityManager The EntityManager for master db
     */
    public static void setEntityManagerForThread(
            EntityManager entityManager) {
        entityManagerThreadLocal.set(entityManager);
    }

    /**
     * Stores into the database the information contained in the given entity,
     * performing either an insert or an update depending on the presense of the
     * primaryKey attribute of the entity (insert when primaryKey is null,
     * update otherwise).
     *
     * @param entity
     *            The entity to be stored
     * @return The entity containing the stored information which in case of
     *         inserts with identity keys, contains the value generated for the
     *         primaryKey.
     */
    protected <T extends EntityBase> T storeEntity(T entity) {
        return storeEntity(entity, false);
    }

    /**
     * Stores into the database the information contained in the given entity,
     * performing either an insert or an update depending on the presense of the
     * primaryKey attribute of the entity (insert when primaryKey is null,
     * update otherwise).
     * <p/>
     * When flushAndRefresh is set to true, the operations in the current
     * EntityManager's context are flushed (all pending SQL commands sent to the
     * database) and the entity is refreshed from the database, such that it
     * reflects any updates made to the underlying database record from outside
     * the application (via database triggers and/or database field default
     * values). The flushAndRefresh should be set to true strictly when such a
     * case exist because it may reduce performance. Furthermore triggers that
     * update records and database default fields should be avoided altogether
     * whenever possible.
     *
     * @param entity
     *            The entity to be stored
     * @param flushAndRefresh
     *            Indicates if a flush and refresh should be performed.
     * @return The entity containing the stored information which in case of
     *         inserts with identity keys, contains the value generated for the
     *         primaryKey.
     */
    protected <T extends EntityBase> T storeEntity(T entity,
                                                   boolean flushAndRefresh) {
        T storedEntity;

        // store entity
        Object idFieldValue = getIdFieldValueForEntity(entity);

        if (idFieldValue == null) {
            storedEntity = persistEntity(entity, flushAndRefresh);
        } else {
            storedEntity = mergeEntity(entity, flushAndRefresh);
        }

        return storedEntity;
    }

    private <T extends EntityBase> void flushAndRefreshEntity(T entity) {
        EntityManager entityManager = getEntityManager();

        entityManager.flush();
        entityManager.refresh(entity);

    }

    /**
     * Merges (updates) the specified entity.
     *
     * @param entity
     *            The entity
     * @return The merged entity
     */
    protected <T extends EntityBase> T mergeEntity(T entity) {
        return mergeEntity(entity, false);
    }

    /**
     * Merges (updates) the specified entity.
     *
     * @param entity
     *            The entity
     * @param flushAndRefresh
     *            Indicates if to flush the changes and refresh the entity
     * @return The merged entity
     */
    protected <T extends EntityBase> T mergeEntity(T entity,
                                                   boolean flushAndRefresh) {
        EntityManager entityManager = getEntityManager();
        T storedEntity=null;
        try{
            storedEntity = entityManager.merge(entity);
        }catch(Exception e ){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
        if (flushAndRefresh) {
            flushAndRefreshEntity(entity);
        }

        return storedEntity;
    }

    /**
     * Persists (inserts) the specified entity.
     *
     * @param entity
     *            The entity
     * @return The persisted entity
     */
    protected <T extends EntityBase> T persistEntity(T entity) {
        return persistEntity(entity, false);
    }

    /**
     * Persists (inserts) the specified entity.
     *
     * @param entity
     *            The entity
     * @param flushAndRefresh
     *            Indicates if to flush the changes and refresh the entity
     * @return The persisted entity
     */
    protected <T extends EntityBase> T persistEntity(T entity,
                                                     boolean flushAndRefresh) {
        EntityManager entityManager = getEntityManager();
        try {
            entityManager.persist(entity);

        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }

        if (flushAndRefresh) {
            flushAndRefreshEntity(entity);
        }
        return entity;
    }

    /**
     * Returns the value of the field that has the @Id annotation for the
     * specified entity.
     *
     * @param entity
     *            The entity
     * @return The value of the id field
     */
    private Object getIdFieldValueForEntity(EntityBase entity) {

        Class entityClass = entity.getClass();
        Field idField = idFieldByEntityClassNameMap.get(entityClass.getName());

        if (idField == null) {
            for (Field field : entityClass.getDeclaredFields()) {
                if (field.getAnnotation(Id.class) != null) {
                    idField = field;
                    idField.setAccessible(true);
                    break;
                }
            }

            if (idField != null) {
                idFieldByEntityClassNameMap.put(entityClass.getName(), idField);
            } else {
                throw new IllegalStateException(
                        "Entity class "
                                + entityClass.getName()
                                + " does not have ID annotation defined for any field and it cannot be saved");
            }
        }

        try {
            return idField.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes from the database the record represented by the given
     * entityPrimaryKey for the entity represented by the given class.
     * <p/>
     * When flush is set to true, the operations in the current EntityManager's
     * context are flushed (all pending SQL commands sent to the database).
     *
     * @param entityClass
     *            The class of the entity
     * @param entityPrimaryKey
     *            The entity primary key
     * @param flush
     *            Indicates if a flush should be performed.
     */
    protected void removeEntity(Class<? extends EntityBase> entityClass,
                                Object entityPrimaryKey, boolean flush) {
        EntityManager entityManager = getEntityManager();
        EntityBase entity = getEntity(entityClass, entityPrimaryKey);

        entityManager.remove(entity);

        if (flush) {
            entityManager.flush();
        }
    }

    /**
     * Removes from the database the record represented by the given
     * entityPrimaryKey for the entity represented by the given class.
     *
     * @param entityClass
     *            The class of the entity
     * @param entityPrimaryKey
     *            The entity primary key
     */
    protected void removeEntity(Class<? extends EntityBase> entityClass,
                                Object entityPrimaryKey) {
        removeEntity(entityClass, entityPrimaryKey, false);
    }

    /**
     * Finds and returns the entity of the given class type, by the specified
     * primary key, or null if no entity is found.
     *
     * @param entityClass
     *            The Class of the entity to be found
     * @param entityPrimaryKey
     *            The entity's primary key
     * @return The entity with the given primary key
     */

    protected <T extends EntityBase> T getEntity(Class<T> entityClass,
                                                 Object entityPrimaryKey) {
        T entity = null;

        if (entityPrimaryKey != null) {
            entity = getEntityManager().find(entityClass, entityPrimaryKey,
                    LockModeType.PESSIMISTIC_READ);
        }

        return entity;
    }

    /**
     * Finds and returns the List of transfer objects for the given JPA query
     * builder and searchCriteria. The query must use named parameters and each
     * of the parameter names from the query must match an attribute name in the
     * searchCriteria. In addition the searchCriteria can contain any other
     * attributes not directly bound to the query parameter names, for example
     * booleans for controlling fetch joins or anything else needed for
     * constructing the queryString before calling this method.
     *
     * @param jpaQueryBuilder
     *            The JPA query builder
     * @param searchCriteria
     *            The EntitySearchCriteria
     * @return The List of objects or empty list (NOT null) if nothing is found
     */
    protected <T> List<T> findObjects(JpaQueryBuilder jpaQueryBuilder,
                                      EntitySearchCriteria searchCriteria) {
        Query query = createQuery(jpaQueryBuilder.getQuery(), searchCriteria);

        return new ArrayList<>(new LinkedHashSet<T>(query.getResultList()));
    }

    /**
     * Finds and returns the List of Entities for the given JPA query builder
     * and searchCriteria. The queryString must use named parameters and each of
     * the parameter names from the query must match an attribute name in the
     * searchCriteria. In addition the searchCriteria can contain any other
     * attributes not directly bound to the query parameter names, for example
     * booleans for controlling fetch joins or anything else needed for
     * constructing the queryString before calling this method.
     * <p/>
     *
     * @param jpaQueryBuilder
     *            The JPA query builder
     * @param searchCriteria
     *            The EntitySearchCriteria
     * @return The List of entities found or empty list (NOT null) if nothing is
     *         found
     */
    protected <T extends EntityBase> List<T> findEntities(
            JpaQueryBuilder jpaQueryBuilder, EntitySearchCriteria searchCriteria) {
        Query query = createQuery(jpaQueryBuilder.getQuery(), searchCriteria);
        List<T> e = null;
        try{
            e = new ArrayList<>(new LinkedHashSet<T>(query.getResultList()));
        }catch(Exception ex){
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage(), ex);
        }
        return e;
    }

    /**
     * Finds and returns the List of Entities for the given JPA query builder
     * and no search criteria
     *
     * @param jpaQueryBuilder
     *            The JPA query builder
     * @return The list of entities returned by the JPA query.
     */
    protected <T extends EntityBase> List<T> findEntities(
            JpaQueryBuilder jpaQueryBuilder) {
        return findEntities(jpaQueryBuilder, null);
    }

    /**
     * Creates and returns JPA Query object for the given queryString. Should be
     * used directly only when doing bulk update or remove operations or when
     * constructing and executing "special" finder queries that don't fit into
     * what findEntities and getEntity offers.
     *
     * @param queryString
     *            The JPA query string
     * @return The JPA Query object
     */
    protected Query createQuery(String queryString) {
        return createQuery(queryString, null);
    }

    /**
     * Creates and returns JPA Query object for the given queryString and
     * searchCriteria.
     *
     * @param queryString
     *            The JPA query string
     * @param searchCriteria
     *            The search criteria from which the parameter values should be
     *            populated.
     * @return The JPA Query object
     */
    private Query createQuery(String queryString,
                              EntitySearchCriteria searchCriteria) {
        Query query;

        try {
            EntityManager entityManager = getEntityManager();
            query = entityManager.createQuery(queryString);

            try {
                query.setLockMode(LockModeType.PESSIMISTIC_READ);
            } catch (Exception ignore) {
            }

            if (searchCriteria != null
                    && searchCriteria.getMaxResults() != null) {
                query.setMaxResults(searchCriteria.getMaxResults());
            }

            if (searchCriteria != null
                    && searchCriteria.getStartResultIndex() != null) {
                query.setFirstResult(searchCriteria.getStartResultIndex());
            }

            if (searchCriteria != null) {
                Class searchCriteriaClass = searchCriteria.getClass();
                int parameterIndex = -1;
                int parameterEndIndex;

                while ((parameterIndex = queryString.indexOf(":",
                        parameterIndex + 1)) > -1) {
                    Matcher matcher = STATEMENT_PARAM_END_PATTERN
                            .matcher(queryString);

                    if (matcher.find(parameterIndex + 1)) {
                        parameterEndIndex = matcher.start();
                        String parameterName = queryString.substring(
                                parameterIndex + 1, parameterEndIndex);
                        Method parameterGetterMethod = searchCriteriaClass
                                .getMethod("get"
                                        + parameterName.substring(0, 1)
                                        .toUpperCase()
                                        + parameterName.substring(1));
                        Object parameterValue = parameterGetterMethod
                                .invoke(searchCriteria);
                        query.setParameter(parameterName, parameterValue);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return query;
    }

    /**
     * Returns the EntityManager associated with the current
     * thread.
     *
     * @return The EntityManager
     */
    public static EntityManager getEntityManager() {
        return entityManagerThreadLocal.get();
    }
}
