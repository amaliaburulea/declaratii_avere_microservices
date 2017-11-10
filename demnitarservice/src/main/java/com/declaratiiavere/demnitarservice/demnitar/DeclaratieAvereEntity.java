package com.declaratiiavere.demnitarservice.demnitar;

import com.declaratiiavere.jpaframework.EntityBase;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * JPA entity for declaratie_avere table.
 *
 * @author Razvan Dani
 */
@Table(name = "declaratie_avere")
@Entity
public class DeclaratieAvereEntity extends EntityBase {
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

    @Column(name ="link_declaratie")
    private String linkDeclaratie;

    @Column(name ="voluntar_id")
    private Integer voluntarId;

    @Column(name ="is_done")
    private Boolean isDone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demnitar_id", nullable = false, insertable = false, updatable = false)
    private DemnitarEntity demnitarEntity;

    @OneToMany(mappedBy = "declaratieAvereEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DeclaratieAvereAlteActiveEntity> declaratieAvereAlteActiveEntitySet;

    @OneToMany(mappedBy = "declaratieAvereEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DeclaratieAvereBunImobilEntity> declaratieAvereBunImobilEntitySet;

    @OneToMany(mappedBy = "declaratieAvereEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DeclaratieAvereBunMobilEntity> declaratieAvereBunMobilEntitySet;

    @OneToMany(mappedBy = "declaratieAvereEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DeclaratieAvereBijuterieEntity> declaratieAvereBijuterieEntitySet;

    @OneToMany(mappedBy = "declaratieAvereEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DeclaratieAverePlasamentEntity> declaratieAverePlasamentEntitySet;

    @OneToMany(mappedBy = "declaratieAvereEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DeclaratieAvereDatorieEntity> declaratieAvereDatorieEntitySet;

    @OneToMany(mappedBy = "declaratieAvereEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DeclaratieAvereContEntity> declaratieAvereContEntitySet;

    @OneToMany(mappedBy = "declaratieAvereEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DeclaratieAvereBunInstrainatEntity> declaratieAvereBunInstrainatEntitySet;

    @OneToMany(mappedBy = "declaratieAvereEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DeclaratieAvereCadouEntity> declaratieAvereCadouEntitySet;

    @OneToMany(mappedBy = "declaratieAvereEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DeclaratieAvereVenitEntity> declaratieAvereVenitEntitySet;

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

    public Date getDataDepunerii() {
        return dataDepunerii;
    }

    public void setDataDepunerii(Date dataDepunerii) {
        this.dataDepunerii = dataDepunerii;
    }

    public Set<DeclaratieAvereAlteActiveEntity> getDeclaratieAvereAlteActiveEntitySet() {
        return declaratieAvereAlteActiveEntitySet;
    }

    public void setDeclaratieAvereAlteActiveEntitySet(Set<DeclaratieAvereAlteActiveEntity> declaratieAvereAlteActiveEntitySet) {
        this.declaratieAvereAlteActiveEntitySet = declaratieAvereAlteActiveEntitySet;
    }

    public Set<DeclaratieAvereBunImobilEntity> getDeclaratieAvereBunImobilEntitySet() {
        return declaratieAvereBunImobilEntitySet;
    }

    public void setDeclaratieAvereBunImobilEntitySet(Set<DeclaratieAvereBunImobilEntity> declaratieAvereBunImobilEntitySet) {
        this.declaratieAvereBunImobilEntitySet = declaratieAvereBunImobilEntitySet;
    }

    public Set<DeclaratieAvereBunMobilEntity> getDeclaratieAvereBunMobilEntitySet() {
        return declaratieAvereBunMobilEntitySet;
    }

    public void setDeclaratieAvereBunMobilEntitySet(Set<DeclaratieAvereBunMobilEntity> declaratieAvereBunMobilEntitySet) {
        this.declaratieAvereBunMobilEntitySet = declaratieAvereBunMobilEntitySet;
    }

    public Set<DeclaratieAvereBijuterieEntity> getDeclaratieAvereBijuterieEntitySet() {
        return declaratieAvereBijuterieEntitySet;
    }

    public void setDeclaratieAvereBijuterieEntitySet(Set<DeclaratieAvereBijuterieEntity> declaratieAvereBijuterieEntitySet) {
        this.declaratieAvereBijuterieEntitySet = declaratieAvereBijuterieEntitySet;
    }

    public Set<DeclaratieAverePlasamentEntity> getDeclaratieAverePlasamentEntitySet() {
        return declaratieAverePlasamentEntitySet;
    }

    public void setDeclaratieAverePlasamentEntitySet(Set<DeclaratieAverePlasamentEntity> declaratieAverePlasamentEntitySet) {
        this.declaratieAverePlasamentEntitySet = declaratieAverePlasamentEntitySet;
    }

    public Set<DeclaratieAvereDatorieEntity> getDeclaratieAvereDatorieEntitySet() {
        return declaratieAvereDatorieEntitySet;
    }

    public void setDeclaratieAvereDatorieEntitySet(Set<DeclaratieAvereDatorieEntity> declaratieAvereDatorieEntitySet) {
        this.declaratieAvereDatorieEntitySet = declaratieAvereDatorieEntitySet;
    }

    public Set<DeclaratieAvereContEntity> getDeclaratieAvereContEntitySet() {
        return declaratieAvereContEntitySet;
    }

    public void setDeclaratieAvereContEntitySet(Set<DeclaratieAvereContEntity> declaratieAvereContEntitySet) {
        this.declaratieAvereContEntitySet = declaratieAvereContEntitySet;
    }

    public Set<DeclaratieAvereBunInstrainatEntity> getDeclaratieAvereBunInstrainatEntitySet() {
        return declaratieAvereBunInstrainatEntitySet;
    }

    public void setDeclaratieAvereBunInstrainatEntitySet(Set<DeclaratieAvereBunInstrainatEntity> declaratieAvereBunInstrainatEntitySet) {
        this.declaratieAvereBunInstrainatEntitySet = declaratieAvereBunInstrainatEntitySet;
    }

    public Set<DeclaratieAvereVenitEntity> getDeclaratieAvereVenitEntitySet() {
        return declaratieAvereVenitEntitySet;
    }

    public void setDeclaratieAvereVenitEntitySet(Set<DeclaratieAvereVenitEntity> declaratieAvereVenitEntitySet) {
        this.declaratieAvereVenitEntitySet = declaratieAvereVenitEntitySet;
    }

    public Set<DeclaratieAvereCadouEntity> getDeclaratieAvereCadouEntitySet() {
        return declaratieAvereCadouEntitySet;
    }

    public void setDeclaratieAvereCadouEntitySet(Set<DeclaratieAvereCadouEntity> declaratieAvereCadouEntitySet) {
        this.declaratieAvereCadouEntitySet = declaratieAvereCadouEntitySet;
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
}
