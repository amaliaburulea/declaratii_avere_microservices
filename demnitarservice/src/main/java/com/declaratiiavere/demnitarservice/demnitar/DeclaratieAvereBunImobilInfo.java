package com.declaratiiavere.demnitarservice.demnitar;

import java.math.BigDecimal;

/**
 * Encapsulates info about declaratie avere bun imobil.
 *
 * @author Razvan Dani
 */
public class DeclaratieAvereBunImobilInfo {
    private Integer id;
    private Boolean isTeren;
    private String adresaImobil;
    private Integer terenCategorie;
    private Integer cladireCategorie;
    private String anDobandire;
    private BigDecimal suprafata;
    private String explicatieSuprafata;
    private String unitateMasura;
    private String cotaParte;
    private String modDobandire;
    private String titular;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getExplicatieSuprafata(){
        return explicatieSuprafata;
    }

    public void setExplicatieSuprafata(String explicatieSuprafata){
        this.explicatieSuprafata=explicatieSuprafata;
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
}
