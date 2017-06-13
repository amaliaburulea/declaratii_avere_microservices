package com.declaratiiavere.demnitarservice.demnitar;

import com.declaratiiavere.jpaframework.EntityAccessObjectBase;
import com.declaratiiavere.jpaframework.JpaQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Entity access object for demnitar table.
 *
 * @author Razvan Dani
 */
@Component
public class DemnitarEAO extends EntityAccessObjectBase {
    /**
     * Finds the demnitars for the specified search criteria.
     *
     * @param searchCriteria    The DemnitarEntitySearchCriteria
     * @return                  The List of DemnitarEntity objects
     */
    public List<DemnitarEntity> findDemnitars(DemnitarEntitySearchCriteria searchCriteria) {
        JpaQueryBuilder queryBuilder = constructFindDemnitarsBuilder(searchCriteria);

        return findEntities(queryBuilder, searchCriteria);
    }

    private JpaQueryBuilder constructFindDemnitarsBuilder(DemnitarEntitySearchCriteria searchCriteria) {
        JpaQueryBuilder queryBuilder = new JpaQueryBuilder("DemnitarEntity", "d");

        if (searchCriteria.getNume() != null) {
            queryBuilder.addCondition("d.nume = :nume");
        }

        if (searchCriteria.getPrenume() != null) {
            queryBuilder.addCondition("d.prenume = :prenume");
        }

        if (searchCriteria.getNumeStartsWith() != null) {
            queryBuilder.addCondition("d.nume LIKE :numeStartsWith");
        }

        if (searchCriteria.getPrenumeStartsWith() != null) {
            queryBuilder.addCondition("d.prenume LIKE :prenumeStartsWith");
        }

        return queryBuilder;
    }

    /**
     * Saves the demnitar entity.
     *
     * @param demnitarEntity    The DemnitarEntity
     * @return              The stored DemnitarEntity
     */
    public DemnitarEntity saveDemnitar(DemnitarEntity demnitarEntity) {
        return storeEntity(demnitarEntity);
    }

    /**
     * Gets the DemnitarEntity for the specified demnitar id.
     *
     * @param demnitarId    The demnitar id
     * @return          The DemnitarEntity
     */
    public DemnitarEntity getDemnitar(Integer demnitarId) {
        return getEntity(DemnitarEntity.class, demnitarId);
    }

    public List<DeclaratieAvereEntity> findDeclaratiiAvere(DeclaratieAvereEntitySearchCriteria searchCriteria) {
        JpaQueryBuilder queryBuilder = constructFindDeclaratieAveresBuilder(searchCriteria);

        return findEntities(queryBuilder, searchCriteria);
    }

    private JpaQueryBuilder constructFindDeclaratieAveresBuilder(DeclaratieAvereEntitySearchCriteria searchCriteria) {
        JpaQueryBuilder queryBuilder = new JpaQueryBuilder("DeclaratieAvereEntity", "d");

        if (searchCriteria.getDemnitarId() != null) {
            queryBuilder.addCondition("d.demnitarId= :demnitarId");
        }

        if (searchCriteria.getDataDeclaratiei() != null) {
            queryBuilder.addCondition("d.dataDeclaratiei = :dataDeclaratiei");
        }

        if (searchCriteria.isEagerLoadAllRelations()) {
            queryBuilder.addInnerFetchJoin("d.demnitarEntity");

            queryBuilder.addLeftFetchJoin("d.declaratieAvereAlteActiveEntitySet");
            queryBuilder.addLeftFetchJoin("d.declaratieAvereBunImobilEntitySet");
            queryBuilder.addLeftFetchJoin("d.declaratieAvereBunMobilEntitySet");
            queryBuilder.addLeftFetchJoin("d.declaratieAvereBijuterieEntitySet");
            queryBuilder.addLeftFetchJoin("d.declaratieAverePlasamentEntitySet");
            queryBuilder.addLeftFetchJoin("d.declaratieAvereDatorieEntitySet");
            queryBuilder.addLeftFetchJoin("d.declaratieAvereContEntitySet");
            queryBuilder.addLeftFetchJoin("d.declaratieAvereBunInstrainatEntitySet");
            queryBuilder.addLeftFetchJoin("d.declaratieAvereCadouEntitySet");
            queryBuilder.addLeftFetchJoin("d.declaratieAvereVenitEntitySet");
        }

        return queryBuilder;
    }

    public DeclaratieAvereEntity saveDeclaratieAvere(DeclaratieAvereEntity declaratieAvereEntity) {
        return storeEntity(declaratieAvereEntity);
    }

    public DeclaratieAvereEntity getDeclaratieAvere(Integer declaratieAvereId) {
        return getEntity(DeclaratieAvereEntity.class, declaratieAvereId);
    }
}
