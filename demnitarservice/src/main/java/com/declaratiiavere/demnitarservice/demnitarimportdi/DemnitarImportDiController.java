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
import java.math.BigDecimal;
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
        String date = revenueDeclarationInfo.getDataDepunerii().replaceAll("[.]", "/");
        declaratieIntereseInfo.setDataDepunerii(DateUtilities.parseDate(date, "dd/MM/yyyy"));
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
            declaratieIntereseAsociatInfo.setRolul(revenueDeclarationInfo.getAsociat1rolul());
            declaratieIntereseAsociatInfo.setPartiSociale(revenueDeclarationInfo.getAsociat1partiSociale());
            declaratieIntereseAsociatInfo.setValoare(revenueDeclarationInfo.getAsociat1valoare());
            declaratieIntereseAsociatInfo.setMoneda(revenueDeclarationInfo.getAsociat1moneda());
            declaratieIntereseAsociatInfo.setExplicatie(revenueDeclarationInfo.getAsociat1explicatie());
            declaratieIntereseAsociatInfoList.add(declaratieIntereseAsociatInfo);
        }
        if (revenueDeclarationInfo.getExistaAsociat2().equals("Da")) {
            DeclaratieIntereseAsociatInfo declaratieIntereseAsociatInfo = new DeclaratieIntereseAsociatInfo();
            declaratieIntereseAsociatInfo.setUnitatea(revenueDeclarationInfo.getAsociat2unitatea());
            declaratieIntereseAsociatInfo.setRolul(revenueDeclarationInfo.getAsociat2rolul());
            declaratieIntereseAsociatInfo.setPartiSociale(revenueDeclarationInfo.getAsociat2partiSociale());
            declaratieIntereseAsociatInfo.setValoare(revenueDeclarationInfo.getAsociat2valoare());
            declaratieIntereseAsociatInfo.setMoneda(revenueDeclarationInfo.getAsociat2moneda());
            declaratieIntereseAsociatInfo.setExplicatie(revenueDeclarationInfo.getAsociat2explicatie());
            declaratieIntereseAsociatInfoList.add(declaratieIntereseAsociatInfo);
        }

        if (revenueDeclarationInfo.getExistaAsociat3().equals("Da")) {
            DeclaratieIntereseAsociatInfo declaratieIntereseAsociatInfo = new DeclaratieIntereseAsociatInfo();
            declaratieIntereseAsociatInfo.setUnitatea(revenueDeclarationInfo.getAsociat3unitatea());
            declaratieIntereseAsociatInfo.setRolul(revenueDeclarationInfo.getAsociat3rolul());
            declaratieIntereseAsociatInfo.setPartiSociale(revenueDeclarationInfo.getAsociat3partiSociale());
            declaratieIntereseAsociatInfo.setValoare(revenueDeclarationInfo.getAsociat3valoare());
            declaratieIntereseAsociatInfo.setMoneda(revenueDeclarationInfo.getAsociat3moneda());
            declaratieIntereseAsociatInfo.setExplicatie(revenueDeclarationInfo.getAsociat3explicatie());
            declaratieIntereseAsociatInfoList.add(declaratieIntereseAsociatInfo);
        }

        if (revenueDeclarationInfo.getExistaAsociat4().equals("Da")) {
            DeclaratieIntereseAsociatInfo declaratieIntereseAsociatInfo = new DeclaratieIntereseAsociatInfo();
            declaratieIntereseAsociatInfo.setUnitatea(revenueDeclarationInfo.getAsociat4unitatea());
            declaratieIntereseAsociatInfo.setRolul(revenueDeclarationInfo.getAsociat4rolul());
            declaratieIntereseAsociatInfo.setPartiSociale(revenueDeclarationInfo.getAsociat4partiSociale());
            declaratieIntereseAsociatInfo.setValoare(revenueDeclarationInfo.getAsociat4valoare());
            declaratieIntereseAsociatInfo.setMoneda(revenueDeclarationInfo.getAsociat4moneda());
            declaratieIntereseAsociatInfo.setExplicatie(revenueDeclarationInfo.getAsociat4explicatie());
            declaratieIntereseAsociatInfoList.add(declaratieIntereseAsociatInfo);
        }

        if (revenueDeclarationInfo.getExistaAsociat5().equals("Da")) {
            DeclaratieIntereseAsociatInfo declaratieIntereseAsociatInfo = new DeclaratieIntereseAsociatInfo();
            declaratieIntereseAsociatInfo.setUnitatea(revenueDeclarationInfo.getAsociat5unitatea());
            declaratieIntereseAsociatInfo.setRolul(revenueDeclarationInfo.getAsociat5rolul());
            declaratieIntereseAsociatInfo.setPartiSociale(revenueDeclarationInfo.getAsociat5partiSociale());
            declaratieIntereseAsociatInfo.setValoare(revenueDeclarationInfo.getAsociat5valoare());
            declaratieIntereseAsociatInfo.setMoneda(revenueDeclarationInfo.getAsociat5moneda());
            declaratieIntereseAsociatInfo.setExplicatie(revenueDeclarationInfo.getAsociat5explicatie());
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
            columnMapping.put("roleInComp", "asociat1rolul");
            columnMapping.put("noAct", "asociat1partiSociale");
            columnMapping.put("valAssoc", "asociat1valoare");
            columnMapping.put("currAssoc", "asociat1moneda");
            columnMapping.put("expl", "asociat1explicatie");

            //asociat 2

            columnMapping.put("moreAssoc", "existaAsociat2");
            columnMapping.put("companyName2", "asociat2unitatea");
            columnMapping.put("roleInComp2", "asociat2rolul");
            columnMapping.put("noAct2", "asociat2partiSociale");
            columnMapping.put("valAssoc2", "asociat2valoare");
            columnMapping.put("currAssoc2", "asociat2moneda");
            columnMapping.put("expl2", "asociat2explicatie");

            //asociat 3
            columnMapping.put("moreAssoc2", "existaAsociat3");
            columnMapping.put("companyName3", "asociat3unitatea");
            columnMapping.put("roleInComp3", "asociat3rolul");
            columnMapping.put("noAct3", "asociat3partiSociale");
            columnMapping.put("valAssoc3", "asociat3valoare");
            columnMapping.put("currAssoc3", "asociat3moneda");
            columnMapping.put("expl3", "asociat3explicatie");

            //asociat 4
            columnMapping.put("moreAssoc3", "existaAsociat4");
            columnMapping.put("companyName4", "asociat4unitatea");
            columnMapping.put("roleInComp4", "asociat4rolul");
            columnMapping.put("noAct4", "asociat4partiSociale");
            columnMapping.put("valAssoc4", "asociat4valoare");
            columnMapping.put("currAssoc4", "asociat4moneda");
            columnMapping.put("expl4", "asociat4explicatie");

            //asociat 5
            columnMapping.put("moreAssoc4", "existaAsociat5");
            columnMapping.put("companyName5", "asociat5unitatea");
            columnMapping.put("roleInComp5", "asociat5rolul");
            columnMapping.put("noAct5", "asociat5partiSociale");
            columnMapping.put("valAssoc5", "asociat5valoare");
            columnMapping.put("currAssoc5", "asociat5moneda");
            columnMapping.put("expl5", "asociat5explicatie");

            //asociat 6
            columnMapping.put("moreAssoc5", "existaAsociat6");
            columnMapping.put("companyName6", "asociat6unitatea");
            columnMapping.put("roleInComp6", "asociat6rolul");
            columnMapping.put("noAct6", "asociat6partiSociale");
            columnMapping.put("valAssoc6", "asociat6valoare");
            columnMapping.put("currAssoc6", "asociat6moneda");
            columnMapping.put("expl6", "asociat6explicatie");

            return columnMapping;
        }
    }


