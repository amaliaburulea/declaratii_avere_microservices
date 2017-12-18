package com.declaratiiavere.demnitarservice.demnitar;

import javax.persistence.*;

/**
 * Created by Amalia on 12/12/2017.
 */
@Table(name = "declaratie_interese_sindicat")
@Entity
public class DeclaratieIntereseSindicatEntity {
    @Column(name = "id")
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name ="declaratie_interese_id", insertable = false, updatable = false)
    private Integer declaratieIntereseId;

    @Column(name ="nume")
    private String nume;

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

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public DeclaratieIntereseEntity getDeclaratieIntereseEntity() {
        return declaratieIntereseEntity;
    }

    public void setDeclaratieIntereseEntity(DeclaratieIntereseEntity declaratieIntereseEntity) {
        this.declaratieIntereseEntity = declaratieIntereseEntity;
    }
}
