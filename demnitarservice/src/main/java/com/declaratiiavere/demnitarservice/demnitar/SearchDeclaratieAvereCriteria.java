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
}
