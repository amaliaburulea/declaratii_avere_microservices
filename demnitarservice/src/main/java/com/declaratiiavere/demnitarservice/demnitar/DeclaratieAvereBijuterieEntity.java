package com.declaratiiavere.demnitarservice.demnitar;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * JPA entity for declaratie_avere_bijuterie table.
 *
 * @author Razvan Dani
 */
@Table(name = "declaratie_avere_bijuterie")
@Entity
public class DeclaratieAvereBijuterieEntity {
    @Column(name = "id")
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name ="declaratie_avere_id", insertable = false, updatable = false)
    private Integer declaratieAvereId;

    @Column(name ="descriere")
    private String descriere;

    @Column(name ="an_dobandire")
    private String anDobandire;

    @Column(name ="valoare_estimata")
    private BigDecimal valoareEstimate;

    @Column(name ="moneda")
    private String moneda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "declaratie_avere_id", nullable = false)
    private DeclaratieAvereEntity declaratieAvereEntity;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDeclaratieAvereId() {
        return declaratieAvereId;
    }

    public void setDeclaratieAvereId(Integer declaratieAvereId) {
        this.declaratieAvereId = declaratieAvereId;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public String getAnDobandire() {
        return anDobandire;
    }

    public void setAnDobandire(String anDobandire) {
        this.anDobandire = anDobandire;
    }

    public BigDecimal getValoareEstimate() {
        return valoareEstimate;
    }

    public void setValoareEstimate(BigDecimal valoareEstimate) {
        this.valoareEstimate = valoareEstimate;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public DeclaratieAvereEntity getDeclaratieAvereEntity() {
        return declaratieAvereEntity;
    }

    public void setDeclaratieAvereEntity(DeclaratieAvereEntity declaratieAvereEntity) {
        this.declaratieAvereEntity = declaratieAvereEntity;
    }
}
