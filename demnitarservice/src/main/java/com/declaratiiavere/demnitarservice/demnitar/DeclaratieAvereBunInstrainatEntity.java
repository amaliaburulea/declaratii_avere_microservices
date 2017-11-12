package com.declaratiiavere.demnitarservice.demnitar;

import com.declaratiiavere.jpaframework.EntityBase;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 *
 * JPA entity for declaratie_avere_bun_instrainat table.
 *
 * @author Razvan Dani
 */
@Table(name = "declaratie_avere_bun_instrainat")
@Entity
public class DeclaratieAvereBunInstrainatEntity extends EntityBase {
    @Column(name = "id")
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name ="declaratie_avere_id", insertable = false, updatable = false)
    private Integer declaratieAvereId;

    @Column(name ="tip")
    private String tip;

    /*@Column(name ="is_imobil")
    private Boolean isImobil;*/

    @Column(name ="data_instrainarii")
    private String dataInstrainarii;

    @Column(name ="persoana_beneficiara")
    private String persoanaBeneficiara;

    @Column(name ="forma_instrainarii")
    private String formaInstrainarii;

    @Column(name ="valoarea")
    private BigDecimal valoarea;

    @Column(name ="moneda")
    private String moneda;

    @Column(name ="explicatie_suma")
    private String explicatieSuma;

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

   /* public Boolean getIsImobil() {
        return isImobil;
    }

    public void setIsImobil(Boolean imobil) {
        isImobil = imobil;
    }*/

    public String getDataInstrainarii() {
        return dataInstrainarii;
    }

    public void setDataInstrainarii(String dataInstrainarii) {
        this.dataInstrainarii = dataInstrainarii;
    }

    public String getPersoanaBeneficiara() {
        return persoanaBeneficiara;
    }

    public void setPersoanaBeneficiara(String persoanaBeneficiara) {
        this.persoanaBeneficiara = persoanaBeneficiara;
    }

    public String getFormaInstrainarii() {
        return formaInstrainarii;
    }

    public void setFormaInstrainarii(String formaInstrainarii) {
        this.formaInstrainarii = formaInstrainarii;
    }

    public BigDecimal getValoarea() {
        return valoarea;
    }

    public void setValoarea(BigDecimal valoarea) {
        this.valoarea = valoarea;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getExplicatieSuma() {
        return explicatieSuma;
    }

    public void setExplicatieSuma(String explicatieSuma) {
        this.explicatieSuma = explicatieSuma;
    }


    public DeclaratieAvereEntity getDeclaratieAvereEntity() {
        return declaratieAvereEntity;
    }

    public void setDeclaratieAvereEntity(DeclaratieAvereEntity declaratieAvereEntity) {
        this.declaratieAvereEntity = declaratieAvereEntity;
    }
}
