package com.declaratiiavere.demnitarservice.demnitar;

import com.declaratiiavere.jpaframework.EntityBase;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by Amalia on 11/13/2017.
 */

@Table(name = "declaratie_interese")
@Entity
public class DeclaratieIntereseEntity extends EntityBase {
    @Column(name = "id")
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name ="demnitar_id")
    private Integer demnitarId;

    @Column(name ="data_declaratiei")
    private Date dataDeclaratiei;

    @Column(name ="data_depunerii")
    private Date dataDepunerii;

    @Column(name ="functie")
    private Integer functieId;

    @Column(name ="functie2")
    private Integer functie2Id;

    @Column(name ="institutie")
    private Integer institutieId;

    @Column(name ="institutie2")
    private Integer institutie2Id;

    @Column(name ="grup_politic")
    private String grupPolitic;

    @Column(name ="link_declaratie")
    private String linkDeclaratie;

    @Column(name ="circumscriptia")
    private String circumscriptie;

    @Column(name ="voluntar_id")
    private Integer voluntarId;

    @Column(name ="is_done")
    private Boolean isDone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demnitar_id", nullable = false, insertable = false, updatable = false)
    private DemnitarEntity demnitarEntity;

    @OneToMany(mappedBy = "declaratieIntereseEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DeclaratieIntereseAsociatEntity> declaratieIntereseAsociatEntitySet;

    @OneToMany(mappedBy = "declaratieIntereseEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DeclaratieIntereseMembruEntity> declaratieIntereseMembruEntitySet;

    @OneToMany(mappedBy = "declaratieIntereseEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DeclaratieIntereseSindicatEntity> declaratieIntereseSindicatEntitySet;

    @OneToMany(mappedBy = "declaratieIntereseEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DeclaratieInteresePartidEntity> declaratieInteresePartidEntitySet;

    @OneToMany(mappedBy = "declaratieIntereseEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DeclaratieIntereseContractEntity> declaratieIntereseContractEntitySet;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "functie", nullable = false, insertable = false, updatable = false)
    private FunctieEntity functieEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "functie2", nullable = false, insertable = false, updatable = false)
    private FunctieEntity functie2Entity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institutie", nullable = false, insertable = false, updatable = false)
    private InstitutieEntity institutieEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institutie2", nullable = false, insertable = false, updatable = false)
    private InstitutieEntity institutie2Entity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDemnitarId() {
        return demnitarId;
    }

    public void setDemnitarId(Integer demnitarId) {
        this.demnitarId = demnitarId;
    }

    public Date getDataDeclaratiei() {
        return dataDeclaratiei;
    }

    public void setDataDeclaratiei(Date dataDeclaratiei) {
        this.dataDeclaratiei = dataDeclaratiei;
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

    public String getLinkDeclaratie() {
        return linkDeclaratie;
    }

    public void setLinkDeclaratie(String linkDeclaratie) {
        this.linkDeclaratie = linkDeclaratie;
    }

    public String getCircumscriptie() {
        return circumscriptie;
    }

    public void setCircumscriptie(String circumscriptie) {
        this.circumscriptie = circumscriptie;
    }

    public Date getDataDepunerii() {
        return dataDepunerii;
    }

    public void setDataDepunerii(Date dataDepunerii) {
        this.dataDepunerii = dataDepunerii;
    }

    public Set<DeclaratieIntereseAsociatEntity> getDeclaratieIntereseAsociatEntitySet() {
        return declaratieIntereseAsociatEntitySet;
    }

    public void setDeclaratieIntereseAsociatEntitySet(Set<DeclaratieIntereseAsociatEntity> declaratieIntereseAsociatEntitySet) {
        this.declaratieIntereseAsociatEntitySet = declaratieIntereseAsociatEntitySet;
    }

    public DemnitarEntity getDemnitarEntity() {
        return demnitarEntity;
    }

    public void setDemnitarEntity(DemnitarEntity demnitarEntity) {
        this.demnitarEntity = demnitarEntity;
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

    public Integer getVoluntarId() {
        return voluntarId;
    }

    public void setVoluntarId(Integer voluntarId) {
        this.voluntarId = voluntarId;
    }

    public Boolean getIsDone() {
        return isDone;
    }

    public void setIsDone(Boolean done) {
        isDone = done;
    }

    public Set<DeclaratieIntereseMembruEntity> getDeclaratieIntereseMembruEntitySet() {
        return declaratieIntereseMembruEntitySet;
    }

    public void setDeclaratieIntereseMembruEntitySet(Set<DeclaratieIntereseMembruEntity> declaratieIntereseMembruEntitySet) {
        this.declaratieIntereseMembruEntitySet = declaratieIntereseMembruEntitySet;
    }

    public Set<DeclaratieIntereseSindicatEntity> getDeclaratieIntereseSindicatEntitySet() {
        return declaratieIntereseSindicatEntitySet;
    }

    public void setDeclaratieIntereseSindicatEntitySet(Set<DeclaratieIntereseSindicatEntity> declaratieIntereseSindicatEntitySet) {
        this.declaratieIntereseSindicatEntitySet = declaratieIntereseSindicatEntitySet;
    }

    public Set<DeclaratieInteresePartidEntity> getDeclaratieInteresePartidEntitySet() {
        return declaratieInteresePartidEntitySet;
    }

    public void setDeclaratieInteresePartidEntitySet(Set<DeclaratieInteresePartidEntity> declaratieInteresePartidEntitySet) {
        this.declaratieInteresePartidEntitySet = declaratieInteresePartidEntitySet;
    }

    public Set<DeclaratieIntereseContractEntity> getDeclaratieIntereseContractEntitySet() {
        return declaratieIntereseContractEntitySet;
    }

    public void setDeclaratieIntereseContractEntitySet(Set<DeclaratieIntereseContractEntity> declaratieIntereseContractEntitySet) {
        this.declaratieIntereseContractEntitySet = declaratieIntereseContractEntitySet;
    }
}
