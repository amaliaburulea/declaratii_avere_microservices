package com.declaratiiavere.demnitarservice.demnitar;

import com.declaratiiavere.jpaframework.EntityBase;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * JPA entity for declaratie_avere_bun_imobil table.
 *
 * @author Razvan Dani
 */
@Table(name = "declaratie_avere_bun_mobil")
@Entity
public class DeclaratieAvereBunMobilEntity extends EntityBase {
    @Column(name = "id")
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name ="declaratie_avere_id", insertable = false, updatable = false)
    private Integer declaratieAvereId;

    @Column(name ="tip")
    private String tip;

    @Column(name ="marca")
    private String marca;

    @Column(name ="cantitate")
    private Integer cantitate;

    @Column(name ="an_fabricare")
    private String anFabricare;

    @Column(name ="mod_dobandire")
    private String modDobandire;

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

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Integer getCantitate() {
        return cantitate;
    }

    public void setCantitate(Integer cantitate) {
        this.cantitate = cantitate;
    }

    public String getAnFabricare() {
        return anFabricare;
    }

    public void setAnFabricare(String anFabricare) {
        this.anFabricare = anFabricare;
    }

    public String getModDobandire() {
        return modDobandire;
    }

    public void setModDobandire(String modDobandire) {
        this.modDobandire = modDobandire;
    }

    public DeclaratieAvereEntity getDeclaratieAvereEntity() {
        return declaratieAvereEntity;
    }

    public void setDeclaratieAvereEntity(DeclaratieAvereEntity declaratieAvereEntity) {
        this.declaratieAvereEntity = declaratieAvereEntity;
    }
}
