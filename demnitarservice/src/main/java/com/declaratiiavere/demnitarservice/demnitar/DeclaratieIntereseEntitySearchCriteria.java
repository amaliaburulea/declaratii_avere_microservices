package com.declaratiiavere.demnitarservice.demnitar;

import com.declaratiiavere.jpaframework.EntitySearchCriteria;

import java.util.Date;

/**
 * Created by Amalia on 11/14/2017.
 */
public class DeclaratieIntereseEntitySearchCriteria extends EntitySearchCriteria {
    private Integer demnitarId;
    private Date dataDeclaratiei;
    private Integer functieId;
    private Integer institutieId;
    private String demnitarNumeLike;
    private String demnitarPrenumeLike;
    private Integer voluntarId;
    private Date startDataDeclaratiei;
    private Date endDataDeclaratiei;
    private Integer status; // 1 - Neinceputa, 2 - Inceputa, 3 - Finalizata
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

    public Integer getFunctieId() {
        return functieId;
    }

    public void setFunctieId(Integer functieId) {
        this.functieId = functieId;
    }

    public Integer getInstitutieId() {
        return institutieId;
    }

    public void setInstitutieId(Integer institutieId) {
        this.institutieId = institutieId;
    }

    public String getDemnitarNumeLike() {
        return demnitarNumeLike;
    }

    public void setDemnitarNumeLike(String demnitarNumeLike) {
        this.demnitarNumeLike = demnitarNumeLike;
    }

    public String getDemnitarPrenumeLike() {
        return demnitarPrenumeLike;
    }

    public void setDemnitarPrenumeLike(String demnitarPrenumeLike) {
        this.demnitarPrenumeLike = demnitarPrenumeLike;
    }

    public Integer getVoluntarId() {
        return voluntarId;
    }

    public void setVoluntarId(Integer voluntarId) {
        this.voluntarId = voluntarId;
    }

    public Date getStartDataDeclaratiei() {
        return startDataDeclaratiei;
    }

    public void setStartDataDeclaratiei(Date startDataDeclaratiei) {
        this.startDataDeclaratiei = startDataDeclaratiei;
    }

    public Date getEndDataDeclaratiei() {
        return endDataDeclaratiei;
    }

    public void setEndDataDeclaratiei(Date endDataDeclaratiei) {
        this.endDataDeclaratiei = endDataDeclaratiei;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
