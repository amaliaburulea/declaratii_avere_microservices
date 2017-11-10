package com.declaratiiavere.demnitarservice.demnitar;

import java.math.BigDecimal;

/**
 * Contains information about declaratie avere cadou.
 *
 * @author Razvan Dani
 */
public class DeclaratieAvereCadouInfo {
    private Integer id;
    private String titular;
    private String sursaVenit;
    private String serviciulPrestat;
    private BigDecimal venit;
    private String explicatie;
    private String moneda;

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

    public BigDecimal getVenit() {
        return venit;
    }

    public void setVenit(BigDecimal venit) {
        this.venit = venit;
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
