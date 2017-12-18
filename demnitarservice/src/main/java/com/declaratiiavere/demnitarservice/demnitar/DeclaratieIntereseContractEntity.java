package com.declaratiiavere.demnitarservice.demnitar;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by Amalia on 12/15/2017.
 */
@Table(name = "declaratie_interese_contract")
@Entity
public class DeclaratieIntereseContractEntity {
    @Column(name = "id")
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name ="declaratie_interese_id", insertable = false, updatable = false)
    private Integer declaratieIntereseId;

    @Column(name ="titular")
    private String titular;

    @Column(name ="beneficiar")
    private String beneficiar;


    @Column(name ="instritutia_contractanta")
    private String instritutiaContractanta;

    @Column(name ="procedura_incredintare")
    private String procedura;

    @Column(name ="tip_contract")
    private String tipContract;

    @Column(name ="data")
    private String data;

    @Column(name ="durata")
    private String durata;

    @Column(name ="valoare")
    private BigDecimal valoare;

    @Column(name ="moneda")
    private String moneda;

    @Column(name ="explicatie_contract")
    private String explicatieContract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "declaratie_interese_id", nullable = false)
    private DeclaratieIntereseEntity declaratieIntereseEntity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDeclaratieIntereseId() {
        return declaratieIntereseId;
    }

    public void setDeclaratieIntereseId(Integer declaratieIntereseId) {
        this.declaratieIntereseId = declaratieIntereseId;
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

    public DeclaratieIntereseEntity getDeclaratieIntereseEntity() {
        return declaratieIntereseEntity;
    }

    public void setDeclaratieIntereseEntity(DeclaratieIntereseEntity declaratieIntereseEntity) {
        this.declaratieIntereseEntity = declaratieIntereseEntity;
    }
}
