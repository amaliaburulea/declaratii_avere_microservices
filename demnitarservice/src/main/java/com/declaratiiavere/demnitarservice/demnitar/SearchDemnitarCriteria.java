package com.declaratiiavere.demnitarservice.demnitar;

/**
 * Search criteria for locations.
 *
 * @author Razvan Dani
 */
public class SearchDemnitarCriteria {
    private String nume;
    private String prenume;
    private String numeStartsWith;
    private String prenumeStartsWith;

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

    public String getNumeStartsWith() {
        return numeStartsWith;
    }

    public void setNumeStartsWith(String numeStartsWith) {
        this.numeStartsWith = numeStartsWith;
    }

    public String getPrenumeStartsWith() {
        return prenumeStartsWith;
    }

    public void setPrenumeStartsWith(String prenumeStartsWith) {
        this.prenumeStartsWith = prenumeStartsWith;
    }
}
