package com.declaratiiavere.demnitarservice.demnitar;

import com.declaratiiavere.jpaframework.EntityBase;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * JPA entity for declaratie_avere_venit entity.
 *
 * @author Razvan Dani
 */
@Table(name = "declaratie_avere_venit")
@Entity
public class DeclaratieAvereVenitEntity extends EntityBase {
    @Column(name = "id")
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name ="declaratie_avere_id", insertable = false, updatable = false)
    private Integer declaratieAvereId;

    @Column(name ="tip")
    private Integer tip;

    @Column(name ="titular")
    private String titular;

    @Column(name ="sursa_venit")
    private String sursaVenit;

    @Column(name ="serviciul_prestat")
    private String serviciulPrestat;

    @Column(name ="venit_anual")
    private BigDecimal venitAnual;

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

    public Integer getTip() {
        return tip;
    }

    public void setTip(Integer tip) {
        this.tip = tip;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getSursaVenit() {
        return sursaVenit;
    }

    public void setSursaVenit(String sursaVenit) {
        this.sursaVenit = sursaVenit;
    }

    public String getServiciulPrestat() {
        return serviciulPrestat;
    }

    public void setServiciulPrestat(String serviciulPrestat) {
        this.serviciulPrestat = serviciulPrestat;
    }

    public BigDecimal getVenitAnual() {
        return venitAnual;
    }

    public void setVenitAnual(BigDecimal venitAnual) {
        this.venitAnual = venitAnual;
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
