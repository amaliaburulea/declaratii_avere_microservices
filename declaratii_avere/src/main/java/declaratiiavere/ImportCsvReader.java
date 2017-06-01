package declaratiiavere;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameMappingStrategy;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by razvan.dani on 23.04.2017.
 */
public class ImportCsvReader {
    public static void main(String[] args) throws FileNotFoundException {
        CsvToBean<RevenueDeclarationInfo> csvToBean = new CsvToBean<RevenueDeclarationInfo>();

        Map<String, String> columnMapping = getColumnMapping();

        HeaderColumnNameTranslateMappingStrategy<RevenueDeclarationInfo> strategy = new HeaderColumnNameTranslateMappingStrategy<RevenueDeclarationInfo>();
        strategy.setType(RevenueDeclarationInfo.class);
        strategy.setColumnMapping(columnMapping);

        List<RevenueDeclarationInfo> list;
        CSVReader reader = new CSVReader(new FileReader("C:\\Users\\dr\\declaratii_avere\\RAW - Declaratii de Avere - Form Responses.csv"), ',', '"');
        list = csvToBean.parse(strategy, reader);

        TreeSet<String> set = new TreeSet<String>();

        for (RevenueDeclarationInfo revenueDeclarationInfo : list) {
//            System.out.println("revenueDeclarationInfo = " + revenueDeclarationInfo);

            if (!revenueDeclarationInfo.getPlasament1Tip().equals("")) {
//                System.out.println("MA CAC IN GURA CUI????? revenueDeclarationInfo = " + revenueDeclarationInfo);
                set.add(revenueDeclarationInfo.getPlasament1Tip());
            }
        }

        System.out.println("cladireCategorieSet = " + set);
    }

    private static Map<String, String> getColumnMapping() {
        Map<String, String> columnMapping = new HashMap<String, String>();
        columnMapping.put("[pnDem] Prenume demnitar", "firstName");
        columnMapping.put("[nDem] Nume demnitar", "lastName");
        columnMapping.put("[datDec] Data completării declarației de avere de către demnitar", "dataDeclaratiei");
        columnMapping.put("[funct] Funcție", "functie");
        columnMapping.put("[inst] Instituție:", "institutie");

        // teren 1
        columnMapping.put("[dimobt] Exista terenuri listate în declarație?", "existaTerenuri1");
        columnMapping.put("[imobt1_adr] Adresa sau zona:", "teren1Adresa");
        columnMapping.put("[imobt1_cat] Categoria:", "teren1Categoria");
        columnMapping.put("[imobt1_an] Anul dobândirii: ", "teren1AnDobindire");
        columnMapping.put("[imobt1_sup] Suprafaţa: ", "teren1Suprafata");
        columnMapping.put("[imobt1_unit] Unitate de măsură suprafață:", "teren1UnitateDeMasura");
        columnMapping.put("[imobt1_cot] Cota-parte:", "teren1CotaParte");
        columnMapping.put("[imobt1_dob] Modul de dobândire:", "teren1ModDobandire");
        columnMapping.put("[imobt1_tit] Titularul:", "teren1Titular");

        // teren 2
        columnMapping.put("[dimobt1] Alte terenuri?", "existaTerenuri2");
        columnMapping.put("[imobt2_adr] Adresa sau zona:", "teren2Adresa");
        columnMapping.put("[imobt2_cat] Categoria:", "teren2Categoria");
        columnMapping.put("[imobt2_an] Anul dobândirii: ", "teren2AnDobindire");
        columnMapping.put("[imobt2_sup] Suprafaţa: ", "teren2Suprafata");
        columnMapping.put("[imobt2_unit] Unitate de măsură suprafață:", "teren2UnitateDeMasura");
        columnMapping.put("[imobt2_cot] Cota-parte:", "teren2CotaParte");
        columnMapping.put("[imobt2_dob] Modul de dobândire:", "teren2ModDobandire");
        columnMapping.put("[imobt2_tit] Titularul:", "teren2Titular");

        // teren 3
        columnMapping.put("[dimobt2] Alte terenuri?", "existaTerenuri3");
        columnMapping.put("[imobt3_adr] Adresa sau zona:", "teren3Adresa");
        columnMapping.put("[imobt3_cat] Categoria:", "teren3Categoria");
        columnMapping.put("[imobt3_an] Anul dobândirii: ", "teren3AnDobindire");
        columnMapping.put("[imobt3_sup] Suprafaţa: ", "teren3Suprafata");
        columnMapping.put("[imobt3_unit] Unitate de măsură suprafață:", "teren3UnitateDeMasura");
        columnMapping.put("[imobt3_cot] Cota-parte:", "teren3CotaParte");
        columnMapping.put("[imobt3_dob] Modul de dobândire:", "teren3ModDobandire");
        columnMapping.put("[imobt3_tit] Titularul:", "teren3Titular");

        // teren 4
        columnMapping.put("[dimobt3] Alte terenuri?", "existaTerenuri4");
        columnMapping.put("[imobt4_adr] Adresa sau zona:", "teren4Adresa");
        columnMapping.put("[imobt4_cat] Categoria:", "teren4Categoria");
        columnMapping.put("[imobt4_an] Anul dobândirii: ", "teren4AnDobindire");
        columnMapping.put("[imobt4_sup] Suprafaţa: ", "teren4Suprafata");
        columnMapping.put("[imobt4_unit] Unitate de măsură suprafață:", "teren4UnitateDeMasura");
        columnMapping.put("[imobt4_cot] Cota-parte:", "teren4CotaParte");
        columnMapping.put("[imobt4_dob] Modul de dobândire:", "teren4ModDobandire");
        columnMapping.put("[imobt4_tit] Titularul:", "teren4Titular");

        // teren 5
        columnMapping.put("[dimobt4] Alte terenuri?", "existaTerenuri5");
        columnMapping.put("[imobt5_adr] Adresa sau zona:", "teren5Adresa");
        columnMapping.put("[imobt5_cat] Categoria:", "teren5Categoria");
        columnMapping.put("[imobt5_an] Anul dobândirii: ", "teren5AnDobindire");
        columnMapping.put("[imobt5_sup] Suprafaţa: ", "teren5Suprafata");
        columnMapping.put("[imobt5_unit] Unitate de măsură suprafață:", "teren5UnitateDeMasura");
        columnMapping.put("[imobt5_cot] Cota-parte:", "teren5CotaParte");
        columnMapping.put("[imobt5_dob] Modul de dobândire:", "teren5ModDobandire");
        columnMapping.put("[imobt5_tit] Titularul:", "teren5Titular");

        // teren 6
        columnMapping.put("[dimobt5] Alte terenuri?", "existaTerenuri6");
        columnMapping.put("[imobt6_adr] Adresa sau zona:", "teren6Adresa");
        columnMapping.put("[imobt6_cat] Categoria:", "teren6Categoria");
        columnMapping.put("[imobt6_an] Anul dobândirii: ", "teren6AnDobindire");
        columnMapping.put("[imobt6_sup] Suprafaţa: ", "teren6Suprafata");
        columnMapping.put("[imobt6_unit] Unitate de măsură suprafață:", "teren6UnitateDeMasura");
        columnMapping.put("[imobt6_cot] Cota-parte:", "teren6CotaParte");
        columnMapping.put("[imobt6_dob] Modul de dobândire:", "teren6ModDobandire");
        columnMapping.put("[imobt6_tit] Titularul:", "teren6Titular");

        // teren 7
        columnMapping.put("[dimobt6] Alte terenuri?", "existaTerenuri7");
        columnMapping.put("[imobt7_adr] Adresa sau zona:", "teren7Adresa");
        columnMapping.put("[imobt7_cat] Categoria:", "teren7Categoria");
        columnMapping.put("[imobt7_an] Anul dobândirii: ", "teren7AnDobindire");
        columnMapping.put("[imobt7_sup] Suprafaţa: ", "teren7Suprafata");
        columnMapping.put("[imobt7_unit] Unitate de măsură suprafață:", "teren7UnitateDeMasura");
        columnMapping.put("[imobt7_cot] Cota-parte:", "teren7CotaParte");
        columnMapping.put("[imobt7_dob] Modul de dobândire:", "teren7ModDobandire");
        columnMapping.put("[imobt7_tit] Titularul:", "teren7Titular");

        // teren 8
        columnMapping.put("[dimobt7] Alte terenuri?", "existaTerenuri8");
        columnMapping.put("[imobt8_adr] Adresa sau zona:", "teren8Adresa");
        columnMapping.put("[imobt8_cat] Categoria:", "teren8Categoria");
        columnMapping.put("[imobt8_an] Anul dobândirii: ", "teren8AnDobindire");
        columnMapping.put("[imobt8_sup] Suprafaţa: ", "teren8Suprafata");
        columnMapping.put("[imobt8_unit] Unitate de măsură suprafață:", "teren8UnitateDeMasura");
        columnMapping.put("[imobt8_cot] Cota-parte:", "teren8CotaParte");
        columnMapping.put("[imobt8_dob] Modul de dobândire:", "teren8ModDobandire");
        columnMapping.put("[imobt8_tit] Titularul:", "teren8Titular");

        // teren 9
        columnMapping.put("[dimobt8] Alte terenuri?", "existaTerenuri9");
        columnMapping.put("[imobt9_adr] Adresa sau zona:", "teren9Adresa");
        columnMapping.put("[imobt9_cat] Categoria:", "teren9Categoria");
        columnMapping.put("[imobt9_an] Anul dobândirii: ", "teren9AnDobindire");
        columnMapping.put("[imobt9_sup] Suprafaţa: ", "teren9Suprafata");
        columnMapping.put("[imobt9_unit] Unitate de măsură suprafață:", "teren9UnitateDeMasura");
        columnMapping.put("[imobt9_cot] Cota-parte:", "teren9CotaParte");
        columnMapping.put("[imobt9_dob] Modul de dobândire:", "teren9ModDobandire");
        columnMapping.put("[imobt9_tit] Titularul:", "teren9Titular");

        // teren 10
        columnMapping.put("[dimobt9] Alte terenuri?", "existaTerenuri10");
        columnMapping.put("[imobt10_adr] Adresa sau zona:", "teren10Adresa");
        columnMapping.put("[imobt10_cat] Categoria:", "teren10Categoria");
        columnMapping.put("[imobt10_an] Anul dobândirii: ", "teren10AnDobindire");
        columnMapping.put("[imobt10_sup] Suprafaţa: ", "teren10Suprafata");
        columnMapping.put("[imobt10_unit] Unitate de măsură suprafață:", "teren10UnitateDeMasura");
        columnMapping.put("[imobt10_cot] Cota-parte:", "teren10CotaParte");
        columnMapping.put("[imobt10_dob] Modul de dobândire:", "teren10ModDobandire");
        columnMapping.put("[imobt10_tit] Titularul:", "teren10Titular");

        // teren 11
        columnMapping.put("[dimobt10] Alte terenuri?", "existaTerenuri11");
        columnMapping.put("[imobt11_adr] Adresa sau zona:", "teren11Adresa");
        columnMapping.put("[imobt11_cat] Categoria:", "teren11Categoria");
        columnMapping.put("[imobt11_an] Anul dobândirii: ", "teren11AnDobindire");
        columnMapping.put("[imobt11_sup] Suprafaţa: ", "teren11Suprafata");
        columnMapping.put("[imobt11_unit] Unitate de măsură suprafață:", "teren11UnitateDeMasura");
        columnMapping.put("[imobt11_cot] Cota-parte:", "teren11CotaParte");
        columnMapping.put("[imobt11_dob] Modul de dobândire:", "teren11ModDobandire");
        columnMapping.put("[imobt11_tit] Titularul:", "teren11Titular");

        // teren 12
        columnMapping.put("[dimobt11] Alte terenuri?", "existaTerenuri12");
        columnMapping.put("[imobt12_adr] Adresa sau zona:", "teren12Adresa");
        columnMapping.put("[imobt12_cat] Categoria:", "teren12Categoria");
        columnMapping.put("[imobt12_an] Anul dobândirii: ", "teren12AnDobindire");
        columnMapping.put("[imobt12_sup] Suprafaţa: ", "teren12Suprafata");
        columnMapping.put("[imobt12_unit] Unitate de măsură suprafață:", "teren12UnitateDeMasura");
        columnMapping.put("[imobt12_cot] Cota-parte:", "teren12CotaParte");
        columnMapping.put("[imobt12_dob] Modul de dobândire:", "teren12ModDobandire");
        columnMapping.put("[imobt12_tit] Titularul:", "teren12Titular");

        // teren 13
        columnMapping.put("[dimobt12] Alte terenuri?", "existaTerenuri13");
        columnMapping.put("[imobt13_adr] Adresa sau zona:", "teren13Adresa");
        columnMapping.put("[imobt13_cat] Categoria:", "teren13Categoria");
        columnMapping.put("[imobt13_an] Anul dobândirii: ", "teren13AnDobindire");
        columnMapping.put("[imobt13_sup] Suprafaţa: ", "teren13Suprafata");
        columnMapping.put("[imobt13_unit] Unitate de măsură suprafață:", "teren13UnitateDeMasura");
        columnMapping.put("[imobt13_cot] Cota-parte:", "teren13CotaParte");
        columnMapping.put("[imobt13_dob] Modul de dobândire:", "teren13ModDobandire");
        columnMapping.put("[imobt13_tit] Titularul:", "teren13Titular");

        // teren 14
        columnMapping.put("[dimobt13] Alte terenuri?", "existaTerenuri14");
        columnMapping.put("[imobt14_adr] Adresa sau zona:", "teren14Adresa");
        columnMapping.put("[imobt14_cat] Categoria:", "teren14Categoria");
        columnMapping.put("[imobt14_an] Anul dobândirii: ", "teren14AnDobindire");
        columnMapping.put("[imobt14_sup] Suprafaţa: ", "teren14Suprafata");
        columnMapping.put("[imobt14_unit] Unitate de măsură suprafață:", "teren14UnitateDeMasura");
        columnMapping.put("[imobt14_cot] Cota-parte:", "teren14CotaParte");
        columnMapping.put("[imobt14_dob] Modul de dobândire:", "teren14ModDobandire");
        columnMapping.put("[imobt14_tit] Titularul:", "teren14Titular");

        // teren 15
        columnMapping.put("[dimobt14] Alte terenuri?", "existaTerenuri15");
        columnMapping.put("[imobt15_adr] Adresa sau zona:", "teren15Adresa");
        columnMapping.put("[imobt15_cat] Categoria:", "teren15Categoria");
        columnMapping.put("[imobt15_an] Anul dobândirii: ", "teren15AnDobindire");
        columnMapping.put("[imobt15_sup] Suprafaţa: ", "teren15Suprafata");
        columnMapping.put("[imobt15_unit] Unitate de măsură suprafață:", "teren15UnitateDeMasura");
        columnMapping.put("[imobt15_cot] Cota-parte:", "teren15CotaParte");
        columnMapping.put("[imobt15_dob] Modul de dobândire:", "teren15ModDobandire");
        columnMapping.put("[imobt15_tit] Titularul:", "teren15Titular");

        // alter terenuri free text needs parsing
        columnMapping.put("[manimobt] Te rugăm scrie rândurile rămase din tabel în caseta de mai jos. Separă categoriile cu \";\" și apasă Enter dupa fiecare rând din tabel.", "alteTerenuriFreeText");

        // cladire 1
        columnMapping.put("[dimobc] Există clădiri listate în declarație?", "existaCladiri1");
        columnMapping.put("[imobc1_adr] Adresa sau zona:", "cladire1Adresa");
        columnMapping.put("[imobc1_cat] Categoria:", "cladire1Categoria");
        columnMapping.put("[imobc1_an] Anul dobândirii:", "cladire1AnDobindire");
        columnMapping.put("[imobc1_sup] Suprafaţa:", "cladire1Suprafata");
        columnMapping.put("[imobc1_unit] Unitate de măsură suprafață:", "cladire1UnitateDeMasura");
        columnMapping.put("[imobc1_cot] Cota-parte:", "cladire1CotaParte");
        columnMapping.put("[imobc1_dob] Modul de dobândire:", "cladire1ModDobandire");
        columnMapping.put("[imobc1_tit] Titularul:", "cladire1Titular");

        // cladire 2
        columnMapping.put("[dimobc1] Alte clădiri?", "existaCladiri2");
        columnMapping.put("[imobc2_adr] Adresa sau zona:", "cladire2Adresa");
        columnMapping.put("[imobc2_cat] Categoria:", "cladire2Categoria");
        columnMapping.put("[imobc2_an] Anul dobândirii:", "cladire2AnDobindire");
        columnMapping.put("[imobc2_sup] Suprafaţa:", "cladire2Suprafata");
        columnMapping.put("[imobc2_unit] Unitate de măsură suprafață:", "cladire2UnitateDeMasura");
        columnMapping.put("[imobc2_cot] Cota-parte:", "cladire2CotaParte");
        columnMapping.put("[imobc2_dob] Modul de dobândire:", "cladire2ModDobandire");
        columnMapping.put("[imobc2_tit] Titularul:", "cladire2Titular");

        // cladire 3
        columnMapping.put("[dimobc2] Alte clădiri?", "existaCladiri3");
        columnMapping.put("[imobc3_adr] Adresa sau zona:", "cladire3Adresa");
        columnMapping.put("[imobc3_cat] Categoria:", "cladire3Categoria");
        columnMapping.put("[imobc3_an] Anul dobândirii:", "cladire3AnDobindire");
        columnMapping.put("[imobc3_sup] Suprafaţa:", "cladire3Suprafata");
        columnMapping.put("[imobc3_unit] Unitate de măsură suprafață:", "cladire3UnitateDeMasura");
        columnMapping.put("[imobc3_cot] Cota-parte:", "cladire3CotaParte");
        columnMapping.put("[imobc3_dob] Modul de dobândire:", "cladire3ModDobandire");
        columnMapping.put("[imobc3_tit] Titularul:", "cladire3Titular");

        // cladire 4
        columnMapping.put("[dimobc3] Alte clădiri?", "existaCladiri4");
        columnMapping.put("[imobc4_adr] Adresa sau zona:", "cladire4Adresa");
        columnMapping.put("[imobc4_cat] Categoria:", "cladire4Categoria");
        columnMapping.put("[imobc4_an] Anul dobândirii:", "cladire4AnDobindire");
        columnMapping.put("[imobc4_sup] Suprafaţa:", "cladire4Suprafata");
        columnMapping.put("[imobc4_unit] Unitate de măsură suprafață:", "cladire4UnitateDeMasura");
        columnMapping.put("[imobc4_cot] Cota-parte:", "cladire4CotaParte");
        columnMapping.put("[imobc4_dob] Modul de dobândire:", "cladire4ModDobandire");
        columnMapping.put("[imobc4_tit] Titularul:", "cladire4Titular");

        // cladire 5
        columnMapping.put("[dimobc4] Alte clădiri?", "existaCladiri5");
        columnMapping.put("[imobc5_adr] Adresa sau zona:", "cladire5Adresa");
        columnMapping.put("[imobc5_cat] Categoria:", "cladire5Categoria");
        columnMapping.put("[imobc5_an] Anul dobândirii:", "cladire5AnDobindire");
        columnMapping.put("[imobc5_sup] Suprafaţa:", "cladire5Suprafata");
        columnMapping.put("[imobc5_unit] Unitate de măsură suprafață:", "cladire5UnitateDeMasura");
        columnMapping.put("[imobc5_cot] Cota-parte:", "cladire5CotaParte");
        columnMapping.put("[imobc5_dob] Modul de dobândire:", "cladire5ModDobandire");
        columnMapping.put("[imobc5_tit] Titularul:", "cladire5Titular");

        // cladire 6
        columnMapping.put("[dimobc5] Alte clădiri?", "existaCladiri6");
        columnMapping.put("[imobc6_adr] Adresa sau zona:", "cladire6Adresa");
        columnMapping.put("[imobc6_cat] Categoria:", "cladire6Categoria");
        columnMapping.put("[imobc6_an] Anul dobândirii:", "cladire6AnDobindire");
        columnMapping.put("[imobc6_sup] Suprafaţa:", "cladire6Suprafata");
        columnMapping.put("[imobc6_unit] Unitate de măsură suprafață:", "cladire6UnitateDeMasura");
        columnMapping.put("[imobc6_cot] Cota-parte:", "cladire6CotaParte");
        columnMapping.put("[imobc6_dob] Modul de dobândire:", "cladire6ModDobandire");
        columnMapping.put("[imobc6_tit] Titularul:", "cladire6Titular");

        // cladire 7
        columnMapping.put("[dimobc6] Alte clădiri?", "existaCladiri7");
        columnMapping.put("[imobc7_adr] Adresa sau zona:", "cladire7Adresa");
        columnMapping.put("[imobc7_cat] Categoria:", "cladire7Categoria");
        columnMapping.put("[imobc7_an] Anul dobândirii:", "cladire7AnDobindire");
        columnMapping.put("[imobc7_sup] Suprafaţa:", "cladire7Suprafata");
        columnMapping.put("[imobc7_unit] Unitate de măsură suprafață:", "cladire7UnitateDeMasura");
        columnMapping.put("[imobc7_cot] Cota-parte:", "cladire7CotaParte");
        columnMapping.put("[imobc7_dob] Modul de dobândire:", "cladire7ModDobandire");
        columnMapping.put("[imobc7_tit] Titularul:", "cladire7Titular");

        // cladire 8
        columnMapping.put("[dimobc7] Alte clădiri?", "existaCladiri8");
        columnMapping.put("[imobc8_adr] Adresa sau zona:", "cladire8Adresa");
        columnMapping.put("[imobc8_cat] Categoria:", "cladire8Categoria");
        columnMapping.put("[imobc8_an] Anul dobândirii:", "cladire8AnDobindire");
        columnMapping.put("[imobc8_sup] Suprafaţa:", "cladire8Suprafata");
        columnMapping.put("[imobc8_unit] Unitate de măsură suprafață:", "cladire8UnitateDeMasura");
        columnMapping.put("[imobc8_cot] Cota-parte:", "cladire8CotaParte");
        columnMapping.put("[imobc8_dob] Modul de dobândire:", "cladire8ModDobandire");
        columnMapping.put("[imobc8_tit] Titularul:", "cladire8Titular");

        // cladire 9
        columnMapping.put("[dimobc8] Alte clădiri?", "existaCladiri9");
        columnMapping.put("[imobc9_adr] Adresa sau zona:", "cladire9Adresa");
        columnMapping.put("[imobc9_cat] Categoria:", "cladire9Categoria");
        columnMapping.put("[imobc9_an] Anul dobândirii:", "cladire9AnDobindire");
        columnMapping.put("[imobc9_sup] Suprafaţa:", "cladire9Suprafata");
        columnMapping.put("[imobc9_unit] Unitate de măsură suprafață:", "cladire9UnitateDeMasura");
        columnMapping.put("[imobc9_cot] Cota-parte:", "cladire9CotaParte");
        columnMapping.put("[imobc9_dob] Modul de dobândire:", "cladire9ModDobandire");
        columnMapping.put("[imobc9_tit] Titularul:", "cladire9Titular");

        // cladire 10
        columnMapping.put("[dimobc9] Alte clădiri?", "existaCladiri10");
        columnMapping.put("[imobc10_adr] Adresa sau zona:", "cladire10Adresa");
        columnMapping.put("[imobc10_cat] Categoria:", "cladire10Categoria");
        columnMapping.put("[imobc10_an] Anul dobândirii:", "cladire10AnDobindire");
        columnMapping.put("[imobc10_sup] Suprafaţa:", "cladire10Suprafata");
        columnMapping.put("[imobc10_unit] Unitate de măsură suprafață:", "cladire10UnitateDeMasura");
        columnMapping.put("[imobc10_cot] Cota-parte:", "cladire10CotaParte");
        columnMapping.put("[imobc10_dob] Modul de dobândire:", "cladire10ModDobandire");
        columnMapping.put("[imobc10_tit] Titularul:", "cladire10Titular");

        // cladire 11
        columnMapping.put("[dimobc10] Alte clădiri?", "existaCladiri11");
        columnMapping.put("[imobc11_adr] Adresa sau zona:", "cladire11Adresa");
        columnMapping.put("[imobc11_cat] Categoria:", "cladire11Categoria");
        columnMapping.put("[imobc11_an] Anul dobândirii:", "cladire11AnDobindire");
        columnMapping.put("[imobc11_sup] Suprafaţa:", "cladire11Suprafata");
        columnMapping.put("[imobc11_unit] Unitate de măsură suprafață:", "cladire11UnitateDeMasura");
        columnMapping.put("[imobc11_cot] Cota-parte:", "cladire11CotaParte");
        columnMapping.put("[imobc11_dob] Modul de dobândire:", "cladire11ModDobandire");
        columnMapping.put("[imobc11_tit] Titularul:", "cladire11Titular");

        // cladire 12
        columnMapping.put("[dimobc11] Alte clădiri?", "existaCladiri12");
        columnMapping.put("[imobc12_adr] Adresa sau zona:", "cladire12Adresa");
        columnMapping.put("[imobc12_cat] Categoria:", "cladire12Categoria");
        columnMapping.put("[imobc12_an] Anul dobândirii:", "cladire12AnDobindire");
        columnMapping.put("[imobc12_sup] Suprafaţa:", "cladire12Suprafata");
        columnMapping.put("[imobc12_unit] Unitate de măsură suprafață:", "cladire12UnitateDeMasura");
        columnMapping.put("[imobc12_cot] Cota-parte:", "cladire12CotaParte");
        columnMapping.put("[imobc12_dob] Modul de dobândire:", "cladire12ModDobandire");
        columnMapping.put("[imobc12_tit] Titularul:", "cladire12Titular");

        // cladire 13
        columnMapping.put("[dimobc12] Alte clădiri?", "existaCladiri13");
        columnMapping.put("[imobc13_adr] Adresa sau zona:", "cladire13Adresa");
        columnMapping.put("[imobc13_cat] Categoria:", "cladire13Categoria");
        columnMapping.put("[imobc13_an] Anul dobândirii:", "cladire13AnDobindire");
        columnMapping.put("[imobc13_sup] Suprafaţa:", "cladire13Suprafata");
        columnMapping.put("[imobc13_unit] Unitate de măsură suprafață:", "cladire13UnitateDeMasura");
        columnMapping.put("[imobc13_cot] Cota-parte:", "cladire13CotaParte");
        columnMapping.put("[imobc13_dob] Modul de dobândire:", "cladire13ModDobandire");
        columnMapping.put("[imobc13_tit] Titularul:", "cladire13Titular");

        // cladire 14
        columnMapping.put("[dimobc13] Alte clădiri?", "existaCladiri14");
        columnMapping.put("[imobc14_adr] Adresa sau zona:", "cladire14Adresa");
        columnMapping.put("[imobc14_cat] Categoria:", "cladire14Categoria");
        columnMapping.put("[imobc14_an] Anul dobândirii:", "cladire14AnDobindire");
        columnMapping.put("[imobc14_sup] Suprafaţa:", "cladire14Suprafata");
        columnMapping.put("[imobc14_unit] Unitate de măsură suprafață:", "cladire14UnitateDeMasura");
        columnMapping.put("[imobc14_cot] Cota-parte:", "cladire14CotaParte");
        columnMapping.put("[imobc14_dob] Modul de dobândire:", "cladire14ModDobandire");
        columnMapping.put("[imobc14_tit] Titularul:", "cladire14Titular");

        // cladire 15
        columnMapping.put("[dimobc14] Alte clădiri?", "existaCladiri15");
        columnMapping.put("[imobc15_adr] Adresa sau zona:", "cladire15Adresa");
        columnMapping.put("[imobc15_cat] Categoria:", "cladire15Categoria");
        columnMapping.put("[imobc15_an] Anul dobândirii:", "cladire15AnDobindire");
        columnMapping.put("[imobc15_sup] Suprafaţa:", "cladire15Suprafata");
        columnMapping.put("[imobc15_unit] Unitate de măsură suprafață:", "cladire15UnitateDeMasura");
        columnMapping.put("[imobc15_cot] Cota-parte:", "cladire15CotaParte");
        columnMapping.put("[imobc15_dob] Modul de dobândire:", "cladire15ModDobandire");
        columnMapping.put("[imobc15_tit] Titularul:", "cladire15Titular");

        // alter cladiri free text needs parsing
        columnMapping.put("[manimobc] Te rugăm scrie rândurile rămase din tabel în caseta de mai jos. Separă categoriile cu \";\" și apasă Enter dupa fiecare rând din tabel.", "alteCladiriFreeText");

        // automobil 1
        columnMapping.put("[dmoba] Există bunuri mobile (auto) listate în declarație?", "existaAutomobile1");
        columnMapping.put("[moba1_nat] Natura:", "automobil1Tip");
        columnMapping.put("[moba1_marc] Marca:", "automobil1Marca");
        columnMapping.put("[moba1_nr] Nr. de bucăţi:", "automobil1Cantitate");
        columnMapping.put("[moba1_an] Anul de fabricaţie:", "automobil1AnFabricatie");
        columnMapping.put("[moba1_dob] Modul de dobândire:", "automobil1ModDobandire");

        // automobil 2
        columnMapping.put("[dmoba1] Alte mașini?", "existaAutomobile2");
        columnMapping.put("[moba2_nat] Natura:", "automobil2Tip");
        columnMapping.put("[moba2_marc] Marca:", "automobil2Marca");
        columnMapping.put("[moba2_nr] Nr. de bucăţi:", "automobil2Cantitate");
        columnMapping.put("[moba2_an] Anul de fabricaţie:", "automobil2AnFabricatie");
        columnMapping.put("[moba2_dob] Modul de dobândire:", "automobil2ModDobandire");

        // automobil 3
        columnMapping.put("[dmoba2] Alte mașini?", "existaAutomobile3");
        columnMapping.put("[moba3_nat] Natura:", "automobil3Tip");
        columnMapping.put("[moba3_marc] Marca:", "automobil3Marca");
        columnMapping.put("[moba3_nr] Nr. de bucăţi:", "automobil3Cantitate");
        columnMapping.put("[moba3_an] Anul de fabricaţie:", "automobil3AnFabricatie");
        columnMapping.put("[moba3_dob] Modul de dobândire:", "automobil3ModDobandire");

        // automobil 4
        columnMapping.put("[dmoba3] Alte mașini?", "existaAutomobile4");
        columnMapping.put("[moba4_nat] Natura:", "automobil4Tip");
        columnMapping.put("[moba4_marc] Marca:", "automobil4Marca");
        columnMapping.put("[moba4_nr] Nr. de bucăţi:", "automobil4Cantitate");
        columnMapping.put("[moba4_an] Anul de fabricaţie:", "automobil4AnFabricatie");
        columnMapping.put("[moba4_dob] Modul de dobândire:", "automobil4ModDobandire");

        // automobil 5
        columnMapping.put("[dmoba4] Alte mașini?", "existaAutomobile5");
        columnMapping.put("[moba5_nat] Natura:", "automobil5Tip");
        columnMapping.put("[moba5_marc] Marca:", "automobil5Marca");
        columnMapping.put("[moba5_nr] Nr. de bucăţi:", "automobil5Cantitate");
        columnMapping.put("[moba5_an] Anul de fabricaţie:", "automobil5AnFabricatie");
        columnMapping.put("[moba5_dob] Modul de dobândire:", "automobil5ModDobandire");

        // alte automobile
        columnMapping.put("[manmoba] Te rugăm scrie rândurile rămase din tabel în caseta de mai jos. Separă categoriile cu \";\" și apasă Enter dupa fiecare rând din tabel.", "alteAutomobileFreeText");

        // bijuterie 1
        columnMapping.put("[dmobb] Există bijuterii/obiecte de artă listate în declarație?", "existaBijuterii1");
        columnMapping.put("[mobb1_desc] Descriere sumară:", "bijuterie1Descriere");
        columnMapping.put("[mobb1_an] Anul dobândirii:", "bijuterie1AnDobandire");
        columnMapping.put("[mobb1_val] Valoarea estimată:", "bijuterie1ValoareEstimata");
        columnMapping.put("[mobb1_mon] Moneda în care este exprimată valoarea:", "bijuterie1Moneda");

        // bijuterie 2
        columnMapping.put("[dmobb1] Alte bunuri?", "existaBijuterii2");
        columnMapping.put("[mobb2_desc] Descriere sumară:", "bijuterie2Descriere");
        columnMapping.put("[mobb2_an] Anul dobândirii:", "bijuterie2AnDobandire");
        columnMapping.put("[mobb2_val] Valoarea estimată:", "bijuterie2ValoareEstimata");
        columnMapping.put("[mobb2_mon] Moneda în care este exprimată valoarea:", "bijuterie2Moneda");

        // bijuterie 3
        columnMapping.put("[dmobb2] Alte bunuri?", "existaBijuterii3");
        columnMapping.put("[mobb3_desc] Descriere sumară:", "bijuterie3Descriere");
        columnMapping.put("[mobb3_an] Anul dobândirii:", "bijuterie3AnDobandire");
        columnMapping.put("[mobb3_val] Valoarea estimată:", "bijuterie3ValoareEstimata");
        columnMapping.put("[mobb3_mon] Moneda în care este exprimată valoarea:", "bijuterie3Moneda");

        // bijuterie 4
        columnMapping.put("[dmobb3] Alte bunuri?", "existaBijuterii4");
        columnMapping.put("[mobb4_desc] Descriere sumară:", "bijuterie4Descriere");
        columnMapping.put("[mobb4_an] Anul dobândirii:", "bijuterie4AnDobandire");
        columnMapping.put("[mobb4_val] Valoarea estimată:", "bijuterie4ValoareEstimata");
        columnMapping.put("[mobb4_mon] Moneda în care este exprimată valoarea:", "bijuterie4Moneda");

        // bijuterie 5
        columnMapping.put("[dmobb4] Alte bunuri?", "existaBijuterii5");
        columnMapping.put("[mobb5_desc] Descriere sumară:", "bijuterie5Descriere");
        columnMapping.put("[mobb5_an] Anul dobândirii:", "bijuterie5AnDobandire");
        columnMapping.put("[mobb5_val] Valoarea estimată:", "bijuterie5ValoareEstimata");
        columnMapping.put("[mobb5_mon] Moneda în care este exprimată valoarea:", "bijuterie5Moneda");

        // alte bijuterii
        columnMapping.put("[manmobb] Te rugăm scrie rândurile rămase din tabel în caseta de mai jos. Separă categoriile cu \";\" și apasă Enter dupa fiecare rând din tabel.", "alteBijuteriiFreeText");

        // bunuri instrainate 1
        columnMapping.put("[dinstr] Există obiecte înstrăinate, mobile sau imobile listate în declarație?", "existaBunuriInstrainate1");
        columnMapping.put("[instr1_nat] Natura bunului înstrăinat:", "bunInstrainat1Natura");
        columnMapping.put("[instr1_dat] Data înstrăinării:", "bunInstrainat1Data");
        columnMapping.put("[instr1_pers] Persoana către care s-a înstrăinat:", "bunInstrainat1PersoanaCatreCare");
        columnMapping.put("[instr1_form] Forma înstrăinării:", "bunInstrainat1FormaInstrainarii");
        columnMapping.put("[instr1_val] Valoarea", "bunInstrainat1Valoarea");
        columnMapping.put("[instr1_mon] Moneda în care este exprimată valoarea:", "bunInstrainat1Moneda");

        // bunuri instrainate 2
        columnMapping.put("[dinstr1] Alte bunuri?", "existaBunuriInstrainate2");
        columnMapping.put("[instr2_nat] Natura bunului înstrăinat:", "bunInstrainat2Natura");
        columnMapping.put("[instr2_dat] Data înstrăinării:", "bunInstrainat2Data");
        columnMapping.put("[instr2_pers] Persoana către care s-a înstrăinat:", "bunInstrainat2PersoanaCatreCare");
        columnMapping.put("[instr2_form] Forma înstrăinării:", "bunInstrainat2FormaInstrainarii");
        columnMapping.put("[instr2_val] Valoarea", "bunInstrainat2Valoarea");
        columnMapping.put("[instr2_mon] Moneda în care este exprimată valoarea:", "bunInstrainat2Moneda");

        // bunuri instrainate 3
        columnMapping.put("[dinstr2] Alte bunuri?", "existaBunuriInstrainate3");
        columnMapping.put("[instr3_nat] Natura bunului înstrăinat:", "bunInstrainat3Natura");
        columnMapping.put("[instr3_dat] Data înstrăinării:", "bunInstrainat3Data");
        columnMapping.put("[instr3_pers] Persoana către care s-a înstrăinat:", "bunInstrainat3PersoanaCatreCare");
        columnMapping.put("[instr3_form] Forma înstrăinării:", "bunInstrainat3FormaInstrainarii");
        columnMapping.put("[instr3_val] Valoarea", "bunInstrainat3Valoarea");
        columnMapping.put("[instr3_mon] Moneda în care este exprimată valoarea:", "bunInstrainat3Moneda");

        // bunuri instrainate 4
        columnMapping.put("[dinstr3] Alte bunuri?", "existaBunuriInstrainate4");
        columnMapping.put("[instr4_nat] Natura bunului înstrăinat:", "bunInstrainat4Natura");
        columnMapping.put("[instr4_dat] Data înstrăinării:", "bunInstrainat4Data");
        columnMapping.put("[instr4_pers] Persoana către care s-a înstrăinat:", "bunInstrainat4PersoanaCatreCare");
        columnMapping.put("[instr4_form] Forma înstrăinării:", "bunInstrainat4FormaInstrainarii");
        columnMapping.put("[instr4_val] Valoarea", "bunInstrainat4Valoarea");
        columnMapping.put("[instr4_mon] Moneda în care este exprimată valoarea:", "bunInstrainat4Moneda");

        // bunuri instrainate 5
        columnMapping.put("[dinstr4] Alte bunuri?", "existaBunuriInstrainate5");
        columnMapping.put("[instr5_nat] Natura bunului înstrăinat:", "bunInstrainat5Natura");
        columnMapping.put("[instr5_dat] Data înstrăinării:", "bunInstrainat5Data");
        columnMapping.put("[instr5_pers] Persoana către care s-a înstrăinat:", "bunInstrainat5PersoanaCatreCare");
        columnMapping.put("[instr5_form] Forma înstrăinării:", "bunInstrainat5FormaInstrainarii");
        columnMapping.put("[instr5_val] Valoarea", "bunInstrainat5Valoarea");
        columnMapping.put("[instr5_mon] Moneda în care este exprimată valoarea:", "bunInstrainat5Moneda");

        // alte fututi dumnezeii matii psd si alde bunuri instrainate
        columnMapping.put("[maninstr] Te rugăm scrie rândurile rămase din tabel în caseta de mai jos. Separă categoriile cu \";\" și apasă Enter dupa fiecare rând din tabel.", "alteBunuriInstrainateFreeText");

        // cont 1
        columnMapping.put("[dAFcd] Există conturi și depozite listate în declarație?", "existaConturi1");
        columnMapping.put("[AFcd1_cui] În numele cui este deschis contul/depozitul?", "cont1Titular");
        columnMapping.put("[AFcd1_inst] Instituţia care administrează şi adresa acesteia:", "cont1Institutia");
        columnMapping.put("[AFcd1_tip] Tipul", "cont1Tip");
        columnMapping.put("[AFcd1_mon] Valuta:", "cont1Moneda");
        columnMapping.put("[AFcd1_an] Deschis în anul", "cont1AnDeschidere");
        columnMapping.put("[AFcd1_sold] Sold/valoare la zi", "cont1Sold");

        // cont 2
        columnMapping.put("[dAFcd1] Alte conturi/depozite?", "existaConturi2");
        columnMapping.put("[AFcd2_cui] În numele cui este deschis contul/depozitul?", "cont2Titular");
        columnMapping.put("[AFcd2_inst] Instituţia care administrează şi adresa acesteia:", "cont2Institutia");
        columnMapping.put("[AFcd2_tip] Tipul", "cont2Tip");
        columnMapping.put("[AFcd2_mon] Valuta:", "cont2Moneda");
        columnMapping.put("[AFcd2_an] Deschis în anul", "cont2AnDeschidere");
        columnMapping.put("[AFcd2_sold] Sold/valoare la zi", "cont2Sold");

        // cont 3
        columnMapping.put("[dAFcd2] Alte conturi/depozite?", "existaConturi3");
        columnMapping.put("[AFcd3_cui] În numele cui este deschis contul/depozitul?", "cont3Titular");
        columnMapping.put("[AFcd3_inst] Instituţia care administrează şi adresa acesteia:", "cont3Institutia");
        columnMapping.put("[AFcd3_tip] Tipul", "cont3Tip");
        columnMapping.put("[AFcd3_mon] Valuta:", "cont3Moneda");
        columnMapping.put("[AFcd3_an] Deschis în anul", "cont3AnDeschidere");
        columnMapping.put("[AFcd3_sold] Sold/valoare la zi", "cont3Sold");

        // cont 4
        columnMapping.put("[dAFcd3] Alte conturi/depozite?", "existaConturi4");
        columnMapping.put("[AFcd4_cui] În numele cui este deschis contul/depozitul?", "cont4Titular");
        columnMapping.put("[AFcd4_inst] Instituţia care administrează şi adresa acesteia:", "cont4Institutia");
        columnMapping.put("[AFcd4_tip] Tipul", "cont4Tip");
        columnMapping.put("[AFcd4_mon] Valuta:", "cont4Moneda");
        columnMapping.put("[AFcd4_an] Deschis în anul", "cont4AnDeschidere");
        columnMapping.put("[AFcd4_sold] Sold/valoare la zi", "cont4Sold");

        // cont 5
        columnMapping.put("[dAFcd4] Alte conturi/depozite?", "existaConturi5");
        columnMapping.put("[AFcd5_cui] În numele cui este deschis contul/depozitul?", "cont5Titular");
        columnMapping.put("[AFcd5_inst] Instituţia care administrează şi adresa acesteia:", "cont5Institutia");
        columnMapping.put("[AFcd5_tip] Tipul", "cont5Tip");
        columnMapping.put("[AFcd5_mon] Valuta:", "cont5Moneda");
        columnMapping.put("[AFcd5_an] Deschis în anul", "cont5AnDeschidere");
        columnMapping.put("[AFcd5_sold] Sold/valoare la zi", "cont5Sold");

        // cont 6
        columnMapping.put("[dAFcd5] Alte conturi/depozite?", "existaConturi6");
        columnMapping.put("[AFcd6_cui] În numele cui este deschis contul/depozitul?", "cont6Titular");
        columnMapping.put("[AFcd6_inst] Instituţia care administrează şi adresa acesteia:", "cont6Institutia");
        columnMapping.put("[AFcd6_tip] Tipul", "cont6Tip");
        columnMapping.put("[AFcd6_mon] Valuta:", "cont6Moneda");
        columnMapping.put("[AFcd6_an] Deschis în anul", "cont6AnDeschidere");
        columnMapping.put("[AFcd6_sold] Sold/valoare la zi", "cont6Sold");

        // cont 7
        columnMapping.put("[dAFcd6] Alte conturi/depozite?", "existaConturi7");
        columnMapping.put("[AFcd7_cui] În numele cui este deschis contul/depozitul?", "cont7Titular");
        columnMapping.put("[AFcd7_inst] Instituţia care administrează şi adresa acesteia:", "cont7Institutia");
        columnMapping.put("[AFcd7_tip] Tipul", "cont7Tip");
        columnMapping.put("[AFcd7_mon] Valuta:", "cont7Moneda");
        columnMapping.put("[AFcd7_an] Deschis în anul", "cont7AnDeschidere");
        columnMapping.put("[AFcd7_sold] Sold/valoare la zi", "cont7Sold");

        // cont 8
        columnMapping.put("[dAFcd7] Alte conturi/depozite?", "existaConturi8");
        columnMapping.put("[AFcd8_cui] În numele cui este deschis contul/depozitul?", "cont8Titular");
        columnMapping.put("[AFcd8_inst] Instituţia care administrează şi adresa acesteia:", "cont8Institutia");
        columnMapping.put("[AFcd8_tip] Tipul", "cont8Tip");
        columnMapping.put("[AFcd8_mon] Valuta:", "cont8Moneda");
        columnMapping.put("[AFcd8_an] Deschis în anul", "cont8AnDeschidere");
        columnMapping.put("[AFcd8_sold] Sold/valoare la zi", "cont8Sold");

        // cont 9
        columnMapping.put("[dAFcd8] Alte conturi/depozite?", "existaConturi9");
        columnMapping.put("[AFcd9_cui] În numele cui este deschis contul/depozitul?", "cont9Titular");
        columnMapping.put("[AFcd9_inst] Instituţia care administrează şi adresa acesteia:", "cont9Institutia");
        columnMapping.put("[AFcd9_tip] Tipul", "cont9Tip");
        columnMapping.put("[AFcd9_mon] Valuta:", "cont9Moneda");
        columnMapping.put("[AFcd9_an] Deschis în anul", "cont9AnDeschidere");
        columnMapping.put("[AFcd9_sold] Sold/valoare la zi", "cont9Sold");

        // cont 10
        columnMapping.put("[dAFcd9] Alte conturi/depozite?", "existaConturi10");
        columnMapping.put("[AFcd10_cui] În numele cui este deschis contul/depozitul?", "cont10Titular");
        columnMapping.put("[AFcd10_inst] Instituţia care administrează şi adresa acesteia:", "cont10Institutia");
        columnMapping.put("[AFcd10_tip] Tipul", "cont10Tip");
        columnMapping.put("[AFcd10_mon] Valuta:", "cont10Moneda");
        columnMapping.put("[AFcd10_an] Deschis în anul", "cont10AnDeschidere");
        columnMapping.put("[AFcd10_sold] Sold/valoare la zi", "cont10Sold");

        // alte conturi mai futu-ti dumnezeii matii baescu si iliescu si dragnea su constantinescu???
        // alte conturi free text
        columnMapping.put("[manAFcd] Te rugăm scrie rândurile rămase din tabel în caseta de mai jos. Separă categoriile cu \";\" și apasă Enter dupa fiecare rând din tabel.", "alteConturiFreeText");

        // plasament 1
        columnMapping.put("[dAFp] Există plasamente listate în declarație?", "existaPlasamente1");
        columnMapping.put("[AFp1_cui] În numele cui este făcut plasamentul/investiția?", "plasament1Titular");
        columnMapping.put("[AFp1_emit] Emitent titlu/societatea în care persoana este acţionar sau asociat/beneficiar de împrumut:", "plasament1Emitent");
        columnMapping.put("[AFp1_tip] Tipul:", "plasament1Tip");
        columnMapping.put("[AFp1_cot] Număr de titluri/ cota de participare:", "plasament1NumarTitluri");
        columnMapping.put("[AFp1_val] Valoarea totală la zi:", "plasament1Valoare");
        columnMapping.put("[AFp1_mon] Moneda în care este exprimată valoarea:", "plasament1Moneda");

        // plasament 2
        columnMapping.put("[dAFp1] Alte plasamente/împrumuturi?", "existaPlasamente2");
        columnMapping.put("[AFp2_cui] În numele cui este făcut plasamentul/investiția?", "plasament2Titular");
        columnMapping.put("[AFp2_emit] Emitent titlu/societatea în care persoana este acţionar sau asociat/beneficiar de împrumut:", "plasament2Emitent");
        columnMapping.put("[AFp2_tip] Tipul:", "plasament2Tip");
        columnMapping.put("[AFp2_cot] Număr de titluri/ cota de participare:", "plasament2NumarTitluri");
        columnMapping.put("[AFp2_val] Valoarea totală la zi:", "plasament2Valoare");
        columnMapping.put("[AFp2_mon] Moneda în care este exprimată valoarea:", "plasament2Moneda");

        // plasament 3
        columnMapping.put("[dAFp2] Alte plasamente/împrumuturi?", "existaPlasamente3");
        columnMapping.put("[AFp3_cui] În numele cui este făcut plasamentul/investiția?", "plasament3Titular");
        columnMapping.put("[AFp3_emit] Emitent titlu/societatea în care persoana este acţionar sau asociat/beneficiar de împrumut:", "plasament3Emitent");
        columnMapping.put("[AFp3_tip] Tipul:", "plasament3Tip");
        columnMapping.put("[AFp3_cot] Număr de titluri/ cota de participare:", "plasament3NumarTitluri");
        columnMapping.put("[AFp3_val] Valoarea totală la zi:", "plasament3Valoare");
        columnMapping.put("[AFp3_mon] Moneda în care este exprimată valoarea:", "plasament3Moneda");

        // plasament 4
        columnMapping.put("[dAFp3] Alte plasamente/împrumuturi?", "existaPlasamente4");
        columnMapping.put("[AFp4_cui] În numele cui este făcut plasamentul/investiția?", "plasament4Titular");
        columnMapping.put("[AFp4_emit] Emitent titlu/societatea în care persoana este acţionar sau asociat/beneficiar de împrumut:", "plasament4Emitent");
        columnMapping.put("[AFp4_tip] Tipul:", "plasament4Tip");
        columnMapping.put("[AFp4_cot] Număr de titluri/ cota de participare:", "plasament4NumarTitluri");
        columnMapping.put("[AFp4_val] Valoarea totală la zi:", "plasament4Valoare");
        columnMapping.put("[AFp4_mon] Moneda în care este exprimată valoarea:", "plasament4Moneda");

        // plasament 5
        columnMapping.put("[dAFp4] Alte plasamente/împrumuturi?", "existaPlasamente5");
        columnMapping.put("[AFp5_cui] În numele cui este făcut plasamentul/investiția?", "plasament5Titular");
        columnMapping.put("[AFp5_emit] Emitent titlu/societatea în care persoana este acţionar sau asociat/beneficiar de împrumut:", "plasament5Emitent");
        columnMapping.put("[AFp5_tip] Tipul:", "plasament5Tip");
        columnMapping.put("[AFp5_cot] Număr de titluri/ cota de participare:", "plasament5NumarTitluri");
        columnMapping.put("[AFp5_val] Valoarea totală la zi:", "plasament5Valoare");
        columnMapping.put("[AFp5_mon] Moneda în care este exprimată valoarea:", "plasament5Moneda");

        // alte plasamente (muie Firea, muie Dragnea)
        columnMapping.put("[manAFp] Te rugăm scrie rândurile rămase din tabel în caseta de mai jos. Separă categoriile cu \";\" și apasă Enter dupa fiecare rând din tabel.", "altePlasamenteFreeText");

        // alte active
        columnMapping.put("[AFaa] Alte active producătoare de venituri nete, care însumate depăşesc echivalentul a 5.000 de euro pe an:", "alteActiveFreeText");

        // datorie 1
        columnMapping.put("[dDat] Există datorii listate în declarație?", "existaDatorii1");
        columnMapping.put("[dat1_cred] Creditor:", "datorie1Creditor");
        columnMapping.put("[dat1_an] Contractat în anul:", "datorie1AnContractare");
        columnMapping.put("[dat1_scad] Scadent la:", "datorie1DataScadenta");
        columnMapping.put("[dat1_val] Valoare:", "datorie1Valoare");
        columnMapping.put("[dat1_mon] Moneda în care este exprimată valoarea:", "datorie1Moneda");

        // datorie 2
        columnMapping.put("[ddat1] Alte datorii?", "existaDatorii2");
        columnMapping.put("[dat2_cred] Creditor:", "datorie2Creditor");
        columnMapping.put("[dat2_an] Contractat în anul:", "datorie2AnContractare");
        columnMapping.put("[dat2_scad] Scadent la:", "datorie2DataScadenta");
        columnMapping.put("[dat2_val] Valoare:", "datorie2Valoare");
        columnMapping.put("[dat2_mon] Moneda în care este exprimată valoarea:", "datorie2Moneda");

        // datorie 3
        columnMapping.put("[ddat2] Alte datorii?", "existaDatorii3");
        columnMapping.put("[dat3_cred] Creditor:", "datorie3Creditor");
        columnMapping.put("[dat3_an] Contractat în anul:", "datorie3AnContractare");
        columnMapping.put("[dat3_scad] Scadent la:", "datorie3DataScadenta");
        columnMapping.put("[dat3_val] Valoare:", "datorie3Valoare");
        columnMapping.put("[dat3_mon] Moneda în care este exprimată valoarea:", "datorie3Moneda");

        // datorie 4
        columnMapping.put("[ddat3] Alte datorii?", "existaDatorii4");
        columnMapping.put("[dat4_cred] Creditor:", "datorie4Creditor");
        columnMapping.put("[dat4_an] Contractat în anul:", "datorie4AnContractare");
        columnMapping.put("[dat4_scad] Scadent la:", "datorie4DataScadenta");
        columnMapping.put("[dat4_val] Valoare:", "datorie4Valoare");
        columnMapping.put("[dat4_mon] Moneda în care este exprimată valoarea:", "datorie4Moneda");

        // datorie 5
        columnMapping.put("[ddat4] Alte datorii?", "existaDatorii5");
        columnMapping.put("[dat5_cred] Creditor:", "datorie5Creditor");
        columnMapping.put("[dat5_an] Contractat în anul:", "datorie5AnContractare");
        columnMapping.put("[dat5_scad] Scadent la:", "datorie5DataScadenta");
        columnMapping.put("[dat5_val] Valoare:", "datorie5Valoare");
        columnMapping.put("[dat5_mon] Moneda în care este exprimată valoarea:", "datorie5Moneda");

        // alte datorii free text (fuck the system!!!!! vreau democratie directa in Romania)
        columnMapping.put("[manDat] Te rugăm scrie rândurile rămase din tabel în caseta de mai jos. Separă categoriile cu \";\" și apasă Enter dupa fiecare rând din tabel.", "alteDatoriiFreeText");

        // cadou 1
        columnMapping.put("[dCad] Există cadouri listate în declarație?", "existaCadouri1");
        columnMapping.put("[cad1_cine] Cine a realizat venitul?", "cadou1Titular");
        columnMapping.put("[cad1_surs] Sursa venitului: numele, adresa", "cadou1SursaVenit");
        columnMapping.put("[cad1_serv] Serviciul prestat / Obiectul generator de venit:", "cadou1ServiciuPrestat");
        columnMapping.put("[cad1_ven] Venitul anual încasat", "cadou1VenitAnual");
        columnMapping.put("[cad1_mon] Moneda în care este exprimat venitul:", "cadou1Monenda");

        // cadou 2
        columnMapping.put("[dcad1] Alte cadouri?", "existaCadouri2");
        columnMapping.put("[cad2_cine] Cine a realizat venitul?", "cadou2Titular");
        columnMapping.put("[cad2_surs] Sursa venitului: numele, adresa", "cadou2SursaVenit");
        columnMapping.put("[cad2_serv] Serviciul prestat / Obiectul generator de venit:", "cadou2ServiciuPrestat");
        columnMapping.put("[cad2_ven] Venitul anual încasat", "cadou2VenitAnual");
        columnMapping.put("[cad2_mon] Moneda în care este exprimat venitul:", "cadou2Monenda");

        // alte cadouri free text
        columnMapping.put("[mancad] Te rugăm scrie rândurile rămase din tabel în caseta de mai jos. Separă categoriile cu \";\" și apasă Enter dupa fiecare rând din tabel.", "alteCadouriFreeText");

        // venit salar 1
        columnMapping.put("[dVenSal] Există venituri din salarii listate în declarație?", "existaVenituriSalarii1");
        columnMapping.put("[VenSal1_cine] Cine a realizat venitul?", "venitSalariu1Titular");
        columnMapping.put("[VenSal1_surs] Sursa venitului: numele, adresa", "venitSalariu1Sursa");
        columnMapping.put("[VenSal1_serv] Serviciul prestat / Obiectul generator de venit:", "venit1SalariuServiciPrestat");
        columnMapping.put("[VenSal1_ven] Venitul anual încasat:", "venit1SalariuVenitAnual");
        columnMapping.put("[VenSal1_mon] Moneda în care este exprimat venitul:", "venit1SalariuMonenda");

        // venit salar 2
        columnMapping.put("[dVenSal1] Alte salarii?", "existaVenituriSalarii2");
        columnMapping.put("[VenSal2_cine] Cine a realizat venitul?", "venitSalariu2Titular");
        columnMapping.put("[VenSal2_surs] Sursa venitului: numele, adresa", "venitSalariu2Sursa");
        columnMapping.put("[VenSal2_serv] Serviciul prestat / Obiectul generator de venit:", "venit2SalariuServiciPrestat");
        columnMapping.put("[VenSal2_ven] Venitul anual încasat:", "venit2SalariuVenitAnual");
        columnMapping.put("[VenSal2_mon] Moneda în care este exprimat venitul:", "venit2SalariuMonenda");

        // venit salar 3
        columnMapping.put("[dVenSal2] Alte salarii?", "existaVenituriSalarii3");
        columnMapping.put("[VenSal3_cine] Cine a realizat venitul?", "venitSalariu3Titular");
        columnMapping.put("[VenSal3_surs] Sursa venitului: numele, adresa", "venitSalariu3Sursa");
        columnMapping.put("[VenSal3_serv] Serviciul prestat / Obiectul generator de venit:", "venit3SalariuServiciPrestat");
        columnMapping.put("[VenSal3_ven] Venitul anual încasat:", "venit3SalariuVenitAnual");
        columnMapping.put("[VenSal3_mon] Moneda în care este exprimat venitul:", "venit3SalariuMonenda");

        // venit salar 4
        columnMapping.put("[dVenSal3] Alte salarii?", "existaVenituriSalarii4");
        columnMapping.put("[VenSal4_cine] Cine a realizat venitul?", "venitSalariu4Titular");
        columnMapping.put("[VenSal4_surs] Sursa venitului: numele, adresa", "venitSalariu4Sursa");
        columnMapping.put("[VenSal4_serv] Serviciul prestat / Obiectul generator de venit:", "venit4SalariuServiciPrestat");
        columnMapping.put("[VenSal4_ven] Venitul anual încasat:", "venit4SalariuVenitAnual");
        columnMapping.put("[VenSal4_mon] Moneda în care este exprimat venitul:", "venit4SalariuMonenda");

        // venit salar 5
        columnMapping.put("[dVenSal4] Alte salarii?", "existaVenituriSalarii5");
        columnMapping.put("[VenSal5_cine] Cine a realizat venitul?", "venitSalariu5Titular");
        columnMapping.put("[VenSal5_surs] Sursa venitului: numele, adresa", "venitSalariu5Sursa");
        columnMapping.put("[VenSal5_serv] Serviciul prestat / Obiectul generator de venit:", "venit5SalariuServiciPrestat");
        columnMapping.put("[VenSal5_ven] Venitul anual încasat:", "venit5SalariuVenitAnual");
        columnMapping.put("[VenSal5_mon] Moneda în care este exprimat venitul:", "venit5SalariuMonenda");

        // venit salar 6
        columnMapping.put("[dVenSal5] Alte salarii?", "existaVenituriSalarii6");
        columnMapping.put("[VenSal6_cine] Cine a realizat venitul?", "venitSalariu6Titular");
        columnMapping.put("[VenSal6_surs] Sursa venitului: numele, adresa", "venitSalariu6Sursa");
        columnMapping.put("[VenSal6_serv] Serviciul prestat / Obiectul generator de venit:", "venit6SalariuServiciPrestat");
        columnMapping.put("[VenSal6_ven] Venitul anual încasat:", "venit6SalariuVenitAnual");
        columnMapping.put("[VenSal6_mon] Moneda în care este exprimat venitul:", "venit6SalariuMonenda");

        // alte venituri salar free text
        columnMapping.put("[manVenSal] Te rugăm scrie rândurile rămase din tabel în caseta de mai jos. Separă categoriile cu \";\" și apasă Enter dupa fiecare rând din tabel.", "alteVenituriSalarFreeText");

        // venit activitati independente 1
        columnMapping.put("[dVenInd] Există venituri din activități independente listate în declarație?", "existaVenituriActivitatiIndependente1");
        columnMapping.put("[venInd1_cine] Cine a realizat venitul?", "venitActivitatiIndependente1Titular");
        columnMapping.put("[venInd1_sursa] Sursa venitului: numele, adresa", "venitActivitatiIndependente1Sursa");
        columnMapping.put("[venInd1_serv] Serviciul prestat / Obiectul generator de venit:", "venitActivitatiIndependente1ServiciPrestat");
        columnMapping.put("[venInd1_c] Venitul anual încasat:", "venitActivitatiIndependente1VenitAnual");
        columnMapping.put("[venInd1_mon] Moneda în care este exprimat venitul:", "venitActivitatiIndependente1Moneda");

        // venit activitati independente 2
        columnMapping.put("[dVenInd1] Alte venituri din activități independente?", "existaVenituriActivitatiIndependente2");
        columnMapping.put("[venInd2_cine] Cine a realizat venitul?", "venitActivitatiIndependente2Titular");
        columnMapping.put("[venInd2_sursa] Sursa venitului: numele, adresa", "venitActivitatiIndependente2Sursa");
        columnMapping.put("[venInd2_serv] Serviciul prestat / Obiectul generator de venit:", "venitActivitatiIndependente2ServiciPrestat");
        columnMapping.put("[venInd2_c] Venitul anual încasat:", "venitActivitatiIndependente2VenitAnual");
        columnMapping.put("[venInd2_mon] Moneda în care este exprimat venitul:", "venitActivitatiIndependente2Moneda");

        // venit activitati independente 3
        columnMapping.put("[dVenInd2] Alte venituri din activități independente?", "existaVenituriActivitatiIndependente3");
        columnMapping.put("[venInd3_cine] Cine a realizat venitul?", "venitActivitatiIndependente3Titular");
        columnMapping.put("[venInd3_sursa] Sursa venitului: numele, adresa", "venitActivitatiIndependente3Sursa");
        columnMapping.put("[venInd3_serv] Serviciul prestat / Obiectul generator de venit:", "venitActivitatiIndependente3ServiciPrestat");
        columnMapping.put("[venInd3_c] Venitul anual încasat:", "venitActivitatiIndependente3VenitAnual");
        columnMapping.put("[venInd3_mon] Moneda în care este exprimat venitul:", "venitActivitatiIndependente3Moneda");

        // venit activitati independente 4
        columnMapping.put("[dVenInd3] Alte venituri din activități independente?", "existaVenituriActivitatiIndependente4");
        columnMapping.put("[venInd4_cine] Cine a realizat venitul?", "venitActivitatiIndependente4Titular");
        columnMapping.put("[venInd4_sursa] Sursa venitului: numele, adresa", "venitActivitatiIndependente4Sursa");
        columnMapping.put("[venInd4_serv] Serviciul prestat / Obiectul generator de venit:", "venitActivitatiIndependente4ServiciPrestat");
        columnMapping.put("[venInd4_c] Venitul anual încasat:", "venitActivitatiIndependente4VenitAnual");
        columnMapping.put("[venInd4_mon] Moneda în care este exprimat venitul:", "venitActivitatiIndependente4Moneda");

        // alte venituri activitati independente
        columnMapping.put("[manVenInd] Te rugăm scrie rândurile rămase din tabel în caseta de mai jos. Separă categoriile cu \";\" și apasă Enter dupa fiecare rând din tabel.", "alteVenituriActivitatiIndependenteFreeText");

        // venit cedarea folosintei 1
        columnMapping.put("[dVenCed] Există venituri din cedarea folosinței bunurilor listate în declarație?", "existaVenituriCedareaFolosintei1");
        columnMapping.put("[venCed1_cine] Cine a realizat venitul?", "venitCedareaFolosintei1Titular");
        columnMapping.put("[venCed1_surs] Sursa venitului: numele, adresa", "venitCedareaFolosintei1Sursa");
        columnMapping.put("[venCed1_serv] Serviciul prestat / Obiectul generator de venit:", "venitCedareaFolosintei1ServiciPrestat");
        columnMapping.put("[venCed1_c] Venitul anual încasat:", "venitCedareaFolosintei1VenitAnual");
        columnMapping.put("[venCed1_mon] Moneda în care este exprimat venitul:", "venitCedareaFolosintei1Moneda");

        // venit cedarea folosintei 2
        columnMapping.put("[dVenCed1] Alte venituri din cedarea folosinţei bunurilor?", "existaVenituriCedareaFolosintei2");
        columnMapping.put("[venCed2_cine] Cine a realizat venitul?", "venitCedareaFolosintei2Titular");
        columnMapping.put("[venCed2_surs] Sursa venitului: numele, adresa", "venitCedareaFolosintei2Sursa");
        columnMapping.put("[venCed2_serv] Serviciul prestat / Obiectul generator de venit:", "venitCedareaFolosintei2ServiciPrestat");
        columnMapping.put("[venCed2_c] Venitul anual încasat:", "venitCedareaFolosintei2VenitAnual");
        columnMapping.put("[venCed2_mon] Moneda în care este exprimat venitul:", "venitCedareaFolosintei2Moneda");

        // venit cedarea folosintei 3
        columnMapping.put("[dVenCed2] Alte venituri din cedarea folosinţei bunurilor?", "existaVenituriCedareaFolosintei3");
        columnMapping.put("[venCed3_cine] Cine a realizat venitul?", "venitCedareaFolosintei3Titular");
        columnMapping.put("[venCed3_surs] Sursa venitului: numele, adresa", "venitCedareaFolosintei3Sursa");
        columnMapping.put("[venCed3_serv] Serviciul prestat / Obiectul generator de venit:", "venitCedareaFolosintei3ServiciPrestat");
        columnMapping.put("[venCed3_c] Venitul anual încasat:", "venitCedareaFolosintei3VenitAnual");
        columnMapping.put("[venCed3_mon] Moneda în care este exprimat venitul:", "venitCedareaFolosintei3Moneda");

        // venit cedarea folosintei 4
        columnMapping.put("[dVenCed3] Alte venituri din cedarea folosinţei bunurilor?", "existaVenituriCedareaFolosintei4");
        columnMapping.put("[venCed4_cine] Cine a realizat venitul?", "venitCedareaFolosintei4Titular");
        columnMapping.put("[venCed4_surs] Sursa venitului: numele, adresa", "venitCedareaFolosintei4Sursa");
        columnMapping.put("[venCed4_serv] Serviciul prestat / Obiectul generator de venit:", "venitCedareaFolosintei4ServiciPrestat");
        columnMapping.put("[venCed4_c] Venitul anual încasat:", "venitCedareaFolosintei4VenitAnual");
        columnMapping.put("[venCed4_mon] Moneda în care este exprimat venitul:", "venitCedareaFolosintei4Moneda");

        // venit cedarea folosintei 5
        columnMapping.put("[dVenCed4] Alte venituri din cedarea folosinţei bunurilor?", "existaVenituriCedareaFolosintei5");
        columnMapping.put("[venCed5_cine] Cine a realizat venitul?", "venitCedareaFolosintei5Titular");
        columnMapping.put("[venCed5_surs] Sursa venitului: numele, adresa", "venitCedareaFolosintei5Sursa");
        columnMapping.put("[venCed5_serv] Serviciul prestat / Obiectul generator de venit:", "venitCedareaFolosintei5ServiciPrestat");
        columnMapping.put("[venCed5_c] Venitul anual încasat:", "venitCedareaFolosintei5VenitAnual");
        columnMapping.put("[venCed5_mon] Moneda în care este exprimat venitul:", "venitCedareaFolosintei5Moneda");

        // venit cedarea folosintei 6
        columnMapping.put("[dVenCed5] Alte venituri din cedarea folosinţei bunurilor?", "existaVenituriCedareaFolosintei6");
        columnMapping.put("[venCed6_cine] Cine a realizat venitul?", "venitCedareaFolosintei6Titular");
        columnMapping.put("[venCed6_surs] Sursa venitului: numele, adresa", "venitCedareaFolosintei6Sursa");
        columnMapping.put("[venCed6_serv] Serviciul prestat / Obiectul generator de venit:", "venitCedareaFolosintei6ServiciPrestat");
        columnMapping.put("[venCed6_c] Venitul anual încasat:", "venitCedareaFolosintei6VenitAnual");
        columnMapping.put("[venCed6_mon] Moneda în care este exprimat venitul:", "venitCedareaFolosintei6Moneda");

        // alte venituri cedarea folosintei
        columnMapping.put("[manVenCed] Te rugăm scrie rândurile rămase din tabel în caseta de mai jos. Separă categoriile cu \";\" și apasă Enter dupa fiecare rând din tabel.", "alteVenituriCedareaFolosinteiFreeText");

        // venit investitii 1
        columnMapping.put("[dVenInv] Există venituri din investiții listate în declarație?", "existaVenituriInvestitii1");
        columnMapping.put("[venInv1_cine] Cine a realizat venitul?", "venitInvestitii1Titular");
        columnMapping.put("[venInv1_sursa] Sursa venitului: numele, adresa", "venitInvestitii1Sursa");
        columnMapping.put("[venInv1_serv] Serviciul prestat / Obiectul generator de venit:", "venitInvestitii1ServiciPrestat");
        columnMapping.put("[venInv1_c] Venitul anual încasat:", "venitInvestitii1VenitAnual");
        columnMapping.put("[venInv1_mon] Moneda în care este exprimat venitul:", "venitInvestitii1Moneda");

        // venit investitii 2
        columnMapping.put("[dVenInv1] Alte venituri din investiții?", "existaVenituriInvestitii2");
        columnMapping.put("[venInv2_cine] Cine a realizat venitul?", "venitInvestitii2Titular");
        columnMapping.put("[venInv2_sursa] Sursa venitului: numele, adresa", "venitInvestitii2Sursa");
        columnMapping.put("[venInv2_serv] Serviciul prestat / Obiectul generator de venit:", "venitInvestitii2ServiciPrestat");
        columnMapping.put("[venInv2_c] Venitul anual încasat:", "venitInvestitii2VenitAnual");
        columnMapping.put("[venInv2_mon] Moneda în care este exprimat venitul:", "venitInvestitii2Moneda");

        // venit investitii 3
        columnMapping.put("[dVenInv2] Alte venituri din investiții?", "existaVenituriInvestitii3");
        columnMapping.put("[venInv3_cine] Cine a realizat venitul?", "venitInvestitii3Titular");
        columnMapping.put("[venInv3_sursa] Sursa venitului: numele, adresa", "venitInvestitii3Sursa");
        columnMapping.put("[venInv3_serv] Serviciul prestat / Obiectul generator de venit:", "venitInvestitii3ServiciPrestat");
        columnMapping.put("[venInv3_c] Venitul anual încasat:", "venitInvestitii3VenitAnual");
        columnMapping.put("[venInv3_mon] Moneda în care este exprimat venitul:", "venitInvestitii3Moneda");

        // venit investitii 4
        columnMapping.put("[dVenInv3] Alte venituri din investiții?", "existaVenituriInvestitii4");
        columnMapping.put("[venInv4_cine] Cine a realizat venitul?", "venitInvestitii4Titular");
        columnMapping.put("[venInv4_sursa] Sursa venitului: numele, adresa", "venitInvestitii4Sursa");
        columnMapping.put("[venInv4_serv] Serviciul prestat / Obiectul generator de venit:", "venitInvestitii4ServiciPrestat");
        columnMapping.put("[venInv4_c] Venitul anual încasat:", "venitInvestitii4VenitAnual");
        columnMapping.put("[venInv4_mon] Moneda în care este exprimat venitul:", "venitInvestitii4Moneda");

        // alte venituri din investitii (MUIE BIRCHALL, MUIE CIORDACHE, MUIE TOADER SAU CUM PULA MEA IL CHEAMA)
        columnMapping.put("[manVenInv] Te rugăm scrie rândurile rămase din tabel în caseta de mai jos. Separă categoriile cu \";\" și apasă Enter dupa fiecare rând din tabel.", "alteVenituriInvestitiiFreeText");

        // venituri pensii 1
        columnMapping.put("[dVenPen] Există venituri din pensii listate în declarație?", "existaVenituriPensii1");
        columnMapping.put("[venPen1_cine] Cine a realizat venitul?", "venitPensii1Titular");
        columnMapping.put("[venPen1_surs] Sursa venitului: numele, adresa", "venitPensii1Sursa");
        columnMapping.put("[venPen1_serv] Serviciul prestat / Obiectul generator de venit:", "venitPensii1ServiciPrestat");
        columnMapping.put("[venPen1_c] Venitul anual încasat:", "venitPensii1VenitAnual");
        columnMapping.put("[venPen1_mon] Moneda în care este exprimat venitul:", "venitPensii1Moneda");

        // venituri pensii 2
        columnMapping.put("[dVenPen1] Alte pensii?", "existaVenituriPensii2");
        columnMapping.put("[venPen2_cine] Cine a realizat venitul?", "venitPensii2Titular");
        columnMapping.put("[venPen2_surs] Sursa venitului: numele, adresa", "venitPensii2Sursa");
        columnMapping.put("[venPen2_serv] Serviciul prestat / Obiectul generator de venit:", "venitPensii2ServiciPrestat");
        columnMapping.put("[venPen2_c] Venitul anual încasat:", "venitPensii2VenitAnual");
        columnMapping.put("[venPen2_mon] Moneda în care este exprimat venitul:", "venitPensii2Moneda");

        // alte venituri pensii
        columnMapping.put("[manVenPen] Te rugăm scrie rândurile rămase din tabel în caseta de mai jos. Separă categoriile cu \";\" și apasă Enter dupa fiecare rând din tabel.", "alteVenituriPensiiFreeText");

        // venituri agricole 1
        columnMapping.put("[dVenAgr] Există venituri din activități agricole listate în declarație?", "existaVenituriAgricole1");
        columnMapping.put("[venAgr1_cine] Cine a realizat venitul?", "venitAgricol1Titular");
        columnMapping.put("[venAgr1_surs] Sursa venitului: numele, adresa", "venitAgricol1Sursa");
        columnMapping.put("[venAgr1_serv] Serviciul prestat / Obiectul generator de venit:", "venitAgricol1ServiciPrestat");
        columnMapping.put("[venAgr1_c] Venitul anual încasat:", "venitAgricol1VenitAnual");
        columnMapping.put("[venAgr1_mon] Moneda în care este exprimat venitul:", "venitAgricol1Moneda");

        // venituri agricole 2
        columnMapping.put("[dVenAgr1] Alte venituri din activităţi agricole?", "existaVenituriAgricole2");
        columnMapping.put("[venAgr2_cine] Cine a realizat venitul?", "venitAgricol2Titular");
        columnMapping.put("[venAgr2_surs] Sursa venitului: numele, adresa", "venitAgricol2Sursa");
        columnMapping.put("[venAgr2_serv] Serviciul prestat / Obiectul generator de venit:", "venitAgricol2ServiciPrestat");
        columnMapping.put("[venAgr2_c] Venitul anual încasat:", "venitAgricol2VenitAnual");
        columnMapping.put("[venAgr2_mon] Moneda în care este exprimat venitul:", "venitAgricol2Moneda");

        // alte venituri agricole
        columnMapping.put("[manVenAgr] Te rugăm scrie rândurile rămase din tabel în caseta de mai jos. Separă categoriile cu \";\" și apasă Enter dupa fiecare rând din tabel.", "alteVenituriAgricoleFreeText");

        // venituri noroc 1
        columnMapping.put("[dVenNor] Există venituri din premii și jocuri de noroc listate în declarație?", "existaVenituriNoroc1");
        columnMapping.put("[venNor1_cine] Cine a realizat venitul?\t[venNor1_nume] Numele soțului/soției/copiilor:", "venitNoroc1Titular");
        columnMapping.put("[venNor1_surs] Sursa venitului: numele, adresa", "venitNoroc1Sursa");
        columnMapping.put("[venNor1_serv] Serviciul prestat / Obiectul generator de venit:", "venitNoroc1ServiciPrestat");
        columnMapping.put("[venNor1_c] Venitul anual încasat:", "venitNoroc1VenitAnual");
        columnMapping.put("[venNor1_mon] Moneda în care este exprimat venitul:", "venitNoroc1Moneda");

        // venituri noroc 2
        columnMapping.put("[dVenNor1] Alte venituri din premii şi din jocuri de noroc?", "existaVenituriNoroc2");
        columnMapping.put("[venNor2_cine] Cine a realizat venitul?\t[venNor2_nume] Numele soțului/soției/copiilor:", "venitNoroc2Titular");
        columnMapping.put("[venNor2_surs] Sursa venitului: numele, adresa", "venitNoroc2Sursa");
        columnMapping.put("[venNor2_serv] Serviciul prestat / Obiectul generator de venit:", "venitNoroc2ServiciPrestat");
        columnMapping.put("[venNor2_c] Venitul anual încasat:", "venitNoroc2VenitAnual");
        columnMapping.put("[venNor2_mon] Moneda în care este exprimat venitul:", "venitNoroc2Moneda");
        
        // alte venituri noroc
        columnMapping.put("[manVenNor] Te rugăm scrie rândurile rămase din tabel în caseta de mai jos. Separă categoriile cu \";\" și apasă Enter dupa fiecare rând din tabel.", "alteVenituriNorocFreeText");

        // alte venituri 1 (muie Rovana Plumb)
        columnMapping.put("[dVenAlt] Există venituri din alte surse listate în declarație?", "existaVenituriAlte1");
        columnMapping.put("[venAlt1_cine] Cine a realizat venitul?", "venitAlte1Titular");
        columnMapping.put("[venAlt1_surs] Sursa venitului: numele, adresa", "venitAlte1Sursa");
        columnMapping.put("[venAlt1_serv] Serviciul prestat / Obiectul generator de venit:", "venitAlte1ServiciPrestat");
        columnMapping.put("[venAlt1_c] Venitul anual încasat:", "veniAlte1VenitAnual");
        columnMapping.put("[venAlt1_mon] Moneda în care este exprimat venitul:", "venitAlte1Moneda");

        // alte venituri 2 (muie Csep)
        columnMapping.put("[[dVenAlt1] Alte venituri?", "existaVenituriAlte2");
        columnMapping.put("[venAlt2_cine] Cine a realizat venitul?", "venitAlte2Titular");
        columnMapping.put("[venAlt2_surs] Sursa venitului: numele, adresa", "venitAlte2Sursa");
        columnMapping.put("[venAlt2_serv] Serviciul prestat / Obiectul generator de venit:", "venitAlte2ServiciPrestat");
        columnMapping.put("[venAlt2_c] Venitul anual încasat:", "veniAlte2VenitAnual");
        columnMapping.put("[venAlt2_mon] Moneda în care este exprimat venitul:", "venitAlte2Moneda");

        // alte venituri 3 
        columnMapping.put("[[dVenAlt2] Alte venituri?", "existaVenituriAlte3");
        columnMapping.put("[venAlt3_cine] Cine a realizat venitul?", "venitAlte3Titular");
        columnMapping.put("[venAlt3_surs] Sursa venitului: numele, adresa", "venitAlte3Sursa");
        columnMapping.put("[venAlt3_serv] Serviciul prestat / Obiectul generator de venit:", "venitAlte3ServiciPrestat");
        columnMapping.put("[venAlt3_c] Venitul anual încasat:", "veniAlte3VenitAnual");
        columnMapping.put("[venAlt3_mon] Moneda în care este exprimat venitul:", "venitAlte3Moneda");

        // alte venituri 4 
        columnMapping.put("[[dVenAlt3] Alte venituri?", "existaVenituriAlte4");
        columnMapping.put("[venAlt4_cine] Cine a realizat venitul?", "venitAlte4Titular");
        columnMapping.put("[venAlt4_surs] Sursa venitului: numele, adresa", "venitAlte4Sursa");
        columnMapping.put("[venAlt4_serv] Serviciul prestat / Obiectul generator de venit:", "venitAlte4ServiciPrestat");
        columnMapping.put("[venAlt4_c] Venitul anual încasat:", "veniAlte4VenitAnual");
        columnMapping.put("[venAlt4_mon] Moneda în care este exprimat venitul:", "venitAlte4Moneda");
        
        // alte venituri din muie
        columnMapping.put("[manVenAlt] Te rugăm scrie rândurile rămase din tabel în caseta de mai jos. Separă categoriile cu \";\" și apasă Enter dupa fiecare rând din tabel.", "alteVenituriAlteFreeText");
        return columnMapping;
    }
}
