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
    private Integer functieId;
    private Integer institutieId;
    private boolean searchOnlyDemnitarsAssignedToMe;

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

    public Integer getFunctieId() {
        return functieId;
    }

    public void setFunctieId(Integer functieId) {
        this.functieId = functieId;
    }

    public Integer getInstitutieId() {
        return institutieId;
    }

    public void setInstitutieId(Integer institutieId) {
        this.institutieId = institutieId;
    }

    public boolean isSearchOnlyDemnitarsAssignedToMe() {
        return searchOnlyDemnitarsAssignedToMe;
    }

    public void setSearchOnlyDemnitarsAssignedToMe(boolean searchOnlyDemnitarsAssignedToMe) {
        this.searchOnlyDemnitarsAssignedToMe = searchOnlyDemnitarsAssignedToMe;
    }
}
