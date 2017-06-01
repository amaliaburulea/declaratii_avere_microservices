package com.declaratiiavere.jpaframework;

import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

/**
 * Repository for entities.
 *
 * @author Razvan Dani
 */
public interface EntityRepository extends CrudRepository<EntityBase, Serializable> {
}
