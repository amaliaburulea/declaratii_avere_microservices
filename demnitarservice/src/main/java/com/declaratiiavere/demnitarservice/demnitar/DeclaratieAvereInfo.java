package com.declaratiiavere.demnitarservice.demnitar;

import com.declaratiiavere.common.utils.JsonDateDeserializer;
import com.declaratiiavere.common.utils.JsonDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;
import java.util.List;

/**
 * Encapsulates information about revenue declaration.
 *
 * @author Razvan Dani
 */
public class DeclaratieAvereInfo {
    private Integer id;
    private Integer demnitarId;
    private String demnitarNume;
    private String demnitarPrenume;
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date dataDeclaratiei;
    private Integer functieId;
    private Integer functie2Id;
    private Integer institutieId;
    private Integer institutie2Id;
    private String grupPolitic;
    private String linkDeclaratie;
    private Integer voluntarId;
    private Boolean isDone;

    private String functie;
    private String functie2;
    private String institutie;
    private String institutie2;
    private String voluntarUserName;

    private List<DeclaratieAvereAlteActiveInfo> declaratieAvereAlteActiveInfoList;
    private List<DeclaratieAvereBunImobilInfo> declaratieAvereBunImobilInfoList;
    private List<DeclaratieAvereBunMobilInfo> declaratieAvereBunMobilInfoList;
    private List<DeclaratieAvereBijuterieInfo> declaratieAvereBijuterieInfoList;
    private List<DeclaratieAverePlasamentInfo> declaratieAverePlasamentInfoList;
    private List<DeclaratieAvereDatorieInfo> declaratieAvereDatorieInfoList;
    private List<DeclaratieAvereContInfo> declaratieAvereContInfoList;
    private List<DeclaratieAvereBunInstrainatInfo> declaratieAvereBunInstrainatInfoList;
    private List<DeclaratieAvereCadouInfo> declaratieAvereCadouInfoList;
    private List<DeclaratieAvereVenitInfo> declaratieAvereVenitInfoList;

    public String getDemnitarNume() {
        return demnitarNume;
    }

    public void setDemnitarNume(String demnitarNume) {
        this.demnitarNume = demnitarNume;
    }

    public String getDemnitarPrenume() {
        return demnitarPrenume;
    }

    public void setDemnitarPrenume(String demnitarPrenume) {
        this.demnitarPrenume = demnitarPrenume;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDemnitarId() {
        return demnitarId;
    }

    public void setDemnitarId(Integer demnitarId) {
        this.demnitarId = demnitarId;
    }

    public Date getDataDeclaratiei() {
        return dataDeclaratiei;
    }

    @JsonDeserialize(using = JsonDateDeserializer.class)
    public void setDataDeclaratiei(Date dataDeclaratiei) {
        this.dataDeclaratiei = dataDeclaratiei;
    }

    public Integer getFunctieId() {
        return functieId;
    }

    public void setFunctieId(Integer functieId) {
        this.functieId = functieId;
    }

    public Integer getFunctie2Id() {
        return functie2Id;
    }

    public void setFunctie2Id(Integer functie2Id) {
        this.functie2Id = functie2Id;
    }

    public Integer getInstitutieId() {
        return institutieId;
    }

    public void setInstitutieId(Integer institutieId) {
        this.institutieId = institutieId;
    }

    public Integer getInstitutie2Id() {
        return institutie2Id;
    }

    public void setInstitutie2Id(Integer institutie2Id) {
        this.institutie2Id = institutie2Id;
    }

    public String getGrupPolitic() {
        return grupPolitic;
    }

    public void setGrupPolitic(String grupPolitic) {
        this.grupPolitic = grupPolitic;
    }

    public String getLinkDeclaratie() {
        return linkDeclaratie;
    }

    public void setLinkDeclaratie(String linkDeclaratie) {
        this.linkDeclaratie = linkDeclaratie;
    }

    public Integer getVoluntarId() {
        return voluntarId;
    }

    public void setVoluntarId(Integer voluntarId) {
        this.voluntarId = voluntarId;
    }

    public String getVoluntarUserName() {
        return voluntarUserName;
    }

    public void setVoluntarUserName(String voluntarUserName) {
        this.voluntarUserName = voluntarUserName;
    }

    public List<DeclaratieAvereAlteActiveInfo> getDeclaratieAvereAlteActiveInfoList() {
        return declaratieAvereAlteActiveInfoList;
    }

    public void setDeclaratieAvereAlteActiveInfoList(List<DeclaratieAvereAlteActiveInfo> declaratieAvereAlteActiveInfoList) {
        this.declaratieAvereAlteActiveInfoList  = declaratieAvereAlteActiveInfoList;
    }

    public List<DeclaratieAvereBunImobilInfo> getDeclaratieAvereBunImobilInfoList() {
        return declaratieAvereBunImobilInfoList;
    }

    public void setDeclaratieAvereBunImobilInfoList(List<DeclaratieAvereBunImobilInfo> declaratieAvereBunImobilInfoList) {
        this.declaratieAvereBunImobilInfoList = declaratieAvereBunImobilInfoList;
    }

    public List<DeclaratieAvereBunMobilInfo> getDeclaratieAvereBunMobilInfoList() {
        return declaratieAvereBunMobilInfoList;
    }

    public void setDeclaratieAvereBunMobilInfoList(List<DeclaratieAvereBunMobilInfo> declaratieAvereBunMobilInfoList) {
        this.declaratieAvereBunMobilInfoList = declaratieAvereBunMobilInfoList;
    }

    public List<DeclaratieAvereBijuterieInfo> getDeclaratieAvereBijuterieInfoList() {
        return declaratieAvereBijuterieInfoList;
    }

    public void setDeclaratieAvereBijuterieInfoList(List<DeclaratieAvereBijuterieInfo> declaratieAvereBijuterieInfoList) {
        this.declaratieAvereBijuterieInfoList = declaratieAvereBijuterieInfoList;
    }

    public List<DeclaratieAverePlasamentInfo> getDeclaratieAverePlasamentInfoList() {
        return declaratieAverePlasamentInfoList;
    }

    public void setDeclaratieAverePlasamentInfoList(List<DeclaratieAverePlasamentInfo> declaratieAverePlasamentInfoList) {
        this.declaratieAverePlasamentInfoList = declaratieAverePlasamentInfoList;
    }

    public List<DeclaratieAvereDatorieInfo> getDeclaratieAvereDatorieInfoList() {
        return declaratieAvereDatorieInfoList;
    }

    public void setDeclaratieAvereDatorieInfoList(List<DeclaratieAvereDatorieInfo> declaratieAvereDatorieInfoList) {
        this.declaratieAvereDatorieInfoList = declaratieAvereDatorieInfoList;
    }

    public List<DeclaratieAvereContInfo> getDeclaratieAvereContInfoList() {
        return declaratieAvereContInfoList;
    }

    public void setDeclaratieAvereContInfoList(List<DeclaratieAvereContInfo> declaratieAvereContInfoList) {
        this.declaratieAvereContInfoList = declaratieAvereContInfoList;
    }

    public List<DeclaratieAvereBunInstrainatInfo> getDeclaratieAvereBunInstrainatInfoList() {
        return declaratieAvereBunInstrainatInfoList;
    }

    public void setDeclaratieAvereBunInstrainatInfoList(List<DeclaratieAvereBunInstrainatInfo> declaratieAvereBunInstrainatInfoList) {
        this.declaratieAvereBunInstrainatInfoList = declaratieAvereBunInstrainatInfoList;
    }

    public List<DeclaratieAvereCadouInfo> getDeclaratieAvereCadouInfoList() {
        return declaratieAvereCadouInfoList;
    }

    public void setDeclaratieAvereCadouInfoList(List<DeclaratieAvereCadouInfo> declaratieAvereCadouInfoList) {
        this.declaratieAvereCadouInfoList = declaratieAvereCadouInfoList;
    }

    public List<DeclaratieAvereVenitInfo> getDeclaratieAvereVenitInfoList() {
        return declaratieAvereVenitInfoList;
    }

    public void setDeclaratieAvereVenitInfoList(List<DeclaratieAvereVenitInfo> declaratieAvereVenitInfoList) {
        this.declaratieAvereVenitInfoList = declaratieAvereVenitInfoList;
    }

    public String getFunctie() {
        return functie;
    }

    public void setFunctie(String functie) {
        this.functie = functie;
    }

    public String getFunctie2() {
        return functie2;
    }

    public void setFunctie2(String functie2) {
        this.functie2 = functie2;
    }

    public String getInstitutie() {
        return institutie;
    }

    public void setInstitutie(String institutie) {
        this.institutie = institutie;
    }

    public String getInstitutie2() {
        return institutie2;
    }

    public void setInstitutie2(String institutie2) {
        this.institutie2 = institutie2;
    }

    public long getNegativeTimestamp() {
        return -dataDeclaratiei.getTime();
    }

    public Boolean getIsDone() {
        return isDone;
    }

    public void setIsDone(Boolean done) {
        isDone = done;
    }
}
