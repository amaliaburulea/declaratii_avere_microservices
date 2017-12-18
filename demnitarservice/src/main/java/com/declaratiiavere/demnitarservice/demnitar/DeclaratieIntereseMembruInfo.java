package com.declaratiiavere.demnitarservice.demnitar;

/**
 * Created by Amalia on 11/14/2017.
 */
public class DeclaratieIntereseMembruInfo {
    private Integer id;
    private String unitatea;
    private String adresa;
    private String rolul;
    private String valoare;
    private String explicatieVenitMembru;
    private String moneda;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUnitatea() {
        return unitatea;
    }

    public void setUnitatea(String unitatea) {
        this.unitatea = unitatea;
    }

    public String getRolul() {
        return rolul;
    }

    public void setRolul(String rolul) {
        this.rolul = rolul;
    }

    public String getValoare() {
        return valoare;
    }

    public void setValoare(String valoare) {
        this.valoare = valoare;
    }

    public String getExplicatieVenitMembru() {
        return explicatieVenitMembru;
    }

    public void setExplicatieVenitMembru(String explicatieVenitMembru) {
        this.explicatieVenitMembru = explicatieVenitMembru;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }
}
