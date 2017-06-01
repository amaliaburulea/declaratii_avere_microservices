package com.declaratiiavere.demnitarservice.demnitar;

import java.math.BigDecimal;

public class DeclaratieAvereBijuterieInfo {
    private Integer id;
    private String descriere;
    private String anDobandire;
    private BigDecimal valoareEstimate;
    private String moneda;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public String getAnDobandire() {
        return anDobandire;
    }

    public void setAnDobandire(String anDobandire) {
        this.anDobandire = anDobandire;
    }

    public BigDecimal getValoareEstimate() {
        return valoareEstimate;
    }

    public void setValoareEstimate(BigDecimal valoareEstimate) {
        this.valoareEstimate = valoareEstimate;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }
}
