package com.declaratiiavere.demnitarservice.demnitar;

import com.declaratiiavere.common.utils.JsonDateDeserializer;
import com.declaratiiavere.common.utils.JsonDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;
import java.util.List;

/**
 * Created by Amalia on 11/13/2017.
 */
public class DeclaratieIntereseInfo {
    private Integer id;
    private Integer demnitarId;
    private String demnitarNume;
    private String demnitarPrenume;
    private String anNastere;
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date dataDeclaratiei;
    private Date dataDepunerii;
    private Integer functieId;
    private Integer functie2Id;
    private Integer institutieId;
    private Integer institutie2Id;
    private String grupPolitic;
    private String linkDeclaratie;
    private String circumscriptie;
    private Integer voluntarId;
    private Boolean isDone;

    private String functie;
    private String functie2;
    private String institutie;
    private String institutie2;
    private String voluntarUserName;

    private List<DeclaratieIntereseAsociatInfo> declaratieIntereseAsociatInfoList;
    private List<DeclaratieIntereseMembruInfo> declaratieIntereseMembruInfoList;
    private List<DeclaratieIntereseSindicatInfo> declaratieIntereseSindicatInfoList;
    private List<DeclaratieInteresePartidInfo> declaratieInteresePartidInfoList;
    private List<DeclaratieIntereseContractInfo> declaratieIntereseContractInfoList;


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

    public String getCircumscriptie() {
        return circumscriptie;
    }

    public void setCircumscriptie(String circumscriptie) {
        this.circumscriptie = circumscriptie;
    }

    public Integer getVoluntarId() {
        return voluntarId;
    }

    public void setVoluntarId(Integer voluntarId) {
        this.voluntarId = voluntarId;
    }

    public Date getDataDepunerii() {
        return dataDepunerii;
    }

    public void setDataDepunerii(Date dataDepunerii) {
        this.dataDepunerii = dataDepunerii;
    }

    public String getVoluntarUserName() {
        return voluntarUserName;
    }

    public void setVoluntarUserName(String voluntarUserName) {
        this.voluntarUserName = voluntarUserName;
    }

    public List<DeclaratieIntereseAsociatInfo> getDeclaratieIntereseAsociatInfoList() {
        return declaratieIntereseAsociatInfoList;
    }

    public void setDeclaratieIntereseAsociatInfoList(List<DeclaratieIntereseAsociatInfo> declaratieIntereseAsociatInfoList) {
        this.declaratieIntereseAsociatInfoList = declaratieIntereseAsociatInfoList;
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

    public String getAnNastere() {
        return anNastere;
    }

    public void setAnNastere(String anNastere) {
        this.anNastere = anNastere;
    }

    public List<DeclaratieIntereseMembruInfo> getDeclaratieIntereseMembruInfoList() {
        return declaratieIntereseMembruInfoList;
    }

    public void setDeclaratieIntereseMembruInfoList(List<DeclaratieIntereseMembruInfo> declaratieIntereseMembruInfoList) {
        this.declaratieIntereseMembruInfoList = declaratieIntereseMembruInfoList;
    }

    public List<DeclaratieIntereseSindicatInfo> getDeclaratieIntereseSindicatInfoList() {
        return declaratieIntereseSindicatInfoList;
    }

    public void setDeclaratieIntereseSindicatInfoList(List<DeclaratieIntereseSindicatInfo> declaratieIntereseSindicatInfoList) {
        this.declaratieIntereseSindicatInfoList = declaratieIntereseSindicatInfoList;
    }

    public List<DeclaratieInteresePartidInfo> getDeclaratieInteresePartidInfoList() {
        return declaratieInteresePartidInfoList;
    }

    public void setDeclaratieInteresePartidInfoList(List<DeclaratieInteresePartidInfo> declaratieInteresePartidInfoList) {
        this.declaratieInteresePartidInfoList = declaratieInteresePartidInfoList;
    }

    public List<DeclaratieIntereseContractInfo> getDeclaratieIntereseContractInfoList() {
        return declaratieIntereseContractInfoList;
    }

    public void setDeclaratieIntereseContractInfoList(List<DeclaratieIntereseContractInfo> declaratieIntereseContractInfoList) {
        this.declaratieIntereseContractInfoList = declaratieIntereseContractInfoList;
    }
}
