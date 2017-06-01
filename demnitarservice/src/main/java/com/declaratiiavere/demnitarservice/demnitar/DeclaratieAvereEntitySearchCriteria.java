package com.declaratiiavere.demnitarservice.demnitar;

import com.declaratiiavere.jpaframework.EntitySearchCriteria;

import java.util.Date;

/**
 * Entity search criteria for declaratie_avere table.
 *
 * @author Razvan Dani
 */
public class DeclaratieAvereEntitySearchCriteria extends EntitySearchCriteria {
    private Integer demnitarId;
    private Date dataDeclaratiei;
    private boolean eagerLoadAllRelations;

    public Integer getDemnitarId() {
        return demnitarId;
    }

    public void setDemnitarId(Integer demnitarId) {
        this.demnitarId = demnitarId;
    }

    public Date getDataDeclaratiei() {
        return dataDeclaratiei;
    }

    public void setDataDeclaratiei(Date dataDeclaratiei) {
        this.dataDeclaratiei = dataDeclaratiei;
    }

    public boolean isEagerLoadAllRelations() {
        return eagerLoadAllRelations;
    }

    public void setEagerLoadAllRelations(boolean eagerLoadAllRelations) {
        this.eagerLoadAllRelations = eagerLoadAllRelations;
    }
}
