package com.declaratiiavere.demnitarservice.demnitar;

/**
 * Encapsulates info about declaratie avere bun mobil.
 *
 * @author Razvan Dani
 */
public class DeclaratieAvereBunMobilInfo {
    private Integer id;
    private String tip;
    private String marca;
    private Integer cantitate;
    private String anFabricare;
    private String modDobandire;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Integer getCantitate() {
        return cantitate;
    }

    public void setCantitate(Integer cantitate) {
        this.cantitate = cantitate;
    }

    public String getAnFabricare() {
        return anFabricare;
    }

    public void setAnFabricare(String anFabricare) {
        this.anFabricare = anFabricare;
    }

    public String getModDobandire() {
        return modDobandire;
    }

    public void setModDobandire(String modDobandire) {
        this.modDobandire = modDobandire;
    }
}
