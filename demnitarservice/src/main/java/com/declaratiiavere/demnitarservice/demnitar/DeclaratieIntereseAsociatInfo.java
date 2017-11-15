package com.declaratiiavere.demnitarservice.demnitar;

import java.math.BigDecimal;

/**
 * Created by Amalia on 11/14/2017.
 */
public class DeclaratieIntereseAsociatInfo {
    private Integer id;
    private String unitatea;
    private String rolul;
    private String partiSociale;
    private String valoare;
    private String explicatie;
    private String moneda;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUnitatea() {
        return unitatea;
    }

    public void setUnitatea(String unitatea) {
        this.unitatea = unitatea;
    }

    public String getRolul() {
        return rolul;
    }

    public void setRolul(String rolul) {
        this.rolul = rolul;
    }

    public String getPartiSociale() {
        return partiSociale;
    }

    public void setPartiSociale(String partiSociale) {
        this.partiSociale = partiSociale;
    }

    public String getValoare() {
        return valoare;
    }

    public void setValoare(String valoare) {
        this.valoare = valoare;
    }

    public String getExplicatie() {
        return explicatie;
    }

    public void setExplicatie(String explicatie) {
        this.explicatie = explicatie;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }
}
