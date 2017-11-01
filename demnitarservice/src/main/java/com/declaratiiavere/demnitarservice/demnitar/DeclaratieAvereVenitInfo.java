package com.declaratiiavere.demnitarservice.demnitar;

import java.math.BigDecimal;

/**
 * Contains information about declaratie avere venit.
 *
 * @author Razvan Dani
 */
public class DeclaratieAvereVenitInfo {
    private Integer id;
    private Integer tip;
    private String titular;
    private String sursaVenit;
    private String serviciulPrestat;
    private BigDecimal venitAnual;
    private String explicatieVenit;
    private String moneda;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTip() {
        return tip;
    }

    public void setTip(Integer tip) {
        this.tip = tip;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getSursaVenit() {
        return sursaVenit;
    }

    public void setSursaVenit(String sursaVenit) {
        this.sursaVenit = sursaVenit;
    }

    public String getServiciulPrestat() {
        return serviciulPrestat;
    }

    public void setServiciulPrestat(String serviciulPrestat) {
        this.serviciulPrestat = serviciulPrestat;
    }

    public BigDecimal getVenitAnual() {
        return venitAnual;
    }

    public void setVenitAnual(BigDecimal venitAnual) {
        this.venitAnual = venitAnual;
    }

    public String getExplicatieVenit() {
        return explicatieVenit;
    }

    public void setExplicatieVenit(String explicatieVenit) {
        this.explicatieVenit = explicatieVenit;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }
}
