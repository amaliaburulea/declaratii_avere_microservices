package com.declaratiiavere.demnitarservice.demnitar;

import com.declaratiiavere.jpaframework.EntityBase;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by Amalia on 11/14/2017.
 */

@Table(name = "declaratie_interese_asociat_sc")
@Entity
public class DeclaratieIntereseAsociatEntity extends EntityBase {
    @Column(name = "id")
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name ="declaratie_interese_id", insertable = false, updatable = false)
    private Integer declaratieIntereseId;

    @Column(name ="unitatea")
    private String unitatea;

    @Column(name ="rolul")
    private String rolul;

    @Column(name ="parti_sociale_actiuni")
    private String partiSociale;

    @Column(name ="valoarea")
    private String valoarea;

    @Column(name ="moneda")
    private String moneda;

    @Column(name ="explicatie_venit")
    private String explicatieVenit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "declaratie_interese_id", nullable = false)
    private DeclaratieIntereseEntity declaratieIntereseEntity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDeclaratieIntereseId() {
        return declaratieIntereseId;
    }

    public void setDeclaratieIntereseId(Integer declaratieIntereseId) {
        this.declaratieIntereseId = declaratieIntereseId;
    }

    public String getUnitatea() {
        return unitatea;
    }

    public void setUnitatea(String unitatea) {
        this.unitatea = unitatea;
    }

    public String getRolul() {
        return rolul;
    }

    public void setRolul(String rolul) {
        this.rolul = rolul;
    }

    public String getPartiSociale() {
        return partiSociale;
    }

    public void setPartiSociale(String partiSociale) {
        this.partiSociale = partiSociale;
    }

    public String getValoarea() {
        return valoarea;
    }

    public void setValoarea(String valoarea) {
        this.valoarea = valoarea;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getExplicatieVenit() {
        return explicatieVenit;
    }

    public void setExplicatieVenit(String explicatieVenit) {
        this.explicatieVenit = explicatieVenit;
    }

    public DeclaratieIntereseEntity getDeclaratieIntereseEntity() {
        return declaratieIntereseEntity;
    }

    public void setDeclaratieIntereseEntity(DeclaratieIntereseEntity declaratieIntereseEntity) {
        this.declaratieIntereseEntity = declaratieIntereseEntity;
    }
}
