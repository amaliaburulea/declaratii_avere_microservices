package com.declaratiiavere.demnitarservice.demnitar;

/**
 * Search criteria for locations.
 *
 * @author Razvan Dani
 */
public class SearchDemnitarCriteria {
    private String nume;
    private String prenume;

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }
}
