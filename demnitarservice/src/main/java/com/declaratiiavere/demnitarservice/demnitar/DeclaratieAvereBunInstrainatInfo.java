package com.declaratiiavere.demnitarservice.demnitar;

import java.math.BigDecimal;

/**
 * Contains information about declaratie avere bun instrainat.
 *
 * @author Razvan Dani
 */
public class DeclaratieAvereBunInstrainatInfo {
    private Integer id;
    private String tip;
    private Boolean isImobil;
    private String marca;
    private String dataInstrainarii;
    private String persoanaBeneficiara;
    private String formaInstrainarii;
    private BigDecimal valoarea;
    private String moneda;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public Boolean getIsImobil() {
        return isImobil;
    }

    public void setIsImobil(Boolean imobil) {
        isImobil = imobil;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

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
}
