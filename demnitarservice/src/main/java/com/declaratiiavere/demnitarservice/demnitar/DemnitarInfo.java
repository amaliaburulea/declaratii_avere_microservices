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
}
