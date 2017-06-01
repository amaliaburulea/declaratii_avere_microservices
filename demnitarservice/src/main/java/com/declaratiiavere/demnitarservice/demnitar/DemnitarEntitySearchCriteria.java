package com.declaratiiavere.demnitarservice.demnitar;

import com.declaratiiavere.jpaframework.EntitySearchCriteria;

/**
 * Entity search criteria for locations.
 *
 * @author Razvan Dani
 */
public class DemnitarEntitySearchCriteria extends EntitySearchCriteria {
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
