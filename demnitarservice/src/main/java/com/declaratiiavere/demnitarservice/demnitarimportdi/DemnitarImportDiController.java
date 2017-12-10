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

        return declaratieIntereseInfo;
    }

    private List<DeclaratieIntereseAsociatInfo> getDeclaratieIntereseAsociatInfoList(RevenueDeclarationInfo revenueDeclarationInfo) {
        List<DeclaratieIntereseAsociatInfo> declaratieIntereseAsociatInfoList = new ArrayList<>();

        if (revenueDeclarationInfo.getExistaAsociat1().equals("Da")) {
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
        if (revenueDeclarationInfo.getExistaAsociat2().equals("Da")) {
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

        if (revenueDeclarationInfo.getExistaAsociat3().equals("Da")) {
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

        if (revenueDeclarationInfo.getExistaAsociat4().equals("Da")) {
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

        if (revenueDeclarationInfo.getExistaAsociat5().equals("Da")) {
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

        if (revenueDeclarationInfo.getExistaAsociat6().equals("Da")) {
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

        if (revenueDeclarationInfo.getExistaAsociat7().equals("Da")) {
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

        if (revenueDeclarationInfo.getExistaAsociat8().equals("Da")) {
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

        if (revenueDeclarationInfo.getExistaAsociat9().equals("Da")) {
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

        if (revenueDeclarationInfo.getExistaAsociat10().equals("Da")) {
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

        if (revenueDeclarationInfo.getExistaAsociat11().equals("Da")) {
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

        if (revenueDeclarationInfo.getExistaAsociat12().equals("Da")) {
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

        if (revenueDeclarationInfo.getExistaAsociat13().equals("Da")) {
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

        if (revenueDeclarationInfo.getExistaAsociat14().equals("Da")) {
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

        if (revenueDeclarationInfo.getExistaAsociat15().equals("Da")) {
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

            return columnMapping;
        }
    }


