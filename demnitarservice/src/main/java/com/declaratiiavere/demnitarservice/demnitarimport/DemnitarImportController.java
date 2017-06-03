package com.declaratiiavere.demnitarservice.demnitarimport;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.declaratiiavere.common.utils.DateUtilities;
import com.declaratiiavere.common.utils.Utilities;
import com.declaratiiavere.demnitarservice.demnitar.*;
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
 * Controller for demnitar import.
 *
 * @author Razvan Dani
 */
@RestController
@RequestMapping(value = "/demnitar/import")
public class DemnitarImportController {
    private static final Integer TIP_VENIT_SALAR = 1;
    private static final Integer TIP_VENIT_ACTIVITATI_INDEPENDENTE = 2;
    private static final Integer TIP_VENIT_CEDAREA_FOLOSINTEI = 3;
    private static final Integer TIP_VENIT_INVESTITII = 4;
    private static final Integer TIP_VENIT_PENSII = 5;
    private static final Integer TIP_VENIT_AGRICOL = 6;
    private static final Integer TIP_VENIT_NOROC = 7;
    private static final Integer TIP_VENIT_ALTE_VENITURI = 8;

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

                DeclaratieAvereInfo declaratieAvereInfo = getDeclaratieAvereInfo(revenueDeclarationInfo, demnitarInfo.getId());

                try {
                    demnitarService.saveDeclaratieAvere(declaratieAvereInfo);
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

    private DeclaratieAvereInfo getDeclaratieAvereInfo(RevenueDeclarationInfo revenueDeclarationInfo, Integer demnitarId) {
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

        SearchDeclaratieAvereCriteria searchDeclaratieAvereCriteria = new SearchDeclaratieAvereCriteria();
        searchDeclaratieAvereCriteria.setDemnitarId(demnitarId);
        searchDeclaratieAvereCriteria.setDataDeclaratiei(dataDeclaratiei);

        List<DeclaratieAvereInfo> declaratieAvereInfoList = demnitarService.findDeclaratiiAvere(searchDeclaratieAvereCriteria);

        DeclaratieAvereInfo declaratieAvereInfo;

        if (declaratieAvereInfoList.isEmpty()) {
            declaratieAvereInfo = new DeclaratieAvereInfo();
        } else {
            declaratieAvereInfo = declaratieAvereInfoList.get(0);
        }

        declaratieAvereInfo.setDemnitarId(demnitarId);
        declaratieAvereInfo.setDataDeclaratiei(dataDeclaratiei);
        declaratieAvereInfo.setFunctie(revenueDeclarationInfo.getFunctie());
        declaratieAvereInfo.setInstitutie(revenueDeclarationInfo.getInstitutie());
        declaratieAvereInfo.setLinkDeclaratie(revenueDeclarationInfo.getLinkDeclaratie());


        List<DeclaratieAvereAlteActiveInfo> declaratieAvereAlteActiveInfoList = getDeclaratieAvereAlteActiveInfoList(revenueDeclarationInfo);
        declaratieAvereInfo.setDeclaratieAvereAlteActiveInfoList(declaratieAvereAlteActiveInfoList);

        List<DeclaratieAvereBunImobilInfo> declaratieAvereBunImobilInfoList = getDeclaratieAvereBunImobilInfoList(revenueDeclarationInfo);
        declaratieAvereInfo.setDeclaratieAvereBunImobilInfoList(declaratieAvereBunImobilInfoList);

        List<DeclaratieAvereBunMobilInfo> declaratieAvereBunMobilInfoList = getDeclaratieAvereBunMobilInfoList(revenueDeclarationInfo);
        declaratieAvereInfo.setDeclaratieAvereBunMobilInfoList(declaratieAvereBunMobilInfoList);

        List<DeclaratieAvereBijuterieInfo> declaratieAvereBijuterieInfoList = getDeclaratieAvereBijuterieInfoList(revenueDeclarationInfo);
        declaratieAvereInfo.setDeclaratieAvereBijuterieInfoList(declaratieAvereBijuterieInfoList);

        List<DeclaratieAverePlasamentInfo> declaratieAverePlasamentInfoList = getDeclaratieAverePlasamentInfoList(revenueDeclarationInfo);
        declaratieAvereInfo.setDeclaratieAverePlasamentInfoList(declaratieAverePlasamentInfoList);

        List<DeclaratieAvereDatorieInfo> declaratieAvereDatorieInfoList = getDeclaratieAvereDatorieInfoList(revenueDeclarationInfo);
        declaratieAvereInfo.setDeclaratieAvereDatorieInfoList(declaratieAvereDatorieInfoList);

        List<DeclaratieAvereContInfo> declaratieAvereContInfoList = getDeclaratieAvereContInfoList(revenueDeclarationInfo);
        declaratieAvereInfo.setDeclaratieAvereContInfoList(declaratieAvereContInfoList);

        List<DeclaratieAvereBunInstrainatInfo> declaratieAvereBunInstrainatInfoList = getDeclaratieAvereBunInstrainatInfoList(revenueDeclarationInfo);
        declaratieAvereInfo.setDeclaratieAvereBunInstrainatInfoList(declaratieAvereBunInstrainatInfoList);

        List<DeclaratieAvereCadouInfo> declaratieAvereCadouInfoList = getDeclaratieAvereCadouInfoList(revenueDeclarationInfo);
        declaratieAvereInfo.setDeclaratieAvereCadouInfoList(declaratieAvereCadouInfoList);

        List<DeclaratieAvereVenitInfo> declaratieAvereVenitInfoList = getDeclaratieAvereVenitInfoList(revenueDeclarationInfo);
        declaratieAvereInfo.setDeclaratieAvereVenitInfoList(declaratieAvereVenitInfoList);

        return declaratieAvereInfo;
    }

    private List<DeclaratieAvereVenitInfo> getDeclaratieAvereVenitInfoList(RevenueDeclarationInfo revenueDeclarationInfo) {
        List<DeclaratieAvereVenitInfo> declaratieAvereVenitInfoList = new ArrayList<>();
        
        declaratieAvereVenitInfoList.addAll(getDeclaratieAvereVenitSalarInfoList(revenueDeclarationInfo));
        declaratieAvereVenitInfoList.addAll(getDeclaratieAvereVenitActivitatiIndependenteInfoList(revenueDeclarationInfo));
        declaratieAvereVenitInfoList.addAll(getDeclaratieAvereVenitCedareaFolosinteiInfoList(revenueDeclarationInfo));
        declaratieAvereVenitInfoList.addAll(getDeclaratieAvereVenitInvestitiiInfoList(revenueDeclarationInfo));
        declaratieAvereVenitInfoList.addAll(getDeclaratieAvereVenitPensiiInfoList(revenueDeclarationInfo));
        declaratieAvereVenitInfoList.addAll(getDeclaratieAvereVenitAgricolInfoList(revenueDeclarationInfo));
        declaratieAvereVenitInfoList.addAll(getDeclaratieAvereVenitNorocInfoList(revenueDeclarationInfo));
        declaratieAvereVenitInfoList.addAll(getDeclaratieAvereVenitAlteVenituriInfoList(revenueDeclarationInfo));
        return declaratieAvereVenitInfoList;
    }

    private List<DeclaratieAvereVenitInfo>getDeclaratieAvereVenitAlteVenituriInfoList(RevenueDeclarationInfo revenueDeclarationInfo){
        List<DeclaratieAvereVenitInfo> declaratieAvereVenitInfoList = new ArrayList<>();
        if(revenueDeclarationInfo.getExistaVenituriAlte1().equals("Da")){
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_ALTE_VENITURI);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitAlte1Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitAlte1Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitAlte1ServiciPrestat());
            try {
                revenueDeclarationInfo.setVeniAlte1VenitAnual(revenueDeclarationInfo.getVeniAlte1VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVeniAlte1VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitAlteVenitAnual format invalid " + revenueDeclarationInfo.getVeniAlte1VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitAlte1Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }
        if(revenueDeclarationInfo.getExistaVenituriAlte2().equals("Da")){
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_ALTE_VENITURI);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitAlte2Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitAlte2Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitAlte2ServiciPrestat());
            try {
                revenueDeclarationInfo.setVeniAlte2VenitAnual(revenueDeclarationInfo.getVeniAlte2VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVeniAlte2VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitAlteVenitAnual format invalid " + revenueDeclarationInfo.getVeniAlte2VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitAlte2Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }

        if(revenueDeclarationInfo.getExistaVenituriAlte3().equals("Da")){
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_ALTE_VENITURI);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitAlte3Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitAlte3Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitAlte3ServiciPrestat());
            try {
                revenueDeclarationInfo.setVeniAlte3VenitAnual(revenueDeclarationInfo.getVeniAlte3VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVeniAlte3VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitAlteVenitAnual format invalid " + revenueDeclarationInfo.getVeniAlte3VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitAlte3Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }

        if(revenueDeclarationInfo.getExistaVenituriAlte4().equals("Da")){
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_ALTE_VENITURI);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitAlte4Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitAlte4Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitAlte4ServiciPrestat());
            try {
                revenueDeclarationInfo.setVeniAlte4VenitAnual(revenueDeclarationInfo.getVeniAlte4VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVeniAlte4VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitAlteVenitAnual format invalid " + revenueDeclarationInfo.getVeniAlte4VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitAlte4Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }
        return declaratieAvereVenitInfoList;
    }
    private List<DeclaratieAvereVenitInfo>getDeclaratieAvereVenitNorocInfoList(RevenueDeclarationInfo revenueDeclarationInfo){
        List<DeclaratieAvereVenitInfo> declaratieAvereVenitInfoList = new ArrayList<>();
        if(revenueDeclarationInfo.getExistaVenituriNoroc1().equals("Da")){
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_NOROC);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitNoroc1Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitNoroc1Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitNoroc1ServiciPrestat());
            try {
                revenueDeclarationInfo.setVenitNoroc1VenitAnual(revenueDeclarationInfo.getVenitNoroc1VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenitNoroc1VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitNoroc1VenitAnual format invalid " + revenueDeclarationInfo.getVenitNoroc1VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitNoroc1Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }
        if(revenueDeclarationInfo.getExistaVenituriNoroc2().equals("Da")){
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_NOROC);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitNoroc2Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitNoroc2Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitNoroc2ServiciPrestat());
            try {
                revenueDeclarationInfo.setVenitNoroc2VenitAnual(revenueDeclarationInfo.getVenitNoroc2VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenitNoroc2VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitNoroc2VenitAnual format invalid " + revenueDeclarationInfo.getVenitNoroc2VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitNoroc2Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }
        return declaratieAvereVenitInfoList;
    }
    private List<DeclaratieAvereVenitInfo>getDeclaratieAvereVenitAgricolInfoList(RevenueDeclarationInfo revenueDeclarationInfo){
        List<DeclaratieAvereVenitInfo> declaratieAvereVenitInfoList = new ArrayList<>();
        if(revenueDeclarationInfo.getExistaVenituriAgricole1().equals("Da")){
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_AGRICOL);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitAgricol1Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitAgricol1Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitAgricol1ServiciPrestat());
            try {
                revenueDeclarationInfo.setVenitAgricol1VenitAnual(revenueDeclarationInfo.getVenitAgricol1VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenitAgricol1VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitAgricol1VenitAnual format invalid " + revenueDeclarationInfo.getVenitAgricol1VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitAgricol1Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }
        if(revenueDeclarationInfo.getExistaVenituriAgricole2().equals("Da")){
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_AGRICOL);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitAgricol2Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitAgricol2Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitAgricol2ServiciPrestat());
            try {
                revenueDeclarationInfo.setVenitAgricol2VenitAnual(revenueDeclarationInfo.getVenitAgricol2VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenitAgricol2VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitAgricol2VenitAnual format invalid " + revenueDeclarationInfo.getVenitAgricol2VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitAgricol2Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }
        return declaratieAvereVenitInfoList;
    }

    private List<DeclaratieAvereVenitInfo>getDeclaratieAvereVenitPensiiInfoList(RevenueDeclarationInfo revenueDeclarationInfo){
        List<DeclaratieAvereVenitInfo> declaratieAvereVenitInfoList = new ArrayList<>();
        if(revenueDeclarationInfo.getExistaVenituriPensii1().equals("Da")){
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_PENSII);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitPensii1Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitPensii1Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitPensii1ServiciPrestat());
            try {
                revenueDeclarationInfo.setVenitPensii1VenitAnual(revenueDeclarationInfo.getVenitPensii1VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenitPensii1VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitPensii1VenitAnual format invalid " + revenueDeclarationInfo.getVenitPensii1VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitPensii1Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }
        if(revenueDeclarationInfo.getExistaVenituriPensii2().equals("Da")){
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_PENSII);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitPensii2Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitPensii2Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitPensii2ServiciPrestat());
            try {
                revenueDeclarationInfo.setVenitPensii2VenitAnual(revenueDeclarationInfo.getVenitPensii2VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenitPensii2VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitPensii2VenitAnual format invalid " + revenueDeclarationInfo.getVenitPensii2VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitPensii2Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }
        return declaratieAvereVenitInfoList;
    }

    private List<DeclaratieAvereVenitInfo>getDeclaratieAvereVenitInvestitiiInfoList(RevenueDeclarationInfo revenueDeclarationInfo){
        List<DeclaratieAvereVenitInfo> declaratieAvereVenitInfoList = new ArrayList<>();
        if(revenueDeclarationInfo.getExistaVenituriInvestitii1().equals("Da")){
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_INVESTITII);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitInvestitii1Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitInvestitii1Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitInvestitii1ServiciPrestat());
            try {
                revenueDeclarationInfo.setVenitInvestitii1VenitAnual(revenueDeclarationInfo.getVenitInvestitii1VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenitInvestitii1VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitInvestitii1VenitAnual format invalid " + revenueDeclarationInfo.getVenitInvestitii1VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitInvestitii1Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }
        if(revenueDeclarationInfo.getExistaVenituriInvestitii2().equals("Da")){
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_INVESTITII);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitInvestitii2Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitInvestitii2Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitInvestitii2ServiciPrestat());
            try {
                revenueDeclarationInfo.setVenitInvestitii2VenitAnual(revenueDeclarationInfo.getVenitInvestitii2VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenitInvestitii2VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitInvestitii2VenitAnual format invalid " + revenueDeclarationInfo.getVenitInvestitii2VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitInvestitii2Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }

        if(revenueDeclarationInfo.getExistaVenituriInvestitii3().equals("Da")){
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_INVESTITII);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitInvestitii3Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitInvestitii3Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitInvestitii3ServiciPrestat());
            try {
                revenueDeclarationInfo.setVenitInvestitii3VenitAnual(revenueDeclarationInfo.getVenitInvestitii3VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenitInvestitii3VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitInvestitii3VenitAnual format invalid " + revenueDeclarationInfo.getVenitInvestitii3VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitInvestitii3Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }

        if(revenueDeclarationInfo.getExistaVenituriInvestitii4().equals("Da")){
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_INVESTITII);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitInvestitii4Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitInvestitii4Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitInvestitii4ServiciPrestat());
            try {
                revenueDeclarationInfo.setVenitInvestitii4VenitAnual(revenueDeclarationInfo.getVenitInvestitii4VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenitInvestitii4VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitInvestitii4VenitAnual format invalid " + revenueDeclarationInfo.getVenitInvestitii4VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitInvestitii4Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }
        

        return declaratieAvereVenitInfoList;
    }

    private List<DeclaratieAvereVenitInfo>getDeclaratieAvereVenitCedareaFolosinteiInfoList(RevenueDeclarationInfo revenueDeclarationInfo){
        List<DeclaratieAvereVenitInfo> declaratieAvereVenitInfoList = new ArrayList<>();
        if(revenueDeclarationInfo.getExistaVenituriCedareaFolosintei1().equals("Da")){
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_CEDAREA_FOLOSINTEI);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitCedareaFolosintei1Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitCedareaFolosintei1Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitCedareaFolosintei1ServiciPrestat());
            try {
                revenueDeclarationInfo.setVenitCedareaFolosintei1VenitAnual(revenueDeclarationInfo.getVenitCedareaFolosintei1VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenitCedareaFolosintei1VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitCedareaFolosintei1VenitAnual format invalid " + revenueDeclarationInfo.getVenitCedareaFolosintei1VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitCedareaFolosintei1Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }
        if(revenueDeclarationInfo.getExistaVenituriCedareaFolosintei2().equals("Da")){
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_CEDAREA_FOLOSINTEI);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitCedareaFolosintei2Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitCedareaFolosintei2Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitCedareaFolosintei2ServiciPrestat());
            try {
                revenueDeclarationInfo.setVenitCedareaFolosintei2VenitAnual(revenueDeclarationInfo.getVenitCedareaFolosintei2VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenitCedareaFolosintei2VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitCedareaFolosintei2VenitAnual format invalid " + revenueDeclarationInfo.getVenitCedareaFolosintei2VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitCedareaFolosintei2Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }

        if(revenueDeclarationInfo.getExistaVenituriCedareaFolosintei3().equals("Da")){
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_CEDAREA_FOLOSINTEI);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitCedareaFolosintei3Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitCedareaFolosintei3Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitCedareaFolosintei3ServiciPrestat());
            try {
                revenueDeclarationInfo.setVenitCedareaFolosintei3VenitAnual(revenueDeclarationInfo.getVenitCedareaFolosintei3VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenitCedareaFolosintei3VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitCedareaFolosintei3VenitAnual format invalid " + revenueDeclarationInfo.getVenitCedareaFolosintei3VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitCedareaFolosintei3Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }

        if(revenueDeclarationInfo.getExistaVenituriCedareaFolosintei4().equals("Da")){
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_CEDAREA_FOLOSINTEI);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitCedareaFolosintei4Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitCedareaFolosintei4Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitCedareaFolosintei4ServiciPrestat());
            try {
                revenueDeclarationInfo.setVenitCedareaFolosintei4VenitAnual(revenueDeclarationInfo.getVenitCedareaFolosintei4VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenitCedareaFolosintei4VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitCedareaFolosintei4VenitAnual format invalid " + revenueDeclarationInfo.getVenitCedareaFolosintei4VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitCedareaFolosintei4Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }

        if(revenueDeclarationInfo.getExistaVenituriCedareaFolosintei5().equals("Da")){
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_CEDAREA_FOLOSINTEI);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitCedareaFolosintei5Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitCedareaFolosintei5Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitCedareaFolosintei5ServiciPrestat());
            try {
                revenueDeclarationInfo.setVenitCedareaFolosintei5VenitAnual(revenueDeclarationInfo.getVenitCedareaFolosintei5VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenitCedareaFolosintei5VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitCedareaFolosintei5VenitAnual format invalid " + revenueDeclarationInfo.getVenitCedareaFolosintei5VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitCedareaFolosintei5Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }

        if(revenueDeclarationInfo.getExistaVenituriCedareaFolosintei6().equals("Da")){
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_CEDAREA_FOLOSINTEI);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitCedareaFolosintei6Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitCedareaFolosintei6Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitCedareaFolosintei6ServiciPrestat());
            try {
                revenueDeclarationInfo.setVenitCedareaFolosintei6VenitAnual(revenueDeclarationInfo.getVenitCedareaFolosintei6VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenitCedareaFolosintei6VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitCedareaFolosintei6VenitAnual format invalid " + revenueDeclarationInfo.getVenitCedareaFolosintei6VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitCedareaFolosintei6Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }

        return declaratieAvereVenitInfoList;
    }
    private List<DeclaratieAvereVenitInfo> getDeclaratieAvereVenitActivitatiIndependenteInfoList(RevenueDeclarationInfo revenueDeclarationInfo) {
        List<DeclaratieAvereVenitInfo> declaratieAvereVenitInfoList = new ArrayList<>();

        if (revenueDeclarationInfo.getExistaVenituriActivitatiIndependente1().equals("Da")) {
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_ACTIVITATI_INDEPENDENTE);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitActivitatiIndependente1Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitActivitatiIndependente1Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitActivitatiIndependente1ServiciPrestat());

            try {
                revenueDeclarationInfo.setVenitActivitatiIndependente1VenitAnual(revenueDeclarationInfo.getVenitActivitatiIndependente1VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenitActivitatiIndependente1VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitActivitatiIndependente1VenitAnual format invalid " + revenueDeclarationInfo.getVenitActivitatiIndependente1VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitActivitatiIndependente1Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }

        if (revenueDeclarationInfo.getExistaVenituriActivitatiIndependente2().equals("Da")) {
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_ACTIVITATI_INDEPENDENTE);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitActivitatiIndependente2Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitActivitatiIndependente2Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitActivitatiIndependente2ServiciPrestat());

            try {
                revenueDeclarationInfo.setVenitActivitatiIndependente2VenitAnual(revenueDeclarationInfo.getVenitActivitatiIndependente2VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenitActivitatiIndependente2VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitActivitatiIndependente2VenitAnual format invalid " + revenueDeclarationInfo.getVenitActivitatiIndependente2VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitActivitatiIndependente2Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }

        if (revenueDeclarationInfo.getExistaVenituriActivitatiIndependente3().equals("Da")) {
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_ACTIVITATI_INDEPENDENTE);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitActivitatiIndependente3Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitActivitatiIndependente3Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitActivitatiIndependente3ServiciPrestat());

            try {
                revenueDeclarationInfo.setVenitActivitatiIndependente3VenitAnual(revenueDeclarationInfo.getVenitActivitatiIndependente3VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenitActivitatiIndependente3VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitActivitatiIndependente3VenitAnual format invalid " + revenueDeclarationInfo.getVenitActivitatiIndependente3VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitActivitatiIndependente3Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }

        if (revenueDeclarationInfo.getExistaVenituriActivitatiIndependente4().equals("Da")) {
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_ACTIVITATI_INDEPENDENTE);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitActivitatiIndependente4Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitActivitatiIndependente4Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenitActivitatiIndependente4ServiciPrestat());

            try {
                revenueDeclarationInfo.setVenitActivitatiIndependente4VenitAnual(revenueDeclarationInfo.getVenitActivitatiIndependente4VenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenitActivitatiIndependente4VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenitActivitatiIndependente4VenitAnual format invalid " + revenueDeclarationInfo.getVenitActivitatiIndependente4VenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenitActivitatiIndependente4Moneda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }
        
        return declaratieAvereVenitInfoList;
    }

    private List<DeclaratieAvereVenitInfo> getDeclaratieAvereVenitSalarInfoList(RevenueDeclarationInfo revenueDeclarationInfo) {
        List<DeclaratieAvereVenitInfo> declaratieAvereVenitInfoList = new ArrayList<>();

        if (revenueDeclarationInfo.getExistaVenituriSalarii1().equals("Da")) {
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_SALAR);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitSalariu1Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitSalariu1Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenit1SalariuServiciPrestat());

            try {
                revenueDeclarationInfo.setVenit1SalariuVenitAnual(revenueDeclarationInfo.getVenit1SalariuVenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenit1SalariuVenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenit1SalariuVenitAnual format invalid " + revenueDeclarationInfo.getVenit1SalariuVenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenit1SalariuMonenda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }

        if (revenueDeclarationInfo.getExistaVenituriSalarii2().equals("Da")) {
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_SALAR);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitSalariu2Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitSalariu2Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenit2SalariuServiciPrestat());

            try {
                revenueDeclarationInfo.setVenit2SalariuVenitAnual(revenueDeclarationInfo.getVenit2SalariuVenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenit2SalariuVenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenit2SalariuVenitAnual format invalid " + revenueDeclarationInfo.getVenit2SalariuVenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenit2SalariuMonenda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }

        if (revenueDeclarationInfo.getExistaVenituriSalarii3().equals("Da")) {
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_SALAR);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitSalariu3Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitSalariu3Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenit3SalariuServiciPrestat());

            try {
                revenueDeclarationInfo.setVenit3SalariuVenitAnual(revenueDeclarationInfo.getVenit3SalariuVenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenit3SalariuVenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenit3SalariuVenitAnual format invalid " + revenueDeclarationInfo.getVenit3SalariuVenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenit3SalariuMonenda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }

        if (revenueDeclarationInfo.getExistaVenituriSalarii4().equals("Da")) {
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_SALAR);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitSalariu4Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitSalariu4Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenit4SalariuServiciPrestat());

            try {
                revenueDeclarationInfo.setVenit4SalariuVenitAnual(revenueDeclarationInfo.getVenit4SalariuVenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenit4SalariuVenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenit4SalariuVenitAnual format invalid " + revenueDeclarationInfo.getVenit4SalariuVenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenit4SalariuMonenda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }

        if (revenueDeclarationInfo.getExistaVenituriSalarii5().equals("Da")) {
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_SALAR);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitSalariu5Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitSalariu5Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenit5SalariuServiciPrestat());

            try {
                revenueDeclarationInfo.setVenit5SalariuVenitAnual(revenueDeclarationInfo.getVenit5SalariuVenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenit5SalariuVenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenit5SalariuVenitAnual format invalid " + revenueDeclarationInfo.getVenit5SalariuVenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenit5SalariuMonenda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }

        if (revenueDeclarationInfo.getExistaVenituriSalarii6().equals("Da")) {
            DeclaratieAvereVenitInfo declaratieAvereVenitInfo = new DeclaratieAvereVenitInfo();
            declaratieAvereVenitInfo.setTip(TIP_VENIT_SALAR);
            declaratieAvereVenitInfo.setTitular(revenueDeclarationInfo.getVenitSalariu6Titular());
            declaratieAvereVenitInfo.setSursaVenit(revenueDeclarationInfo.getVenitSalariu6Sursa());
            declaratieAvereVenitInfo.setServiciulPrestat(revenueDeclarationInfo.getVenit6SalariuServiciPrestat());

            try {
                revenueDeclarationInfo.setVenit6SalariuVenitAnual(revenueDeclarationInfo.getVenit6SalariuVenitAnual().replaceAll(",", "."));
                declaratieAvereVenitInfo.setVenitAnual(new BigDecimal(revenueDeclarationInfo.getVenit6SalariuVenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getVenit6SalariuVenitAnual format invalid " + revenueDeclarationInfo.getVenit6SalariuVenitAnual());
            }

            declaratieAvereVenitInfo.setMoneda(revenueDeclarationInfo.getVenit6SalariuMonenda());

            declaratieAvereVenitInfoList.add(declaratieAvereVenitInfo);
        }

        return declaratieAvereVenitInfoList;
    }

    private List<DeclaratieAvereCadouInfo> getDeclaratieAvereCadouInfoList(RevenueDeclarationInfo revenueDeclarationInfo) {
        List<DeclaratieAvereCadouInfo> declaratieAvereCadouInfoList = new ArrayList<>();

        if (revenueDeclarationInfo.getExistaCadouri1().equals("Da")) {
            DeclaratieAvereCadouInfo declaratieAvereCadouInfo = new DeclaratieAvereCadouInfo();
            declaratieAvereCadouInfo.setTitular(revenueDeclarationInfo.getCadou1Titular());
            declaratieAvereCadouInfo.setServiciulPrestat(revenueDeclarationInfo.getCadou1ServiciuPrestat());
            declaratieAvereCadouInfo.setSursaVenit(revenueDeclarationInfo.getCadou1SursaVenit());

            try {
                revenueDeclarationInfo.setCadou1VenitAnual(revenueDeclarationInfo.getCadou1VenitAnual().replaceAll(",", "."));
                declaratieAvereCadouInfo.setVenit(new BigDecimal(revenueDeclarationInfo.getCadou1VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getCadou1VenitAnual format invalid " + revenueDeclarationInfo.getCadou1VenitAnual());
            }

            declaratieAvereCadouInfo.setMoneda(revenueDeclarationInfo.getCadou1Monenda());

            declaratieAvereCadouInfoList.add(declaratieAvereCadouInfo);
        }

        if (revenueDeclarationInfo.getExistaCadouri2().equals("Da")) {
            DeclaratieAvereCadouInfo declaratieAvereCadouInfo = new DeclaratieAvereCadouInfo();
            declaratieAvereCadouInfo.setTitular(revenueDeclarationInfo.getCadou2Titular());
            declaratieAvereCadouInfo.setServiciulPrestat(revenueDeclarationInfo.getCadou2ServiciuPrestat());
            declaratieAvereCadouInfo.setSursaVenit(revenueDeclarationInfo.getCadou2SursaVenit());

            try {
                revenueDeclarationInfo.setCadou2VenitAnual(revenueDeclarationInfo.getCadou2VenitAnual().replaceAll(",", "."));
                declaratieAvereCadouInfo.setVenit(new BigDecimal(revenueDeclarationInfo.getCadou2VenitAnual()));
            } catch (Exception e) {
                throw new ValidationException("getCadou2VenitAnual format invalid " + revenueDeclarationInfo.getCadou2VenitAnual());
            }

            declaratieAvereCadouInfo.setMoneda(revenueDeclarationInfo.getCadou2Monenda());

            declaratieAvereCadouInfoList.add(declaratieAvereCadouInfo);
        }

        return declaratieAvereCadouInfoList;
    }
    
    private List<DeclaratieAvereBunInstrainatInfo> getDeclaratieAvereBunInstrainatInfoList(RevenueDeclarationInfo revenueDeclarationInfo) {
        List<DeclaratieAvereBunInstrainatInfo> declaratieAvereBunInstrainatInfoList = new ArrayList<>();

        if (revenueDeclarationInfo.getExistaBunuriInstrainate1().equals("Da")) {
            DeclaratieAvereBunInstrainatInfo declaratieAvereBunInstrainatInfo = new DeclaratieAvereBunInstrainatInfo();
            declaratieAvereBunInstrainatInfo.setTip(revenueDeclarationInfo.getBunInstrainat1Natura());
            declaratieAvereBunInstrainatInfo.setDataInstrainarii(revenueDeclarationInfo.getBunInstrainat1Data());
            declaratieAvereBunInstrainatInfo.setPersoanaBeneficiara(revenueDeclarationInfo.getBunInstrainat1PersoanaCatreCare());
            declaratieAvereBunInstrainatInfo.setFormaInstrainarii(revenueDeclarationInfo.getBunInstrainat1FormaInstrainarii());

            try {
                revenueDeclarationInfo.setBunInstrainat1Valoarea(revenueDeclarationInfo.getBunInstrainat1Valoarea().replaceAll(",", "."));
                declaratieAvereBunInstrainatInfo.setValoarea(new BigDecimal(revenueDeclarationInfo.getBunInstrainat1Valoarea()));
            } catch (Exception e) {
                throw new ValidationException("getBunInstrainat1Valoarea format invalid " + revenueDeclarationInfo.getBunInstrainat1Valoarea());
            }

            declaratieAvereBunInstrainatInfo.setMoneda(revenueDeclarationInfo.getBunInstrainat1Moneda());

            declaratieAvereBunInstrainatInfoList.add(declaratieAvereBunInstrainatInfo);
        }

        if (revenueDeclarationInfo.getExistaBunuriInstrainate2().equals("Da")) {
            DeclaratieAvereBunInstrainatInfo declaratieAvereBunInstrainatInfo = new DeclaratieAvereBunInstrainatInfo();
            declaratieAvereBunInstrainatInfo.setTip(revenueDeclarationInfo.getBunInstrainat2Natura());
            declaratieAvereBunInstrainatInfo.setDataInstrainarii(revenueDeclarationInfo.getBunInstrainat2Data());
            declaratieAvereBunInstrainatInfo.setPersoanaBeneficiara(revenueDeclarationInfo.getBunInstrainat2PersoanaCatreCare());
            declaratieAvereBunInstrainatInfo.setFormaInstrainarii(revenueDeclarationInfo.getBunInstrainat2FormaInstrainarii());

            try {
                revenueDeclarationInfo.setBunInstrainat2Valoarea(revenueDeclarationInfo.getBunInstrainat2Valoarea().replaceAll(",", "."));
                declaratieAvereBunInstrainatInfo.setValoarea(new BigDecimal(revenueDeclarationInfo.getBunInstrainat2Valoarea()));
            } catch (Exception e) {
                throw new ValidationException("getBunInstrainat2Valoarea format invalid " + revenueDeclarationInfo.getBunInstrainat2Valoarea());
            }

            declaratieAvereBunInstrainatInfo.setMoneda(revenueDeclarationInfo.getBunInstrainat2Moneda());

            declaratieAvereBunInstrainatInfoList.add(declaratieAvereBunInstrainatInfo);
        }

        if (revenueDeclarationInfo.getExistaBunuriInstrainate3().equals("Da")) {
            DeclaratieAvereBunInstrainatInfo declaratieAvereBunInstrainatInfo = new DeclaratieAvereBunInstrainatInfo();
            declaratieAvereBunInstrainatInfo.setTip(revenueDeclarationInfo.getBunInstrainat3Natura());
            declaratieAvereBunInstrainatInfo.setDataInstrainarii(revenueDeclarationInfo.getBunInstrainat3Data());
            declaratieAvereBunInstrainatInfo.setPersoanaBeneficiara(revenueDeclarationInfo.getBunInstrainat3PersoanaCatreCare());
            declaratieAvereBunInstrainatInfo.setFormaInstrainarii(revenueDeclarationInfo.getBunInstrainat3FormaInstrainarii());

            try {
                revenueDeclarationInfo.setBunInstrainat3Valoarea(revenueDeclarationInfo.getBunInstrainat3Valoarea().replaceAll(",", "."));
                declaratieAvereBunInstrainatInfo.setValoarea(new BigDecimal(revenueDeclarationInfo.getBunInstrainat3Valoarea()));
            } catch (Exception e) {
                throw new ValidationException("getBunInstrainat3Valoarea format invalid " + revenueDeclarationInfo.getBunInstrainat3Valoarea());
            }

            declaratieAvereBunInstrainatInfo.setMoneda(revenueDeclarationInfo.getBunInstrainat3Moneda());

            declaratieAvereBunInstrainatInfoList.add(declaratieAvereBunInstrainatInfo);
        }

        if (revenueDeclarationInfo.getExistaBunuriInstrainate4().equals("Da")) {
            DeclaratieAvereBunInstrainatInfo declaratieAvereBunInstrainatInfo = new DeclaratieAvereBunInstrainatInfo();
            declaratieAvereBunInstrainatInfo.setTip(revenueDeclarationInfo.getBunInstrainat4Natura());
            declaratieAvereBunInstrainatInfo.setDataInstrainarii(revenueDeclarationInfo.getBunInstrainat4Data());
            declaratieAvereBunInstrainatInfo.setPersoanaBeneficiara(revenueDeclarationInfo.getBunInstrainat4PersoanaCatreCare());
            declaratieAvereBunInstrainatInfo.setFormaInstrainarii(revenueDeclarationInfo.getBunInstrainat4FormaInstrainarii());

            try {
                revenueDeclarationInfo.setBunInstrainat4Valoarea(revenueDeclarationInfo.getBunInstrainat4Valoarea().replaceAll(",", "."));
                declaratieAvereBunInstrainatInfo.setValoarea(new BigDecimal(revenueDeclarationInfo.getBunInstrainat4Valoarea()));
            } catch (Exception e) {
                throw new ValidationException("getBunInstrainat4Valoarea format invalid " + revenueDeclarationInfo.getBunInstrainat4Valoarea());
            }

            declaratieAvereBunInstrainatInfo.setMoneda(revenueDeclarationInfo.getBunInstrainat4Moneda());

            declaratieAvereBunInstrainatInfoList.add(declaratieAvereBunInstrainatInfo);
        }

        if (revenueDeclarationInfo.getExistaBunuriInstrainate5().equals("Da")) {
            DeclaratieAvereBunInstrainatInfo declaratieAvereBunInstrainatInfo = new DeclaratieAvereBunInstrainatInfo();
            declaratieAvereBunInstrainatInfo.setTip(revenueDeclarationInfo.getBunInstrainat5Natura());
            declaratieAvereBunInstrainatInfo.setDataInstrainarii(revenueDeclarationInfo.getBunInstrainat5Data());
            declaratieAvereBunInstrainatInfo.setPersoanaBeneficiara(revenueDeclarationInfo.getBunInstrainat5PersoanaCatreCare());
            declaratieAvereBunInstrainatInfo.setFormaInstrainarii(revenueDeclarationInfo.getBunInstrainat5FormaInstrainarii());

            try {
                revenueDeclarationInfo.setBunInstrainat5Valoarea(revenueDeclarationInfo.getBunInstrainat5Valoarea().replaceAll(",", "."));
                declaratieAvereBunInstrainatInfo.setValoarea(new BigDecimal(revenueDeclarationInfo.getBunInstrainat5Valoarea()));
            } catch (Exception e) {
                throw new ValidationException("getBunInstrainat5Valoarea format invalid " + revenueDeclarationInfo.getBunInstrainat5Valoarea());
            }

            declaratieAvereBunInstrainatInfo.setMoneda(revenueDeclarationInfo.getBunInstrainat5Moneda());

            declaratieAvereBunInstrainatInfoList.add(declaratieAvereBunInstrainatInfo);
        }

        return declaratieAvereBunInstrainatInfoList;
    }

    private List<DeclaratieAvereContInfo> getDeclaratieAvereContInfoList(RevenueDeclarationInfo revenueDeclarationInfo) {
        List<DeclaratieAvereContInfo> declaratieAvereContInfoList = new ArrayList<>();

        if (revenueDeclarationInfo.getExistaConturi1().equals("Da")) {
            DeclaratieAvereContInfo declaratieAvereContInfo = new DeclaratieAvereContInfo();
            declaratieAvereContInfo.setTitular(revenueDeclarationInfo.getCont1Titular());
            declaratieAvereContInfo.setInstitutieBancara(revenueDeclarationInfo.getCont1Institutia());


            try {
                if (!revenueDeclarationInfo.getCont1Tip().equals("")) {
                    declaratieAvereContInfo.setTipCont(new Integer(revenueDeclarationInfo.getCont1Tip().substring(0, 1)));
                }
            } catch (Exception e) {
                throw new ValidationException("getCont1Tip format invalid " + revenueDeclarationInfo.getCont1Tip());
            }

            try {
                revenueDeclarationInfo.setCont1Sold(revenueDeclarationInfo.getCont1Sold().replaceAll(",", "."));
                declaratieAvereContInfo.setSoldCont(new BigDecimal(revenueDeclarationInfo.getCont1Sold()));
            } catch (Exception e) {
                throw new ValidationException("getCont1Sold format invalid " + revenueDeclarationInfo.getCont1Sold());
            }


            declaratieAvereContInfo.setMoneda(revenueDeclarationInfo.getCont1Moneda());
            declaratieAvereContInfo.setAnDeschidereCont(revenueDeclarationInfo.getCont1AnDeschidere());

            declaratieAvereContInfoList.add(declaratieAvereContInfo);
        }

        if (revenueDeclarationInfo.getExistaConturi2().equals("Da")) {
            DeclaratieAvereContInfo declaratieAvereContInfo = new DeclaratieAvereContInfo();
            declaratieAvereContInfo.setTitular(revenueDeclarationInfo.getCont2Titular());
            declaratieAvereContInfo.setInstitutieBancara(revenueDeclarationInfo.getCont2Institutia());


            try {
                if (!revenueDeclarationInfo.getCont2Tip().equals("")) {
                    declaratieAvereContInfo.setTipCont(new Integer(revenueDeclarationInfo.getCont2Tip().substring(0, 1)));
                }
            } catch (Exception e) {
                throw new ValidationException("getCont2Tip format invalid " + revenueDeclarationInfo.getCont2Tip());
            }

            try {
                revenueDeclarationInfo.setCont2Sold(revenueDeclarationInfo.getCont2Sold().replaceAll(",", "."));
                declaratieAvereContInfo.setSoldCont(new BigDecimal(revenueDeclarationInfo.getCont2Sold()));
            } catch (Exception e) {
                throw new ValidationException("getCont2Sold format invalid " + revenueDeclarationInfo.getCont2Sold());
            }


            declaratieAvereContInfo.setMoneda(revenueDeclarationInfo.getCont2Moneda());
            declaratieAvereContInfo.setAnDeschidereCont(revenueDeclarationInfo.getCont2AnDeschidere());

            declaratieAvereContInfoList.add(declaratieAvereContInfo);
        }

        if (revenueDeclarationInfo.getExistaConturi3().equals("Da")) {
            DeclaratieAvereContInfo declaratieAvereContInfo = new DeclaratieAvereContInfo();
            declaratieAvereContInfo.setTitular(revenueDeclarationInfo.getCont3Titular());
            declaratieAvereContInfo.setInstitutieBancara(revenueDeclarationInfo.getCont3Institutia());


            try {
                if (!revenueDeclarationInfo.getCont3Tip().equals("")) {
                    declaratieAvereContInfo.setTipCont(new Integer(revenueDeclarationInfo.getCont3Tip().substring(0, 1)));
                }
            } catch (Exception e) {
                throw new ValidationException("getCont3Tip format invalid " + revenueDeclarationInfo.getCont3Tip());
            }

            try {
                revenueDeclarationInfo.setCont3Sold(revenueDeclarationInfo.getCont3Sold().replaceAll(",", "."));
                declaratieAvereContInfo.setSoldCont(new BigDecimal(revenueDeclarationInfo.getCont3Sold()));
            } catch (Exception e) {
                throw new ValidationException("getCont3Sold format invalid " + revenueDeclarationInfo.getCont3Sold());
            }


            declaratieAvereContInfo.setMoneda(revenueDeclarationInfo.getCont3Moneda());
            declaratieAvereContInfo.setAnDeschidereCont(revenueDeclarationInfo.getCont3AnDeschidere());

            declaratieAvereContInfoList.add(declaratieAvereContInfo);
        }

        if (revenueDeclarationInfo.getExistaConturi4().equals("Da")) {
            DeclaratieAvereContInfo declaratieAvereContInfo = new DeclaratieAvereContInfo();
            declaratieAvereContInfo.setTitular(revenueDeclarationInfo.getCont4Titular());
            declaratieAvereContInfo.setInstitutieBancara(revenueDeclarationInfo.getCont4Institutia());


            try {
                if (!revenueDeclarationInfo.getCont4Tip().equals("")) {
                    declaratieAvereContInfo.setTipCont(new Integer(revenueDeclarationInfo.getCont4Tip().substring(0, 1)));
                }
            } catch (Exception e) {
                throw new ValidationException("getCont4Tip format invalid " + revenueDeclarationInfo.getCont4Tip());
            }

            try {
                revenueDeclarationInfo.setCont4Sold(revenueDeclarationInfo.getCont4Sold().replaceAll(",", "."));
                declaratieAvereContInfo.setSoldCont(new BigDecimal(revenueDeclarationInfo.getCont4Sold()));
            } catch (Exception e) {
                throw new ValidationException("getCont4Sold format invalid " + revenueDeclarationInfo.getCont4Sold());
            }


            declaratieAvereContInfo.setMoneda(revenueDeclarationInfo.getCont4Moneda());
            declaratieAvereContInfo.setAnDeschidereCont(revenueDeclarationInfo.getCont4AnDeschidere());

            declaratieAvereContInfoList.add(declaratieAvereContInfo);
        }

        if (revenueDeclarationInfo.getExistaConturi5().equals("Da")) {
            DeclaratieAvereContInfo declaratieAvereContInfo = new DeclaratieAvereContInfo();
            declaratieAvereContInfo.setTitular(revenueDeclarationInfo.getCont5Titular());
            declaratieAvereContInfo.setInstitutieBancara(revenueDeclarationInfo.getCont5Institutia());

            try {
                if (!revenueDeclarationInfo.getCont5Tip().equals("")) {
                    declaratieAvereContInfo.setTipCont(new Integer(revenueDeclarationInfo.getCont5Tip().substring(0, 1)));
                }
            } catch (Exception e) {
                throw new ValidationException("getCont5Tip format invalid " + revenueDeclarationInfo.getCont5Tip());
            }

            try {
                revenueDeclarationInfo.setCont5Sold(revenueDeclarationInfo.getCont5Sold().replaceAll(",", "."));
                declaratieAvereContInfo.setSoldCont(new BigDecimal(revenueDeclarationInfo.getCont5Sold()));
            } catch (Exception e) {
                throw new ValidationException("getCont5Sold format invalid " + revenueDeclarationInfo.getCont5Sold());
            }


            declaratieAvereContInfo.setMoneda(revenueDeclarationInfo.getCont5Moneda());
            declaratieAvereContInfo.setAnDeschidereCont(revenueDeclarationInfo.getCont5AnDeschidere());

            declaratieAvereContInfoList.add(declaratieAvereContInfo);
        }

        if (revenueDeclarationInfo.getExistaConturi6().equals("Da")) {
            DeclaratieAvereContInfo declaratieAvereContInfo = new DeclaratieAvereContInfo();
            declaratieAvereContInfo.setTitular(revenueDeclarationInfo.getCont6Titular());
            declaratieAvereContInfo.setInstitutieBancara(revenueDeclarationInfo.getCont6Institutia());

            try {
                if (!revenueDeclarationInfo.getCont6Tip().equals("")) {
                    declaratieAvereContInfo.setTipCont(new Integer(revenueDeclarationInfo.getCont6Tip().substring(0, 1)));
                }
            } catch (Exception e) {
                throw new ValidationException("getCont6Tip format invalid " + revenueDeclarationInfo.getCont6Tip());
            }

            try {
                revenueDeclarationInfo.setCont6Sold(revenueDeclarationInfo.getCont6Sold().replaceAll(",", "."));
                declaratieAvereContInfo.setSoldCont(new BigDecimal(revenueDeclarationInfo.getCont6Sold()));
            } catch (Exception e) {
                throw new ValidationException("getCont6Sold format invalid " + revenueDeclarationInfo.getCont6Sold());
            }


            declaratieAvereContInfo.setMoneda(revenueDeclarationInfo.getCont6Moneda());
            declaratieAvereContInfo.setAnDeschidereCont(revenueDeclarationInfo.getCont6AnDeschidere());

            declaratieAvereContInfoList.add(declaratieAvereContInfo);
        }

        if (revenueDeclarationInfo.getExistaConturi7().equals("Da")) {
            DeclaratieAvereContInfo declaratieAvereContInfo = new DeclaratieAvereContInfo();
            declaratieAvereContInfo.setTitular(revenueDeclarationInfo.getCont7Titular());
            declaratieAvereContInfo.setInstitutieBancara(revenueDeclarationInfo.getCont7Institutia());

            try {
                if (!revenueDeclarationInfo.getCont7Tip().equals("")) {
                    declaratieAvereContInfo.setTipCont(new Integer(revenueDeclarationInfo.getCont7Tip().substring(0, 1)));
                }
            } catch (Exception e) {
                throw new ValidationException("getCont7Tip format invalid " + revenueDeclarationInfo.getCont7Tip());
            }

            try {
                revenueDeclarationInfo.setCont7Sold(revenueDeclarationInfo.getCont7Sold().replaceAll(",", "."));
                declaratieAvereContInfo.setSoldCont(new BigDecimal(revenueDeclarationInfo.getCont7Sold()));
            } catch (Exception e) {
                throw new ValidationException("getCont7Sold format invalid " + revenueDeclarationInfo.getCont7Sold());
            }


            declaratieAvereContInfo.setMoneda(revenueDeclarationInfo.getCont7Moneda());
            declaratieAvereContInfo.setAnDeschidereCont(revenueDeclarationInfo.getCont7AnDeschidere());

            declaratieAvereContInfoList.add(declaratieAvereContInfo);
        }

        if (revenueDeclarationInfo.getExistaConturi8().equals("Da")) {
            DeclaratieAvereContInfo declaratieAvereContInfo = new DeclaratieAvereContInfo();
            declaratieAvereContInfo.setTitular(revenueDeclarationInfo.getCont8Titular());
            declaratieAvereContInfo.setInstitutieBancara(revenueDeclarationInfo.getCont8Institutia());

            try {
                if (!revenueDeclarationInfo.getCont8Tip().equals("")) {
                    declaratieAvereContInfo.setTipCont(new Integer(revenueDeclarationInfo.getCont8Tip().substring(0, 1)));
                }
            } catch (Exception e) {
                throw new ValidationException("getCont8Tip format invalid " + revenueDeclarationInfo.getCont8Tip());
            }

            try {
                revenueDeclarationInfo.setCont8Sold(revenueDeclarationInfo.getCont8Sold().replaceAll(",", "."));
                declaratieAvereContInfo.setSoldCont(new BigDecimal(revenueDeclarationInfo.getCont8Sold()));
            } catch (Exception e) {
                throw new ValidationException("getCont8Sold format invalid " + revenueDeclarationInfo.getCont8Sold());
            }


            declaratieAvereContInfo.setMoneda(revenueDeclarationInfo.getCont8Moneda());
            declaratieAvereContInfo.setAnDeschidereCont(revenueDeclarationInfo.getCont8AnDeschidere());

            declaratieAvereContInfoList.add(declaratieAvereContInfo);
        }

        if (revenueDeclarationInfo.getExistaConturi9().equals("Da")) {
            DeclaratieAvereContInfo declaratieAvereContInfo = new DeclaratieAvereContInfo();
            declaratieAvereContInfo.setTitular(revenueDeclarationInfo.getCont9Titular());
            declaratieAvereContInfo.setInstitutieBancara(revenueDeclarationInfo.getCont9Institutia());

            try {
                if (!revenueDeclarationInfo.getCont9Tip().equals("")) {
                    declaratieAvereContInfo.setTipCont(new Integer(revenueDeclarationInfo.getCont9Tip().substring(0, 1)));
                }
            } catch (Exception e) {
                throw new ValidationException("getCont9Tip format invalid " + revenueDeclarationInfo.getCont9Tip());
            }

            try {
                revenueDeclarationInfo.setCont9Sold(revenueDeclarationInfo.getCont9Sold().replaceAll(",", "."));
                declaratieAvereContInfo.setSoldCont(new BigDecimal(revenueDeclarationInfo.getCont9Sold()));
            } catch (Exception e) {
                throw new ValidationException("getCont9Sold format invalid " + revenueDeclarationInfo.getCont9Sold());
            }


            declaratieAvereContInfo.setMoneda(revenueDeclarationInfo.getCont9Moneda());
            declaratieAvereContInfo.setAnDeschidereCont(revenueDeclarationInfo.getCont9AnDeschidere());

            declaratieAvereContInfoList.add(declaratieAvereContInfo);
        }

        if (revenueDeclarationInfo.getExistaConturi10().equals("Da")) {
            DeclaratieAvereContInfo declaratieAvereContInfo = new DeclaratieAvereContInfo();
            declaratieAvereContInfo.setTitular(revenueDeclarationInfo.getCont10Titular());
            declaratieAvereContInfo.setInstitutieBancara(revenueDeclarationInfo.getCont10Institutia());

            try {
                if (!revenueDeclarationInfo.getCont10Tip().equals("")) {
                    declaratieAvereContInfo.setTipCont(new Integer(revenueDeclarationInfo.getCont10Tip().substring(0, 1)));
                }
            } catch (Exception e) {
                throw new ValidationException("getCont10Tip format invalid " + revenueDeclarationInfo.getCont10Tip());
            }

            try {
                revenueDeclarationInfo.setCont10Sold(revenueDeclarationInfo.getCont10Sold().replaceAll(",", "."));
                declaratieAvereContInfo.setSoldCont(new BigDecimal(revenueDeclarationInfo.getCont10Sold()));
            } catch (Exception e) {
                throw new ValidationException("getCont10Sold format invalid " + revenueDeclarationInfo.getCont10Sold());
            }


            declaratieAvereContInfo.setMoneda(revenueDeclarationInfo.getCont10Moneda());
            declaratieAvereContInfo.setAnDeschidereCont(revenueDeclarationInfo.getCont10AnDeschidere());

            declaratieAvereContInfoList.add(declaratieAvereContInfo);
        }

        return declaratieAvereContInfoList;
    }

    private List<DeclaratieAvereDatorieInfo> getDeclaratieAvereDatorieInfoList(RevenueDeclarationInfo revenueDeclarationInfo) {
        List<DeclaratieAvereDatorieInfo> declaratieAvereDatorieInfoList = new ArrayList<>();

        if (revenueDeclarationInfo.getExistaDatorii1().equals("Da")) {
            DeclaratieAvereDatorieInfo declaratieAvereDatorieInfo = new DeclaratieAvereDatorieInfo();
            declaratieAvereDatorieInfo.setCreditor(revenueDeclarationInfo.getDatorie1Creditor());
            declaratieAvereDatorieInfo.setAnContractare(revenueDeclarationInfo.getDatorie1AnContractare());
            declaratieAvereDatorieInfo.setScadenta(revenueDeclarationInfo.getDatorie1DataScadenta());
            declaratieAvereDatorieInfo.setMoneda(revenueDeclarationInfo.getDatorie1Moneda());

            try {
                revenueDeclarationInfo.setDatorie1Valoare(revenueDeclarationInfo.getDatorie1Valoare().replaceAll(",", "."));
                declaratieAvereDatorieInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getDatorie1Valoare()));
            } catch (Exception e) {
                throw new ValidationException("getDatorie1Valoare format invalid " + revenueDeclarationInfo.getDatorie1Valoare());
            }

            declaratieAvereDatorieInfoList.add(declaratieAvereDatorieInfo);
        }

        if (revenueDeclarationInfo.getExistaDatorii2().equals("Da")) {
            DeclaratieAvereDatorieInfo declaratieAvereDatorieInfo = new DeclaratieAvereDatorieInfo();
            declaratieAvereDatorieInfo.setCreditor(revenueDeclarationInfo.getDatorie2Creditor());
            declaratieAvereDatorieInfo.setAnContractare(revenueDeclarationInfo.getDatorie2AnContractare());
            declaratieAvereDatorieInfo.setScadenta(revenueDeclarationInfo.getDatorie2DataScadenta());
            declaratieAvereDatorieInfo.setMoneda(revenueDeclarationInfo.getDatorie2Moneda());

            try {
                revenueDeclarationInfo.setDatorie2Valoare(revenueDeclarationInfo.getDatorie2Valoare().replaceAll(",", "."));
                declaratieAvereDatorieInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getDatorie2Valoare()));
            } catch (Exception e) {
                throw new ValidationException("getDatorie2Valoare format invalid " + revenueDeclarationInfo.getDatorie2Valoare());
            }

            declaratieAvereDatorieInfoList.add(declaratieAvereDatorieInfo);
        }

        if (revenueDeclarationInfo.getExistaDatorii3().equals("Da")) {
            DeclaratieAvereDatorieInfo declaratieAvereDatorieInfo = new DeclaratieAvereDatorieInfo();
            declaratieAvereDatorieInfo.setCreditor(revenueDeclarationInfo.getDatorie3Creditor());
            declaratieAvereDatorieInfo.setAnContractare(revenueDeclarationInfo.getDatorie3AnContractare());
            declaratieAvereDatorieInfo.setScadenta(revenueDeclarationInfo.getDatorie3DataScadenta());
            declaratieAvereDatorieInfo.setMoneda(revenueDeclarationInfo.getDatorie3Moneda());

            try {
                revenueDeclarationInfo.setDatorie3Valoare(revenueDeclarationInfo.getDatorie3Valoare().replaceAll(",", "."));
                declaratieAvereDatorieInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getDatorie3Valoare()));
            } catch (Exception e) {
                throw new ValidationException("getDatorie3Valoare format invalid " + revenueDeclarationInfo.getDatorie3Valoare());
            }

            declaratieAvereDatorieInfoList.add(declaratieAvereDatorieInfo);
        }

        if (revenueDeclarationInfo.getExistaDatorii4().equals("Da")) {
            DeclaratieAvereDatorieInfo declaratieAvereDatorieInfo = new DeclaratieAvereDatorieInfo();
            declaratieAvereDatorieInfo.setCreditor(revenueDeclarationInfo.getDatorie4Creditor());
            declaratieAvereDatorieInfo.setAnContractare(revenueDeclarationInfo.getDatorie4AnContractare());
            declaratieAvereDatorieInfo.setScadenta(revenueDeclarationInfo.getDatorie4DataScadenta());
            declaratieAvereDatorieInfo.setMoneda(revenueDeclarationInfo.getDatorie4Moneda());

            try {
                revenueDeclarationInfo.setDatorie4Valoare(revenueDeclarationInfo.getDatorie4Valoare().replaceAll(",", "."));
                declaratieAvereDatorieInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getDatorie4Valoare()));
            } catch (Exception e) {
                throw new ValidationException("getDatorie4Valoare format invalid " + revenueDeclarationInfo.getDatorie4Valoare());
            }

            declaratieAvereDatorieInfoList.add(declaratieAvereDatorieInfo);
        }

        if (revenueDeclarationInfo.getExistaDatorii5().equals("Da")) {
            DeclaratieAvereDatorieInfo declaratieAvereDatorieInfo = new DeclaratieAvereDatorieInfo();
            declaratieAvereDatorieInfo.setCreditor(revenueDeclarationInfo.getDatorie5Creditor());
            declaratieAvereDatorieInfo.setAnContractare(revenueDeclarationInfo.getDatorie5AnContractare());
            declaratieAvereDatorieInfo.setScadenta(revenueDeclarationInfo.getDatorie5DataScadenta());
            declaratieAvereDatorieInfo.setMoneda(revenueDeclarationInfo.getDatorie5Moneda());

            try {
                revenueDeclarationInfo.setDatorie5Valoare(revenueDeclarationInfo.getDatorie5Valoare().replaceAll(",", "."));
                declaratieAvereDatorieInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getDatorie5Valoare()));
            } catch (Exception e) {
                throw new ValidationException("getDatorie5Valoare format invalid " + revenueDeclarationInfo.getDatorie5Valoare());
            }

            declaratieAvereDatorieInfoList.add(declaratieAvereDatorieInfo);
        }

        return declaratieAvereDatorieInfoList;
    }

    private List<DeclaratieAverePlasamentInfo> getDeclaratieAverePlasamentInfoList(RevenueDeclarationInfo revenueDeclarationInfo) {
        List<DeclaratieAverePlasamentInfo> declaratieAverePlasamentInfoList = new ArrayList<>();

        if (revenueDeclarationInfo.getExistaPlasamente1().equals("Da")) {
            DeclaratieAverePlasamentInfo declaratieAverePlasamentInfo = new DeclaratieAverePlasamentInfo();
            declaratieAverePlasamentInfo.setTitular(revenueDeclarationInfo.getPlasament1Titular());
            declaratieAverePlasamentInfo.setEmitentTitlu(revenueDeclarationInfo.getPlasament1Emitent());

            try {
                if (!revenueDeclarationInfo.getPlasament1Tip().equals("")) {
                    declaratieAverePlasamentInfo.setTipulPlasamentului(new Integer(revenueDeclarationInfo.getPlasament1Tip().substring(0, 1)));
                }
            } catch (Exception e) {
                throw new ValidationException("getPlasament1Tip format invalid " + revenueDeclarationInfo.getPlasament1Tip());
            }

            declaratieAverePlasamentInfo.setNumarTitluriSauCotaParte(revenueDeclarationInfo.getPlasament1NumarTitluri());

            try {
                revenueDeclarationInfo.setPlasament1Valoare(revenueDeclarationInfo.getPlasament1Valoare().replaceAll(",", "."));
                declaratieAverePlasamentInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getPlasament1Valoare()));
            } catch (Exception e) {
                throw new ValidationException("getPlasament1Valoare format invalid " + revenueDeclarationInfo.getPlasament1Valoare());
            }

            declaratieAverePlasamentInfo.setMoneda(revenueDeclarationInfo.getPlasament1Moneda());

            declaratieAverePlasamentInfoList.add(declaratieAverePlasamentInfo);
        }

        if (revenueDeclarationInfo.getExistaPlasamente2().equals("Da")) {
            DeclaratieAverePlasamentInfo declaratieAverePlasamentInfo = new DeclaratieAverePlasamentInfo();
            declaratieAverePlasamentInfo.setTitular(revenueDeclarationInfo.getPlasament2Titular());
            declaratieAverePlasamentInfo.setEmitentTitlu(revenueDeclarationInfo.getPlasament2Emitent());

            try {
                if (!revenueDeclarationInfo.getPlasament2Tip().equals("")) {
                    declaratieAverePlasamentInfo.setTipulPlasamentului(new Integer(revenueDeclarationInfo.getPlasament2Tip().substring(0, 1)));
                }
            } catch (Exception e) {
                throw new ValidationException("getPlasament2Tip format invalid " + revenueDeclarationInfo.getPlasament2Tip());
            }

            declaratieAverePlasamentInfo.setNumarTitluriSauCotaParte(revenueDeclarationInfo.getPlasament2NumarTitluri());

            try {
                revenueDeclarationInfo.setPlasament2Valoare(revenueDeclarationInfo.getPlasament2Valoare().replaceAll(",", "."));
                declaratieAverePlasamentInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getPlasament2Valoare()));
            } catch (Exception e) {
                throw new ValidationException("getPlasament2Valoare format invalid " + revenueDeclarationInfo.getPlasament2Valoare());
            }

            declaratieAverePlasamentInfo.setMoneda(revenueDeclarationInfo.getPlasament2Moneda());

            declaratieAverePlasamentInfoList.add(declaratieAverePlasamentInfo);
        }

        if (revenueDeclarationInfo.getExistaPlasamente3().equals("Da")) {
            DeclaratieAverePlasamentInfo declaratieAverePlasamentInfo = new DeclaratieAverePlasamentInfo();
            declaratieAverePlasamentInfo.setTitular(revenueDeclarationInfo.getPlasament3Titular());
            declaratieAverePlasamentInfo.setEmitentTitlu(revenueDeclarationInfo.getPlasament3Emitent());

            try {
                if (!revenueDeclarationInfo.getPlasament3Tip().equals("")) {
                    declaratieAverePlasamentInfo.setTipulPlasamentului(new Integer(revenueDeclarationInfo.getPlasament3Tip().substring(0, 1)));
                }
            } catch (Exception e) {
                throw new ValidationException("getPlasament3Tip format invalid " + revenueDeclarationInfo.getPlasament3Tip());
            }

            declaratieAverePlasamentInfo.setNumarTitluriSauCotaParte(revenueDeclarationInfo.getPlasament3NumarTitluri());

            try {
                revenueDeclarationInfo.setPlasament3Valoare(revenueDeclarationInfo.getPlasament3Valoare().replaceAll(",", "."));
                declaratieAverePlasamentInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getPlasament3Valoare()));
            } catch (Exception e) {
                throw new ValidationException("getPlasament3Valoare format invalid " + revenueDeclarationInfo.getPlasament3Valoare());
            }

            declaratieAverePlasamentInfo.setMoneda(revenueDeclarationInfo.getPlasament3Moneda());

            declaratieAverePlasamentInfoList.add(declaratieAverePlasamentInfo);
        }

        if (revenueDeclarationInfo.getExistaPlasamente4().equals("Da")) {
            DeclaratieAverePlasamentInfo declaratieAverePlasamentInfo = new DeclaratieAverePlasamentInfo();
            declaratieAverePlasamentInfo.setTitular(revenueDeclarationInfo.getPlasament4Titular());
            declaratieAverePlasamentInfo.setEmitentTitlu(revenueDeclarationInfo.getPlasament4Emitent());

            try {
                if (!revenueDeclarationInfo.getPlasament4Tip().equals("")) {
                    declaratieAverePlasamentInfo.setTipulPlasamentului(new Integer(revenueDeclarationInfo.getPlasament4Tip().substring(0, 1)));
                }
            } catch (Exception e) {
                throw new ValidationException("getPlasament4Tip format invalid " + revenueDeclarationInfo.getPlasament4Tip());
            }

            declaratieAverePlasamentInfo.setNumarTitluriSauCotaParte(revenueDeclarationInfo.getPlasament4NumarTitluri());

            try {
                revenueDeclarationInfo.setPlasament4Valoare(revenueDeclarationInfo.getPlasament4Valoare().replaceAll(",", "."));
                declaratieAverePlasamentInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getPlasament4Valoare()));
            } catch (Exception e) {
                throw new ValidationException("getPlasament4Valoare format invalid " + revenueDeclarationInfo.getPlasament4Valoare());
            }

            declaratieAverePlasamentInfo.setMoneda(revenueDeclarationInfo.getPlasament4Moneda());

            declaratieAverePlasamentInfoList.add(declaratieAverePlasamentInfo);
        }

        if (revenueDeclarationInfo.getExistaPlasamente5().equals("Da")) {
            DeclaratieAverePlasamentInfo declaratieAverePlasamentInfo = new DeclaratieAverePlasamentInfo();
            declaratieAverePlasamentInfo.setTitular(revenueDeclarationInfo.getPlasament5Titular());
            declaratieAverePlasamentInfo.setEmitentTitlu(revenueDeclarationInfo.getPlasament5Emitent());

            try {
                if (!revenueDeclarationInfo.getPlasament5Tip().equals("")) {
                    declaratieAverePlasamentInfo.setTipulPlasamentului(new Integer(revenueDeclarationInfo.getPlasament5Tip().substring(0, 1)));
                }
            } catch (Exception e) {
                throw new ValidationException("getPlasament5Tip format invalid " + revenueDeclarationInfo.getPlasament5Tip());
            }

            declaratieAverePlasamentInfo.setNumarTitluriSauCotaParte(revenueDeclarationInfo.getPlasament5NumarTitluri());

            try {
                revenueDeclarationInfo.setPlasament5Valoare(revenueDeclarationInfo.getPlasament5Valoare().replaceAll(",", "."));
                declaratieAverePlasamentInfo.setValoare(new BigDecimal(revenueDeclarationInfo.getPlasament5Valoare()));
            } catch (Exception e) {
                throw new ValidationException("getPlasament5Valoare format invalid " + revenueDeclarationInfo.getPlasament5Valoare());
            }

            declaratieAverePlasamentInfo.setMoneda(revenueDeclarationInfo.getPlasament5Moneda());

            declaratieAverePlasamentInfoList.add(declaratieAverePlasamentInfo);
        }

        return declaratieAverePlasamentInfoList;
    }

    private List<DeclaratieAvereBijuterieInfo> getDeclaratieAvereBijuterieInfoList(RevenueDeclarationInfo revenueDeclarationInfo) {
        List<DeclaratieAvereBijuterieInfo> declaratieAvereBijuterieInfoList = new ArrayList<>();

        if (revenueDeclarationInfo.getExistaBijuterii1().equals("Da")) {
            DeclaratieAvereBijuterieInfo declaratieAvereBijuterieInfo = new DeclaratieAvereBijuterieInfo();
            declaratieAvereBijuterieInfo.setDescriere(revenueDeclarationInfo.getBijuterie1Descriere());
            declaratieAvereBijuterieInfo.setAnDobandire(revenueDeclarationInfo.getBijuterie1AnDobandire());

            try {
                declaratieAvereBijuterieInfo.setValoareEstimate(new BigDecimal(revenueDeclarationInfo.getBijuterie1ValoareEstimata()));
            } catch (Exception e) {
                throw new ValidationException("getBijuterie1ValoareEstimata format invalid " + revenueDeclarationInfo.getBijuterie1ValoareEstimata());
            }

            declaratieAvereBijuterieInfo.setMoneda(revenueDeclarationInfo.getBijuterie1Moneda());

            declaratieAvereBijuterieInfoList.add(declaratieAvereBijuterieInfo);
        }

        if (revenueDeclarationInfo.getExistaBijuterii2().equals("Da")) {
            DeclaratieAvereBijuterieInfo declaratieAvereBijuterieInfo = new DeclaratieAvereBijuterieInfo();
            declaratieAvereBijuterieInfo.setDescriere(revenueDeclarationInfo.getBijuterie2Descriere());
            declaratieAvereBijuterieInfo.setAnDobandire(revenueDeclarationInfo.getBijuterie2AnDobandire());

            try {
                declaratieAvereBijuterieInfo.setValoareEstimate(new BigDecimal(revenueDeclarationInfo.getBijuterie2ValoareEstimata()));
            } catch (Exception e) {
                throw new ValidationException("getBijuterie2ValoareEstimata format invalid " + revenueDeclarationInfo.getBijuterie2ValoareEstimata());
            }

            declaratieAvereBijuterieInfo.setMoneda(revenueDeclarationInfo.getBijuterie2Moneda());

            declaratieAvereBijuterieInfoList.add(declaratieAvereBijuterieInfo);
        }

        if (revenueDeclarationInfo.getExistaBijuterii3().equals("Da")) {
            DeclaratieAvereBijuterieInfo declaratieAvereBijuterieInfo = new DeclaratieAvereBijuterieInfo();
            declaratieAvereBijuterieInfo.setDescriere(revenueDeclarationInfo.getBijuterie3Descriere());
            declaratieAvereBijuterieInfo.setAnDobandire(revenueDeclarationInfo.getBijuterie3AnDobandire());

            try {
                declaratieAvereBijuterieInfo.setValoareEstimate(new BigDecimal(revenueDeclarationInfo.getBijuterie3ValoareEstimata()));
            } catch (Exception e) {
                throw new ValidationException("getBijuterie3ValoareEstimata format invalid " + revenueDeclarationInfo.getBijuterie3ValoareEstimata());
            }

            declaratieAvereBijuterieInfo.setMoneda(revenueDeclarationInfo.getBijuterie3Moneda());

            declaratieAvereBijuterieInfoList.add(declaratieAvereBijuterieInfo);
        }

        if (revenueDeclarationInfo.getExistaBijuterii4().equals("Da")) {
            DeclaratieAvereBijuterieInfo declaratieAvereBijuterieInfo = new DeclaratieAvereBijuterieInfo();
            declaratieAvereBijuterieInfo.setDescriere(revenueDeclarationInfo.getBijuterie4Descriere());
            declaratieAvereBijuterieInfo.setAnDobandire(revenueDeclarationInfo.getBijuterie4AnDobandire());

            try {
                declaratieAvereBijuterieInfo.setValoareEstimate(new BigDecimal(revenueDeclarationInfo.getBijuterie4ValoareEstimata()));
            } catch (Exception e) {
                throw new ValidationException("getBijuterie4ValoareEstimata format invalid " + revenueDeclarationInfo.getBijuterie4ValoareEstimata());
            }

            declaratieAvereBijuterieInfo.setMoneda(revenueDeclarationInfo.getBijuterie4Moneda());

            declaratieAvereBijuterieInfoList.add(declaratieAvereBijuterieInfo);
        }

        if (revenueDeclarationInfo.getExistaBijuterii5().equals("Da")) {
            DeclaratieAvereBijuterieInfo declaratieAvereBijuterieInfo = new DeclaratieAvereBijuterieInfo();
            declaratieAvereBijuterieInfo.setDescriere(revenueDeclarationInfo.getBijuterie5Descriere());
            declaratieAvereBijuterieInfo.setAnDobandire(revenueDeclarationInfo.getBijuterie5AnDobandire());


            try {
                declaratieAvereBijuterieInfo.setValoareEstimate(new BigDecimal(revenueDeclarationInfo.getBijuterie5ValoareEstimata()));
            } catch (Exception e) {
                throw new ValidationException("getBijuterie5ValoareEstimata format invalid " + revenueDeclarationInfo.getBijuterie5ValoareEstimata());
            }

            declaratieAvereBijuterieInfo.setMoneda(revenueDeclarationInfo.getBijuterie5Moneda());

            declaratieAvereBijuterieInfoList.add(declaratieAvereBijuterieInfo);
        }

        return declaratieAvereBijuterieInfoList;
    }

    private List<DeclaratieAvereBunMobilInfo> getDeclaratieAvereBunMobilInfoList(RevenueDeclarationInfo revenueDeclarationInfo) {
        List<DeclaratieAvereBunMobilInfo> declaratieAvereBunMobilInfoList = new ArrayList<>();

        if (revenueDeclarationInfo.getExistaAutomobile1().equals("Da")) {
            DeclaratieAvereBunMobilInfo declaratieAvereBunMobilInfo = new DeclaratieAvereBunMobilInfo();
            declaratieAvereBunMobilInfo.setTip(revenueDeclarationInfo.getAutomobil1Tip());
            declaratieAvereBunMobilInfo.setMarca(revenueDeclarationInfo.getAutomobil1Marca());

            try {
                declaratieAvereBunMobilInfo.setCantitate(new Integer(revenueDeclarationInfo.getAutomobil1Cantitate()));
            } catch (Exception e) {
                throw new ValidationException("getAutomobil1Cantitate format invalid " + revenueDeclarationInfo.getAutomobil1Cantitate());
            }

            declaratieAvereBunMobilInfo.setAnFabricare(revenueDeclarationInfo.getAutomobil1AnFabricatie());
            declaratieAvereBunMobilInfo.setModDobandire(revenueDeclarationInfo.getAutomobil1ModDobandire());
            declaratieAvereBunMobilInfoList.add(declaratieAvereBunMobilInfo);
        }

        if (revenueDeclarationInfo.getExistaAutomobile2().equals("Da")) {
            DeclaratieAvereBunMobilInfo declaratieAvereBunMobilInfo = new DeclaratieAvereBunMobilInfo();
            declaratieAvereBunMobilInfo.setTip(revenueDeclarationInfo.getAutomobil2Tip());
            declaratieAvereBunMobilInfo.setMarca(revenueDeclarationInfo.getAutomobil2Marca());

            try {
                declaratieAvereBunMobilInfo.setCantitate(new Integer(revenueDeclarationInfo.getAutomobil2Cantitate()));
            } catch (Exception e) {
                throw new ValidationException("getAutomobil2Cantitate format invalid " + revenueDeclarationInfo.getAutomobil2Cantitate());
            }

            declaratieAvereBunMobilInfo.setAnFabricare(revenueDeclarationInfo.getAutomobil2AnFabricatie());
            declaratieAvereBunMobilInfo.setModDobandire(revenueDeclarationInfo.getAutomobil2ModDobandire());
            declaratieAvereBunMobilInfoList.add(declaratieAvereBunMobilInfo);
        }

        if (revenueDeclarationInfo.getExistaAutomobile3().equals("Da")) {
            DeclaratieAvereBunMobilInfo declaratieAvereBunMobilInfo = new DeclaratieAvereBunMobilInfo();
            declaratieAvereBunMobilInfo.setTip(revenueDeclarationInfo.getAutomobil3Tip());
            declaratieAvereBunMobilInfo.setMarca(revenueDeclarationInfo.getAutomobil3Marca());

            try {
                declaratieAvereBunMobilInfo.setCantitate(new Integer(revenueDeclarationInfo.getAutomobil3Cantitate()));
            } catch (Exception e) {
                throw new ValidationException("getAutomobil3Cantitate format invalid " + revenueDeclarationInfo.getAutomobil3Cantitate());
            }

            declaratieAvereBunMobilInfo.setAnFabricare(revenueDeclarationInfo.getAutomobil3AnFabricatie());
            declaratieAvereBunMobilInfo.setModDobandire(revenueDeclarationInfo.getAutomobil3ModDobandire());
            declaratieAvereBunMobilInfoList.add(declaratieAvereBunMobilInfo);
        }

        if (revenueDeclarationInfo.getExistaAutomobile4().equals("Da")) {
            DeclaratieAvereBunMobilInfo declaratieAvereBunMobilInfo = new DeclaratieAvereBunMobilInfo();
            declaratieAvereBunMobilInfo.setTip(revenueDeclarationInfo.getAutomobil4Tip());
            declaratieAvereBunMobilInfo.setMarca(revenueDeclarationInfo.getAutomobil4Marca());

            try {
                declaratieAvereBunMobilInfo.setCantitate(new Integer(revenueDeclarationInfo.getAutomobil4Cantitate()));
            } catch (Exception e) {
                throw new ValidationException("getAutomobil4Cantitate format invalid " + revenueDeclarationInfo.getAutomobil4Cantitate());
            }

            declaratieAvereBunMobilInfo.setAnFabricare(revenueDeclarationInfo.getAutomobil4AnFabricatie());
            declaratieAvereBunMobilInfo.setModDobandire(revenueDeclarationInfo.getAutomobil4ModDobandire());
            declaratieAvereBunMobilInfoList.add(declaratieAvereBunMobilInfo);
        }

        if (revenueDeclarationInfo.getExistaAutomobile5().equals("Da")) {
            DeclaratieAvereBunMobilInfo declaratieAvereBunMobilInfo = new DeclaratieAvereBunMobilInfo();
            declaratieAvereBunMobilInfo.setTip(revenueDeclarationInfo.getAutomobil5Tip());
            declaratieAvereBunMobilInfo.setMarca(revenueDeclarationInfo.getAutomobil5Marca());

            try {
                declaratieAvereBunMobilInfo.setCantitate(new Integer(revenueDeclarationInfo.getAutomobil5Cantitate()));
            } catch (Exception e) {
                throw new ValidationException("getAutomobil5Cantitate format invalid " + revenueDeclarationInfo.getAutomobil5Cantitate());
            }

            declaratieAvereBunMobilInfo.setAnFabricare(revenueDeclarationInfo.getAutomobil5AnFabricatie());
            declaratieAvereBunMobilInfo.setModDobandire(revenueDeclarationInfo.getAutomobil5ModDobandire());
            declaratieAvereBunMobilInfoList.add(declaratieAvereBunMobilInfo);
        }

        return declaratieAvereBunMobilInfoList;
    }

    private List<DeclaratieAvereBunImobilInfo> getDeclaratieAvereBunImobilInfoList(RevenueDeclarationInfo revenueDeclarationInfo) {
        List<DeclaratieAvereBunImobilInfo> declaratieAvereBunImobilInfoList = new ArrayList<>();

        if (revenueDeclarationInfo.getExistaTerenuri1().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(true);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getTeren1Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getTeren1ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setTerenCategorie(new Integer(revenueDeclarationInfo.getTeren1Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("teren1Categoria format invalid " + revenueDeclarationInfo.getTeren1Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getTeren1AnDobindire());

            try {
                revenueDeclarationInfo.setTeren1Suprafata(revenueDeclarationInfo.getTeren1Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setTeren1Suprafata(revenueDeclarationInfo.getTeren1Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setTeren1Suprafata(revenueDeclarationInfo.getTeren1Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getTeren1Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getTeren1Suprafata format invalid " + revenueDeclarationInfo.getTeren1Suprafata());
            }
            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getTeren1ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getTeren1Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getTeren1CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getTeren1UnitateDeMasura());


            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaTerenuri2().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(true);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getTeren2Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getTeren2ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setTerenCategorie(new Integer(revenueDeclarationInfo.getTeren2Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("teren2Categoria format invalid " + revenueDeclarationInfo.getTeren2Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getTeren2AnDobindire());

            try {
                revenueDeclarationInfo.setTeren2Suprafata(revenueDeclarationInfo.getTeren2Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setTeren2Suprafata(revenueDeclarationInfo.getTeren2Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setTeren2Suprafata(revenueDeclarationInfo.getTeren2Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getTeren2Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getTeren2Suprafata format invalid " + revenueDeclarationInfo.getTeren2Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getTeren2ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getTeren2Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getTeren2CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getTeren2UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaTerenuri3().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(true);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getTeren3Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getTeren3ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setTerenCategorie(new Integer(revenueDeclarationInfo.getTeren3Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("teren3Categoria format invalid " + revenueDeclarationInfo.getTeren3Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getTeren3AnDobindire());

            try {
                revenueDeclarationInfo.setTeren3Suprafata(revenueDeclarationInfo.getTeren3Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setTeren3Suprafata(revenueDeclarationInfo.getTeren3Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setTeren3Suprafata(revenueDeclarationInfo.getTeren3Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getTeren3Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getTeren3Suprafata format invalid " + revenueDeclarationInfo.getTeren3Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getTeren3ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getTeren3Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getTeren3CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getTeren3UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaTerenuri4().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(true);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getTeren4Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getTeren4ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setTerenCategorie(new Integer(revenueDeclarationInfo.getTeren4Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("teren4Categoria format invalid " + revenueDeclarationInfo.getTeren4Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getTeren4AnDobindire());

            try {
                revenueDeclarationInfo.setTeren4Suprafata(revenueDeclarationInfo.getTeren4Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setTeren4Suprafata(revenueDeclarationInfo.getTeren4Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setTeren4Suprafata(revenueDeclarationInfo.getTeren4Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getTeren4Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getTeren4Suprafata format invalid " + revenueDeclarationInfo.getTeren4Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getTeren4ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getTeren4Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getTeren4CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getTeren4UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaTerenuri5().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(true);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getTeren5Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getTeren5ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setTerenCategorie(new Integer(revenueDeclarationInfo.getTeren5Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("teren5Categoria format invalid " + revenueDeclarationInfo.getTeren5Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getTeren5AnDobindire());

            try {
                revenueDeclarationInfo.setTeren5Suprafata(revenueDeclarationInfo.getTeren5Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setTeren5Suprafata(revenueDeclarationInfo.getTeren5Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setTeren5Suprafata(revenueDeclarationInfo.getTeren5Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getTeren5Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getTeren5Suprafata format invalid " + revenueDeclarationInfo.getTeren5Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getTeren5ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getTeren5Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getTeren5CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getTeren5UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaTerenuri6().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(true);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getTeren6Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getTeren6ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setTerenCategorie(new Integer(revenueDeclarationInfo.getTeren6Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("teren6Categoria format invalid " + revenueDeclarationInfo.getTeren6Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getTeren6AnDobindire());

            try {
                revenueDeclarationInfo.setTeren6Suprafata(revenueDeclarationInfo.getTeren6Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setTeren6Suprafata(revenueDeclarationInfo.getTeren6Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setTeren6Suprafata(revenueDeclarationInfo.getTeren6Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getTeren6Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getTeren6Suprafata format invalid " + revenueDeclarationInfo.getTeren6Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getTeren6ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getTeren6Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getTeren6CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getTeren6UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaTerenuri7().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(true);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getTeren7Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getTeren7ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setTerenCategorie(new Integer(revenueDeclarationInfo.getTeren7Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("teren7Categoria format invalid " + revenueDeclarationInfo.getTeren7Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getTeren7AnDobindire());

            try {
                revenueDeclarationInfo.setTeren7Suprafata(revenueDeclarationInfo.getTeren7Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setTeren7Suprafata(revenueDeclarationInfo.getTeren7Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setTeren7Suprafata(revenueDeclarationInfo.getTeren7Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getTeren7Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getTeren7Suprafata format invalid " + revenueDeclarationInfo.getTeren7Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getTeren7ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getTeren7Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getTeren7CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getTeren7UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaTerenuri8().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(true);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getTeren8Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getTeren8ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setTerenCategorie(new Integer(revenueDeclarationInfo.getTeren8Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("teren8Categoria format invalid " + revenueDeclarationInfo.getTeren8Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getTeren8AnDobindire());

            try {
                revenueDeclarationInfo.setTeren8Suprafata(revenueDeclarationInfo.getTeren8Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setTeren8Suprafata(revenueDeclarationInfo.getTeren8Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setTeren8Suprafata(revenueDeclarationInfo.getTeren8Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getTeren8Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getTeren8Suprafata format invalid " + revenueDeclarationInfo.getTeren8Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getTeren8ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getTeren8Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getTeren8CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getTeren8UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaTerenuri9().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(true);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getTeren9Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getTeren9ExplicatieSuprafata());
            try {
                declaratieAvereBunImobilInfo.setTerenCategorie(new Integer(revenueDeclarationInfo.getTeren9Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("teren9Categoria format invalid " + revenueDeclarationInfo.getTeren9Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getTeren9AnDobindire());

            try {
                revenueDeclarationInfo.setTeren9Suprafata(revenueDeclarationInfo.getTeren9Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setTeren9Suprafata(revenueDeclarationInfo.getTeren9Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setTeren9Suprafata(revenueDeclarationInfo.getTeren9Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getTeren9Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getTeren9Suprafata format invalid " + revenueDeclarationInfo.getTeren9Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getTeren9ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getTeren9Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getTeren9CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getTeren9UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaTerenuri10().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(true);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getTeren10Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getTeren10ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setTerenCategorie(new Integer(revenueDeclarationInfo.getTeren10Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("teren10Categoria format invalid " + revenueDeclarationInfo.getTeren10Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getTeren10AnDobindire());

            try {
                revenueDeclarationInfo.setTeren10Suprafata(revenueDeclarationInfo.getTeren10Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setTeren10Suprafata(revenueDeclarationInfo.getTeren10Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setTeren10Suprafata(revenueDeclarationInfo.getTeren10Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getTeren10Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getTeren10Suprafata format invalid " + revenueDeclarationInfo.getTeren10Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getTeren10ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getTeren10Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getTeren10CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getTeren10UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaTerenuri11().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(true);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getTeren11Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getTeren11ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setTerenCategorie(new Integer(revenueDeclarationInfo.getTeren11Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("teren11Categoria format invalid " + revenueDeclarationInfo.getTeren11Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getTeren11AnDobindire());

            try {
                revenueDeclarationInfo.setTeren11Suprafata(revenueDeclarationInfo.getTeren11Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setTeren11Suprafata(revenueDeclarationInfo.getTeren11Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setTeren11Suprafata(revenueDeclarationInfo.getTeren11Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getTeren11Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getTeren11Suprafata format invalid " + revenueDeclarationInfo.getTeren11Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getTeren11ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getTeren11Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getTeren11CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getTeren11UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaTerenuri12().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(true);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getTeren12Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getTeren12ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setTerenCategorie(new Integer(revenueDeclarationInfo.getTeren12Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("teren12Categoria format invalid " + revenueDeclarationInfo.getTeren12Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getTeren12AnDobindire());

            try {
                revenueDeclarationInfo.setTeren12Suprafata(revenueDeclarationInfo.getTeren12Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setTeren12Suprafata(revenueDeclarationInfo.getTeren12Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setTeren12Suprafata(revenueDeclarationInfo.getTeren12Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getTeren12Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getTeren12Suprafata format invalid " + revenueDeclarationInfo.getTeren12Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getTeren12ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getTeren12Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getTeren12CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getTeren12UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaTerenuri13().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(true);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getTeren13Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getTeren13ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setTerenCategorie(new Integer(revenueDeclarationInfo.getTeren13Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("teren13Categoria format invalid " + revenueDeclarationInfo.getTeren13Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getTeren13AnDobindire());

            try {
                revenueDeclarationInfo.setTeren13Suprafata(revenueDeclarationInfo.getTeren13Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setTeren13Suprafata(revenueDeclarationInfo.getTeren13Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setTeren13Suprafata(revenueDeclarationInfo.getTeren13Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getTeren13Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getTeren13Suprafata format invalid " + revenueDeclarationInfo.getTeren13Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getTeren13ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getTeren13Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getTeren13CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getTeren13UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaTerenuri14().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(true);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getTeren14Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getTeren14ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setTerenCategorie(new Integer(revenueDeclarationInfo.getTeren14Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("teren14Categoria format invalid " + revenueDeclarationInfo.getTeren14Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getTeren14AnDobindire());

            try {
                revenueDeclarationInfo.setTeren14Suprafata(revenueDeclarationInfo.getTeren14Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setTeren14Suprafata(revenueDeclarationInfo.getTeren14Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setTeren14Suprafata(revenueDeclarationInfo.getTeren14Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getTeren14Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getTeren14Suprafata format invalid " + revenueDeclarationInfo.getTeren14Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getTeren14ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getTeren14Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getTeren14CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getTeren14UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaTerenuri15().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(true);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getTeren15Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getTeren15ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setTerenCategorie(new Integer(revenueDeclarationInfo.getTeren15Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("teren15Categoria format invalid " + revenueDeclarationInfo.getTeren15Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getTeren15AnDobindire());

            try {
                revenueDeclarationInfo.setTeren15Suprafata(revenueDeclarationInfo.getTeren15Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setTeren15Suprafata(revenueDeclarationInfo.getTeren15Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setTeren15Suprafata(revenueDeclarationInfo.getTeren15Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getTeren15Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getTeren15Suprafata format invalid " + revenueDeclarationInfo.getTeren15Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getTeren15ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getTeren15Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getTeren15CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getTeren15UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }


        // CLADIRI

        if (revenueDeclarationInfo.getExistaCladiri1().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(false);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getCladire1Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getCladire1ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setCladireCategorie(new Integer(revenueDeclarationInfo.getCladire1Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("cladire1Categoria format invalid " + revenueDeclarationInfo.getCladire1Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getCladire1AnDobindire());

            try {
                revenueDeclarationInfo.setCladire1Suprafata(revenueDeclarationInfo.getCladire1Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setCladire1Suprafata(revenueDeclarationInfo.getCladire1Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setCladire1Suprafata(revenueDeclarationInfo.getCladire1Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getCladire1Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getCladire1Suprafata format invalid " + revenueDeclarationInfo.getCladire1Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getCladire1ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getCladire1Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getCladire1CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getCladire1UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaCladiri2().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(false);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getCladire2Adresa());


            try {
                declaratieAvereBunImobilInfo.setCladireCategorie(new Integer(revenueDeclarationInfo.getCladire2Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("cladire2Categoria format invalid " + revenueDeclarationInfo.getCladire2Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getCladire2AnDobindire());

            try {
                revenueDeclarationInfo.setCladire2Suprafata(revenueDeclarationInfo.getCladire2Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setCladire2Suprafata(revenueDeclarationInfo.getCladire2Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setCladire2Suprafata(revenueDeclarationInfo.getCladire2Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getCladire2Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getCladire2Suprafata format invalid " + revenueDeclarationInfo.getCladire2Suprafata());
            }
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getCladire2ExplicatieSuprafata());
            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getCladire2ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getCladire2Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getCladire2CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getCladire2UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaCladiri3().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(false);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getCladire3Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getCladire3ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setCladireCategorie(new Integer(revenueDeclarationInfo.getCladire3Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("cladire3Categoria format invalid " + revenueDeclarationInfo.getCladire3Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getCladire3AnDobindire());

            try {
                revenueDeclarationInfo.setCladire3Suprafata(revenueDeclarationInfo.getCladire3Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setCladire3Suprafata(revenueDeclarationInfo.getCladire3Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setCladire3Suprafata(revenueDeclarationInfo.getCladire3Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getCladire3Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getCladire3Suprafata format invalid " + revenueDeclarationInfo.getCladire3Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getCladire3ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getCladire3Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getCladire3CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getCladire3UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaCladiri4().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(false);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getCladire4Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getCladire4ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setCladireCategorie(new Integer(revenueDeclarationInfo.getCladire4Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("cladire4Categoria format invalid " + revenueDeclarationInfo.getCladire4Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getCladire4AnDobindire());

            try {
                revenueDeclarationInfo.setCladire4Suprafata(revenueDeclarationInfo.getCladire4Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setCladire4Suprafata(revenueDeclarationInfo.getCladire4Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setCladire4Suprafata(revenueDeclarationInfo.getCladire4Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getCladire4Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getCladire4Suprafata format invalid " + revenueDeclarationInfo.getCladire4Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getCladire4ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getCladire4Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getCladire4CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getCladire4UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaCladiri5().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(false);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getCladire5Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getCladire5ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setCladireCategorie(new Integer(revenueDeclarationInfo.getCladire5Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("cladire5Categoria format invalid " + revenueDeclarationInfo.getCladire5Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getCladire5AnDobindire());

            try {
                revenueDeclarationInfo.setCladire5Suprafata(revenueDeclarationInfo.getCladire5Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setCladire5Suprafata(revenueDeclarationInfo.getCladire5Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setCladire5Suprafata(revenueDeclarationInfo.getCladire5Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getCladire5Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getCladire5Suprafata format invalid " + revenueDeclarationInfo.getCladire5Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getCladire5ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getCladire5Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getCladire5CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getCladire5UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaCladiri6().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(false);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getCladire6Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getCladire6ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setCladireCategorie(new Integer(revenueDeclarationInfo.getCladire6Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("cladire6Categoria format invalid " + revenueDeclarationInfo.getCladire6Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getCladire6AnDobindire());

            try {
                revenueDeclarationInfo.setCladire6Suprafata(revenueDeclarationInfo.getCladire6Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setCladire6Suprafata(revenueDeclarationInfo.getCladire6Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setCladire6Suprafata(revenueDeclarationInfo.getCladire6Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getCladire6Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getCladire6Suprafata format invalid " + revenueDeclarationInfo.getCladire6Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getCladire6ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getCladire6Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getCladire6CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getCladire6UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaCladiri7().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(false);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getCladire7Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getCladire7ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setCladireCategorie(new Integer(revenueDeclarationInfo.getCladire7Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("cladire7Categoria format invalid " + revenueDeclarationInfo.getCladire7Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getCladire7AnDobindire());

            try {
                revenueDeclarationInfo.setCladire7Suprafata(revenueDeclarationInfo.getCladire7Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setCladire7Suprafata(revenueDeclarationInfo.getCladire7Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setCladire7Suprafata(revenueDeclarationInfo.getCladire7Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getCladire7Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getCladire7Suprafata format invalid " + revenueDeclarationInfo.getCladire7Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getCladire7ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getCladire7Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getCladire7CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getCladire7UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaCladiri8().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(false);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getCladire8Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getCladire8ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setCladireCategorie(new Integer(revenueDeclarationInfo.getCladire8Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("cladire8Categoria format invalid " + revenueDeclarationInfo.getCladire8Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getCladire8AnDobindire());

            try {
                revenueDeclarationInfo.setCladire8Suprafata(revenueDeclarationInfo.getCladire8Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setCladire8Suprafata(revenueDeclarationInfo.getCladire8Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setCladire8Suprafata(revenueDeclarationInfo.getCladire8Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getCladire8Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getCladire8Suprafata format invalid " + revenueDeclarationInfo.getCladire8Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getCladire8ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getCladire8Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getCladire8CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getCladire8UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaCladiri9().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(false);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getCladire9Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getCladire9ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setCladireCategorie(new Integer(revenueDeclarationInfo.getCladire9Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("cladire9Categoria format invalid " + revenueDeclarationInfo.getCladire9Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getCladire9AnDobindire());

            try {
                revenueDeclarationInfo.setCladire9Suprafata(revenueDeclarationInfo.getCladire9Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setCladire9Suprafata(revenueDeclarationInfo.getCladire9Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setCladire9Suprafata(revenueDeclarationInfo.getCladire9Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getCladire9Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getCladire9Suprafata format invalid " + revenueDeclarationInfo.getCladire9Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getCladire9ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getCladire9Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getCladire9CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getCladire9UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaCladiri10().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(false);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getCladire10Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getCladire10ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setCladireCategorie(new Integer(revenueDeclarationInfo.getCladire10Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("cladire10Categoria format invalid " + revenueDeclarationInfo.getCladire10Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getCladire10AnDobindire());

            try {
                revenueDeclarationInfo.setCladire10Suprafata(revenueDeclarationInfo.getCladire10Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setCladire10Suprafata(revenueDeclarationInfo.getCladire10Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setCladire10Suprafata(revenueDeclarationInfo.getCladire10Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getCladire10Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getCladire10Suprafata format invalid " + revenueDeclarationInfo.getCladire10Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getCladire10ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getCladire10Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getCladire10CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getCladire10UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaCladiri11().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(false);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getCladire11Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getCladire11ExplicatieSuprafata());
            try {
                declaratieAvereBunImobilInfo.setCladireCategorie(new Integer(revenueDeclarationInfo.getCladire11Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("cladire11Categoria format invalid " + revenueDeclarationInfo.getCladire11Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getCladire11AnDobindire());

            try {
                revenueDeclarationInfo.setCladire11Suprafata(revenueDeclarationInfo.getCladire11Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setCladire11Suprafata(revenueDeclarationInfo.getCladire11Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setCladire11Suprafata(revenueDeclarationInfo.getCladire11Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getCladire11Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getCladire11Suprafata format invalid " + revenueDeclarationInfo.getCladire11Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getCladire11ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getCladire11Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getCladire11CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getCladire11UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaCladiri12().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(false);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getCladire12Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getCladire12ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setCladireCategorie(new Integer(revenueDeclarationInfo.getCladire12Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("cladire12Categoria format invalid " + revenueDeclarationInfo.getCladire12Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getCladire12AnDobindire());

            try {
                revenueDeclarationInfo.setCladire12Suprafata(revenueDeclarationInfo.getCladire12Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setCladire12Suprafata(revenueDeclarationInfo.getCladire12Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setCladire12Suprafata(revenueDeclarationInfo.getCladire12Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getCladire12Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getCladire12Suprafata format invalid " + revenueDeclarationInfo.getCladire12Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getCladire12ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getCladire12Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getCladire12CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getCladire12UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaCladiri13().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(false);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getCladire13Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getCladire13ExplicatieSuprafata());
            try {
                declaratieAvereBunImobilInfo.setCladireCategorie(new Integer(revenueDeclarationInfo.getCladire13Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("cladire13Categoria format invalid " + revenueDeclarationInfo.getCladire13Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getCladire13AnDobindire());

            try {
                revenueDeclarationInfo.setCladire13Suprafata(revenueDeclarationInfo.getCladire13Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setCladire13Suprafata(revenueDeclarationInfo.getCladire13Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setCladire13Suprafata(revenueDeclarationInfo.getCladire13Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getCladire13Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getCladire13Suprafata format invalid " + revenueDeclarationInfo.getCladire13Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getCladire13ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getCladire13Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getCladire13CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getCladire13UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaCladiri14().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(false);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getCladire14Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getCladire14ExplicatieSuprafata());

            try {
                declaratieAvereBunImobilInfo.setCladireCategorie(new Integer(revenueDeclarationInfo.getCladire14Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("cladire14Categoria format invalid " + revenueDeclarationInfo.getCladire14Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getCladire14AnDobindire());

            try {
                revenueDeclarationInfo.setCladire14Suprafata(revenueDeclarationInfo.getCladire14Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setCladire14Suprafata(revenueDeclarationInfo.getCladire14Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setCladire14Suprafata(revenueDeclarationInfo.getCladire14Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getCladire14Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getCladire14Suprafata format invalid " + revenueDeclarationInfo.getCladire14Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getCladire14ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getCladire14Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getCladire14CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getCladire14UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        if (revenueDeclarationInfo.getExistaCladiri15().equals("Da")) {
            DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo = new DeclaratieAvereBunImobilInfo();
            declaratieAvereBunImobilInfo.setIsTeren(false);
            declaratieAvereBunImobilInfo.setAdresaImobil(revenueDeclarationInfo.getCladire15Adresa());
            declaratieAvereBunImobilInfo.setExplicatieSuprafata(revenueDeclarationInfo.getCladire15ExplicatieSuprafata());
            try {
                declaratieAvereBunImobilInfo.setCladireCategorie(new Integer(revenueDeclarationInfo.getCladire15Categoria().substring(0, 1)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new ValidationException("cladire15Categoria format invalid " + revenueDeclarationInfo.getCladire15Categoria());
            }

            declaratieAvereBunImobilInfo.setAnDobandire(revenueDeclarationInfo.getCladire15AnDobindire());

            try {
                revenueDeclarationInfo.setCladire15Suprafata(revenueDeclarationInfo.getCladire15Suprafata().replaceAll(" ", ""));
                revenueDeclarationInfo.setCladire15Suprafata(revenueDeclarationInfo.getCladire15Suprafata().replaceAll("\\.", ""));
                revenueDeclarationInfo.setCladire15Suprafata(revenueDeclarationInfo.getCladire15Suprafata().replaceAll(",", "."));

                declaratieAvereBunImobilInfo.setSuprafata(new BigDecimal(revenueDeclarationInfo.getCladire15Suprafata()));
            } catch (NumberFormatException nfe) {
                throw new ValidationException("getCladire15Suprafata format invalid " + revenueDeclarationInfo.getCladire15Suprafata());
            }

            declaratieAvereBunImobilInfo.setModDobandire(revenueDeclarationInfo.getCladire15ModDobandire());
            declaratieAvereBunImobilInfo.setTitular(revenueDeclarationInfo.getCladire15Titular());
            declaratieAvereBunImobilInfo.setCotaParte(revenueDeclarationInfo.getCladire15CotaParte());
            declaratieAvereBunImobilInfo.setUnitateMasura(revenueDeclarationInfo.getCladire15UnitateDeMasura());

            declaratieAvereBunImobilInfoList.add(declaratieAvereBunImobilInfo);
        }

        return declaratieAvereBunImobilInfoList;
    }


    private List<DeclaratieAvereAlteActiveInfo> getDeclaratieAvereAlteActiveInfoList(RevenueDeclarationInfo revenueDeclarationInfo) {
        List<DeclaratieAvereAlteActiveInfo> declaratieAvereAlteActiveInfoList = new ArrayList<>();

        if (!revenueDeclarationInfo.getAlteActiveFreeText().equals("")) {
            DeclaratieAvereAlteActiveInfo declaratieAvereAlteActiveInfo = new DeclaratieAvereAlteActiveInfo();
            declaratieAvereAlteActiveInfo.setDescriere(revenueDeclarationInfo.getAlteActiveFreeText());

            declaratieAvereAlteActiveInfoList.add(declaratieAvereAlteActiveInfo);
        }

        return declaratieAvereAlteActiveInfoList;
    }

    private void populateDemnitarInfo(DemnitarInfo demnitarInfo, RevenueDeclarationInfo revenueDeclarationInfo) {
        demnitarInfo.setNume(revenueDeclarationInfo.getLastName());
        demnitarInfo.setPrenume(revenueDeclarationInfo.getFirstName());
    }

    private static Map<String, String> getColumnMapping() {
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("pnDem", "firstName");
        columnMapping.put("nDem", "lastName");
        columnMapping.put("datSem", "dataDeclaratiei");
        columnMapping.put("funct", "functie");
        columnMapping.put("inst", "institutie");
        columnMapping.put("l_Fis", "linkDeclaratie");

        // teren 1
        columnMapping.put("dimobt", "existaTerenuri1");
        columnMapping.put("imobt1_adr", "teren1Adresa");
        columnMapping.put("imobt1_cat", "teren1Categoria");
        columnMapping.put("imobt1_an", "teren1AnDobindire");
        columnMapping.put("imobt1_sup", "teren1Suprafata");
        columnMapping.put("imobt1_sup_e","teren1ExplicatieSuprafata");
        columnMapping.put("imobt1_unit", "teren1UnitateDeMasura");
        columnMapping.put("imobt1_cot", "teren1CotaParte");
        columnMapping.put("imobt1_dob", "teren1ModDobandire");
        columnMapping.put("imobt1_tit", "teren1Titular");

        // teren 2
        columnMapping.put("dimobt1", "existaTerenuri2");
        columnMapping.put("imobt2_adr", "teren2Adresa");
        columnMapping.put("imobt2_cat", "teren2Categoria");
        columnMapping.put("imobt2_an", "teren2AnDobindire");
        columnMapping.put("imobt2_sup", "teren2Suprafata");
        columnMapping.put("imobt2_sup_e","teren2ExplicatieSuprafata");
        columnMapping.put("imobt2_unit", "teren2UnitateDeMasura");
        columnMapping.put("imobt2_cot", "teren2CotaParte");
        columnMapping.put("imobt2_dob", "teren2ModDobandire");
        columnMapping.put("imobt2_tit", "teren2Titular");

        // teren 3
        columnMapping.put("dimobt2", "existaTerenuri3");
        columnMapping.put("imobt3_adr", "teren3Adresa");
        columnMapping.put("imobt3_cat", "teren3Categoria");
        columnMapping.put("imobt3_an", "teren3AnDobindire");
        columnMapping.put("imobt3_sup", "teren3Suprafata");
        columnMapping.put("imobt3_sup_e","teren3ExplicatieSuprafata");
        columnMapping.put("imobt3_unit", "teren3UnitateDeMasura");
        columnMapping.put("imobt3_cot", "teren3CotaParte");
        columnMapping.put("imobt3_dob", "teren3ModDobandire");
        columnMapping.put("imobt3_tit", "teren3Titular");

        // teren 4
        columnMapping.put("dimobt3", "existaTerenuri4");
        columnMapping.put("imobt4_adr", "teren4Adresa");
        columnMapping.put("imobt4_cat", "teren4Categoria");
        columnMapping.put("imobt4_an", "teren4AnDobindire");
        columnMapping.put("imobt4_sup", "teren4Suprafata");
        columnMapping.put("imobt4_sup_e","teren4ExplicatieSuprafata");
        columnMapping.put("imobt4_unit", "teren4UnitateDeMasura");
        columnMapping.put("imobt4_cot", "teren4CotaParte");
        columnMapping.put("imobt4_dob", "teren4ModDobandire");
        columnMapping.put("imobt4_tit", "teren4Titular");

        // teren 5
        columnMapping.put("dimobt4", "existaTerenuri5");
        columnMapping.put("imobt5_adr", "teren5Adresa");
        columnMapping.put("imobt5_cat", "teren5Categoria");
        columnMapping.put("imobt5_an", "teren5AnDobindire");
        columnMapping.put("imobt5_sup", "teren5Suprafata");
        columnMapping.put("imobt5_sup_e","teren5ExplicatieSuprafata");
        columnMapping.put("imobt5_unit", "teren5UnitateDeMasura");
        columnMapping.put("imobt5_cot", "teren5CotaParte");
        columnMapping.put("imobt5_dob", "teren5ModDobandire");
        columnMapping.put("imobt5_tit", "teren5Titular");

        // teren 6
        columnMapping.put("dimobt5", "existaTerenuri6");
        columnMapping.put("imobt6_adr", "teren6Adresa");
        columnMapping.put("imobt6_cat", "teren6Categoria");
        columnMapping.put("imobt6_an", "teren6AnDobindire");
        columnMapping.put("imobt6_sup", "teren6Suprafata");
        columnMapping.put("imobt6_sup_e","teren6ExplicatieSuprafata");
        columnMapping.put("imobt6_unit", "teren6UnitateDeMasura");
        columnMapping.put("imobt6_cot", "teren6CotaParte");
        columnMapping.put("imobt6_dob", "teren6ModDobandire");
        columnMapping.put("imobt6_tit", "teren6Titular");

        // teren 7
        columnMapping.put("dimobt6", "existaTerenuri7");
        columnMapping.put("imobt7_adr", "teren7Adresa");
        columnMapping.put("imobt7_cat", "teren7Categoria");
        columnMapping.put("imobt7_an", "teren7AnDobindire");
        columnMapping.put("imobt7_sup", "teren7Suprafata");
        columnMapping.put("imobt7_sup_e","teren7ExplicatieSuprafata");
        columnMapping.put("imobt7_unit", "teren7UnitateDeMasura");
        columnMapping.put("imobt7_cot", "teren7CotaParte");
        columnMapping.put("imobt7_dob", "teren7ModDobandire");
        columnMapping.put("imobt7_tit", "teren7Titular");

        // teren 8
        columnMapping.put("dimobt7", "existaTerenuri8");
        columnMapping.put("imobt8_adr", "teren8Adresa");
        columnMapping.put("imobt8_cat", "teren8Categoria");
        columnMapping.put("imobt8_an", "teren8AnDobindire");
        columnMapping.put("imobt8_sup", "teren8Suprafata");
        columnMapping.put("imobt8_sup_e","teren8ExplicatieSuprafata");
        columnMapping.put("imobt8_unit", "teren8UnitateDeMasura");
        columnMapping.put("imobt8_cot", "teren8CotaParte");
        columnMapping.put("imobt8_dob", "teren8ModDobandire");
        columnMapping.put("imobt8_tit", "teren8Titular");

        // teren 9
        columnMapping.put("dimobt8", "existaTerenuri9");
        columnMapping.put("imobt9_adr", "teren9Adresa");
        columnMapping.put("imobt9_cat", "teren9Categoria");
        columnMapping.put("imobt9_an", "teren9AnDobindire");
        columnMapping.put("imobt9_sup", "teren9Suprafata");
        columnMapping.put("imobt9_sup_e","teren9ExplicatieSuprafata");
        columnMapping.put("imobt9_unit", "teren9UnitateDeMasura");
        columnMapping.put("imobt9_cot", "teren9CotaParte");
        columnMapping.put("imobt9_dob", "teren9ModDobandire");
        columnMapping.put("imobt9_tit", "teren9Titular");

        // teren 10
        columnMapping.put("dimobt9", "existaTerenuri10");
        columnMapping.put("imobt10_adr", "teren10Adresa");
        columnMapping.put("imobt10_cat", "teren10Categoria");
        columnMapping.put("imobt10_an", "teren10AnDobindire");
        columnMapping.put("imobt10_sup", "teren10Suprafata");
        columnMapping.put("imobt10_sup_e","teren10ExplicatieSuprafata");
        columnMapping.put("imobt10_unit", "teren10UnitateDeMasura");
        columnMapping.put("imobt10_cot", "teren10CotaParte");
        columnMapping.put("imobt10_dob", "teren10ModDobandire");
        columnMapping.put("imobt10_tit", "teren10Titular");

        // teren 11
        columnMapping.put("dimobt10", "existaTerenuri11");
        columnMapping.put("imobt11_adr", "teren11Adresa");
        columnMapping.put("imobt11_cat", "teren11Categoria");
        columnMapping.put("imobt11_an", "teren11AnDobindire");
        columnMapping.put("imobt11_sup", "teren11Suprafata");
        columnMapping.put("imobt11_sup_e","teren11ExplicatieSuprafata");
        columnMapping.put("imobt11_unit", "teren11UnitateDeMasura");
        columnMapping.put("imobt11_cot", "teren11CotaParte");
        columnMapping.put("imobt11_dob", "teren11ModDobandire");
        columnMapping.put("imobt11_tit", "teren11Titular");

        // teren 12
        columnMapping.put("dimobt11", "existaTerenuri12");
        columnMapping.put("imobt12_adr", "teren12Adresa");
        columnMapping.put("imobt12_cat", "teren12Categoria");
        columnMapping.put("imobt12_an", "teren12AnDobindire");
        columnMapping.put("imobt12_sup", "teren12Suprafata");
        columnMapping.put("imobt12_sup_e","teren12ExplicatieSuprafata");
        columnMapping.put("imobt12_unit", "teren12UnitateDeMasura");
        columnMapping.put("imobt12_cot", "teren12CotaParte");
        columnMapping.put("imobt12_dob", "teren12ModDobandire");
        columnMapping.put("imobt12_tit", "teren12Titular");

        // teren 13
        columnMapping.put("dimobt12", "existaTerenuri13");
        columnMapping.put("imobt13_adr", "teren13Adresa");
        columnMapping.put("imobt13_cat", "teren13Categoria");
        columnMapping.put("imobt13_an", "teren13AnDobindire");
        columnMapping.put("imobt13_sup", "teren13Suprafata");
        columnMapping.put("imobt13_sup_e","teren13ExplicatieSuprafata");
        columnMapping.put("imobt13_unit", "teren13UnitateDeMasura");
        columnMapping.put("imobt13_cot", "teren13CotaParte");
        columnMapping.put("imobt13_dob", "teren13ModDobandire");
        columnMapping.put("imobt13_tit", "teren13Titular");

        // teren 14
        columnMapping.put("dimobt13", "existaTerenuri14");
        columnMapping.put("imobt14_adr", "teren14Adresa");
        columnMapping.put("imobt14_cat", "teren14Categoria");
        columnMapping.put("imobt14_an", "teren14AnDobindire");
        columnMapping.put("imobt14_sup", "teren14Suprafata");
        columnMapping.put("imobt14_sup_e","teren14ExplicatieSuprafata");
        columnMapping.put("imobt14_unit", "teren14UnitateDeMasura");
        columnMapping.put("imobt14_cot", "teren14CotaParte");
        columnMapping.put("imobt14_dob", "teren14ModDobandire");
        columnMapping.put("imobt14_tit", "teren14Titular");

        // teren 15
        columnMapping.put("dimobt14", "existaTerenuri15");
        columnMapping.put("imobt15_adr", "teren15Adresa");
        columnMapping.put("imobt15_cat", "teren15Categoria");
        columnMapping.put("imobt15_an", "teren15AnDobindire");
        columnMapping.put("imobt15_sup", "teren15Suprafata");
        columnMapping.put("imobt15_sup_e","teren15ExplicatieSuprafata");
        columnMapping.put("imobt15_unit", "teren15UnitateDeMasura");
        columnMapping.put("imobt15_cot", "teren15CotaParte");
        columnMapping.put("imobt15_dob", "teren15ModDobandire");
        columnMapping.put("imobt15_tit", "teren15Titular");

        // alter terenuri free text needs parsing
        columnMapping.put("manimobt", "alteTerenuriFreeText");

        // cladire 1
        columnMapping.put("dimobc", "existaCladiri1");
        columnMapping.put("imobc1_adr", "cladire1Adresa");
        columnMapping.put("imobc1_cat", "cladire1Categoria");
        columnMapping.put("imobc1_an", "cladire1AnDobindire");
        columnMapping.put("imobc1_sup", "cladire1Suprafata");
        columnMapping.put("imobc1_sup_e", "cladire1ExplicatieSuprafata");
        columnMapping.put("imobc1_unit", "cladire1UnitateDeMasura");
        columnMapping.put("imobc1_cot", "cladire1CotaParte");
        columnMapping.put("imobc1_dob", "cladire1ModDobandire");
        columnMapping.put("imobc1_tit", "cladire1Titular");

        // cladire 2
        columnMapping.put("dimobc1", "existaCladiri2");
        columnMapping.put("imobc2_adr", "cladire2Adresa");
        columnMapping.put("imobc2_cat", "cladire2Categoria");
        columnMapping.put("imobc2_an", "cladire2AnDobindire");
        columnMapping.put("imobc2_sup", "cladire2ExplicatieSuprafata");
        columnMapping.put("imobc2_sup_e", "explicatieImobc2Suprafata");
        columnMapping.put("imobc2_unit", "cladire2UnitateDeMasura");
        columnMapping.put("imobc2_cot", "cladire2CotaParte");
        columnMapping.put("imobc2_dob", "cladire2ModDobandire");
        columnMapping.put("imobc2_tit", "cladire2Titular");

        // cladire 3
        columnMapping.put("dimobc2", "existaCladiri3");
        columnMapping.put("imobc3_adr", "cladire3Adresa");
        columnMapping.put("imobc3_cat", "cladire3Categoria");
        columnMapping.put("imobc3_an", "cladire3AnDobindire");
        columnMapping.put("imobc3_sup", "cladire3Suprafata");
        columnMapping.put("imobc3_sup_e", "cladire3ExplicatieSuprafata");
        columnMapping.put("imobc3_unit", "cladire3UnitateDeMasura");
        columnMapping.put("imobc3_cot", "cladire3CotaParte");
        columnMapping.put("imobc3_dob", "cladire3ModDobandire");
        columnMapping.put("imobc3_tit", "cladire3Titular");

        // cladire 4
        columnMapping.put("dimobc3", "existaCladiri4");
        columnMapping.put("imobc4_adr", "cladire4Adresa");
        columnMapping.put("imobc4_cat", "cladire4Categoria");
        columnMapping.put("imobc4_an", "cladire4AnDobindire");
        columnMapping.put("imobc4_sup", "cladire4Suprafata");
        columnMapping.put("imobc4_sup_e", "cladire4ExplicatieSuprafata");
        columnMapping.put("imobc4_unit", "cladire4UnitateDeMasura");
        columnMapping.put("imobc4_cot", "cladire4CotaParte");
        columnMapping.put("imobc4_dob", "cladire4ModDobandire");
        columnMapping.put("imobc4_tit", "cladire4Titular");

        // cladire 5
        columnMapping.put("dimobc4", "existaCladiri5");
        columnMapping.put("imobc5_adr", "cladire5Adresa");
        columnMapping.put("imobc5_cat", "cladire5Categoria");
        columnMapping.put("imobc5_an", "cladire5AnDobindire");
        columnMapping.put("imobc5_sup", "cladire5Suprafata");
        columnMapping.put("imobc5_sup_e", "cladire5ExplicatieSuprafata");
        columnMapping.put("imobc5_unit", "cladire5UnitateDeMasura");
        columnMapping.put("imobc5_cot", "cladire5CotaParte");
        columnMapping.put("imobc5_dob", "cladire5ModDobandire");
        columnMapping.put("imobc5_tit", "cladire5Titular");

        // cladire 6
        columnMapping.put("dimobc5", "existaCladiri6");
        columnMapping.put("imobc6_adr", "cladire6Adresa");
        columnMapping.put("imobc6_cat", "cladire6Categoria");
        columnMapping.put("imobc6_an", "cladire6AnDobindire");
        columnMapping.put("imobc6_sup", "cladire6Suprafata");
        columnMapping.put("imobc6_sup_e", "cladire6ExplicatieSuprafata");
        columnMapping.put("imobc6_unit", "cladire6UnitateDeMasura");
        columnMapping.put("imobc6_cot", "cladire6CotaParte");
        columnMapping.put("imobc6_dob", "cladire6ModDobandire");
        columnMapping.put("imobc6_tit", "cladire6Titular");

        // cladire 7
        columnMapping.put("dimobc6", "existaCladiri7");
        columnMapping.put("imobc7_adr", "cladire7Adresa");
        columnMapping.put("imobc7_cat", "cladire7Categoria");
        columnMapping.put("imobc7_an", "cladire7AnDobindire");
        columnMapping.put("imobc7_sup", "cladire7Suprafata");
        columnMapping.put("imobc7_sup_e", "cladire7ExplicatieSuprafata");
        columnMapping.put("imobc7_unit", "cladire7UnitateDeMasura");
        columnMapping.put("imobc7_cot", "cladire7CotaParte");
        columnMapping.put("imobc7_dob", "cladire7ModDobandire");
        columnMapping.put("imobc7_tit", "cladire7Titular");

        // cladire 8
        columnMapping.put("dimobc7", "existaCladiri8");
        columnMapping.put("imobc8_adr", "cladire8Adresa");
        columnMapping.put("imobc8_cat", "cladire8Categoria");
        columnMapping.put("imobc8_an", "cladire8AnDobindire");
        columnMapping.put("imobc8_sup", "cladire8Suprafata");
        columnMapping.put("imobc8_sup_e", "cladire8ExplicatieSuprafata");
        columnMapping.put("imobc8_unit", "cladire8UnitateDeMasura");
        columnMapping.put("imobc8_cot", "cladire8CotaParte");
        columnMapping.put("imobc8_dob", "cladire8ModDobandire");
        columnMapping.put("imobc8_tit", "cladire8Titular");

        // cladire 9
        columnMapping.put("dimobc8", "existaCladiri9");
        columnMapping.put("imobc9_adr", "cladire9Adresa");
        columnMapping.put("imobc9_cat", "cladire9Categoria");
        columnMapping.put("imobc9_an", "cladire9AnDobindire");
        columnMapping.put("imobc9_sup", "cladire9Suprafata");
        columnMapping.put("imobc9_sup_e", "cladire9ExplicatieSuprafata");
        columnMapping.put("imobc9_unit", "cladire9UnitateDeMasura");
        columnMapping.put("imobc9_cot", "cladire9CotaParte");
        columnMapping.put("imobc9_dob", "cladire9ModDobandire");
        columnMapping.put("imobc9_tit", "cladire9Titular");

        // cladire 10
        columnMapping.put("dimobc9", "existaCladiri10");
        columnMapping.put("imobc10_adr", "cladire10Adresa");
        columnMapping.put("imobc10_cat", "cladire10Categoria");
        columnMapping.put("imobc10_an", "cladire10AnDobindire");
        columnMapping.put("imobc10_sup", "cladire10Suprafata");
        columnMapping.put("imobc10_sup_e", "cladire10ExplicatieSuprafata");
        columnMapping.put("imobc10_unit", "cladire10UnitateDeMasura");
        columnMapping.put("imobc10_cot", "cladire10CotaParte");
        columnMapping.put("imobc10_dob", "cladire10ModDobandire");
        columnMapping.put("imobc10_tit", "cladire10Titular");

        // cladire 11
        columnMapping.put("dimobc10", "existaCladiri11");
        columnMapping.put("imobc11_adr", "cladire11Adresa");
        columnMapping.put("imobc11_cat", "cladire11Categoria");
        columnMapping.put("imobc11_an", "cladire11AnDobindire");
        columnMapping.put("imobc11_sup", "cladire11Suprafata");
        columnMapping.put("imobc11_sup_e", "cladire11ExplicatieSuprafata");
        columnMapping.put("imobc11_unit", "cladire11UnitateDeMasura");
        columnMapping.put("imobc11_cot", "cladire11CotaParte");
        columnMapping.put("imobc11_dob", "cladire11ModDobandire");
        columnMapping.put("imobc11_tit", "cladire11Titular");

        // cladire 12
        columnMapping.put("dimobc11", "existaCladiri12");
        columnMapping.put("imobc12_adr", "cladire12Adresa");
        columnMapping.put("imobc12_cat", "cladire12Categoria");
        columnMapping.put("imobc12_an", "cladire12AnDobindire");
        columnMapping.put("imobc12_sup", "cladire12Suprafata");
        columnMapping.put("imobc12_sup_e", "cladire12ExplicatieSuprafata");
        columnMapping.put("imobc12_unit", "cladire12UnitateDeMasura");
        columnMapping.put("imobc12_cot", "cladire12CotaParte");
        columnMapping.put("imobc12_dob", "cladire12ModDobandire");
        columnMapping.put("imobc12_tit", "cladire12Titular");

        // cladire 13
        columnMapping.put("dimobc12", "existaCladiri13");
        columnMapping.put("imobc13_adr", "cladire13Adresa");
        columnMapping.put("imobc13_cat", "cladire13Categoria");
        columnMapping.put("imobc13_an", "cladire13AnDobindire");
        columnMapping.put("imobc13_sup", "cladire13Suprafata");
        columnMapping.put("imobc13_sup_e", "cladire13ExplicatieSuprafata");
        columnMapping.put("imobc13_unit", "cladire13UnitateDeMasura");
        columnMapping.put("imobc13_cot", "cladire13CotaParte");
        columnMapping.put("imobc13_dob", "cladire13ModDobandire");
        columnMapping.put("imobc13_tit", "cladire13Titular");

        // cladire 14
        columnMapping.put("dimobc13", "existaCladiri14");
        columnMapping.put("imobc14_adr", "cladire14Adresa");
        columnMapping.put("imobc14_cat", "cladire14Categoria");
        columnMapping.put("imobc14_an", "cladire14AnDobindire");
        columnMapping.put("imobc14_sup", "cladire14Suprafata");
        columnMapping.put("imobc14_sup_e", "cladire14ExplicatieSuprafata");
        columnMapping.put("imobc14_unit", "cladire14UnitateDeMasura");
        columnMapping.put("imobc14_cot", "cladire14CotaParte");
        columnMapping.put("imobc14_dob", "cladire14ModDobandire");
        columnMapping.put("imobc14_tit", "cladire14Titular");

        // cladire 15
        columnMapping.put("dimobc14", "existaCladiri15");
        columnMapping.put("imobc15_adr", "cladire15Adresa");
        columnMapping.put("imobc15_cat", "cladire15Categoria");
        columnMapping.put("imobc15_an", "cladire15AnDobindire");
        columnMapping.put("imobc15_sup", "cladire15Suprafata");
        columnMapping.put("imobc15_sup_e", "cladire15ExplicatieSuprafata");
        columnMapping.put("imobc15_unit", "cladire15UnitateDeMasura");
        columnMapping.put("imobc15_cot", "cladire15CotaParte");
        columnMapping.put("imobc15_dob", "cladire15ModDobandire");
        columnMapping.put("imobc15_tit", "cladire15Titular");

        // alter cladiri free text needs parsing
        columnMapping.put("manimobc", "alteCladiriFreeText");

        // automobil 1
        columnMapping.put("dmoba", "existaAutomobile1");
        columnMapping.put("moba1_nat", "automobil1Tip");
        columnMapping.put("moba1_marc", "automobil1Marca");
        columnMapping.put("moba1_nr", "automobil1Cantitate");
        columnMapping.put("moba1_an", "automobil1AnFabricatie");
        columnMapping.put("moba1_dob", "automobil1ModDobandire");

        // automobil 2
        columnMapping.put("dmoba1", "existaAutomobile2");
        columnMapping.put("moba2_nat", "automobil2Tip");
        columnMapping.put("moba2_marc", "automobil2Marca");
        columnMapping.put("moba2_nr", "automobil2Cantitate");
        columnMapping.put("moba2_an", "automobil2AnFabricatie");
        columnMapping.put("moba2_dob", "automobil2ModDobandire");

        // automobil 3
        columnMapping.put("dmoba2", "existaAutomobile3");
        columnMapping.put("moba3_nat", "automobil3Tip");
        columnMapping.put("moba3_marc", "automobil3Marca");
        columnMapping.put("moba3_nr", "automobil3Cantitate");
        columnMapping.put("moba3_an", "automobil3AnFabricatie");
        columnMapping.put("moba3_dob", "automobil3ModDobandire");

        // automobil 4
        columnMapping.put("dmoba3", "existaAutomobile4");
        columnMapping.put("moba4_nat", "automobil4Tip");
        columnMapping.put("moba4_marc", "automobil4Marca");
        columnMapping.put("moba4_nr", "automobil4Cantitate");
        columnMapping.put("moba4_an", "automobil4AnFabricatie");
        columnMapping.put("moba4_dob", "automobil4ModDobandire");

        // automobil 5
        columnMapping.put("dmoba4", "existaAutomobile5");
        columnMapping.put("moba5_nat", "automobil5Tip");
        columnMapping.put("moba5_marc", "automobil5Marca");
        columnMapping.put("moba5_nr", "automobil5Cantitate");
        columnMapping.put("moba5_an", "automobil5AnFabricatie");
        columnMapping.put("moba5_dob", "automobil5ModDobandire");

        // alte automobile
        columnMapping.put("manmoba", "alteAutomobileFreeText");

        // bijuterie 1
        columnMapping.put("dmobb", "existaBijuterii1");
        columnMapping.put("mobb1_desc", "bijuterie1Descriere");
        columnMapping.put("mobb1_an", "bijuterie1AnDobandire");
        columnMapping.put("mobb1_val", "bijuterie1ValoareEstimata");
        columnMapping.put("mobb1_mon", "bijuterie1Moneda");

        // bijuterie 2
        columnMapping.put("dmobb1", "existaBijuterii2");
        columnMapping.put("mobb2_desc", "bijuterie2Descriere");
        columnMapping.put("mobb2_an", "bijuterie2AnDobandire");
        columnMapping.put("mobb2_val", "bijuterie2ValoareEstimata");
        columnMapping.put("mobb2_mon", "bijuterie2Moneda");

        // bijuterie 3
        columnMapping.put("dmobb2", "existaBijuterii3");
        columnMapping.put("mobb3_desc", "bijuterie3Descriere");
        columnMapping.put("mobb3_an", "bijuterie3AnDobandire");
        columnMapping.put("mobb3_val", "bijuterie3ValoareEstimata");
        columnMapping.put("mobb3_mon", "bijuterie3Moneda");

        // bijuterie 4
        columnMapping.put("dmobb3", "existaBijuterii4");
        columnMapping.put("mobb4_desc", "bijuterie4Descriere");
        columnMapping.put("mobb4_an", "bijuterie4AnDobandire");
        columnMapping.put("mobb4_val", "bijuterie4ValoareEstimata");
        columnMapping.put("mobb4_mon", "bijuterie4Moneda");

        // bijuterie 5
        columnMapping.put("dmobb4", "existaBijuterii5");
        columnMapping.put("mobb5_desc", "bijuterie5Descriere");
        columnMapping.put("mobb5_an", "bijuterie5AnDobandire");
        columnMapping.put("mobb5_val", "bijuterie5ValoareEstimata");
        columnMapping.put("mobb5_mon", "bijuterie5Moneda");

        // alte bijuterii
        columnMapping.put("manmobb", "alteBijuteriiFreeText");

        // bunuri instrainate 1
        columnMapping.put("dinstr", "existaBunuriInstrainate1");
        columnMapping.put("instr1_nat", "bunInstrainat1Natura");
        columnMapping.put("instr1_dat", "bunInstrainat1Data");
        columnMapping.put("instr1_pers", "bunInstrainat1PersoanaCatreCare");
        columnMapping.put("instr1_form", "bunInstrainat1FormaInstrainarii");
        columnMapping.put("instr1_val", "bunInstrainat1Valoarea");
        columnMapping.put("instr1_mon", "bunInstrainat1Moneda");

        // bunuri instrainate 2
        columnMapping.put("dinstr1", "existaBunuriInstrainate2");
        columnMapping.put("instr2_nat", "bunInstrainat2Natura");
        columnMapping.put("instr2_dat", "bunInstrainat2Data");
        columnMapping.put("instr2_pers", "bunInstrainat2PersoanaCatreCare");
        columnMapping.put("instr2_form", "bunInstrainat2FormaInstrainarii");
        columnMapping.put("instr2_val", "bunInstrainat2Valoarea");
        columnMapping.put("instr2_mon", "bunInstrainat2Moneda");

        // bunuri instrainate 3
        columnMapping.put("dinstr2", "existaBunuriInstrainate3");
        columnMapping.put("instr3_nat", "bunInstrainat3Natura");
        columnMapping.put("instr3_dat", "bunInstrainat3Data");
        columnMapping.put("instr3_pers", "bunInstrainat3PersoanaCatreCare");
        columnMapping.put("instr3_form", "bunInstrainat3FormaInstrainarii");
        columnMapping.put("instr3_val", "bunInstrainat3Valoarea");
        columnMapping.put("instr3_mon", "bunInstrainat3Moneda");

        // bunuri instrainate 4
        columnMapping.put("dinstr3", "existaBunuriInstrainate4");
        columnMapping.put("instr4_nat", "bunInstrainat4Natura");
        columnMapping.put("instr4_dat", "bunInstrainat4Data");
        columnMapping.put("instr4_pers", "bunInstrainat4PersoanaCatreCare");
        columnMapping.put("instr4_form", "bunInstrainat4FormaInstrainarii");
        columnMapping.put("instr4_val", "bunInstrainat4Valoarea");
        columnMapping.put("instr4_mon", "bunInstrainat4Moneda");

        // bunuri instrainate 5
        columnMapping.put("dinstr4", "existaBunuriInstrainate5");
        columnMapping.put("instr5_nat", "bunInstrainat5Natura");
        columnMapping.put("instr5_dat", "bunInstrainat5Data");
        columnMapping.put("instr5_pers", "bunInstrainat5PersoanaCatreCare");
        columnMapping.put("instr5_form", "bunInstrainat5FormaInstrainarii");
        columnMapping.put("instr5_val", "bunInstrainat5Valoarea");
        columnMapping.put("instr5_mon", "bunInstrainat5Moneda");

        // alte bunuri instrainate
        columnMapping.put("maninstr", "alteBunuriInstrainateFreeText");

        // cont 1
        columnMapping.put("dAFcd", "existaConturi1");
        columnMapping.put("AFcd1_cui", "cont1Titular");
        columnMapping.put("AFcd1_inst", "cont1Institutia");
        columnMapping.put("AFcd1_tip", "cont1Tip");
        columnMapping.put("AFcd1_mon", "cont1Moneda");
        columnMapping.put("AFcd1_an", "cont1AnDeschidere");
        columnMapping.put("AFcd1_sold", "cont1Sold");

        // cont 2
        columnMapping.put("dAFcd1", "existaConturi2");
        columnMapping.put("AFcd2_cui", "cont2Titular");
        columnMapping.put("AFcd2_inst", "cont2Institutia");
        columnMapping.put("AFcd2_tip", "cont2Tip");
        columnMapping.put("AFcd2_mon", "cont2Moneda");
        columnMapping.put("AFcd2_an", "cont2AnDeschidere");
        columnMapping.put("AFcd2_sold", "cont2Sold");

        // cont 3
        columnMapping.put("dAFcd2", "existaConturi3");
        columnMapping.put("AFcd3_cui", "cont3Titular");
        columnMapping.put("AFcd3_inst", "cont3Institutia");
        columnMapping.put("AFcd3_tip", "cont3Tip");
        columnMapping.put("AFcd3_mon", "cont3Moneda");
        columnMapping.put("AFcd3_an", "cont3AnDeschidere");
        columnMapping.put("AFcd3_sold", "cont3Sold");

        // cont 4
        columnMapping.put("dAFcd3", "existaConturi4");
        columnMapping.put("AFcd4_cui", "cont4Titular");
        columnMapping.put("AFcd4_inst", "cont4Institutia");
        columnMapping.put("AFcd4_tip", "cont4Tip");
        columnMapping.put("AFcd4_mon", "cont4Moneda");
        columnMapping.put("AFcd4_an", "cont4AnDeschidere");
        columnMapping.put("AFcd4_sold", "cont4Sold");

        // cont 5
        columnMapping.put("dAFcd4", "existaConturi5");
        columnMapping.put("AFcd5_cui", "cont5Titular");
        columnMapping.put("AFcd5_inst", "cont5Institutia");
        columnMapping.put("AFcd5_tip", "cont5Tip");
        columnMapping.put("AFcd5_mon", "cont5Moneda");
        columnMapping.put("AFcd5_an", "cont5AnDeschidere");
        columnMapping.put("AFcd5_sold", "cont5Sold");

        // cont 6
        columnMapping.put("dAFcd5", "existaConturi6");
        columnMapping.put("AFcd6_cui", "cont6Titular");
        columnMapping.put("AFcd6_inst", "cont6Institutia");
        columnMapping.put("AFcd6_tip", "cont6Tip");
        columnMapping.put("AFcd6_mon", "cont6Moneda");
        columnMapping.put("AFcd6_an", "cont6AnDeschidere");
        columnMapping.put("AFcd6_sold", "cont6Sold");

        // cont 7
        columnMapping.put("dAFcd6", "existaConturi7");
        columnMapping.put("AFcd7_cui", "cont7Titular");
        columnMapping.put("AFcd7_inst", "cont7Institutia");
        columnMapping.put("AFcd7_tip", "cont7Tip");
        columnMapping.put("AFcd7_mon", "cont7Moneda");
        columnMapping.put("AFcd7_an", "cont7AnDeschidere");
        columnMapping.put("AFcd7_sold", "cont7Sold");

        // cont 8
        columnMapping.put("dAFcd7", "existaConturi8");
        columnMapping.put("AFcd8_cui", "cont8Titular");
        columnMapping.put("AFcd8_inst", "cont8Institutia");
        columnMapping.put("AFcd8_tip", "cont8Tip");
        columnMapping.put("AFcd8_mon", "cont8Moneda");
        columnMapping.put("AFcd8_an", "cont8AnDeschidere");
        columnMapping.put("AFcd8_sold", "cont8Sold");

        // cont 9
        columnMapping.put("dAFcd8", "existaConturi9");
        columnMapping.put("AFcd9_cui", "cont9Titular");
        columnMapping.put("AFcd9_inst", "cont9Institutia");
        columnMapping.put("AFcd9_tip", "cont9Tip");
        columnMapping.put("AFcd9_mon", "cont9Moneda");
        columnMapping.put("AFcd9_an", "cont9AnDeschidere");
        columnMapping.put("AFcd9_sold", "cont9Sold");

        // cont 10
        columnMapping.put("dAFcd9", "existaConturi10");
        columnMapping.put("AFcd10_cui", "cont10Titular");
        columnMapping.put("AFcd10_inst", "cont10Institutia");
        columnMapping.put("AFcd10_tip", "cont10Tip");
        columnMapping.put("AFcd10_mon", "cont10Moneda");
        columnMapping.put("AFcd10_an", "cont10AnDeschidere");
        columnMapping.put("AFcd10_sold", "cont10Sold");

        // alte conturi mai futu-ti dumnezeii matii baescu si iliescu si dragnea su constantinescu???
        // alte conturi free text
        columnMapping.put("manAFcd", "alteConturiFreeText");

        // plasament 1
        columnMapping.put("dAFp", "existaPlasamente1");
        columnMapping.put("AFp1_cui", "plasament1Titular");
        columnMapping.put("AFp1_emit", "plasament1Emitent");
        columnMapping.put("AFp1_tip", "plasament1Tip");
        columnMapping.put("AFp1_cot", "plasament1NumarTitluri");
        columnMapping.put("AFp1_val", "plasament1Valoare");
        columnMapping.put("AFp1_mon", "plasament1Moneda");

        // plasament 2
        columnMapping.put("dAFp1", "existaPlasamente2");
        columnMapping.put("AFp2_cui", "plasament2Titular");
        columnMapping.put("AFp2_emit", "plasament2Emitent");
        columnMapping.put("AFp2_tip", "plasament2Tip");
        columnMapping.put("AFp2_cot", "plasament2NumarTitluri");
        columnMapping.put("AFp2_val", "plasament2Valoare");
        columnMapping.put("AFp2_mon", "plasament2Moneda");

        // plasament 3
        columnMapping.put("dAFp2", "existaPlasamente3");
        columnMapping.put("AFp3_cui", "plasament3Titular");
        columnMapping.put("AFp3_emit", "plasament3Emitent");
        columnMapping.put("AFp3_tip", "plasament3Tip");
        columnMapping.put("AFp3_cot", "plasament3NumarTitluri");
        columnMapping.put("AFp3_val", "plasament3Valoare");
        columnMapping.put("AFp3_mon", "plasament3Moneda");

        // plasament 4
        columnMapping.put("dAFp3", "existaPlasamente4");
        columnMapping.put("AFp4_cui", "plasament4Titular");
        columnMapping.put("AFp4_emit", "plasament4Emitent");
        columnMapping.put("AFp4_tip", "plasament4Tip");
        columnMapping.put("AFp4_cot", "plasament4NumarTitluri");
        columnMapping.put("AFp4_val", "plasament4Valoare");
        columnMapping.put("AFp4_mon", "plasament4Moneda");

        // plasament 5
        columnMapping.put("dAFp4", "existaPlasamente5");
        columnMapping.put("AFp5_cui", "plasament5Titular");
        columnMapping.put("AFp5_emit", "plasament5Emitent");
        columnMapping.put("AFp5_tip", "plasament5Tip");
        columnMapping.put("AFp5_cot", "plasament5NumarTitluri");
        columnMapping.put("AFp5_val", "plasament5Valoare");
        columnMapping.put("AFp5_mon", "plasament5Moneda");

        // alte plasamente
        columnMapping.put("manAFp", "altePlasamenteFreeText");

        // alte active
        columnMapping.put("AFaa", "alteActiveFreeText");

        // datorie 1
        columnMapping.put("dDat", "existaDatorii1");
        columnMapping.put("dat1_cred", "datorie1Creditor");
        columnMapping.put("dat1_an", "datorie1AnContractare");
        columnMapping.put("dat1_scad", "datorie1DataScadenta");
        columnMapping.put("dat1_val", "datorie1Valoare");
        columnMapping.put("dat1_mon", "datorie1Moneda");

        // datorie 2
        columnMapping.put("ddat1", "existaDatorii2");
        columnMapping.put("dat2_cred", "datorie2Creditor");
        columnMapping.put("dat2_an", "datorie2AnContractare");
        columnMapping.put("dat2_scad", "datorie2DataScadenta");
        columnMapping.put("dat2_val", "datorie2Valoare");
        columnMapping.put("dat2_mon", "datorie2Moneda");

        // datorie 3
        columnMapping.put("ddat2", "existaDatorii3");
        columnMapping.put("dat3_cred", "datorie3Creditor");
        columnMapping.put("dat3_an", "datorie3AnContractare");
        columnMapping.put("dat3_scad", "datorie3DataScadenta");
        columnMapping.put("dat3_val", "datorie3Valoare");
        columnMapping.put("dat3_mon", "datorie3Moneda");

        // datorie 4
        columnMapping.put("ddat3", "existaDatorii4");
        columnMapping.put("dat4_cred", "datorie4Creditor");
        columnMapping.put("dat4_an", "datorie4AnContractare");
        columnMapping.put("dat4_scad", "datorie4DataScadenta");
        columnMapping.put("dat4_val", "datorie4Valoare");
        columnMapping.put("dat4_mon", "datorie4Moneda");

        // datorie 5
        columnMapping.put("ddat4", "existaDatorii5");
        columnMapping.put("dat5_cred", "datorie5Creditor");
        columnMapping.put("dat5_an", "datorie5AnContractare");
        columnMapping.put("dat5_scad", "datorie5DataScadenta");
        columnMapping.put("dat5_val", "datorie5Valoare");
        columnMapping.put("dat5_mon", "datorie5Moneda");

        // alte datorii free text
        columnMapping.put("manDat", "alteDatoriiFreeText");

        // cadou 1
        columnMapping.put("dCad", "existaCadouri1");
        columnMapping.put("cad1_cine", "cadou1Titular");
        columnMapping.put("cad1_surs", "cadou1SursaVenit");
        columnMapping.put("cad1_serv", "cadou1ServiciuPrestat");
        columnMapping.put("cad1_ven", "cadou1VenitAnual");
        columnMapping.put("cad1_mon", "cadou1Monenda");

        // cadou 2
        columnMapping.put("dcad1", "existaCadouri2");
        columnMapping.put("cad2_cine", "cadou2Titular");
        columnMapping.put("cad2_surs", "cadou2SursaVenit");
        columnMapping.put("cad2_serv", "cadou2ServiciuPrestat");
        columnMapping.put("cad2_ven", "cadou2VenitAnual");
        columnMapping.put("cad2_mon", "cadou2Monenda");

        // alte cadouri free text
        columnMapping.put("mancad", "alteCadouriFreeText");

        // venit salar 1
        columnMapping.put("dVenSal", "existaVenituriSalarii1");
        columnMapping.put("VenSal1_cine", "venitSalariu1Titular");
        columnMapping.put("VenSal1_surs", "venitSalariu1Sursa");
        columnMapping.put("VenSal1_serv", "venit1SalariuServiciPrestat");
        columnMapping.put("VenSal1_ven", "venit1SalariuVenitAnual");
        columnMapping.put("VenSal1_mon", "venit1SalariuMonenda");

        // venit salar 2
        columnMapping.put("dVenSal1", "existaVenituriSalarii2");
        columnMapping.put("VenSal2_cine", "venitSalariu2Titular");
        columnMapping.put("VenSal2_surs", "venitSalariu2Sursa");
        columnMapping.put("VenSal2_serv", "venit2SalariuServiciPrestat");
        columnMapping.put("VenSal2_ven", "venit2SalariuVenitAnual");
        columnMapping.put("VenSal2_mon", "venit2SalariuMonenda");

        // venit salar 3
        columnMapping.put("dVenSal2", "existaVenituriSalarii3");
        columnMapping.put("VenSal3_cine", "venitSalariu3Titular");
        columnMapping.put("VenSal3_surs", "venitSalariu3Sursa");
        columnMapping.put("VenSal3_serv", "venit3SalariuServiciPrestat");
        columnMapping.put("VenSal3_ven", "venit3SalariuVenitAnual");
        columnMapping.put("VenSal3_mon", "venit3SalariuMonenda");

        // venit salar 4
        columnMapping.put("dVenSal3", "existaVenituriSalarii4");
        columnMapping.put("VenSal4_cine", "venitSalariu4Titular");
        columnMapping.put("VenSal4_surs", "venitSalariu4Sursa");
        columnMapping.put("VenSal4_serv", "venit4SalariuServiciPrestat");
        columnMapping.put("VenSal4_ven", "venit4SalariuVenitAnual");
        columnMapping.put("VenSal4_mon", "venit4SalariuMonenda");

        // venit salar 5
        columnMapping.put("dVenSal4", "existaVenituriSalarii5");
        columnMapping.put("VenSal5_cine", "venitSalariu5Titular");
        columnMapping.put("VenSal5_surs", "venitSalariu5Sursa");
        columnMapping.put("VenSal5_serv", "venit5SalariuServiciPrestat");
        columnMapping.put("VenSal5_ven", "venit5SalariuVenitAnual");
        columnMapping.put("VenSal5_mon", "venit5SalariuMonenda");

        // venit salar 6
        columnMapping.put("dVenSal5", "existaVenituriSalarii6");
        columnMapping.put("VenSal6_cine", "venitSalariu6Titular");
        columnMapping.put("VenSal6_surs", "venitSalariu6Sursa");
        columnMapping.put("VenSal6_serv", "venit6SalariuServiciPrestat");
        columnMapping.put("VenSal6_ven", "venit6SalariuVenitAnual");
        columnMapping.put("VenSal6_mon", "venit6SalariuMonenda");

        // alte venituri salar free text
        columnMapping.put("manVenSal", "alteVenituriSalarFreeText");

        // venit activitati independente 1
        columnMapping.put("dVenInd", "existaVenituriActivitatiIndependente1");
        columnMapping.put("venInd1_cine", "venitActivitatiIndependente1Titular");
        columnMapping.put("venInd1_sursa", "venitActivitatiIndependente1Sursa");
        columnMapping.put("venInd1_serv", "venitActivitatiIndependente1ServiciPrestat");
        columnMapping.put("venInd1_c", "venitActivitatiIndependente1VenitAnual");
        columnMapping.put("venInd1_mon", "venitActivitatiIndependente1Moneda");

        // venit activitati independente 2
        columnMapping.put("dVenInd1", "existaVenituriActivitatiIndependente2");
        columnMapping.put("venInd2_cine", "venitActivitatiIndependente2Titular");
        columnMapping.put("venInd2_sursa", "venitActivitatiIndependente2Sursa");
        columnMapping.put("venInd2_serv", "venitActivitatiIndependente2ServiciPrestat");
        columnMapping.put("venInd2_c", "venitActivitatiIndependente2VenitAnual");
        columnMapping.put("venInd2_mon", "venitActivitatiIndependente2Moneda");

        // venit activitati independente 3
        columnMapping.put("dVenInd2", "existaVenituriActivitatiIndependente3");
        columnMapping.put("venInd3_cine", "venitActivitatiIndependente3Titular");
        columnMapping.put("venInd3_sursa", "venitActivitatiIndependente3Sursa");
        columnMapping.put("venInd3_serv", "venitActivitatiIndependente3ServiciPrestat");
        columnMapping.put("venInd3_c", "venitActivitatiIndependente3VenitAnual");
        columnMapping.put("venInd3_mon", "venitActivitatiIndependente3Moneda");

        // venit activitati independente 4
        columnMapping.put("dVenInd3", "existaVenituriActivitatiIndependente4");
        columnMapping.put("venInd4_cine", "venitActivitatiIndependente4Titular");
        columnMapping.put("venInd4_sursa", "venitActivitatiIndependente4Sursa");
        columnMapping.put("venInd4_serv", "venitActivitatiIndependente4ServiciPrestat");
        columnMapping.put("venInd4_c", "venitActivitatiIndependente4VenitAnual");
        columnMapping.put("venInd4_mon", "venitActivitatiIndependente4Moneda");

        // alte venituri activitati independente
        columnMapping.put("manVenInd", "alteVenituriActivitatiIndependenteFreeText");

        // venit cedarea folosintei 1
        columnMapping.put("dVenCed", "existaVenituriCedareaFolosintei1");
        columnMapping.put("venCed1_cine", "venitCedareaFolosintei1Titular");
        columnMapping.put("venCed1_surs", "venitCedareaFolosintei1Sursa");
        columnMapping.put("venCed1_serv", "venitCedareaFolosintei1ServiciPrestat");
        columnMapping.put("venCed1_c", "venitCedareaFolosintei1VenitAnual");
        columnMapping.put("venCed1_mon", "venitCedareaFolosintei1Moneda");

        // venit cedarea folosintei 2
        columnMapping.put("dVenCed1", "existaVenituriCedareaFolosintei2");
        columnMapping.put("venCed2_cine", "venitCedareaFolosintei2Titular");
        columnMapping.put("venCed2_surs", "venitCedareaFolosintei2Sursa");
        columnMapping.put("venCed2_serv", "venitCedareaFolosintei2ServiciPrestat");
        columnMapping.put("venCed2_c", "venitCedareaFolosintei2VenitAnual");
        columnMapping.put("venCed2_mon", "venitCedareaFolosintei2Moneda");

        // venit cedarea folosintei 3
        columnMapping.put("dVenCed2", "existaVenituriCedareaFolosintei3");
        columnMapping.put("venCed3_cine", "venitCedareaFolosintei3Titular");
        columnMapping.put("venCed3_surs", "venitCedareaFolosintei3Sursa");
        columnMapping.put("venCed3_serv", "venitCedareaFolosintei3ServiciPrestat");
        columnMapping.put("venCed3_c", "venitCedareaFolosintei3VenitAnual");
        columnMapping.put("venCed3_mon", "venitCedareaFolosintei3Moneda");

        // venit cedarea folosintei 4
        columnMapping.put("dVenCed3", "existaVenituriCedareaFolosintei4");
        columnMapping.put("venCed4_cine", "venitCedareaFolosintei4Titular");
        columnMapping.put("venCed4_surs", "venitCedareaFolosintei4Sursa");
        columnMapping.put("venCed4_serv", "venitCedareaFolosintei4ServiciPrestat");
        columnMapping.put("venCed4_c", "venitCedareaFolosintei4VenitAnual");
        columnMapping.put("venCed4_mon", "venitCedareaFolosintei4Moneda");

        // venit cedarea folosintei 5
        columnMapping.put("dVenCed4", "existaVenituriCedareaFolosintei5");
        columnMapping.put("venCed5_cine", "venitCedareaFolosintei5Titular");
        columnMapping.put("venCed5_surs", "venitCedareaFolosintei5Sursa");
        columnMapping.put("venCed5_serv", "venitCedareaFolosintei5ServiciPrestat");
        columnMapping.put("venCed5_c", "venitCedareaFolosintei5VenitAnual");
        columnMapping.put("venCed5_mon", "venitCedareaFolosintei5Moneda");

        // venit cedarea folosintei 6
        columnMapping.put("dVenCed5", "existaVenituriCedareaFolosintei6");
        columnMapping.put("venCed6_cine", "venitCedareaFolosintei6Titular");
        columnMapping.put("venCed6_surs", "venitCedareaFolosintei6Sursa");
        columnMapping.put("venCed6_serv", "venitCedareaFolosintei6ServiciPrestat");
        columnMapping.put("venCed6_c", "venitCedareaFolosintei6VenitAnual");
        columnMapping.put("venCed6_mon", "venitCedareaFolosintei6Moneda");

        // alte venituri cedarea folosintei
        columnMapping.put("manVenCed", "alteVenituriCedareaFolosinteiFreeText");

        // venit investitii 1
        columnMapping.put("dVenInv", "existaVenituriInvestitii1");
        columnMapping.put("venInv1_cine", "venitInvestitii1Titular");
        columnMapping.put("venInv1_sursa", "venitInvestitii1Sursa");
        columnMapping.put("venInv1_serv", "venitInvestitii1ServiciPrestat");
        columnMapping.put("venInv1_c", "venitInvestitii1VenitAnual");
        columnMapping.put("venInv1_mon", "venitInvestitii1Moneda");

        // venit investitii 2
        columnMapping.put("dVenInv1", "existaVenituriInvestitii2");
        columnMapping.put("venInv2_cine", "venitInvestitii2Titular");
        columnMapping.put("venInv2_sursa", "venitInvestitii2Sursa");
        columnMapping.put("venInv2_serv", "venitInvestitii2ServiciPrestat");
        columnMapping.put("venInv2_c", "venitInvestitii2VenitAnual");
        columnMapping.put("venInv2_mon", "venitInvestitii2Moneda");

        // venit investitii 3
        columnMapping.put("dVenInv2", "existaVenituriInvestitii3");
        columnMapping.put("venInv3_cine", "venitInvestitii3Titular");
        columnMapping.put("venInv3_sursa", "venitInvestitii3Sursa");
        columnMapping.put("venInv3_serv", "venitInvestitii3ServiciPrestat");
        columnMapping.put("venInv3_c", "venitInvestitii3VenitAnual");
        columnMapping.put("venInv3_mon", "venitInvestitii3Moneda");

        // venit investitii 4
        columnMapping.put("dVenInv3", "existaVenituriInvestitii4");
        columnMapping.put("venInv4_cine", "venitInvestitii4Titular");
        columnMapping.put("venInv4_sursa", "venitInvestitii4Sursa");
        columnMapping.put("venInv4_serv", "venitInvestitii4ServiciPrestat");
        columnMapping.put("venInv4_c", "venitInvestitii4VenitAnual");
        columnMapping.put("venInv4_mon", "venitInvestitii4Moneda");

        // alte venituri din investitii
        columnMapping.put("manVenInv", "alteVenituriInvestitiiFreeText");

        // venituri pensii 1
        columnMapping.put("dVenPen", "existaVenituriPensii1");
        columnMapping.put("venPen1_cine", "venitPensii1Titular");
        columnMapping.put("venPen1_surs", "venitPensii1Sursa");
        columnMapping.put("venPen1_serv", "venitPensii1ServiciPrestat");
        columnMapping.put("venPen1_c", "venitPensii1VenitAnual");
        columnMapping.put("venPen1_mon", "venitPensii1Moneda");

        // venituri pensii 2
        columnMapping.put("dVenPen1", "existaVenituriPensii2");
        columnMapping.put("venPen2_cine", "venitPensii2Titular");
        columnMapping.put("venPen2_surs", "venitPensii2Sursa");
        columnMapping.put("venPen2_serv", "venitPensii2ServiciPrestat");
        columnMapping.put("venPen2_c", "venitPensii2VenitAnual");
        columnMapping.put("venPen2_mon", "venitPensii2Moneda");

        // alte venituri pensii
        columnMapping.put("manVenPen", "alteVenituriPensiiFreeText");

        // venituri agricole 1
        columnMapping.put("dVenAgr", "existaVenituriAgricole1");
        columnMapping.put("venAgr1_cine", "venitAgricol1Titular");
        columnMapping.put("venAgr1_surs", "venitAgricol1Sursa");
        columnMapping.put("venAgr1_serv", "venitAgricol1ServiciPrestat");
        columnMapping.put("venAgr1_c", "venitAgricol1VenitAnual");
        columnMapping.put("venAgr1_mon", "venitAgricol1Moneda");

        // venituri agricole 2
        columnMapping.put("dVenAgr1", "existaVenituriAgricole2");
        columnMapping.put("venAgr2_cine", "venitAgricol2Titular");
        columnMapping.put("venAgr2_surs", "venitAgricol2Sursa");
        columnMapping.put("venAgr2_serv", "venitAgricol2ServiciPrestat");
        columnMapping.put("venAgr2_c", "venitAgricol2VenitAnual");
        columnMapping.put("venAgr2_mon", "venitAgricol2Moneda");

        // alte venituri agricole
        columnMapping.put("manVenAgr", "alteVenituriAgricoleFreeText");

        // venituri noroc 1
        columnMapping.put("dVenNor", "existaVenituriNoroc1");
        columnMapping.put("venNor1_cine", "venitNoroc1Titular");
        columnMapping.put("venNor1_surs", "venitNoroc1Sursa");
        columnMapping.put("venNor1_serv", "venitNoroc1ServiciPrestat");
        columnMapping.put("venNor1_c", "venitNoroc1VenitAnual");
        columnMapping.put("venNor1_mon", "venitNoroc1Moneda");

        // venituri noroc 2
        columnMapping.put("dVenNor1", "existaVenituriNoroc2");
        columnMapping.put("venNor2_cine", "venitNoroc2Titular");
        columnMapping.put("venNor2_surs", "venitNoroc2Sursa");
        columnMapping.put("venNor2_serv", "venitNoroc2ServiciPrestat");
        columnMapping.put("venNor2_c", "venitNoroc2VenitAnual");
        columnMapping.put("venNor2_mon", "venitNoroc2Moneda");

        // alte venituri noroc
        columnMapping.put("manVenNor", "alteVenituriNorocFreeText");

        // alte venituri 1
        columnMapping.put("dVenAlt", "existaVenituriAlte1");
        columnMapping.put("venAlt1_cine", "venitAlte1Titular");
        columnMapping.put("venAlt1_surs", "venitAlte1Sursa");
        columnMapping.put("venAlt1_serv", "venitAlte1ServiciPrestat");
        columnMapping.put("venAlt1_c", "veniAlte1VenitAnual");
        columnMapping.put("venAlt1_mon", "venitAlte1Moneda");

        // alte venituri 2
        columnMapping.put("dVenAlt1", "existaVenituriAlte2");
        columnMapping.put("venAlt2_cine", "venitAlte2Titular");
        columnMapping.put("venAlt2_surs", "venitAlte2Sursa");
        columnMapping.put("venAlt2_serv", "venitAlte2ServiciPrestat");
        columnMapping.put("venAlt2_c", "veniAlte2VenitAnual");
        columnMapping.put("venAlt2_mon", "venitAlte2Moneda");

        // alte venituri 3
        columnMapping.put("dVenAlt2", "existaVenituriAlte3");
        columnMapping.put("venAlt3_cine", "venitAlte3Titular");
        columnMapping.put("venAlt3_surs", "venitAlte3Sursa");
        columnMapping.put("venAlt3_serv", "venitAlte3ServiciPrestat");
        columnMapping.put("venAlt3_c", "veniAlte3VenitAnual");
        columnMapping.put("venAlt3_mon", "venitAlte3Moneda");

        // alte venituri 4
        columnMapping.put("dVenAlt3", "existaVenituriAlte4");
        columnMapping.put("venAlt4_cine", "venitAlte4Titular");
        columnMapping.put("venAlt4_surs", "venitAlte4Sursa");
        columnMapping.put("venAlt4_serv", "venitAlte4ServiciPrestat");
        columnMapping.put("venAlt4_c", "veniAlte4VenitAnual");
        columnMapping.put("venAlt4_mon", "venitAlte4Moneda");

        // alte venituri
        columnMapping.put("manVenAlt", "alteVenituriAlteFreeText");
        return columnMapping;
    }
}
