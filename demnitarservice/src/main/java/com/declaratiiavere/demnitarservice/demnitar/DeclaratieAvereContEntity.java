package com.declaratiiavere.demnitarservice.demnitar;

import com.declaratiiavere.jpaframework.EntityBase;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * JPA entity for declaratie_avere_cont entity.
 *
 * @author Razvan Dani
 */
@Table(name = "declaratie_avere_cont")
@Entity
public class DeclaratieAvereContEntity extends EntityBase {
    @Column(name = "id")
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name ="declaratie_avere_id", insertable = false, updatable = false)
    private Integer declaratieAvereId;

    @Column(name ="titular")
    private String titular;

    @Column(name ="institutie_bancara")
    private String institutieBancara;

    @Column(name ="tipCont")
    private Integer tipCont;

    @Column(name ="sold_cont")
    private BigDecimal soldCont;

    @Column(name ="moneda")
    private String moneda;

    @Column(name ="an_deschidere_cont")
    private String anDeschidereCont;

    @Column(name ="explicatie_sold")
    private String explicatieSold;

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

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getInstitutieBancara() {
        return institutieBancara;
    }

    public void setInstitutieBancara(String institutieBancara) {
        this.institutieBancara = institutieBancara;
    }

    public Integer getTipCont() {
        return tipCont;
    }

    public void setTipCont(Integer tipCont) {
        this.tipCont = tipCont;
    }

    public BigDecimal getSoldCont() {
        return soldCont;
    }

    public void setSoldCont(BigDecimal soldCont) {
        this.soldCont = soldCont;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getAnDeschidereCont() {
        return anDeschidereCont;
    }

    public void setAnDeschidereCont(String anDeschidereCont) {
        this.anDeschidereCont = anDeschidereCont;
    }

    public String getExplicatieSold() {
        return explicatieSold;
    }

    public void setExplicatieSold(String explicatieSold) {
        this.explicatieSold = explicatieSold;
    }

    public DeclaratieAvereEntity getDeclaratieAvereEntity() {
        return declaratieAvereEntity;
    }

    public void setDeclaratieAvereEntity(DeclaratieAvereEntity declaratieAvereEntity) {
        this.declaratieAvereEntity = declaratieAvereEntity;
    }
}
