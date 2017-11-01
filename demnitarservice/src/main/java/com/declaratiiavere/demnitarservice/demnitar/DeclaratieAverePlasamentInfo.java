package com.declaratiiavere.demnitarservice.demnitar;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * Contains information about declaratie avere plasament.
 *
 * @author Razvan Dani
 */
public class DeclaratieAverePlasamentInfo {
    private Integer id;
    private String titular;
    private String emitentTitlu;
    private Integer tipulPlasamentului;
    private String numarTitluriSauCotaParte;
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

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getEmitentTitlu() {
        return emitentTitlu;
    }

    public void setEmitentTitlu(String emitentTitlu) {
        this.emitentTitlu = emitentTitlu;
    }

    public Integer getTipulPlasamentului() {
        return tipulPlasamentului;
    }

    public void setTipulPlasamentului(Integer tipulPlasamentului) {
        this.tipulPlasamentului = tipulPlasamentului;
    }

    public String getNumarTitluriSauCotaParte() {
        return numarTitluriSauCotaParte;
    }

    public void setNumarTitluriSauCotaParte(String numarTitluriSauCotaParte) {
        this.numarTitluriSauCotaParte = numarTitluriSauCotaParte;
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
