package com.declaratiiavere.demnitarservice.demnitar;

import com.declaratiiavere.jpaframework.EntityBase;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * JPA entity for declaratie_avere_datorie table.
 *
 * @author Razvan Dani
 */
@Table(name = "declaratie_avere_datorie")
@Entity
public class DeclaratieAvereDatorieEntity extends EntityBase {
    @Column(name = "id")
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name ="declaratie_avere_id", insertable = false, updatable = false)
    private Integer declaratieAvereId;

    @Column(name ="creditor")
    private String creditor;

    @Column(name ="an_contractare")
    private String anContractare;

    @Column(name ="scadenta")
    private String scadenta;

    @Column(name ="valoare")
    private BigDecimal valoare;

    @Column(name ="moneda")
    private String moneda;

    @Column(name ="explicatie_datorie")
    private String explicatieDatorie;

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

    public String getCreditor() {
        return creditor;
    }

    public void setCreditor(String creditor) {
        this.creditor = creditor;
    }

    public String getAnContractare() {
        return anContractare;
    }

    public void setAnContractare(String anContractare) {
        this.anContractare = anContractare;
    }

    public String getScadenta() {
        return scadenta;
    }

    public void setScadenta(String scadenta) {
        this.scadenta = scadenta;
    }

    public BigDecimal getValoare() {
        return valoare;
    }

    public void setValoare(BigDecimal valoare) {
        this.valoare = valoare;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getExplicatieDatorie() {
        return explicatieDatorie;
    }

    public void setExplicatieDatorie(String explicatieDatorie) {
        this.explicatieDatorie = explicatieDatorie;
    }

    public DeclaratieAvereEntity getDeclaratieAvereEntity() {
        return declaratieAvereEntity;
    }

    public void setDeclaratieAvereEntity(DeclaratieAvereEntity declaratieAvereEntity) {
        this.declaratieAvereEntity = declaratieAvereEntity;
    }
}
