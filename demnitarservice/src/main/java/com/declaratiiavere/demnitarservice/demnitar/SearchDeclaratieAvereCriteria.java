package com.declaratiiavere.demnitarservice.demnitar;

import com.declaratiiavere.common.utils.JsonDateDeserializer;
import com.declaratiiavere.common.utils.JsonDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * Search criteria for revenue declarations.
 *
 * @author Razvan Dani
 */
public class SearchDeclaratieAvereCriteria {
    private Integer demnitarId;
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date dataDeclaratiei;
    private Integer functieId;
    private Integer institutieId;
    private String demnitarNumeLike;
    private String demnitarPrenumeLike;
    private Integer voluntarId;
    private Integer anulDeclaratiei;
    private Integer status; // 1 - Neinceputa, 2 - Inceputa, 3 - Finalizata

    public Integer getDemnitarId() {
        return demnitarId;
    }

    public void setDemnitarId(Integer demnitarId) {
        this.demnitarId = demnitarId;
    }

    public Date getDataDeclaratiei() {
        return dataDeclaratiei;
    }

    @JsonDeserialize(using = JsonDateDeserializer.class)
    public void setDataDeclaratiei(Date dataDeclaratiei) {
        this.dataDeclaratiei = dataDeclaratiei;
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

    public Integer getAnulDeclaratiei() {
        return anulDeclaratiei;
    }

    public void setAnulDeclaratiei(Integer anulDeclaratiei) {
        this.anulDeclaratiei = anulDeclaratiei;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
