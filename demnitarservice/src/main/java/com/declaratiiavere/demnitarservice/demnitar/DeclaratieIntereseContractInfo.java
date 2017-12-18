package com.declaratiiavere.demnitarservice.demnitar;
import java.math.BigDecimal;

/**
 * Created by Amalia on 12/15/2017.
 */
public class DeclaratieIntereseContractInfo {
    private Integer id;
    private String titular;
    private String beneficiar;
    private String instritutiaContractanta;
    private String procedura;
    private String tipContract;
    private String data;
    private String durata;
    private BigDecimal valoare;
    private String moneda;
    private String explicatieContract;

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

    public String getBeneficiar() {
        return beneficiar;
    }

    public void setBeneficiar(String beneficiar) {
        this.beneficiar = beneficiar;
    }

    public String getInstritutiaContractanta() {
        return instritutiaContractanta;
    }

    public void setInstritutiaContractanta(String instritutiaContractanta) {
        this.instritutiaContractanta = instritutiaContractanta;
    }

    public String getProcedura() {
        return procedura;
    }

    public void setProcedura(String procedura) {
        this.procedura = procedura;
    }

    public String getTipContract() {
        return tipContract;
    }

    public void setTipContract(String tipContract) {
        this.tipContract = tipContract;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDurata() {
        return durata;
    }

    public void setDurata(String durata) {
        this.durata = durata;
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

    public String getExplicatieContract() {
        return explicatieContract;
    }

    public void setExplicatieContract(String explicatieContract) {
        this.explicatieContract = explicatieContract;
    }
}
