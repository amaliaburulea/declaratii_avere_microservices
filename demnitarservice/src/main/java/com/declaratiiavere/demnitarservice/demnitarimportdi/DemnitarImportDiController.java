package com.declaratiiavere.demnitarservice.demnitarimportdi;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.declaratiiavere.common.utils.DateUtilities;
import com.declaratiiavere.common.utils.Utilities;
import com.declaratiiavere.demnitarservice.demnitar.*;
import com.declaratiiavere.demnitarservice.demnitarimport.ImportDemnitarInfo;
import com.declaratiiavere.demnitarservice.demnitarimport.RevenueDeclarationInfo;
import com.declaratiiavere.restclient.RestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;

import javax.validation.ValidationException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Amalia on 11/13/2017.
 */

@RestController
@RequestMapping(value = "/demnitar/importdi")
public class DemnitarImportDiController {

        @Autowired
        private DemnitarService demnitarService;

        @RequestMapping(method = RequestMethod.POST)
        public String importDemnitari(@RequestBody ImportDemnitarInfo importDemnitarInfo) throws IOException {
            StringBuilder errorBuilder = new StringBuilder();

            if (importDemnitarInfo == null) {
                throw new ValidationException("importDemnitarInfo is required");
            }

            if (Utilities.isEmptyOrNull(importDemnitarInfo.getCsvFilePath())) {
                throw new ValidationException("csvFilePath is required");
            }

            String fileContent = getFileContent(importDemnitarInfo.getCsvFilePath());
            List<RevenueDeclarationInfo> revenueDeclarationInfoList = getRevenueDeclarationInfoList(fileContent);

            for (RevenueDeclarationInfo revenueDeclarationInfo : revenueDeclarationInfoList) {
                try {
                    SearchDemnitarCriteria searchDemnitarCriteria = new SearchDemnitarCriteria();
                    searchDemnitarCriteria.setNume(revenueDeclarationInfo.getLastName());
                    searchDemnitarCriteria.setPrenume(revenueDeclarationInfo.getFirstName());
                    List<DemnitarInfo> demnitarInfoList = demnitarService.findDemnitars(searchDemnitarCriteria);

                    DemnitarInfo demnitarInfo;

                    if (demnitarInfoList.size() == 1) {
                        demnitarInfo = demnitarInfoList.get(0);
                    } else {
                        demnitarInfo = new DemnitarInfo();
                    }

                    populateDemnitarInfo(demnitarInfo, revenueDeclarationInfo);
                    demnitarInfo = demnitarService.saveDemnitar(demnitarInfo);

                    DeclaratieIntereseInfo declaratieIntereseInfo = getDeclaratieIntereseInfo(revenueDeclarationInfo, demnitarInfo.getId());

                    try {
                        demnitarService.saveDeclaratieInterese(declaratieIntereseInfo);
                    } catch (Throwable e) {
                        String exceptionMessage = "Exception for demnitar " + demnitarInfo.getNume() +
                                " " + demnitarInfo.getPrenume() + ": " + e.getMessage() + "\r\n";
                        errorBuilder.append(exceptionMessage);
                        System.out.println(errorBuilder.toString());
                        e.printStackTrace();
                    }
                } catch (Throwable e) {
                    String exceptionMessage = "Exception for demnitar " + revenueDeclarationInfo.getLastName() +
                            " " + revenueDeclarationInfo.getFirstName() + ": " + e.getMessage() + "\r\n";
                    errorBuilder.append(exceptionMessage);
                    System.out.println(exceptionMessage);
                    e.printStackTrace();
                }
            }

            if (errorBuilder.length() > 0) {
                return errorBuilder.toString();
            } else {
                return "SUCCESS";
            }
        }

        private List<RevenueDeclarationInfo> getRevenueDeclarationInfoList(String fileContent) {
            CsvToBean<RevenueDeclarationInfo> csvToBean = new CsvToBean<>();

            Map<String, String> columnMapping = getColumnMapping();

            HeaderColumnNameTranslateMappingStrategy<RevenueDeclarationInfo> strategy = new HeaderColumnNameTranslateMappingStrategy<>();
            strategy.setType(RevenueDeclarationInfo.class);
            strategy.setColumnMapping(columnMapping);

            CSVReader reader = new CSVReader(new StringReader(fileContent), ',', '"');

            return csvToBean.parse(strategy, reader);
        }

        private String getFileContent(String csvFilePath) throws IOException {
            Path path = Paths.get(csvFilePath);
            Stream<String> lines = Files.lines(path);

            Object[] linesArray = lines.toArray();

            StringBuilder builder = new StringBuilder();
            boolean isFirstLine = true;

            for (Object o : linesArray) {
                if (isFirstLine) {
                    String[] elements = ((String) o).split(",");

                    for (String element : elements) {
                        if (element.startsWith(" ")) {
                            element = element.substring(1, element.length());
                        }

                        if (element.startsWith("\"")) {
                            element = element.substring(1, element.length());
                        }

                        if (element.startsWith("[")) {
                            int endBracketIndex = element.indexOf("]");

                            if (endBracketIndex == -1) {
                                System.out.println("element = " + element);
                            }

                            builder.append(element.substring(1, endBracketIndex)).append(",");
                        } else if (element.equals("Timestamp")) {
                            builder.append(element).append(",");
                        } else if (element.equals("NR CRT")) {
                            builder.append(element).append(",");
                        } else if (element.startsWith("Bun mobil sau imobil")) {
                            builder.append(element).append(",");
                        }
                    }
                    builder.deleteCharAt(builder.length() - 1);
                    builder.append("\r\n");
                    isFirstLine = false;
                } else {
                    builder.append(o).append("\r\n");
                }
            }

            return builder.toString();
        }

    private DeclaratieIntereseInfo getDeclaratieIntereseInfo(RevenueDeclarationInfo revenueDeclarationInfo, Integer demnitarId) throws RestException {
        Date dataDeclaratiei;

        try {
            revenueDeclarationInfo.setDataDeclaratiei(revenueDeclarationInfo.getDataDeclaratiei().replaceAll("[.]", "/"));
            dataDeclaratiei = DateUtilities.parseDate(revenueDeclarationInfo.getDataDeclaratiei(), "dd/MM/yyyy");
        } catch (IllegalArgumentException ie) {
            throw new ValidationException("dataDeclaratiei nu este in formatul corect");
        }

        //noinspection deprecation
        if (dataDeclaratiei.getYear() < 0) {
            throw new ValidationException("anul din dataDeclaratiei este invalid");
        }

        SearchDeclaratieIntereseCriteria searchDeclaratieIntereseCriteria = new SearchDeclaratieIntereseCriteria();
        searchDeclaratieIntereseCriteria.setDemnitarId(demnitarId);
        searchDeclaratieIntereseCriteria.setDataDeclaratiei(dataDeclaratiei);

        List<DeclaratieIntereseInfo> declaratieIntereseInfoList = demnitarService.findDeclaratiiInterese(searchDeclaratieIntereseCriteria);

        DeclaratieIntereseInfo declaratieIntereseInfo;

        if (declaratieIntereseInfoList.isEmpty()) {
            declaratieIntereseInfo = new DeclaratieIntereseInfo();
        } else {
            declaratieIntereseInfo = declaratieIntereseInfoList.get(0);
        }

        declaratieIntereseInfo.setDemnitarId(demnitarId);
        declaratieIntereseInfo.setDataDeclaratiei(dataDeclaratiei);


        if (!revenueDeclarationInfo.getFunctie().equals("")) {
            Integer functieId = null;

            try {
                FunctieInfo functieInfo = demnitarService.getFunctieByNume(revenueDeclarationInfo.getFunctie());
                functieId = functieInfo.getId();
            } catch (ValidationException e) {
                FunctieInfo functieInfo = new FunctieInfo();
                functieInfo.setNume(revenueDeclarationInfo.getFunctie());
                functieInfo = demnitarService.saveFunctie(functieInfo);
                functieId = functieInfo.getId();
            }

            declaratieIntereseInfo.setFunctieId(functieId);
        }

        if (!revenueDeclarationInfo.getInstitutie().equals("")) {
            Integer institutieId = null;

            try {
                InstitutieInfo institutieInfo = demnitarService.getInstitutieByNume(revenueDeclarationInfo.getInstitutie());
                institutieId = institutieInfo.getId();
            } catch (ValidationException e) {
                InstitutieInfo institutieInfo = new InstitutieInfo();
                institutieInfo.setNume(revenueDeclarationInfo.getInstitutie());
                institutieInfo = demnitarService.saveInstitutie(institutieInfo);
                institutieId = institutieInfo.getId();
            }

            declaratieIntereseInfo.setInstitutieId(institutieId);
        }

        declaratieIntereseInfo.setLinkDeclaratie(revenueDeclarationInfo.getLinkDeclaratie());
        declaratieIntereseInfo.setGrupPolitic(revenueDeclarationInfo.getGrupPolitic());
        declaratieIntereseInfo.setCircumscriptie(revenueDeclarationInfo.getCircumscriptie());
        String date = revenueDeclarationInfo.getDataDepunerii();
        if (!date.contentEquals("")){
            date = date.replaceAll("[.]", "/");
            declaratieIntereseInfo.setDataDepunerii(DateUtilities.parseDate(date, "dd/MM/yyyy"));
        } else{
            declaratieIntereseInfo.setDataDepunerii(null);
        }

        declaratieIntereseInfo.setIsDone(true);

        List<DeclaratieIntereseAsociatInfo> declaratieIntereseAsociatInfoList = getDeclaratieIntereseAsociatInfoList(revenueDeclarationInfo);
        declaratieIntereseInfo.setDeclaratieIntereseAsociatInfoList(declaratieIntereseAsociatInfoList);

        List<DeclaratieIntereseMembruInfo> declaratieIntereseMembruInfoList = getDeclaratieIntereseMembruInfoList(revenueDeclarationInfo);
        declaratieIntereseInfo.setDeclaratieIntereseMembruInfoList(declaratieIntereseMembruInfoList);

        List<DeclaratieIntereseSindicatInfo> declaratieIntereseSindicatInfoList = getDeclaratieIntereseSindicatInfoList(revenueDeclarationInfo);
        declaratieIntereseInfo.setDeclaratieIntereseSindicatInfoList(declaratieIntereseSindicatInfoList);

        List<DeclaratieInteresePartidInfo> declaratieInteresePartidInfoList = getDeclaratieInteresePartidInfoList(revenueDeclarationInfo);
        declaratieIntereseInfo.setDeclaratieInteresePartidInfoList(declaratieInteresePartidInfoList);

        List<DeclaratieIntereseContractInfo> declaratieIntereseContractInfoList = getDeclaratieIntereseContractInfoList(revenueDeclarationInfo);
        declaratieIntereseInfo.setDeclaratieIntereseContractInfoList(declaratieIntereseContractInfoList);


        return declaratieIntereseInfo;
    }

    private List<DeclaratieIntereseAsociatInfo> getDeclaratieIntereseAsociatInfoList(RevenueDeclarationInfo revenueDeclarationInfo) {
        List<DeclaratieIntereseAsociatInfo> declaratieIntereseAsociatInfoList = new ArrayList<>();
        if (revenueDeclarationInfo.getExistaAsociat1().equals("DA")) {
            DeclaratieIntereseAsociatInfo declaratieIntereseAsociatInfo = new DeclaratieIntereseAsociatInfo();
            declaratieIntereseAsociatInfo.setUnitatea(revenueDeclarationInfo.getAsociat1unitatea());
            declaratieIntereseAsociatInfo.setAdresa(revenueDeclarationInfo.getAsociat1adresa());
            declaratieIntereseAsociatInfo.setRolul(revenueDeclarationInfo.getAsociat1rolul());
            declaratieIntereseAsociatInfo.setPartiSociale(revenueDeclarationInfo.getAsociat1partiSociale());
            declaratieIntereseAsociatInfo.setValoare(revenueDeclarationInfo.getAsociat1valoare());
            declaratieIntereseAsociatInfo.setMoneda(revenueDeclarationInfo.getAsociat1moneda());
            declaratieIntereseAsociatInfo.setExplicatieVenitAsoc(revenueDeclarationInfo.getAsociat1explicatie());
            declaratieIntereseAsociatInfoList.add(declaratieIntereseAsociatInfo);
        }
        if (revenueDeclarationInfo.getExistaAsociat2().equals("DA")) {
            DeclaratieIntereseAsociatInfo declaratieIntereseAsociatInfo = new DeclaratieIntereseAsociatInfo();
            declaratieIntereseAsociatInfo.setUnitatea(revenueDeclarationInfo.getAsociat2unitatea());
            declaratieIntereseAsociatInfo.setAdresa(revenueDeclarationInfo.getAsociat2adresa());
            declaratieIntereseAsociatInfo.setRolul(revenueDeclarationInfo.getAsociat2rolul());
            declaratieIntereseAsociatInfo.setPartiSociale(revenueDeclarationInfo.getAsociat2partiSociale());
            declaratieIntereseAsociatInfo.setValoare(revenueDeclarationInfo.getAsociat2valoare());
            declaratieIntereseAsociatInfo.setMoneda(revenueDeclarationInfo.getAsociat2moneda());
            declaratieIntereseAsociatInfo.setExplicatieVenitAsoc(revenueDeclarationInfo.getAsociat2explicatie());
            declaratieIntereseAsociatInfoList.add(declaratieIntereseAsociatInfo);
        }

        if (revenueDeclarationInfo.getExistaAsociat3().equals("DA")) {
            DeclaratieIntereseAsociatInfo declaratieIntereseAsociatInfo = new DeclaratieIntereseAsociatInfo();
            declaratieIntereseAsociatInfo.setUnitatea(revenueDeclarationInfo.getAsociat3unitatea());
            declaratieIntereseAsociatInfo.setAdresa(revenueDeclarationInfo.getAsociat3adresa());
            declaratieIntereseAsociatInfo.setRolul(revenueDeclarationInfo.getAsociat3rolul());
            declaratieIntereseAsociatInfo.setPartiSociale(revenueDeclarationInfo.getAsociat3partiSociale());
            declaratieIntereseAsociatInfo.setValoare(revenueDeclarationInfo.getAsociat3valoare());
            declaratieIntereseAsociatInfo.setMoneda(revenueDeclarationInfo.getAsociat3moneda());
            declaratieIntereseAsociatInfo.setExplicatieVenitAsoc(revenueDeclarationInfo.getAsociat3explicatie());
            declaratieIntereseAsociatInfoList.add(declaratieIntereseAsociatInfo);
        }

        if (revenueDeclarationInfo.getExistaAsociat4().equals("DA")) {
            DeclaratieIntereseAsociatInfo declaratieIntereseAsociatInfo = new DeclaratieIntereseAsociatInfo();
            declaratieIntereseAsociatInfo.setUnitatea(revenueDeclarationInfo.getAsociat4unitatea());
            declaratieIntereseAsociatInfo.setAdresa(revenueDeclarationInfo.getAsociat4adresa());
            declaratieIntereseAsociatInfo.setRolul(revenueDeclarationInfo.getAsociat4rolul());
            declaratieIntereseAsociatInfo.setPartiSociale(revenueDeclarationInfo.getAsociat4partiSociale());
            declaratieIntereseAsociatInfo.setValoare(revenueDeclarationInfo.getAsociat4valoare());
            declaratieIntereseAsociatInfo.setMoneda(revenueDeclarationInfo.getAsociat4moneda());
            declaratieIntereseAsociatInfo.setExplicatieVenitAsoc(revenueDeclarationInfo.getAsociat4explicatie());
            declaratieIntereseAsociatInfoList.add(declaratieIntereseAsociatInfo);
        }

        if (revenueDeclarationInfo.getExistaAsociat5().equals("DA")) {
            DeclaratieIntereseAsociatInfo declaratieIntereseAsociatInfo = new DeclaratieIntereseAsociatInfo();
            declaratieIntereseAsociatInfo.setUnitatea(revenueDeclarationInfo.getAsociat5unitatea());
            declaratieIntereseAsociatInfo.setAdresa(revenueDeclarationInfo.getAsociat5adresa());
            declaratieIntereseAsociatInfo.setRolul(revenueDeclarationInfo.getAsociat5rolul());
            declaratieIntereseAsociatInfo.setPartiSociale(revenueDeclarationInfo.getAsociat5partiSociale());
            declaratieIntereseAsociatInfo.setValoare(revenueDeclarationInfo.getAsociat5valoare());
            declaratieIntereseAsociatInfo.setMoneda(revenueDeclarationInfo.getAsociat5moneda());
            declaratieIntereseAsociatInfo.setExplicatieVenitAsoc(revenueDeclarationInfo.getAsociat5explicatie());
            declaratieIntereseAsociatInfoList.add(declaratieIntereseAsociatInfo);
        }

        if (revenueDeclarationInfo.getExistaAsociat6().equals("DA")) {
            DeclaratieIntereseAsociatInfo declaratieIntereseAsociatInfo = new DeclaratieIntereseAsociatInfo();
            declaratieIntereseAsociatInfo.setUnitatea(revenueDeclarationInfo.getAsociat6unitatea());
            declaratieIntereseAsociatInfo.setAdresa(revenueDeclarationInfo.getAsociat6adresa());
            declaratieIntereseAsociatInfo.setRolul(revenueDeclarationInfo.getAsociat6rolul());
            declaratieIntereseAsociatInfo.setPartiSociale(revenueDeclarationInfo.getAsociat6partiSociale());
            declaratieIntereseAsociatInfo.setValoare(revenueDeclarationInfo.getAsociat6valoare());
            declaratieIntereseAsociatInfo.setMoneda(revenueDeclarationInfo.getAsociat6moneda());
            declaratieIntereseAsociatInfo.setExplicatieVenitAsoc(revenueDeclarationInfo.getAsociat6explicatie());
            declaratieIntereseAsociatInfoList.add(declaratieIntereseAsociatInfo);
        }

        if (revenueDeclarationInfo.getExistaAsociat7().equals("DA")) {
            DeclaratieIntereseAsociatInfo declaratieIntereseAsociatInfo = new DeclaratieIntereseAsociatInfo();
            declaratieIntereseAsociatInfo.setUnitatea(revenueDeclarationInfo.getAsociat7unitatea());
            declaratieIntereseAsociatInfo.setAdresa(revenueDeclarationInfo.getAsociat7adresa());
            declaratieIntereseAsociatInfo.setRolul(revenueDeclarationInfo.getAsociat7rolul());
            declaratieIntereseAsociatInfo.setPartiSociale(revenueDeclarationInfo.getAsociat7partiSociale());
            declaratieIntereseAsociatInfo.setValoare(revenueDeclarationInfo.getAsociat7valoare());
            declaratieIntereseAsociatInfo.setMoneda(revenueDeclarationInfo.getAsociat7moneda());
            declaratieIntereseAsociatInfo.setExplicatieVenitAsoc(revenueDeclarationInfo.getAsociat7explicatie());
            declaratieIntereseAsociatInfoList.add(declaratieIntereseAsociatInfo);
        }

        if (revenueDeclarationInfo.getExistaAsociat8().equals("DA")) {
            DeclaratieIntereseAsociatInfo declaratieIntereseAsociatInfo = new DeclaratieIntereseAsociatInfo();
            declaratieIntereseAsociatInfo.setUnitatea(revenueDeclarationInfo.getAsociat8unitatea());
            declaratieIntereseAsociatInfo.setAdresa(revenueDeclarationInfo.getAsociat8adresa());
            declaratieIntereseAsociatInfo.setRolul(revenueDeclarationInfo.getAsociat8rolul());
            declaratieIntereseAsociatInfo.setPartiSociale(revenueDeclarationInfo.getAsociat8partiSociale());
            declaratieIntereseAsociatInfo.setValoare(revenueDeclarationInfo.getAsociat8valoare());
            declaratieIntereseAsociatInfo.setMoneda(revenueDeclarationInfo.getAsociat8moneda());
            declaratieIntereseAsociatInfo.setExplicatieVenitAsoc(revenueDeclarationInfo.getAsociat8explicatie());
            declaratieIntereseAsociatInfoList.add(declaratieIntereseAsociatInfo);
        }

        if (revenueDeclarationInfo.getExistaAsociat9().equals("DA")) {
            DeclaratieIntereseAsociatInfo declaratieIntereseAsociatInfo = new DeclaratieIntereseAsociatInfo();
            declaratieIntereseAsociatInfo.setUnitatea(revenueDeclarationInfo.getAsociat9unitatea());
            declaratieIntereseAsociatInfo.setAdresa(revenueDeclarationInfo.getAsociat9adresa());
            declaratieIntereseAsociatInfo.setRolul(revenueDeclarationInfo.getAsociat9rolul());
            declaratieIntereseAsociatInfo.setPartiSociale(revenueDeclarationInfo.getAsociat9partiSociale());
            declaratieIntereseAsociatInfo.setValoare(revenueDeclarationInfo.getAsociat9valoare());
            declaratieIntereseAsociatInfo.setMoneda(revenueDeclarationInfo.getAsociat9moneda());
            declaratieIntereseAsociatInfo.setExplicatieVenitAsoc(revenueDeclarationInfo.getAsociat9explicatie());
            declaratieIntereseAsociatInfoList.add(declaratieIntereseAsociatInfo);
        }

        if (revenueDeclarationInfo.getExistaAsociat10().equals("DA")) {
            DeclaratieIntereseAsociatInfo declaratieIntereseAsociatInfo = new DeclaratieIntereseAsociatInfo();
            declaratieIntereseAsociatInfo.setUnitatea(revenueDeclarationInfo.getAsociat10unitatea());
            declaratieIntereseAsociatInfo.setAdresa(revenueDeclarationInfo.getAsociat10adresa());
            declaratieIntereseAsociatInfo.setRolul(revenueDeclarationInfo.getAsociat10rolul());
            declaratieIntereseAsociatInfo.setPartiSociale(revenueDeclarationInfo.getAsociat10partiSociale());
            declaratieIntereseAsociatInfo.setValoare(revenueDeclarationInfo.getAsociat10valoare());
            declaratieIntereseAsociatInfo.setMoneda(revenueDeclarationInfo.getAsociat10moneda());
            declaratieIntereseAsociatInfo.setExplicatieVenitAsoc(revenueDeclarationInfo.getAsociat10explicatie());
            declaratieIntereseAsociatInfoList.add(declaratieIntereseAsociatInfo);
        }

        if (revenueDeclarationInfo.getExistaAsociat11().equals("DA")) {
            DeclaratieIntereseAsociatInfo declaratieIntereseAsociatInfo = new DeclaratieIntereseAsociatInfo();
            declaratieIntereseAsociatInfo.setUnitatea(revenueDeclarationInfo.getAsociat11unitatea());
            declaratieIntereseAsociatInfo.setAdresa(revenueDeclarationInfo.getAsociat11adresa());
            declaratieIntereseAsociatInfo.setRolul(revenueDeclarationInfo.getAsociat11rolul());
            declaratieIntereseAsociatInfo.setPartiSociale(revenueDeclarationInfo.getAsociat11partiSociale());
            declaratieIntereseAsociatInfo.setValoare(revenueDeclarationInfo.getAsociat11valoare());
            declaratieIntereseAsociatInfo.setMoneda(revenueDeclarationInfo.getAsociat11moneda());
            declaratieIntereseAsociatInfo.setExplicatieVenitAsoc(revenueDeclarationInfo.getAsociat11explicatie());
            declaratieIntereseAsociatInfoList.add(declaratieIntereseAsociatInfo);
        }

        if (revenueDeclarationInfo.getExistaAsociat12().equals("DA")) {
            DeclaratieIntereseAsociatInfo declaratieIntereseAsociatInfo = new DeclaratieIntereseAsociatInfo();
            declaratieIntereseAsociatInfo.setUnitatea(revenueDeclarationInfo.getAsociat12unitatea());
            declaratieIntereseAsociatInfo.setAdresa(revenueDeclarationInfo.getAsociat12adresa());
            declaratieIntereseAsociatInfo.setRolul(revenueDeclarationInfo.getAsociat12rolul());
            declaratieIntereseAsociatInfo.setPartiSociale(revenueDeclarationInfo.getAsociat12partiSociale());
            declaratieIntereseAsociatInfo.setValoare(revenueDeclarationInfo.getAsociat12valoare());
            declaratieIntereseAsociatInfo.setMoneda(revenueDeclarationInfo.getAsociat12moneda());
            declaratieIntereseAsociatInfo.setExplicatieVenitAsoc(revenueDeclarationInfo.getAsociat12explicatie());
            declaratieIntereseAsociatInfoList.add(declaratieIntereseAsociatInfo);
        }

        if (revenueDeclarationInfo.getExistaAsociat13().equals("DA")) {
            DeclaratieIntereseAsociatInfo declaratieIntereseAsociatInfo = new DeclaratieIntereseAsociatInfo();
            declaratieIntereseAsociatInfo.setUnitatea(revenueDeclarationInfo.getAsociat13unitatea());
            declaratieIntereseAsociatInfo.setAdresa(revenueDeclarationInfo.getAsociat13adresa());
            declaratieIntereseAsociatInfo.setRolul(revenueDeclarationInfo.getAsociat13rolul());
            declaratieIntereseAsociatInfo.setPartiSociale(revenueDeclarationInfo.getAsociat13partiSociale());
            declaratieIntereseAsociatInfo.setValoare(revenueDeclarationInfo.getAsociat13valoare());
            declaratieIntereseAsociatInfo.setMoneda(revenueDeclarationInfo.getAsociat13moneda());
            declaratieIntereseAsociatInfo.setExplicatieVenitAsoc(revenueDeclarationInfo.getAsociat13explicatie());
            declaratieIntereseAsociatInfoList.add(declaratieIntereseAsociatInfo);
        }

        if (revenueDeclarationInfo.getExistaAsociat14().equals("DA")) {
            DeclaratieIntereseAsociatInfo declaratieIntereseAsociatInfo = new DeclaratieIntereseAsociatInfo();
            declaratieIntereseAsociatInfo.setUnitatea(revenueDeclarationInfo.getAsociat14unitatea());
            declaratieIntereseAsociatInfo.setAdresa(revenueDeclarationInfo.getAsociat14adresa());
            declaratieIntereseAsociatInfo.setRolul(revenueDeclarationInfo.getAsociat14rolul());
            declaratieIntereseAsociatInfo.setPartiSociale(revenueDeclarationInfo.getAsociat14partiSociale());
            declaratieIntereseAsociatInfo.setValoare(revenueDeclarationInfo.getAsociat14valoare());
            declaratieIntereseAsociatInfo.setMoneda(revenueDeclarationInfo.getAsociat14moneda());
            declaratieIntereseAsociatInfo.setExplicatieVenitAsoc(revenueDeclarationInfo.getAsociat14explicatie());
            declaratieIntereseAsociatInfoList.add(declaratieIntereseAsociatInfo);
        }

        if (revenueDeclarationInfo.getExistaAsociat15().equals("DA")) {
            DeclaratieIntereseAsociatInfo declaratieIntereseAsociatInfo = new DeclaratieIntereseAsociatInfo();
            declaratieIntereseAsociatInfo.setUnitatea(revenueDeclarationInfo.getAsociat15unitatea());
            declaratieIntereseAsociatInfo.setAdresa(revenueDeclarationInfo.getAsociat15adresa());
            declaratieIntereseAsociatInfo.setRolul(revenueDeclarationInfo.getAsociat15rolul());
            declaratieIntereseAsociatInfo.setPartiSociale(revenueDeclarationInfo.getAsociat15partiSociale());
            declaratieIntereseAsociatInfo.setValoare(revenueDeclarationInfo.getAsociat15valoare());
            declaratieIntereseAsociatInfo.setMoneda(revenueDeclarationInfo.getAsociat15moneda());
            declaratieIntereseAsociatInfo.setExplicatieVenitAsoc(revenueDeclarationInfo.getAsociat15explicatie());
            declaratieIntereseAsociatInfoList.add(declaratieIntereseAsociatInfo);
        }

            return declaratieIntereseAsociatInfoList;
}

    private List<DeclaratieIntereseMembruInfo> getDeclaratieIntereseMembruInfoList(RevenueDeclarationInfo revenueDeclarationInfo) {
        List<DeclaratieIntereseMembruInfo> declaratieIntereseMembruInfoList = new ArrayList<>();
        if (revenueDeclarationInfo.getExistaMembru1().equals("DA")) {
            DeclaratieIntereseMembruInfo  declaratieIntereseMembruInfo = new DeclaratieIntereseMembruInfo();
            declaratieIntereseMembruInfo.setUnitatea(revenueDeclarationInfo.getMembru1unitatea());
            declaratieIntereseMembruInfo.setAdresa(revenueDeclarationInfo.getMembru1adresa());
            declaratieIntereseMembruInfo.setRolul(revenueDeclarationInfo.getMembru1rolul());
            declaratieIntereseMembruInfo.setValoare(revenueDeclarationInfo.getMembru1valoare());
            declaratieIntereseMembruInfo.setMoneda(revenueDeclarationInfo.getMembru1moneda());
            declaratieIntereseMembruInfo.setExplicatieVenitMembru(revenueDeclarationInfo.getMembru1explicatie());
            declaratieIntereseMembruInfoList.add(declaratieIntereseMembruInfo);
        }

        if (revenueDeclarationInfo.getExistaMembru2().equals("DA")) {
            DeclaratieIntereseMembruInfo  declaratieIntereseMembruInfo = new DeclaratieIntereseMembruInfo();
            declaratieIntereseMembruInfo.setUnitatea(revenueDeclarationInfo.getMembru2unitatea());
            declaratieIntereseMembruInfo.setAdresa(revenueDeclarationInfo.getMembru2adresa());
            declaratieIntereseMembruInfo.setRolul(revenueDeclarationInfo.getMembru2rolul());
            declaratieIntereseMembruInfo.setValoare(revenueDeclarationInfo.getMembru2valoare());
            declaratieIntereseMembruInfo.setMoneda(revenueDeclarationInfo.getMembru2moneda());
            declaratieIntereseMembruInfo.setExplicatieVenitMembru(revenueDeclarationInfo.getMembru2explicatie());
            declaratieIntereseMembruInfoList.add(declaratieIntereseMembruInfo);
        }

        if (revenueDeclarationInfo.getExistaMembru3().equals("DA")) {
            DeclaratieIntereseMembruInfo  declaratieIntereseMembruInfo = new DeclaratieIntereseMembruInfo();
            declaratieIntereseMembruInfo.setUnitatea(revenueDeclarationInfo.getMembru3unitatea());
            declaratieIntereseMembruInfo.setAdresa(revenueDeclarationInfo.getMembru3adresa());
            declaratieIntereseMembruInfo.setRolul(revenueDeclarationInfo.getMembru3rolul());
            declaratieIntereseMembruInfo.setValoare(revenueDeclarationInfo.getMembru3valoare());
            declaratieIntereseMembruInfo.setMoneda(revenueDeclarationInfo.getMembru3moneda());
            declaratieIntereseMembruInfo.setExplicatieVenitMembru(revenueDeclarationInfo.getMembru3explicatie());
            declaratieIntereseMembruInfoList.add(declaratieIntereseMembruInfo);
        }

        if (revenueDeclarationInfo.getExistaMembru4().equals("DA")) {
            DeclaratieIntereseMembruInfo  declaratieIntereseMembruInfo = new DeclaratieIntereseMembruInfo();
            declaratieIntereseMembruInfo.setUnitatea(revenueDeclarationInfo.getMembru4unitatea());
            declaratieIntereseMembruInfo.setAdresa(revenueDeclarationInfo.getMembru4adresa());
            declaratieIntereseMembruInfo.setRolul(revenueDeclarationInfo.getMembru4rolul());
            declaratieIntereseMembruInfo.setValoare(revenueDeclarationInfo.getMembru4valoare());
            declaratieIntereseMembruInfo.setMoneda(revenueDeclarationInfo.getMembru4moneda());
            declaratieIntereseMembruInfo.setExplicatieVenitMembru(revenueDeclarationInfo.getMembru4explicatie());
            declaratieIntereseMembruInfoList.add(declaratieIntereseMembruInfo);
        }

        if (revenueDeclarationInfo.getExistaMembru5().equals("DA")) {
            DeclaratieIntereseMembruInfo  declaratieIntereseMembruInfo = new DeclaratieIntereseMembruInfo();
            declaratieIntereseMembruInfo.setUnitatea(revenueDeclarationInfo.getMembru5unitatea());
            declaratieIntereseMembruInfo.setAdresa(revenueDeclarationInfo.getMembru5adresa());
            declaratieIntereseMembruInfo.setRolul(revenueDeclarationInfo.getMembru5rolul());
            declaratieIntereseMembruInfo.setValoare(revenueDeclarationInfo.getMembru5valoare());
            declaratieIntereseMembruInfo.setMoneda(revenueDeclarationInfo.getMembru5moneda());
            declaratieIntereseMembruInfo.setExplicatieVenitMembru(revenueDeclarationInfo.getMembru5explicatie());
            declaratieIntereseMembruInfoList.add(declaratieIntereseMembruInfo);
        }

        if (revenueDeclarationInfo.getExistaMembru6().equals("DA")) {
            DeclaratieIntereseMembruInfo  declaratieIntereseMembruInfo = new DeclaratieIntereseMembruInfo();
            declaratieIntereseMembruInfo.setUnitatea(revenueDeclarationInfo.getMembru6unitatea());
            declaratieIntereseMembruInfo.setAdresa(revenueDeclarationInfo.getMembru6adresa());
            declaratieIntereseMembruInfo.setRolul(revenueDeclarationInfo.getMembru6rolul());
            declaratieIntereseMembruInfo.setValoare(revenueDeclarationInfo.getMembru6valoare());
            declaratieIntereseMembruInfo.setMoneda(revenueDeclarationInfo.getMembru6moneda());
            declaratieIntereseMembruInfo.setExplicatieVenitMembru(revenueDeclarationInfo.getMembru6explicatie());
            declaratieIntereseMembruInfoList.add(declaratieIntereseMembruInfo);
        }
        return declaratieIntereseMembruInfoList;
    }

    private List<DeclaratieIntereseSindicatInfo> getDeclaratieIntereseSindicatInfoList(RevenueDeclarationInfo revenueDeclarationInfo) {
        List<DeclaratieIntereseSindicatInfo> declaratieIntereseSindicatInfoList = new ArrayList<>();
        if (revenueDeclarationInfo.getExistaSindicat1().equals("DA")) {
            DeclaratieIntereseSindicatInfo declaratieIntereseSindicatInfo = new DeclaratieIntereseSindicatInfo();
            declaratieIntereseSindicatInfo.setNume(revenueDeclarationInfo.getSindicat1nume());
            declaratieIntereseSindicatInfoList.add(declaratieIntereseSindicatInfo);
        }

        if (revenueDeclarationInfo.getExistaSindicat2().equals("DA")) {
            DeclaratieIntereseSindicatInfo declaratieIntereseSindicatInfo = new DeclaratieIntereseSindicatInfo();
            declaratieIntereseSindicatInfo.setNume(revenueDeclarationInfo.getSindicat2nume());
            declaratieIntereseSindicatInfoList.add(declaratieIntereseSindicatInfo);
        }

        if (revenueDeclarationInfo.getExistaSindicat3().equals("DA")) {
            DeclaratieIntereseSindicatInfo declaratieIntereseSindicatInfo = new DeclaratieIntereseSindicatInfo();
            declaratieIntereseSindicatInfo.setNume(revenueDeclarationInfo.getSindicat3nume());
            declaratieIntereseSindicatInfoList.add(declaratieIntereseSindicatInfo);
        }

        if (revenueDeclarationInfo.getExistaSindicat4().equals("DA")) {
            DeclaratieIntereseSindicatInfo declaratieIntereseSindicatInfo = new DeclaratieIntereseSindicatInfo();
            declaratieIntereseSindicatInfo.setNume(revenueDeclarationInfo.getSindicat4nume());
            declaratieIntereseSindicatInfoList.add(declaratieIntereseSindicatInfo);
        }

        return declaratieIntereseSindicatInfoList;
    }

    private List<DeclaratieInteresePartidInfo> getDeclaratieInteresePartidInfoList(RevenueDeclarationInfo revenueDeclarationInfo) {
        List<DeclaratieInteresePartidInfo> declaratieInteresePartidInfoList = new ArrayList<>();
        if (revenueDeclarationInfo.getExistaPartid1().equals("DA")) {
            DeclaratieInteresePartidInfo declaratieInteresePartidInfo = new DeclaratieInteresePartidInfo();
            declaratieInteresePartidInfo.setNume(revenueDeclarationInfo.getPartid1nume());
            declaratieInteresePartidInfo.setFunctia(revenueDeclarationInfo.getPartid1functia());
            declaratieInteresePartidInfoList.add(declaratieInteresePartidInfo);
        }

        if (revenueDeclarationInfo.getExistaPartid2().equals("DA")) {
            DeclaratieInteresePartidInfo declaratieInteresePartidInfo = new DeclaratieInteresePartidInfo();
            declaratieInteresePartidInfo.setNume(revenueDeclarationInfo.getPartid2nume());
            declaratieInteresePartidInfo.setFunctia(revenueDeclarationInfo.getPartid2functia());
            declaratieInteresePartidInfoList.add(declaratieInteresePartidInfo);
        }

        if (revenueDeclarationInfo.getExistaPartid3().equals("DA")) {
            DeclaratieInteresePartidInfo declaratieInteresePartidInfo = new DeclaratieInteresePartidInfo();
            declaratieInteresePartidInfo.setNume(revenueDeclarationInfo.getPartid3nume());
            declaratieInteresePartidInfo.setFunctia(revenueDeclarationInfo.getPartid3functia());
            declaratieInteresePartidInfoList.add(declaratieInteresePartidInfo);
        }

        if (revenueDeclarationInfo.getExistaPartid4().equals("DA")) {
            DeclaratieInteresePartidInfo declaratieInteresePartidInfo = new DeclaratieInteresePartidInfo();
            declaratieInteresePartidInfo.setNume(revenueDeclarationInfo.getPartid4nume());
            declaratieInteresePartidInfo.setFunctia(revenueDeclarationInfo.getPartid4functia());
            declaratieInteresePartidInfoList.add(declaratieInteresePartidInfo);
        }
        return declaratieInteresePartidInfoList;
        }

    private List<DeclaratieIntereseContractInfo> getDeclaratieIntereseContractInfoList(RevenueDeclarationInfo revenueDeclarationInfo) {
        List<DeclaratieIntereseContractInfo> declaratieIntereseContractInfoList = new ArrayList<>();
        if (revenueDeclarationInfo.getExistaContract1().equals("DA")) {
            DeclaratieIntereseContractInfo declaratieIntereseContractInfo = new DeclaratieIntereseContractInfo();
            declaratieIntereseContractInfo.setTitular(revenueDeclarationInfo.getContract1titular());
            declaratieIntereseContractInfo.setBeneficiar(revenueDeclarationInfo.getContract1beneficiar());
            declaratieIntereseContractInfo.setInstritutiaContractanta(revenueDeclarationInfo.getContract1instritutiaContractanta());
            declaratieIntereseContractInfo.setProcedura(revenueDeclarationInfo.getContract1procedura());
            declaratieIntereseContractInfo.setTipContract(revenueDeclarationInfo.getContract1tipContract());
            declaratieIntereseContractInfo.setData(revenueDeclarationInfo.getContract1data());
            declaratieIntereseContractInfo.setDurata(revenueDeclarationInfo.getContract1durata());
            try {
                revenueDeclarationInfo.setContract1valoare(revenueDeclarationInfo.getContract1valoare().replaceAll(",", "."));
                declaratieIntereseContractInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getContract1valoare()));
            } catch (Exception e) {
                throw new ValidationException("getContract1valoare format invalid " + revenueDeclarationInfo.getContract1valoare());
            }
            declaratieIntereseContractInfo.setMoneda(revenueDeclarationInfo.getContract1moneda());
            declaratieIntereseContractInfo.setExplicatieContract(revenueDeclarationInfo.getContract1explicatieContract());
            declaratieIntereseContractInfoList.add(declaratieIntereseContractInfo);
        }

        if (revenueDeclarationInfo.getExistaContract2().equals("DA")) {
            DeclaratieIntereseContractInfo declaratieIntereseContractInfo = new DeclaratieIntereseContractInfo();
            declaratieIntereseContractInfo.setTitular(revenueDeclarationInfo.getContract2titular());
            declaratieIntereseContractInfo.setBeneficiar(revenueDeclarationInfo.getContract2beneficiar());
            declaratieIntereseContractInfo.setInstritutiaContractanta(revenueDeclarationInfo.getContract2instritutiaContractanta());
            declaratieIntereseContractInfo.setProcedura(revenueDeclarationInfo.getContract2procedura());
            declaratieIntereseContractInfo.setTipContract(revenueDeclarationInfo.getContract2tipContract());
            declaratieIntereseContractInfo.setData(revenueDeclarationInfo.getContract2data());
            declaratieIntereseContractInfo.setDurata(revenueDeclarationInfo.getContract2durata());
            try {
                revenueDeclarationInfo.setContract2valoare(revenueDeclarationInfo.getContract2valoare().replaceAll(",", "."));
                declaratieIntereseContractInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getContract2valoare()));
            } catch (Exception e) {
                throw new ValidationException("getContract2valoare format invalid " + revenueDeclarationInfo.getContract2valoare());
            }
            declaratieIntereseContractInfo.setMoneda(revenueDeclarationInfo.getContract2moneda());
            declaratieIntereseContractInfo.setExplicatieContract(revenueDeclarationInfo.getContract2explicatieContract());
            declaratieIntereseContractInfoList.add(declaratieIntereseContractInfo);
        }

        if (revenueDeclarationInfo.getExistaContract3().equals("DA")) {
            DeclaratieIntereseContractInfo declaratieIntereseContractInfo = new DeclaratieIntereseContractInfo();
            declaratieIntereseContractInfo.setTitular(revenueDeclarationInfo.getContract3titular());
            declaratieIntereseContractInfo.setBeneficiar(revenueDeclarationInfo.getContract3beneficiar());
            declaratieIntereseContractInfo.setInstritutiaContractanta(revenueDeclarationInfo.getContract3instritutiaContractanta());
            declaratieIntereseContractInfo.setProcedura(revenueDeclarationInfo.getContract3procedura());
            declaratieIntereseContractInfo.setTipContract(revenueDeclarationInfo.getContract3tipContract());
            declaratieIntereseContractInfo.setData(revenueDeclarationInfo.getContract3data());
            declaratieIntereseContractInfo.setDurata(revenueDeclarationInfo.getContract3durata());
            try {
                revenueDeclarationInfo.setContract3valoare(revenueDeclarationInfo.getContract3valoare().replaceAll(",", "."));
                declaratieIntereseContractInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getContract3valoare()));
            } catch (Exception e) {
                throw new ValidationException("getContract3valoare format invalid " + revenueDeclarationInfo.getContract3valoare());
            }
            declaratieIntereseContractInfo.setMoneda(revenueDeclarationInfo.getContract3moneda());
            declaratieIntereseContractInfo.setExplicatieContract(revenueDeclarationInfo.getContract3explicatieContract());
            declaratieIntereseContractInfoList.add(declaratieIntereseContractInfo);
        }

        if (revenueDeclarationInfo.getExistaContract4().equals("DA")) {
            DeclaratieIntereseContractInfo declaratieIntereseContractInfo = new DeclaratieIntereseContractInfo();
            declaratieIntereseContractInfo.setTitular(revenueDeclarationInfo.getContract4titular());
            declaratieIntereseContractInfo.setBeneficiar(revenueDeclarationInfo.getContract4beneficiar());
            declaratieIntereseContractInfo.setInstritutiaContractanta(revenueDeclarationInfo.getContract4instritutiaContractanta());
            declaratieIntereseContractInfo.setProcedura(revenueDeclarationInfo.getContract4procedura());
            declaratieIntereseContractInfo.setTipContract(revenueDeclarationInfo.getContract4tipContract());
            declaratieIntereseContractInfo.setData(revenueDeclarationInfo.getContract4data());
            declaratieIntereseContractInfo.setDurata(revenueDeclarationInfo.getContract4durata());
            try {
                revenueDeclarationInfo.setContract4valoare(revenueDeclarationInfo.getContract4valoare().replaceAll(",", "."));
                declaratieIntereseContractInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getContract4valoare()));
            } catch (Exception e) {
                throw new ValidationException("getContract4valoare format invalid " + revenueDeclarationInfo.getContract4valoare());
            }
            declaratieIntereseContractInfo.setMoneda(revenueDeclarationInfo.getContract4moneda());
            declaratieIntereseContractInfo.setExplicatieContract(revenueDeclarationInfo.getContract4explicatieContract());
            declaratieIntereseContractInfoList.add(declaratieIntereseContractInfo);
        }

        if (revenueDeclarationInfo.getExistaContract5().equals("DA")) {
            DeclaratieIntereseContractInfo declaratieIntereseContractInfo = new DeclaratieIntereseContractInfo();
            declaratieIntereseContractInfo.setTitular(revenueDeclarationInfo.getContract5titular());
            declaratieIntereseContractInfo.setBeneficiar(revenueDeclarationInfo.getContract5beneficiar());
            declaratieIntereseContractInfo.setInstritutiaContractanta(revenueDeclarationInfo.getContract5instritutiaContractanta());
            declaratieIntereseContractInfo.setProcedura(revenueDeclarationInfo.getContract5procedura());
            declaratieIntereseContractInfo.setTipContract(revenueDeclarationInfo.getContract5tipContract());
            declaratieIntereseContractInfo.setData(revenueDeclarationInfo.getContract5data());
            declaratieIntereseContractInfo.setDurata(revenueDeclarationInfo.getContract5durata());
            try {
                revenueDeclarationInfo.setContract5valoare(revenueDeclarationInfo.getContract5valoare().replaceAll(",", "."));
                declaratieIntereseContractInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getContract5valoare()));
            } catch (Exception e) {
                throw new ValidationException("getContract5valoare format invalid " + revenueDeclarationInfo.getContract5valoare());
            }
            declaratieIntereseContractInfo.setMoneda(revenueDeclarationInfo.getContract5moneda());
            declaratieIntereseContractInfo.setExplicatieContract(revenueDeclarationInfo.getContract5explicatieContract());
            declaratieIntereseContractInfoList.add(declaratieIntereseContractInfo);
        }

        if (revenueDeclarationInfo.getExistaContract6().equals("DA")) {
            DeclaratieIntereseContractInfo declaratieIntereseContractInfo = new DeclaratieIntereseContractInfo();
            declaratieIntereseContractInfo.setTitular(revenueDeclarationInfo.getContract6titular());
            declaratieIntereseContractInfo.setBeneficiar(revenueDeclarationInfo.getContract6beneficiar());
            declaratieIntereseContractInfo.setInstritutiaContractanta(revenueDeclarationInfo.getContract6instritutiaContractanta());
            declaratieIntereseContractInfo.setProcedura(revenueDeclarationInfo.getContract6procedura());
            declaratieIntereseContractInfo.setTipContract(revenueDeclarationInfo.getContract6tipContract());
            declaratieIntereseContractInfo.setData(revenueDeclarationInfo.getContract6data());
            declaratieIntereseContractInfo.setDurata(revenueDeclarationInfo.getContract6durata());
            try {
                revenueDeclarationInfo.setContract6valoare(revenueDeclarationInfo.getContract6valoare().replaceAll(",", "."));
                declaratieIntereseContractInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getContract6valoare()));
            } catch (Exception e) {
                throw new ValidationException("getContract6valoare format invalid " + revenueDeclarationInfo.getContract6valoare());
            }
            declaratieIntereseContractInfo.setMoneda(revenueDeclarationInfo.getContract6moneda());
            declaratieIntereseContractInfo.setExplicatieContract(revenueDeclarationInfo.getContract6explicatieContract());
            declaratieIntereseContractInfoList.add(declaratieIntereseContractInfo);
        }

        if (revenueDeclarationInfo.getExistaContract7().equals("DA")) {
            DeclaratieIntereseContractInfo declaratieIntereseContractInfo = new DeclaratieIntereseContractInfo();
            declaratieIntereseContractInfo.setTitular(revenueDeclarationInfo.getContract7titular());
            declaratieIntereseContractInfo.setBeneficiar(revenueDeclarationInfo.getContract7beneficiar());
            declaratieIntereseContractInfo.setInstritutiaContractanta(revenueDeclarationInfo.getContract7instritutiaContractanta());
            declaratieIntereseContractInfo.setProcedura(revenueDeclarationInfo.getContract7procedura());
            declaratieIntereseContractInfo.setTipContract(revenueDeclarationInfo.getContract7tipContract());
            declaratieIntereseContractInfo.setData(revenueDeclarationInfo.getContract7data());
            declaratieIntereseContractInfo.setDurata(revenueDeclarationInfo.getContract7durata());
            try {
                revenueDeclarationInfo.setContract7valoare(revenueDeclarationInfo.getContract7valoare().replaceAll(",", "."));
                declaratieIntereseContractInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getContract7valoare()));
            } catch (Exception e) {
                throw new ValidationException("getContract7valoare format invalid " + revenueDeclarationInfo.getContract7valoare());
            }
            declaratieIntereseContractInfo.setMoneda(revenueDeclarationInfo.getContract7moneda());
            declaratieIntereseContractInfo.setExplicatieContract(revenueDeclarationInfo.getContract7explicatieContract());
            declaratieIntereseContractInfoList.add(declaratieIntereseContractInfo);
        }

        if (revenueDeclarationInfo.getExistaContract8().equals("DA")) {
            DeclaratieIntereseContractInfo declaratieIntereseContractInfo = new DeclaratieIntereseContractInfo();
            declaratieIntereseContractInfo.setTitular(revenueDeclarationInfo.getContract8titular());
            declaratieIntereseContractInfo.setBeneficiar(revenueDeclarationInfo.getContract8beneficiar());
            declaratieIntereseContractInfo.setInstritutiaContractanta(revenueDeclarationInfo.getContract8instritutiaContractanta());
            declaratieIntereseContractInfo.setProcedura(revenueDeclarationInfo.getContract8procedura());
            declaratieIntereseContractInfo.setTipContract(revenueDeclarationInfo.getContract8tipContract());
            declaratieIntereseContractInfo.setData(revenueDeclarationInfo.getContract8data());
            declaratieIntereseContractInfo.setDurata(revenueDeclarationInfo.getContract8durata());
            try {
                revenueDeclarationInfo.setContract8valoare(revenueDeclarationInfo.getContract8valoare().replaceAll(",", "."));
                declaratieIntereseContractInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getContract8valoare()));
            } catch (Exception e) {
                throw new ValidationException("getContract8valoare format invalid " + revenueDeclarationInfo.getContract8valoare());
            }
            declaratieIntereseContractInfo.setMoneda(revenueDeclarationInfo.getContract8moneda());
            declaratieIntereseContractInfo.setExplicatieContract(revenueDeclarationInfo.getContract8explicatieContract());
            declaratieIntereseContractInfoList.add(declaratieIntereseContractInfo);
        }

        if (revenueDeclarationInfo.getExistaContract9().equals("DA")) {
            DeclaratieIntereseContractInfo declaratieIntereseContractInfo = new DeclaratieIntereseContractInfo();
            declaratieIntereseContractInfo.setTitular(revenueDeclarationInfo.getContract9titular());
            declaratieIntereseContractInfo.setBeneficiar(revenueDeclarationInfo.getContract9beneficiar());
            declaratieIntereseContractInfo.setInstritutiaContractanta(revenueDeclarationInfo.getContract9instritutiaContractanta());
            declaratieIntereseContractInfo.setProcedura(revenueDeclarationInfo.getContract9procedura());
            declaratieIntereseContractInfo.setTipContract(revenueDeclarationInfo.getContract9tipContract());
            declaratieIntereseContractInfo.setData(revenueDeclarationInfo.getContract9data());
            declaratieIntereseContractInfo.setDurata(revenueDeclarationInfo.getContract9durata());
            try {
                revenueDeclarationInfo.setContract9valoare(revenueDeclarationInfo.getContract9valoare().replaceAll(",", "."));
                declaratieIntereseContractInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getContract9valoare()));
            } catch (Exception e) {
                throw new ValidationException("getContract9valoare format invalid " + revenueDeclarationInfo.getContract9valoare());
            }
            declaratieIntereseContractInfo.setMoneda(revenueDeclarationInfo.getContract9moneda());
            declaratieIntereseContractInfo.setExplicatieContract(revenueDeclarationInfo.getContract9explicatieContract());
            declaratieIntereseContractInfoList.add(declaratieIntereseContractInfo);
        }

        if (revenueDeclarationInfo.getExistaContract10().equals("DA")) {
            DeclaratieIntereseContractInfo declaratieIntereseContractInfo = new DeclaratieIntereseContractInfo();
            declaratieIntereseContractInfo.setTitular(revenueDeclarationInfo.getContract10titular());
            declaratieIntereseContractInfo.setBeneficiar(revenueDeclarationInfo.getContract10beneficiar());
            declaratieIntereseContractInfo.setInstritutiaContractanta(revenueDeclarationInfo.getContract10instritutiaContractanta());
            declaratieIntereseContractInfo.setProcedura(revenueDeclarationInfo.getContract10procedura());
            declaratieIntereseContractInfo.setTipContract(revenueDeclarationInfo.getContract10tipContract());
            declaratieIntereseContractInfo.setData(revenueDeclarationInfo.getContract10data());
            declaratieIntereseContractInfo.setDurata(revenueDeclarationInfo.getContract10durata());
            try {
                revenueDeclarationInfo.setContract10valoare(revenueDeclarationInfo.getContract10valoare().replaceAll(",", "."));
                declaratieIntereseContractInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getContract10valoare()));
            } catch (Exception e) {
                throw new ValidationException("getContract10valoare format invalid " + revenueDeclarationInfo.getContract10valoare());
            }
            declaratieIntereseContractInfo.setMoneda(revenueDeclarationInfo.getContract10moneda());
            declaratieIntereseContractInfo.setExplicatieContract(revenueDeclarationInfo.getContract10explicatieContract());
            declaratieIntereseContractInfoList.add(declaratieIntereseContractInfo);
        }

        if (revenueDeclarationInfo.getExistaContract11().equals("DA")) {
            DeclaratieIntereseContractInfo declaratieIntereseContractInfo = new DeclaratieIntereseContractInfo();
            declaratieIntereseContractInfo.setTitular(revenueDeclarationInfo.getContract11titular());
            declaratieIntereseContractInfo.setBeneficiar(revenueDeclarationInfo.getContract11beneficiar());
            declaratieIntereseContractInfo.setInstritutiaContractanta(revenueDeclarationInfo.getContract11instritutiaContractanta());
            declaratieIntereseContractInfo.setProcedura(revenueDeclarationInfo.getContract11procedura());
            declaratieIntereseContractInfo.setTipContract(revenueDeclarationInfo.getContract11tipContract());
            declaratieIntereseContractInfo.setData(revenueDeclarationInfo.getContract11data());
            declaratieIntereseContractInfo.setDurata(revenueDeclarationInfo.getContract11durata());
            try {
                revenueDeclarationInfo.setContract11valoare(revenueDeclarationInfo.getContract11valoare().replaceAll(",", "."));
                declaratieIntereseContractInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getContract11valoare()));
            } catch (Exception e) {
                throw new ValidationException("getContract11valoare format invalid " + revenueDeclarationInfo.getContract11valoare());
            }
            declaratieIntereseContractInfo.setMoneda(revenueDeclarationInfo.getContract11moneda());
            declaratieIntereseContractInfo.setExplicatieContract(revenueDeclarationInfo.getContract11explicatieContract());
            declaratieIntereseContractInfoList.add(declaratieIntereseContractInfo);
        }

        if (revenueDeclarationInfo.getExistaContract12().equals("DA")) {
            DeclaratieIntereseContractInfo declaratieIntereseContractInfo = new DeclaratieIntereseContractInfo();
            declaratieIntereseContractInfo.setTitular(revenueDeclarationInfo.getContract12titular());
            declaratieIntereseContractInfo.setBeneficiar(revenueDeclarationInfo.getContract12beneficiar());
            declaratieIntereseContractInfo.setInstritutiaContractanta(revenueDeclarationInfo.getContract12instritutiaContractanta());
            declaratieIntereseContractInfo.setProcedura(revenueDeclarationInfo.getContract12procedura());
            declaratieIntereseContractInfo.setTipContract(revenueDeclarationInfo.getContract12tipContract());
            declaratieIntereseContractInfo.setData(revenueDeclarationInfo.getContract12data());
            declaratieIntereseContractInfo.setDurata(revenueDeclarationInfo.getContract12durata());
            try {
                revenueDeclarationInfo.setContract12valoare(revenueDeclarationInfo.getContract12valoare().replaceAll(",", "."));
                declaratieIntereseContractInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getContract12valoare()));
            } catch (Exception e) {
                throw new ValidationException("getContract12valoare format invalid " + revenueDeclarationInfo.getContract12valoare());
            }
            declaratieIntereseContractInfo.setMoneda(revenueDeclarationInfo.getContract12moneda());
            declaratieIntereseContractInfo.setExplicatieContract(revenueDeclarationInfo.getContract12explicatieContract());
            declaratieIntereseContractInfoList.add(declaratieIntereseContractInfo);
        }

        if (revenueDeclarationInfo.getExistaContract13().equals("DA")) {
            DeclaratieIntereseContractInfo declaratieIntereseContractInfo = new DeclaratieIntereseContractInfo();
            declaratieIntereseContractInfo.setTitular(revenueDeclarationInfo.getContract13titular());
            declaratieIntereseContractInfo.setBeneficiar(revenueDeclarationInfo.getContract13beneficiar());
            declaratieIntereseContractInfo.setInstritutiaContractanta(revenueDeclarationInfo.getContract13instritutiaContractanta());
            declaratieIntereseContractInfo.setProcedura(revenueDeclarationInfo.getContract13procedura());
            declaratieIntereseContractInfo.setTipContract(revenueDeclarationInfo.getContract13tipContract());
            declaratieIntereseContractInfo.setData(revenueDeclarationInfo.getContract13data());
            declaratieIntereseContractInfo.setDurata(revenueDeclarationInfo.getContract13durata());
            try {
                revenueDeclarationInfo.setContract13valoare(revenueDeclarationInfo.getContract13valoare().replaceAll(",", "."));
                declaratieIntereseContractInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getContract13valoare()));
            } catch (Exception e) {
                throw new ValidationException("getContract13valoare format invalid " + revenueDeclarationInfo.getContract13valoare());
            }
            declaratieIntereseContractInfo.setMoneda(revenueDeclarationInfo.getContract13moneda());
            declaratieIntereseContractInfo.setExplicatieContract(revenueDeclarationInfo.getContract13explicatieContract());
            declaratieIntereseContractInfoList.add(declaratieIntereseContractInfo);
        }
        return declaratieIntereseContractInfoList;
    }

        private void populateDemnitarInfo(DemnitarInfo demnitarInfo, RevenueDeclarationInfo revenueDeclarationInfo) {
            demnitarInfo.setNume(revenueDeclarationInfo.getLastName());
            demnitarInfo.setPrenume(revenueDeclarationInfo.getFirstName());
            demnitarInfo.setAnNastere(revenueDeclarationInfo.getAnNastere());
        }

        private static Map<String, String> getColumnMapping() {
            Map<String, String> columnMapping = new HashMap<>();
            columnMapping.put("pnDem", "firstName");
            columnMapping.put("nDem", "lastName");
            columnMapping.put("datSem", "dataDeclaratiei");
            columnMapping.put("datDec", "dataDepunerii");
            columnMapping.put("funct", "functie");
            columnMapping.put("inst", "institutie");
            columnMapping.put("l_Fis", "linkDeclaratie");
            columnMapping.put("circ", "circumscriptie");
            columnMapping.put("part", "grupPolitic");
            columnMapping.put("datNDem", "anNastere");

            // asociat 1

            columnMapping.put("isAssoc", "existaAsociat1");
            columnMapping.put("companyName", "asociat1unitatea");
            columnMapping.put("companyAddress", "asociat1adresa");
            columnMapping.put("roleInComp", "asociat1rolul");
            columnMapping.put("noAct", "asociat1partiSociale");
            columnMapping.put("valAssoc", "asociat1valoare");
            columnMapping.put("currAssoc", "asociat1moneda");
            columnMapping.put("valAssoc_e", "asociat1explicatie");

            //asociat 2

            columnMapping.put("moreAssoc", "existaAsociat2");
            columnMapping.put("companyName2", "asociat2unitatea");
            columnMapping.put("companyAddress2", "asociat2adresa");
            columnMapping.put("roleInComp2", "asociat2rolul");
            columnMapping.put("noAct2", "asociat2partiSociale");
            columnMapping.put("valAssoc2", "asociat2valoare");
            columnMapping.put("currAssoc2", "asociat2moneda");
            columnMapping.put("valAssoc_e2", "asociat2explicatie");

            //asociat 3
            columnMapping.put("moreAssoc2", "existaAsociat3");
            columnMapping.put("companyName3", "asociat3unitatea");
            columnMapping.put("companyAddress3", "asociat3adresa");
            columnMapping.put("roleInComp3", "asociat3rolul");
            columnMapping.put("noAct3", "asociat3partiSociale");
            columnMapping.put("valAssoc3", "asociat3valoare");
            columnMapping.put("currAssoc3", "asociat3moneda");
            columnMapping.put("valAssoc_e3", "asociat3explicatie");

            //asociat 4
            columnMapping.put("moreAssoc3", "existaAsociat4");
            columnMapping.put("companyName4", "asociat4unitatea");
            columnMapping.put("companyAddress4", "asociat4adresa");
            columnMapping.put("roleInComp4", "asociat4rolul");
            columnMapping.put("noAct4", "asociat4partiSociale");
            columnMapping.put("valAssoc4", "asociat4valoare");
            columnMapping.put("currAssoc4", "asociat4moneda");
            columnMapping.put("valAssoc_e4", "asociat4explicatie");

            //asociat 5
            columnMapping.put("moreAssoc4", "existaAsociat5");
            columnMapping.put("companyName5", "asociat5unitatea");
            columnMapping.put("companyAddress5", "asociat5adresa");
            columnMapping.put("roleInComp5", "asociat5rolul");
            columnMapping.put("noAct5", "asociat5partiSociale");
            columnMapping.put("valAssoc5", "asociat5valoare");
            columnMapping.put("currAssoc5", "asociat5moneda");
            columnMapping.put("valAssoc_e5", "asociat5explicatie");

            //asociat 6
            columnMapping.put("moreAssoc5", "existaAsociat6");
            columnMapping.put("companyName6", "asociat6unitatea");
            columnMapping.put("companyAddress6", "asociat6adresa");
            columnMapping.put("roleInComp6", "asociat6rolul");
            columnMapping.put("noAct6", "asociat6partiSociale");
            columnMapping.put("valAssoc6", "asociat6valoare");
            columnMapping.put("currAssoc6", "asociat6moneda");
            columnMapping.put("valAssoc_e6", "asociat6explicatie");

            //asociat 7
            columnMapping.put("moreAssoc6", "existaAsociat7");
            columnMapping.put("companyName7", "asociat7unitatea");
            columnMapping.put("companyAddress7", "asociat7adresa");
            columnMapping.put("roleInComp7", "asociat7rolul");
            columnMapping.put("noAct7", "asociat7partiSociale");
            columnMapping.put("valAssoc7", "asociat7valoare");
            columnMapping.put("currAssoc7", "asociat7moneda");
            columnMapping.put("valAssoc_e7", "asociat7explicatie");

            //asociat 8
            columnMapping.put("moreAssoc7", "existaAsociat8");
            columnMapping.put("companyName8", "asociat8unitatea");
            columnMapping.put("companyAddress8", "asociat8adresa");
            columnMapping.put("roleInComp8", "asociat8rolul");
            columnMapping.put("noAct8", "asociat8partiSociale");
            columnMapping.put("valAssoc8", "asociat8valoare");
            columnMapping.put("currAssoc8", "asociat8moneda");
            columnMapping.put("valAssoc_e8", "asociat8explicatie");

            //asociat 9
            columnMapping.put("moreAssoc8", "existaAsociat9");
            columnMapping.put("companyName9", "asociat9unitatea");
            columnMapping.put("companyAddress9", "asociat9adresa");
            columnMapping.put("roleInComp9", "asociat9rolul");
            columnMapping.put("noAct9", "asociat9partiSociale");
            columnMapping.put("valAssoc9", "asociat9valoare");
            columnMapping.put("currAssoc9", "asociat9moneda");
            columnMapping.put("valAssoc_e9", "asociat9explicatie");

            //asociat 10
            columnMapping.put("moreAssoc9", "existaAsociat10");
            columnMapping.put("companyName10", "asociat10unitatea");
            columnMapping.put("companyAddress10", "asociat10adresa");
            columnMapping.put("roleInComp10", "asociat10rolul");
            columnMapping.put("noAct10", "asociat10partiSociale");
            columnMapping.put("valAssoc10", "asociat10valoare");
            columnMapping.put("currAssoc10", "asociat10moneda");
            columnMapping.put("valAssoc_e10", "asociat10explicatie");

            //asociat 11
            columnMapping.put("moreAssoc10", "existaAsociat11");
            columnMapping.put("companyName11", "asociat11unitatea");
            columnMapping.put("companyAddress11", "asociat11adresa");
            columnMapping.put("roleInComp11", "asociat11rolul");
            columnMapping.put("noAct11", "asociat11partiSociale");
            columnMapping.put("valAssoc11", "asociat11valoare");
            columnMapping.put("currAssoc11", "asociat11moneda");
            columnMapping.put("valAssoc_e11", "asociat11explicatie");

            //asociat 12
            columnMapping.put("moreAssoc11", "existaAsociat12");
            columnMapping.put("companyName12", "asociat12unitatea");
            columnMapping.put("companyAddress12", "asociat12adresa");
            columnMapping.put("roleInComp12", "asociat12rolul");
            columnMapping.put("noAct12", "asociat12partiSociale");
            columnMapping.put("valAssoc12", "asociat12valoare");
            columnMapping.put("currAssoc12", "asociat12moneda");
            columnMapping.put("valAssoc_e12", "asociat12explicatie");

            //asociat 13
            columnMapping.put("moreAssoc12", "existaAsociat13");
            columnMapping.put("companyName13", "asociat13unitatea");
            columnMapping.put("companyAddress13", "asociat13adresa");
            columnMapping.put("roleInComp13", "asociat13rolul");
            columnMapping.put("noAct13", "asociat13partiSociale");
            columnMapping.put("valAssoc13", "asociat13valoare");
            columnMapping.put("currAssoc13", "asociat13moneda");
            columnMapping.put("valAssoc_e13", "asociat13explicatie");

            //asociat 14
            columnMapping.put("moreAssoc13", "existaAsociat14");
            columnMapping.put("companyName14", "asociat14unitatea");
            columnMapping.put("companyAddress14", "asociat14adresa");
            columnMapping.put("roleInComp14", "asociat14rolul");
            columnMapping.put("noAct14", "asociat14partiSociale");
            columnMapping.put("valAssoc14", "asociat14valoare");
            columnMapping.put("currAssoc14", "asociat14moneda");
            columnMapping.put("valAssoc_e14", "asociat14explicatie");

            //asociat 15
            columnMapping.put("moreAssoc14", "existaAsociat15");
            columnMapping.put("companyName15", "asociat15unitatea");
            columnMapping.put("companyAddress15", "asociat15adresa");
            columnMapping.put("roleInComp15", "asociat15rolul");
            columnMapping.put("noAct15", "asociat15partiSociale");
            columnMapping.put("valAssoc15", "asociat15valoare");
            columnMapping.put("currAssoc15", "asociat15moneda");
            columnMapping.put("valAssoc_e15", "asociat15explicatie");

            //membru1
            columnMapping.put("compMember", "existaMembru1");
            columnMapping.put("compNameMem", "membru1unitatea");
            columnMapping.put("companyAddressMem", "membru1adresa");
            columnMapping.put("roleMem", "membru1rolul");
            columnMapping.put("valueMem", "membru1valoare");
            columnMapping.put("currencyMem", "membru1moneda");
            columnMapping.put("valMem_e", "membru1explicatie");

            //membru2
            columnMapping.put("moreMem", "existaMembru2");
            columnMapping.put("compNameMem2", "membru2unitatea");
            columnMapping.put("companyAddressMem2", "membru2adresa");
            columnMapping.put("roleMem2", "membru2rolul");
            columnMapping.put("valueMem2", "membru2valoare");
            columnMapping.put("currencyMem2", "membru2moneda");
            columnMapping.put("valMem_e2", "membru2explicatie");

            //membru3
            columnMapping.put("moreMem2", "existaMembru3");
            columnMapping.put("compNameMem3", "membru3unitatea");
            columnMapping.put("companyAddressMem3", "membru3adresa");
            columnMapping.put("roleMem3", "membru3rolul");
            columnMapping.put("valueMem3", "membru3valoare");
            columnMapping.put("currencyMem3", "membru3moneda");
            columnMapping.put("valMem_e3", "membru3explicatie");

            //membru4
            columnMapping.put("moreMem3", "existaMembru4");
            columnMapping.put("compNameMem4", "membru4unitatea");
            columnMapping.put("companyAddressMem4", "membru4adresa");
            columnMapping.put("roleMem4", "membru4rolul");
            columnMapping.put("valueMem4", "membru4valoare");
            columnMapping.put("currencyMem4", "membru4moneda");
            columnMapping.put("valMem_e4", "membru4explicatie");

            //membru5
            columnMapping.put("moreMem4", "existaMembru5");
            columnMapping.put("compNameMem5", "membru5unitatea");
            columnMapping.put("companyAddressMem5", "membru5adresa");
            columnMapping.put("roleMem5", "membru5rolul");
            columnMapping.put("valueMem5", "membru5valoare");
            columnMapping.put("currencyMem5", "membru5moneda");
            columnMapping.put("valMem_e5", "membru5explicatie");

            //membru6
            columnMapping.put("moreMem5", "existaMembru6");
            columnMapping.put("compNameMem6", "membru6unitatea");
            columnMapping.put("companyAddressMem6", "membru6adresa");
            columnMapping.put("roleMem6", "membru6rolul");
            columnMapping.put("valueMem6", "membru6valoare");
            columnMapping.put("currencyMem6", "membru6moneda");
            columnMapping.put("valMem_e6", "membru6explicatie");

            //sindicat 1
            columnMapping.put("isSind", "existaSindicat1");
            columnMapping.put("nameSind", "sindicat1nume");

            //sindicat 2
            columnMapping.put("moreSind", "existaSindicat2");
            columnMapping.put("nameSind2", "sindicat2nume");

            //sindicat 3
            columnMapping.put("moreSind2", "existaSindicat3");
            columnMapping.put("nameSind3", "sindicat3nume");

            //sindicat 4
            columnMapping.put("moreSind3", "existaSindicat4");
            columnMapping.put("nameSind4", "sindicat4nume");

            //partid 1
            columnMapping.put("isPol", "existaPartid1");
            columnMapping.put("namePol", "partid1nume");
            columnMapping.put("rolePol", "partid1functia");

            //partid 2
            columnMapping.put("morePol", "existaPartid2");
            columnMapping.put("namePol2", "partid2nume");
            columnMapping.put("rolePol2", "partid2functia");

            //partid 3
            columnMapping.put("morePol2", "existaPartid3");
            columnMapping.put("namePol3", "partid3nume");
            columnMapping.put("rolePol3", "partid3functia");

            //partid 4
            columnMapping.put("morePol3", "existaPartid4");
            columnMapping.put("namePol4", "partid4nume");
            columnMapping.put("rolePol4", "partid4functia");

            //contract 1
            columnMapping.put("isContr", "existaContract1");
            columnMapping.put("relContr", "contract1titular");
            columnMapping.put("benContr", "contract1beneficiar");
            columnMapping.put("insCont", "contract1instritutiaContractanta");
            columnMapping.put("procCont", "contract1procedura");
            columnMapping.put("typeCon", "contract1tipContract");
            columnMapping.put("dateCont", "contract1data");
            columnMapping.put("durCont", "contract1durata");
            columnMapping.put("valCont", "contract1valoare");
            columnMapping.put("currencyCont", "contract1moneda");
            columnMapping.put("valCont_e", "contract1explicatieContract");

            //contract 2
            columnMapping.put("moreCont", "existaContract2");
            columnMapping.put("relContr2", "contract2titular");
            columnMapping.put("benContr2", "contract2beneficiar");
            columnMapping.put("insCont2", "contract2instritutiaContractanta");
            columnMapping.put("procCont2", "contract2procedura");
            columnMapping.put("typeCon2", "contract2tipContract");
            columnMapping.put("dateCont2", "contract2data");
            columnMapping.put("durCont2", "contract2durata");
            columnMapping.put("valCont2", "contract2valoare");
            columnMapping.put("currencyCont2", "contract2moneda");
            columnMapping.put("valCont_e2", "contract2explicatieContract");

            //contract 3
            columnMapping.put("moreCont2", "existaContract3");
            columnMapping.put("relContr3", "contract3titular");
            columnMapping.put("benContr3", "contract3beneficiar");
            columnMapping.put("insCont3", "contract3instritutiaContractanta");
            columnMapping.put("procCont3", "contract3procedura");
            columnMapping.put("typeCon3", "contract3tipContract");
            columnMapping.put("dateCont3", "contract3data");
            columnMapping.put("durCont3", "contract3durata");
            columnMapping.put("valCont3", "contract3valoare");
            columnMapping.put("currencyCont3", "contract3moneda");
            columnMapping.put("valCont_e3", "contract3explicatieContract");

            //contract 4
            columnMapping.put("moreCont3", "existaContract4");
            columnMapping.put("relContr4", "contract4titular");
            columnMapping.put("benContr4", "contract4beneficiar");
            columnMapping.put("insCont4", "contract4instritutiaContractanta");
            columnMapping.put("procCont4", "contract4procedura");
            columnMapping.put("typeCon4", "contract4tipContract");
            columnMapping.put("dateCont4", "contract4data");
            columnMapping.put("durCont4", "contract4durata");
            columnMapping.put("valCont4", "contract4valoare");
            columnMapping.put("currencyCont4", "contract4moneda");
            columnMapping.put("valCont_e4", "contract4explicatieContract");

            //contract 5
            columnMapping.put("moreCont4", "existaContract5");
            columnMapping.put("relContr5", "contract5titular");
            columnMapping.put("benContr5", "contract5beneficiar");
            columnMapping.put("insCont5", "contract5instritutiaContractanta");
            columnMapping.put("procCont5", "contract5procedura");
            columnMapping.put("typeCon5", "contract5tipContract");
            columnMapping.put("dateCont5", "contract5data");
            columnMapping.put("durCont5", "contract5durata");
            columnMapping.put("valCont5", "contract5valoare");
            columnMapping.put("currencyCont5", "contract5moneda");
            columnMapping.put("valCont_e5", "contract5explicatieContract");

            //contract 6
            columnMapping.put("moreCont5", "existaContract6");
            columnMapping.put("relContr6", "contract6titular");
            columnMapping.put("benContr6", "contract6beneficiar");
            columnMapping.put("insCont6", "contract6instritutiaContractanta");
            columnMapping.put("procCont6", "contract6procedura");
            columnMapping.put("typeCon6", "contract6tipContract");
            columnMapping.put("dateCont6", "contract6data");
            columnMapping.put("durCont6", "contract6durata");
            columnMapping.put("valCont6", "contract6valoare");
            columnMapping.put("currencyCont6", "contract6moneda");
            columnMapping.put("valCont_e6", "contract6explicatieContract");

            //contract 7
            columnMapping.put("moreCont6", "existaContract7");
            columnMapping.put("relContr7", "contract7titular");
            columnMapping.put("benContr7", "contract7beneficiar");
            columnMapping.put("insCont7", "contract7instritutiaContractanta");
            columnMapping.put("procCont7", "contract7procedura");
            columnMapping.put("typeCon7", "contract7tipContract");
            columnMapping.put("dateCont7", "contract7data");
            columnMapping.put("durCont7", "contract7durata");
            columnMapping.put("valCont7", "contract7valoare");
            columnMapping.put("currencyCont7", "contract7moneda");
            columnMapping.put("valCont_e7", "contract7explicatieContract");

            //contract 8
            columnMapping.put("moreCont7", "existaContract8");
            columnMapping.put("relContr8", "contract8titular");
            columnMapping.put("benContr8", "contract8beneficiar");
            columnMapping.put("insCont8", "contract8instritutiaContractanta");
            columnMapping.put("procCont8", "contract8procedura");
            columnMapping.put("typeCon8", "contract8tipContract");
            columnMapping.put("dateCont8", "contract8data");
            columnMapping.put("durCont8", "contract8durata");
            columnMapping.put("valCont8", "contract8valoare");
            columnMapping.put("currencyCont8", "contract8moneda");
            columnMapping.put("valCont_e8", "contract8explicatieContract");

            //contract 9
            columnMapping.put("moreCont8", "existaContract9");
            columnMapping.put("relContr9", "contract9titular");
            columnMapping.put("benContr9", "contract9beneficiar");
            columnMapping.put("insCont9", "contract9instritutiaContractanta");
            columnMapping.put("procCont9", "contract9procedura");
            columnMapping.put("typeCon9", "contract9tipContract");
            columnMapping.put("dateCont9", "contract9data");
            columnMapping.put("durCont9", "contract9durata");
            columnMapping.put("valCont9", "contract9valoare");
            columnMapping.put("currencyCont9", "contract9moneda");
            columnMapping.put("valCont_e9", "contract9explicatieContract");

            //contract 10
            columnMapping.put("moreCont9", "existaContract10");
            columnMapping.put("relContr10", "contract10titular");
            columnMapping.put("benContr10", "contract10beneficiar");
            columnMapping.put("insCont10", "contract10instritutiaContractanta");
            columnMapping.put("procCont10", "contract10procedura");
            columnMapping.put("typeCon10", "contract10tipContract");
            columnMapping.put("dateCont10", "contract10data");
            columnMapping.put("durCont10", "contract10durata");
            columnMapping.put("valCont10", "contract10valoare");
            columnMapping.put("currencyCont10", "contract10moneda");
            columnMapping.put("valCont_e10", "contract10explicatieContract");

            //contract 11
            columnMapping.put("moreCont10", "existaContract11");
            columnMapping.put("relContr11", "contract11titular");
            columnMapping.put("benContr11", "contract11beneficiar");
            columnMapping.put("insCont11", "contract11instritutiaContractanta");
            columnMapping.put("procCont11", "contract11procedura");
            columnMapping.put("typeCon11", "contract11tipContract");
            columnMapping.put("dateCont11", "contract11data");
            columnMapping.put("durCont11", "contract11durata");
            columnMapping.put("valCont11", "contract11valoare");
            columnMapping.put("currencyCont11", "contract11moneda");
            columnMapping.put("valCont_e11", "contract11explicatieContract");

            //contract 12
            columnMapping.put("moreCont11", "existaContract12");
            columnMapping.put("relContr12", "contract12titular");
            columnMapping.put("benContr12", "contract12beneficiar");
            columnMapping.put("insCont12", "contract12instritutiaContractanta");
            columnMapping.put("procCont12", "contract12procedura");
            columnMapping.put("typeCon12", "contract12tipContract");
            columnMapping.put("dateCont12", "contract12data");
            columnMapping.put("durCont12", "contract12durata");
            columnMapping.put("valCont12", "contract12valoare");
            columnMapping.put("currencyCont12", "contract12moneda");
            columnMapping.put("valCont_e12", "contract12explicatieContract");


            //contract 13
            columnMapping.put("moreCont12", "existaContract13");
            columnMapping.put("relContr13", "contract13titular");
            columnMapping.put("benContr13", "contract13beneficiar");
            columnMapping.put("insCont13", "contract13instritutiaContractanta");
            columnMapping.put("procCont13", "contract13procedura");
            columnMapping.put("typeCon13", "contract13tipContract");
            columnMapping.put("dateCont13", "contract13data");
            columnMapping.put("durCont13", "contract13durata");
            columnMapping.put("valCont13", "contract13valoare");
            columnMapping.put("currencyCont13", "contract13moneda");
            columnMapping.put("valCont_e13", "contract13explicatieContract");

            return columnMapping;
        }
    }


