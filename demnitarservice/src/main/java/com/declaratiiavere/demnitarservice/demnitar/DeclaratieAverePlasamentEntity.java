package com.declaratiiavere.demnitarservice.demnitar;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * JPA entity for declaratie_avere_plasament table.
 *
 * @author Razvan Dani
 */
@Table(name = "declaratie_avere_plasament")
@Entity
public class DeclaratieAverePlasamentEntity {
    @Column(name = "id")
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name ="declaratie_avere_id", insertable = false, updatable = false)
    private Integer declaratieAvereId;

    @Column(name ="titular")
    private String titular;

    @Column(name ="emitent_titlu")
    private String emitentTitlu;

    @Column(name ="tipul_plasamentului")
    private Integer tipulPlasamentului;

    @Column(name ="numar_titluri_sau_cota_parte")
    private String numarTitluriSauCotaParte;

    @Column(name ="valoare")
    private BigDecimal valoare;

    @Column(name ="moneda")
    private String moneda;

    @Column(name ="explicatie_plasament")
    private String explicatiePlasament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "declaratie_avere_id", nullable = false)
    private DeclaratieAvereEntity declaratieAvereEntity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDeclaratieAvereId() {
        return declaratieAvereId;
    }

    public void setDeclaratieAvereId(Integer declaratieAvereId) {
        this.declaratieAvereId = declaratieAvereId;
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

    public String getExplicatiePlasament() {
        return explicatiePlasament;
    }

    public void setExplicatiePlasament(String explicatiePlasamen) {
        this.explicatiePlasament = explicatiePlasamen;
    }

    public DeclaratieAvereEntity getDeclaratieAvereEntity() {
        return declaratieAvereEntity;
    }

    public void setDeclaratieAvereEntity(DeclaratieAvereEntity declaratieAvereEntity) {
        this.declaratieAvereEntity = declaratieAvereEntity;
    }
}
