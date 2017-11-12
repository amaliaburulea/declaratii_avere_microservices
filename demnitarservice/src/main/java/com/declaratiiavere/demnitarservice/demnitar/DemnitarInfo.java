package com.declaratiiavere.demnitarservice.demnitar;

/**
 * Encapsulates demnitar information.
 *
 * @author Razvan Dani
 */
public class DemnitarInfo {
    private Integer id;
    private String nume;
    private String prenume;
    private Integer functieId;
    private Integer functie2Id;
    private Integer institutieId;
    private Integer institutie2Id;
    private String functie;
    private String functie2;
    private String institutie;
    private String institutie2;
    private String anNastere;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Integer getFunctieId() {
        return functieId;
    }

    public void setFunctieId(Integer functieId) {
        this.functieId = functieId;
    }

    public Integer getFunctie2Id() {
        return functie2Id;
    }

    public void setFunctie2Id(Integer functie2Id) {
        this.functie2Id = functie2Id;
    }

    public Integer getInstitutieId() {
        return institutieId;
    }

    public void setInstitutieId(Integer institutieId) {
        this.institutieId = institutieId;
    }

    public Integer getInstitutie2Id() {
        return institutie2Id;
    }

    public void setInstitutie2Id(Integer institutie2Id) {
        this.institutie2Id = institutie2Id;
    }

    public String getFunctie() {
        return functie;
    }

    public void setFunctie(String functie) {
        this.functie = functie;
    }

    public String getFunctie2() {
        return functie2;
    }

    public void setFunctie2(String functie2) {
        this.functie2 = functie2;
    }

    public String getInstitutie() {
        return institutie;
    }

    public void setInstitutie(String institutie) {
        this.institutie = institutie;
    }

    public String getInstitutie2() {
        return institutie2;
    }

    public void setInstitutie2(String institutie2) {
        this.institutie2 = institutie2;
    }

    public String getAnNastere() {
        return anNastere;
    }

    public void setAnNastere(String anNastere) {
        this.anNastere = anNastere;
    }
}
