package com.declaratiiavere.demnitarservice.demnitar;

import com.declaratiiavere.jpaframework.EntityBase;

import javax.persistence.*;

/**
 * JPA entity for demnitar table.
 *
 * @author Razvan Dani
 */
@Table(name = "demnitar")
@Entity
public class DemnitarEntity extends EntityBase {
    @Column(name = "id")
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name ="nume")
    private String nume;

    @Column(name ="prenume")
    private String prenume;

    @Column(name ="functie_id")
    private Integer functieId;

    @Column(name ="functie2_id")
    private Integer functie2Id;

    @Column(name ="institutie_id")
    private Integer institutieId;

    @Column(name ="institutie2_id")
    private Integer institutie2Id;

    @Column(name ="grup_politic")
    private String grupPolitic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "functie_id", nullable = false, insertable = false, updatable = false)
    private FunctieEntity functieEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "functie2_id", nullable = false, insertable = false, updatable = false)
    private FunctieEntity functie2Entity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institutie_id", nullable = false, insertable = false, updatable = false)
    private InstitutieEntity institutieEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institutie2_id", nullable = false, insertable = false, updatable = false)
    private InstitutieEntity institutie2Entity;

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

    public String getGrupPolitic() {
        return grupPolitic;
    }

    public void setGrupPolitic(String grupPolitic) {
        this.grupPolitic = grupPolitic;
    }

    public FunctieEntity getFunctieEntity() {
        return functieEntity;
    }

    public void setFunctieEntity(FunctieEntity functieEntity) {
        this.functieEntity = functieEntity;
    }

    public FunctieEntity getFunctie2Entity() {
        return functie2Entity;
    }

    public void setFunctie2Entity(FunctieEntity functie2Entity) {
        this.functie2Entity = functie2Entity;
    }

    public InstitutieEntity getInstitutieEntity() {
        return institutieEntity;
    }

    public void setInstitutieEntity(InstitutieEntity institutieEntity) {
        this.institutieEntity = institutieEntity;
    }

    public InstitutieEntity getInstitutie2Entity() {
        return institutie2Entity;
    }

    public void setInstitutie2Entity(InstitutieEntity institutie2Entity) {
        this.institutie2Entity = institutie2Entity;
    }

}
