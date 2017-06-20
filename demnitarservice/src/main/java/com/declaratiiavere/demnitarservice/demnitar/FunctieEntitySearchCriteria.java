package com.declaratiiavere.demnitarservice.demnitar;

import com.declaratiiavere.jpaframework.EntitySearchCriteria;

/**
 * Entity search criteria for functie.
 *
 * @author Razvan Dani
 */
public class FunctieEntitySearchCriteria extends EntitySearchCriteria {
    private String nume;

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }
}
