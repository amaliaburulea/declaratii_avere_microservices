package com.declaratiiavere.demnitarservice.demnitar;

import java.math.BigDecimal;

/**
 * Created by razvan.dani on 21.05.2017.
 */
public class DeclaratieAvereDatorieInfo {
    private Integer id;
    private String creditor;
    private String anContractare;
    private String scadenta;
    private BigDecimal valoare;
    private String moneda;

    public String getExplicatie() {
        return explicatie;
    }

    public void setExplicatie(String explicatie) {
        this.explicatie = explicatie;
    }

    private String explicatie;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreditor() {
        return creditor;
    }

    public void setCreditor(String creditor) {
        this.creditor = creditor;
    }

    public String getAnContractare() {
        return anContractare;
    }

    public void setAnContractare(String anContractare) {
        this.anContractare = anContractare;
    }

    public String getScadenta() {
        return scadenta;
    }

    public void setScadenta(String scadenta) {
        this.scadenta = scadenta;
    }

    public BigDecimal getValoare() {
        return valoare;
    }

    public void setValoare(BigDecimal valoare) {
        this.valoare = valoare;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }
}
