package com.declaratiiavere.demnitarservice.demnitar;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * JPA entity for declaratie_avere_bun_imobil table.
 *
 * @author Razvan Dani
 */
@Table(name = "declaratie_avere_bun_imobil")
@Entity
public class DeclaratieAvereBunImobilEntity {
    @Column(name = "id")
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name ="declaratie_avere_id", insertable = false, updatable = false)
    private Integer declaratieAvereId;

    @Column(name ="is_teren")
    private Boolean isTeren;

    @Column(name ="adresaImobil")
    private String adresaImobil;

    @Column(name ="teren_categorie")
    private Integer terenCategorie;

    @Column(name ="cladire_categorie")
    private Integer cladireCategorie;

    @Column(name ="an_dobandire")
    private String anDobandire;

    @Column(name ="suprafata")
    private BigDecimal suprafata;

    @Column(name ="unitate_masura")
    private String unitateMasura;

    @Column(name ="cota_parte")
    private String cotaParte;

    @Column(name ="mod_dobandire")
    private String modDobandire;

    @Column(name ="titular")
    private String titular;

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

    public Boolean getIsTeren() {
        return isTeren;
    }

    public void setIsTeren(Boolean teren) {
        isTeren = teren;
    }

    public String getAdresaImobil() {
        return adresaImobil;
    }

    public void setAdresaImobil(String adresaImobil) {
        this.adresaImobil = adresaImobil;
    }

    public Integer getTerenCategorie() {
        return terenCategorie;
    }

    public void setTerenCategorie(Integer terenCategorie) {
        this.terenCategorie = terenCategorie;
    }

    public Integer getCladireCategorie() {
        return cladireCategorie;
    }

    public void setCladireCategorie(Integer cladireCategorie) {
        this.cladireCategorie = cladireCategorie;
    }

    public String getAnDobandire() {
        return anDobandire;
    }

    public void setAnDobandire(String anDobandire) {
        this.anDobandire = anDobandire;
    }

    public BigDecimal getSuprafata() {
        return suprafata;
    }

    public void setSuprafata(BigDecimal suprafata) {
        this.suprafata = suprafata;
    }

    public String getUnitateMasura() {
        return unitateMasura;
    }

    public void setUnitateMasura(String unitateMasura) {
        this.unitateMasura = unitateMasura;
    }

    public String getCotaParte() {
        return cotaParte;
    }

    public void setCotaParte(String cotaParte) {
        this.cotaParte = cotaParte;
    }

    public String getModDobandire() {
        return modDobandire;
    }

    public void setModDobandire(String modDobandire) {
        this.modDobandire = modDobandire;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public DeclaratieAvereEntity getDeclaratieAvereEntity() {
        return declaratieAvereEntity;
    }

    public void setDeclaratieAvereEntity(DeclaratieAvereEntity declaratieAvereEntity) {
        this.declaratieAvereEntity = declaratieAvereEntity;
    }
}
