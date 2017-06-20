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
    private static final Integer STATUS_NEINCEPUT = 1;
    private static final Integer STATUS_INCEPUT = 2;
    private static final Integer STATUS_FINALIZAT = 3;

    /**
     * Finds the demnitars for the specified search criteria.
     *
     * @param searchCriteria The DemnitarEntitySearchCriteria
     * @return The List of DemnitarEntity objects
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

        if (searchCriteria.isEagerLoadAllRelations()) {
            queryBuilder.addLeftFetchJoin("d.functieEntity f");
            queryBuilder.addLeftFetchJoin("d.functie2Entity f2");
            queryBuilder.addLeftFetchJoin("d.institutieEntity i");
            queryBuilder.addLeftFetchJoin("d.institutie2Entity i2");

            if (searchCriteria.getFunctieId() != null) {
                queryBuilder.addCondition("f.id = :functieId");
            }

            if (searchCriteria.getInstitutieId() != null) {
                queryBuilder.addCondition("i.id = :institutieId");
            }
        }

        return queryBuilder;
    }

    public InstitutieEntity getInstitutieByNume(String nume) {
        InstitutieEntitySearchCriteria institutieEntitySearchCriteria = new InstitutieEntitySearchCriteria();
        institutieEntitySearchCriteria.setNume(nume);

        List<InstitutieEntity> institutieEntityList = findInstitutii(institutieEntitySearchCriteria);

        return institutieEntityList.size() == 1 ? institutieEntityList.get(0) : null;
    }

    public List<InstitutieEntity> findInstitutii(InstitutieEntitySearchCriteria searchCriteria) {
        JpaQueryBuilder queryBuilder = constructFindInstitutiesBuilder(searchCriteria);

        return findEntities(queryBuilder, searchCriteria);
    }

    private JpaQueryBuilder constructFindInstitutiesBuilder(InstitutieEntitySearchCriteria searchCriteria) {
        JpaQueryBuilder queryBuilder = new JpaQueryBuilder("InstitutieEntity", "i");

        if (searchCriteria.getNume() != null) {
            queryBuilder.addCondition("i.nume = :nume");
        }

        return queryBuilder;
    }

    public FunctieEntity getFunctieByNume(String nume) {
        FunctieEntitySearchCriteria functieEntitySearchCriteria = new FunctieEntitySearchCriteria();
        functieEntitySearchCriteria.setNume(nume);

        List<FunctieEntity> functieEntityList = findFunctii(functieEntitySearchCriteria);

        return functieEntityList.size() == 1 ? functieEntityList.get(0) : null;
    }

    public List<FunctieEntity> findFunctii(FunctieEntitySearchCriteria searchCriteria) {
        JpaQueryBuilder queryBuilder = constructFindFunctiesBuilder(searchCriteria);

        return findEntities(queryBuilder, searchCriteria);
    }

    private JpaQueryBuilder constructFindFunctiesBuilder(FunctieEntitySearchCriteria searchCriteria) {
        JpaQueryBuilder queryBuilder = new JpaQueryBuilder("FunctieEntity", "f");

        if (searchCriteria.getNume() != null) {
            queryBuilder.addCondition("f.nume = :nume");
        }

        return queryBuilder;
    }

    /**
     * Saves the demnitar entity.
     *
     * @param demnitarEntity The DemnitarEntity
     * @return The stored DemnitarEntity
     */
    public DemnitarEntity saveDemnitar(DemnitarEntity demnitarEntity) {
        return storeEntity(demnitarEntity);
    }

    public FunctieEntity saveFunctie(FunctieEntity functieEntity) {
        return storeEntity(functieEntity);
    }

    public InstitutieEntity saveInstitutie(InstitutieEntity institutieEntity) {
        return storeEntity(institutieEntity);
    }

    /**
     * Gets the DemnitarEntity for the specified demnitar id.
     *
     * @param demnitarId The demnitar id
     * @return The DemnitarEntity
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

        queryBuilder.addInnerFetchJoin("d.demnitarEntity de");
        queryBuilder.addLeftFetchJoin("d.functieEntity f");
        queryBuilder.addLeftFetchJoin("d.functie2Entity f2");
        queryBuilder.addLeftFetchJoin("d.institutieEntity i");
        queryBuilder.addLeftFetchJoin("d.institutie2Entity i2");

        if (searchCriteria.isEagerLoadAllRelations()) {
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

        if (searchCriteria.getFunctieId() != null) {
            queryBuilder.addCondition("f.id = :functieId");
        }

        if (searchCriteria.getInstitutieId() != null) {
            queryBuilder.addCondition("i.id = :institutieId");
        }

        if (searchCriteria.getDemnitarNumeLike() != null) {
            queryBuilder.addCondition("de.nume LIKE :demnitarNumeLike");
        }

        if (searchCriteria.getDemnitarPrenumeLike() != null) {
            queryBuilder.addCondition("de.prenume LIKE :demnitarPrenumeLike");
        }

        if (searchCriteria.getVoluntarId() != null) {
            queryBuilder.addCondition("d.voluntarId = :voluntarId");
        }

        if (searchCriteria.getStartDataDeclaratiei() != null && searchCriteria.getEndDataDeclaratiei() != null) {
            queryBuilder.addCondition("d.dataDeclaratiei BETWEEN :startDataDeclaratiei AND :endDataDeclaratiei");
        }

        if (STATUS_NEINCEPUT.equals(searchCriteria.getStatus())) {
            queryBuilder.addCondition("d.voluntarId IS NULL AND d.isDone = false");
        } else if (STATUS_INCEPUT.equals(searchCriteria.getStatus())) {
            queryBuilder.addCondition("d.voluntarId IS NOT NULL AND d.isDone = false");
        } else if (STATUS_FINALIZAT.equals(searchCriteria.getStatus())) {
            queryBuilder.addCondition("d.isDone = true");
        }

        if (searchCriteria.getOrderByInfoList() != null) {
            queryBuilder.addOrderByStatements(searchCriteria.getOrderByInfoList());
        }

        return queryBuilder;
    }

    public DeclaratieAvereEntity saveDeclaratieAvere(DeclaratieAvereEntity declaratieAvereEntity) {
        return storeEntity(declaratieAvereEntity);
    }

    public DeclaratieAvereEntity getDeclaratieAvere(Integer declaratieAvereId) {
        return getEntity(DeclaratieAvereEntity.class, declaratieAvereId);
    }

    public FunctieEntity getFunctie(Integer functieId) {
        return getEntity(FunctieEntity.class, functieId);
    }

    public InstitutieEntity getInstitutie(Integer institutieId) {
        return getEntity(InstitutieEntity.class, institutieId);
    }
}
