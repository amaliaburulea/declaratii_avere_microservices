package com.declaratiiavere.demnitarservice.demnitar;

import com.declaratiiavere.jpaframework.EntityBase;

import javax.persistence.*;

/**
 * JPA entity for institutie table.
 *
 * @author Razvan Dani
 */
@Table(name = "institutie")
@Entity
public class InstitutieEntity extends EntityBase {
    @Column(name = "id")
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name ="nume")
    private String nume;

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
}
