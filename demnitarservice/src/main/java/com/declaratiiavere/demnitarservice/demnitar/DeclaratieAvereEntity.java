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

    @Column(name ="functie")
    private String functie;

    @Column(name ="functie2")
    private String functie2;

    @Column(name ="institutie")
    private String institutie;

    @Column(name ="institutie2")
    private String institutie2;

    @Column(name ="grup_politic")
    private String grupPolitic;

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

    public String getGrupPolitic() {
        return grupPolitic;
    }

    public void setGrupPolitic(String grupPolitic) {
        this.grupPolitic = grupPolitic;
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
}
