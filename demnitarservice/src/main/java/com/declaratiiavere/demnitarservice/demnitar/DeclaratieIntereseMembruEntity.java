package com.declaratiiavere.demnitarservice.demnitar;

import javax.persistence.*;

/**
 * Created by Amalia on 11/14/2017.
 */

@Table(name = "declaratie_interese_membru_sc")
@Entity
public class DeclaratieIntereseMembruEntity {
    @Column(name = "id")
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name ="declaratie_interese_id", insertable = false, updatable = false)
    private Integer declaratieIntereseId;

    @Column(name ="unitatea")
    private String unitatea;

    @Column(name ="adresa")
    private String adresa;

    @Column(name ="rolul")
    private String rolul;

    @Column(name ="valoarea")
    private String valoarea;

    @Column(name ="moneda")
    private String moneda;

    @Column(name ="explicatie_venit")
    private String explicatieVenitMembru;

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

    public String getExplicatieVenitMembru() {
        return explicatieVenitMembru;
    }

    public void setExplicatieVenitAsoc(String explicatieVenitMembru) {
        this.explicatieVenitMembru = explicatieVenitMembru;
    }

    public DeclaratieIntereseEntity getDeclaratieIntereseEntity() {
        return declaratieIntereseEntity;
    }

    public void setDeclaratieIntereseEntity(DeclaratieIntereseEntity declaratieIntereseEntity) {
        this.declaratieIntereseEntity = declaratieIntereseEntity;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }
}
