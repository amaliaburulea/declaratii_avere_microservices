package com.declaratiiavere.demnitarservice.demnitar;

import java.math.BigDecimal;

/**
 * Contains information about declaratie avere cont.
 *
 * @author Razvan Dani
 */
public class DeclaratieAvereContInfo {
    private Integer id;
    private String titular;
    private String institutieBancara;
    private Integer tipCont;
    private BigDecimal soldCont;
    private String moneda;
    private String anDeschidereCont;

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

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getInstitutieBancara() {
        return institutieBancara;
    }

    public void setInstitutieBancara(String institutieBancara) {
        this.institutieBancara = institutieBancara;
    }

    public Integer getTipCont() {
        return tipCont;
    }

    public void setTipCont(Integer tipCont) {
        this.tipCont = tipCont;
    }

    public BigDecimal getSoldCont() {
        return soldCont;
    }

    public void setSoldCont(BigDecimal soldCont) {
        this.soldCont = soldCont;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getAnDeschidereCont() {
        return anDeschidereCont;
    }

    public void setAnDeschidereCont(String anDeschidereCont) {
        this.anDeschidereCont = anDeschidereCont;
    }
}
