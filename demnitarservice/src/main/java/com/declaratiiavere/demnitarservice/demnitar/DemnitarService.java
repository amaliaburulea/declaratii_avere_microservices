package com.declaratiiavere.demnitarservice.demnitar;

import com.declaratiiavere.common.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.util.*;

/**
 * Demnitar service.
 *
 * @author Razvan Dani
 */
@Service
public class DemnitarService {
    @Autowired
    private DemnitarEAO demnitarEAO;

    private DemnitarInfo getDemnitarInfo(DemnitarEntity demnitarEntity) {
        DemnitarInfo demnitarInfo = new DemnitarInfo();
        demnitarInfo.setId(demnitarEntity.getId());
        demnitarInfo.setNume(demnitarEntity.getNume());
        demnitarInfo.setPrenume(demnitarEntity.getPrenume());

        return demnitarInfo;
    }

    /**
     * Saves a demnitar.
     *
     * @param demnitarInfo The DemnitarInfo
     * @return The saved DemnitarInfo
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public DemnitarInfo saveDemnitar(DemnitarInfo demnitarInfo) {
        validateDemnitar(demnitarInfo);

        DemnitarEntity demnitarEntity = populateDemnitarEntity(demnitarInfo);

        return getDemnitarInfo(demnitarEAO.saveDemnitar(demnitarEntity));
    }

    private void validateDemnitar(DemnitarInfo demnitarInfo) {
        if (Utilities.isEmptyOrNull(demnitarInfo.getNume())) {
            throw new ValidationException("nume este obligatoriu");
        }

        if (Utilities.isEmptyOrNull(demnitarInfo.getPrenume())) {
            throw new ValidationException("prenume este obligatoriu");
        }
    }

    private DemnitarEntity populateDemnitarEntity(DemnitarInfo demnitarInfo) {
        DemnitarEntity demnitarEntity;

        if (demnitarInfo.getId() != null) {
            demnitarEntity = demnitarEAO.getDemnitar(demnitarInfo.getId());

            if (demnitarEntity == null) {
                throw new ValidationException("Demnitar does not exist");
            }
        } else {
            demnitarEntity = new DemnitarEntity();
        }

        demnitarEntity.setNume(demnitarInfo.getNume());
        demnitarEntity.setPrenume(demnitarInfo.getPrenume());

        return demnitarEntity;
    }

    /**
     * Gets a demnitar.
     *
     * @param id The demnitar id
     * @return The DemnitarInfo
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED, rollbackFor = Exception.class)
    public DemnitarInfo getDemnitar(Integer id) {
        if (id == null) {
            throw new ValidationException("id is required");
        }

        DemnitarEntity demnitarEntity = demnitarEAO.getDemnitar(id);

        if (demnitarEntity == null) {
            throw new ValidationException("demnitar does not exist");
        }

        return getDemnitarInfo(demnitarEntity);
    }

    /**
     * Finds demnitars by the specified search criteria.
     *
     * @param searchDemnitarCriteria The SearchDemnitarCriteria object
     * @return The List of DemnitarInfo objects
     */
    @Transactional(readOnly = true)
    public List<DemnitarInfo> findDemnitars(SearchDemnitarCriteria searchDemnitarCriteria) {
        List<DemnitarInfo> demnitarInfoList = new ArrayList<>();

        DemnitarEntitySearchCriteria demnitarEntitySearchCriteria = new DemnitarEntitySearchCriteria();
        demnitarEntitySearchCriteria.setNume(searchDemnitarCriteria.getNume());
        demnitarEntitySearchCriteria.setPrenume(searchDemnitarCriteria.getPrenume());

        List<DemnitarEntity> demnitarEntityList = demnitarEAO.findDemnitars(demnitarEntitySearchCriteria);

        for (DemnitarEntity demnitarEntity : demnitarEntityList) {
            demnitarInfoList.add(getDemnitarInfo(demnitarEntity));
        }

        return demnitarInfoList;
    }

    private DeclaratieAvereInfo getDeclaratieAvereInfo(DeclaratieAvereEntity declaratieAvereEntity) {
        DeclaratieAvereInfo declaratieAvereInfo = new DeclaratieAvereInfo();
        declaratieAvereInfo.setId(declaratieAvereEntity.getId());
        declaratieAvereInfo.setDemnitarId(declaratieAvereEntity.getDemnitarId());

        if (declaratieAvereEntity.getDemnitarEntity() != null) {
            declaratieAvereInfo.setDemnitarNume(declaratieAvereEntity.getDemnitarEntity().getNume());
            declaratieAvereInfo.setDemnitarPrenume(declaratieAvereEntity.getDemnitarEntity().getPrenume());
        }

        declaratieAvereInfo.setDataDeclaratiei(declaratieAvereEntity.getDataDeclaratiei());
        declaratieAvereInfo.setFunctie(declaratieAvereEntity.getFunctie());
        declaratieAvereInfo.setFunctie2(declaratieAvereEntity.getFunctie2());
        declaratieAvereInfo.setInstitutie(declaratieAvereEntity.getInstitutie());
        declaratieAvereInfo.setInstitutie2(declaratieAvereEntity.getInstitutie2());

        List<DeclaratieAvereAlteActiveInfo> declaratieActiveAlteActiveInfoList = new ArrayList<>();

        if (declaratieAvereEntity.getDeclaratieAvereAlteActiveEntitySet() != null) {
            for (DeclaratieAvereAlteActiveEntity declaratieAvereAlteActiveEntity : declaratieAvereEntity.getDeclaratieAvereAlteActiveEntitySet()) {
                DeclaratieAvereAlteActiveInfo declaratieActiveAlteActiveInfo = getDeclaratieActiveAlteActiveInfo(declaratieAvereAlteActiveEntity);
                declaratieActiveAlteActiveInfoList.add(declaratieActiveAlteActiveInfo);
            }
        }

        declaratieAvereInfo.setDeclaratieAvereAlteActiveInfoList(declaratieActiveAlteActiveInfoList);

        List<DeclaratieAvereBunImobilInfo> declaratieActiveBunImobilInfoList = new ArrayList<>();

        if (declaratieAvereEntity.getDeclaratieAvereBunImobilEntitySet() != null) {
            for (DeclaratieAvereBunImobilEntity declaratieAvereBunImobilEntity : declaratieAvereEntity.getDeclaratieAvereBunImobilEntitySet()) {
                DeclaratieAvereBunImobilInfo declaratieActiveBunImobilInfo = getDeclaratieActiveBunImobilInfo(declaratieAvereBunImobilEntity);
                declaratieActiveBunImobilInfoList.add(declaratieActiveBunImobilInfo);
            }
        }

        declaratieAvereInfo.setDeclaratieAvereBunImobilInfoList(declaratieActiveBunImobilInfoList);

        List<DeclaratieAvereBunMobilInfo> declaratieActiveBunMobilInfoList = new ArrayList<>();

        if (declaratieAvereEntity.getDeclaratieAvereBunMobilEntitySet() != null) {
            for (DeclaratieAvereBunMobilEntity declaratieAvereBunMobilEntity : declaratieAvereEntity.getDeclaratieAvereBunMobilEntitySet()) {
                DeclaratieAvereBunMobilInfo declaratieActiveBunMobilInfo = getDeclaratieActiveBunMobilInfo(declaratieAvereBunMobilEntity);
                declaratieActiveBunMobilInfoList.add(declaratieActiveBunMobilInfo);
            }
        }

        declaratieAvereInfo.setDeclaratieAvereBunMobilInfoList(declaratieActiveBunMobilInfoList);

        List<DeclaratieAvereBijuterieInfo> declaratieActiveBijuterieInfoList = new ArrayList<>();

        if (declaratieAvereEntity.getDeclaratieAvereBijuterieEntitySet() != null) {
            for (DeclaratieAvereBijuterieEntity declaratieAvereBijuterieEntity : declaratieAvereEntity.getDeclaratieAvereBijuterieEntitySet()) {
                DeclaratieAvereBijuterieInfo declaratieActiveBijuterieInfo = getDeclaratieActiveBijuterieInfo(declaratieAvereBijuterieEntity);
                declaratieActiveBijuterieInfoList.add(declaratieActiveBijuterieInfo);
            }
        }

        declaratieAvereInfo.setDeclaratieAvereBijuterieInfoList(declaratieActiveBijuterieInfoList);

        List<DeclaratieAverePlasamentInfo> declaratieActivePlasamentInfoList = new ArrayList<>();

        if (declaratieAvereEntity.getDeclaratieAverePlasamentEntitySet() != null) {
            for (DeclaratieAverePlasamentEntity declaratieAverePlasamentEntity : declaratieAvereEntity.getDeclaratieAverePlasamentEntitySet()) {
                DeclaratieAverePlasamentInfo declaratieActivePlasamentInfo = getDeclaratieActivePlasamentInfo(declaratieAverePlasamentEntity);
                declaratieActivePlasamentInfoList.add(declaratieActivePlasamentInfo);
            }
        }

        declaratieAvereInfo.setDeclaratieAverePlasamentInfoList(declaratieActivePlasamentInfoList);

        List<DeclaratieAvereDatorieInfo> declaratieActiveDatorieInfoList = new ArrayList<>();

        if (declaratieAvereEntity.getDeclaratieAvereDatorieEntitySet() != null) {
            for (DeclaratieAvereDatorieEntity declaratieAvereDatorieEntity : declaratieAvereEntity.getDeclaratieAvereDatorieEntitySet()) {
                DeclaratieAvereDatorieInfo declaratieActiveDatorieInfo = getDeclaratieActiveDatorieInfo(declaratieAvereDatorieEntity);
                declaratieActiveDatorieInfoList.add(declaratieActiveDatorieInfo);
            }
        }

        declaratieAvereInfo.setDeclaratieAvereDatorieInfoList(declaratieActiveDatorieInfoList);

        List<DeclaratieAvereContInfo> declaratieActiveContInfoList = new ArrayList<>();

        if (declaratieAvereEntity.getDeclaratieAvereContEntitySet() != null) {
            for (DeclaratieAvereContEntity declaratieAvereContEntity : declaratieAvereEntity.getDeclaratieAvereContEntitySet()) {
                DeclaratieAvereContInfo declaratieActiveContInfo = getDeclaratieActiveContInfo(declaratieAvereContEntity);
                declaratieActiveContInfoList.add(declaratieActiveContInfo);
            }
        }

        declaratieAvereInfo.setDeclaratieAvereContInfoList(declaratieActiveContInfoList);

        List<DeclaratieAvereBunInstrainatInfo> declaratieActiveBunInstrainatInfoList = new ArrayList<>();

        if (declaratieAvereEntity.getDeclaratieAvereBunInstrainatEntitySet() != null) {
            for (DeclaratieAvereBunInstrainatEntity declaratieAvereBunInstrainatEntity : declaratieAvereEntity.getDeclaratieAvereBunInstrainatEntitySet()) {
                DeclaratieAvereBunInstrainatInfo declaratieActiveBunInstrainatInfo = getDeclaratieActiveBunInstrainatInfo(declaratieAvereBunInstrainatEntity);
                declaratieActiveBunInstrainatInfoList.add(declaratieActiveBunInstrainatInfo);
            }
        }

        declaratieAvereInfo.setDeclaratieAvereBunInstrainatInfoList(declaratieActiveBunInstrainatInfoList);

        List<DeclaratieAvereCadouInfo> declaratieActiveCadouInfoList = new ArrayList<>();

        if (declaratieAvereEntity.getDeclaratieAvereCadouEntitySet() != null) {
            for (DeclaratieAvereCadouEntity declaratieAvereCadouEntity : declaratieAvereEntity.getDeclaratieAvereCadouEntitySet()) {
                DeclaratieAvereCadouInfo declaratieActiveCadouInfo = getDeclaratieActiveCadouInfo(declaratieAvereCadouEntity);
                declaratieActiveCadouInfoList.add(declaratieActiveCadouInfo);
            }
        }

        declaratieAvereInfo.setDeclaratieAvereCadouInfoList(declaratieActiveCadouInfoList);

        List<DeclaratieAvereVenitInfo> declaratieActiveVenitInfoList = new ArrayList<>();

        if (declaratieAvereEntity.getDeclaratieAvereVenitEntitySet() != null) {
            for (DeclaratieAvereVenitEntity declaratieAvereVenitEntity : declaratieAvereEntity.getDeclaratieAvereVenitEntitySet()) {
                DeclaratieAvereVenitInfo declaratieActiveVenitInfo = getDeclaratieActiveVenitInfo(declaratieAvereVenitEntity);
                declaratieActiveVenitInfoList.add(declaratieActiveVenitInfo);
            }
        }

        declaratieAvereInfo.setDeclaratieAvereVenitInfoList(declaratieActiveVenitInfoList);

        return declaratieAvereInfo;
    }

    private DeclaratieAvereBunImobilInfo getDeclaratieActiveBunImobilInfo(DeclaratieAvereBunImobilEntity declaratieAvereBunImobilEntity) {
        DeclaratieAvereBunImobilInfo declaratieActiveBunImobilInfo = new DeclaratieAvereBunImobilInfo();
        declaratieActiveBunImobilInfo.setId(declaratieAvereBunImobilEntity.getId());
        declaratieActiveBunImobilInfo.setIsTeren(declaratieAvereBunImobilEntity.getIsTeren());
        declaratieActiveBunImobilInfo.setAdresaImobil(declaratieAvereBunImobilEntity.getAdresaImobil());
        declaratieActiveBunImobilInfo.setTerenCategorie(declaratieAvereBunImobilEntity.getTerenCategorie());
        declaratieActiveBunImobilInfo.setCladireCategorie(declaratieAvereBunImobilEntity.getCladireCategorie());
        declaratieActiveBunImobilInfo.setAnDobandire(declaratieAvereBunImobilEntity.getAnDobandire());
        declaratieActiveBunImobilInfo.setSuprafata(declaratieAvereBunImobilEntity.getSuprafata());
        declaratieActiveBunImobilInfo.setUnitateMasura(declaratieAvereBunImobilEntity.getUnitateMasura());
        declaratieActiveBunImobilInfo.setCotaParte(declaratieAvereBunImobilEntity.getCotaParte());
        declaratieActiveBunImobilInfo.setModDobandire(declaratieAvereBunImobilEntity.getModDobandire());
        declaratieActiveBunImobilInfo.setTitular(declaratieAvereBunImobilEntity.getTitular());

        return declaratieActiveBunImobilInfo;
    }

    private DeclaratieAvereBunMobilInfo getDeclaratieActiveBunMobilInfo(DeclaratieAvereBunMobilEntity declaratieAvereBunMobilEntity) {
        DeclaratieAvereBunMobilInfo declaratieActiveBunMobilInfo = new DeclaratieAvereBunMobilInfo();
        declaratieActiveBunMobilInfo.setId(declaratieAvereBunMobilEntity.getId());
        declaratieActiveBunMobilInfo.setTip(declaratieAvereBunMobilEntity.getTip());
        declaratieActiveBunMobilInfo.setMarca(declaratieAvereBunMobilEntity.getMarca());
        declaratieActiveBunMobilInfo.setCantitate(declaratieAvereBunMobilEntity.getCantitate());
        declaratieActiveBunMobilInfo.setAnFabricare(declaratieAvereBunMobilEntity.getAnFabricare());
        declaratieActiveBunMobilInfo.setModDobandire(declaratieAvereBunMobilEntity.getModDobandire());

        return declaratieActiveBunMobilInfo;
    }

    private DeclaratieAvereBunInstrainatInfo getDeclaratieActiveBunInstrainatInfo(DeclaratieAvereBunInstrainatEntity declaratieAvereBunInstrainatEntity) {
        DeclaratieAvereBunInstrainatInfo declaratieActiveBunInstrainatInfo = new DeclaratieAvereBunInstrainatInfo();
        declaratieActiveBunInstrainatInfo.setId(declaratieAvereBunInstrainatEntity.getId());
        declaratieActiveBunInstrainatInfo.setTip(declaratieAvereBunInstrainatEntity.getTip());
        declaratieActiveBunInstrainatInfo.setIsImobil(declaratieAvereBunInstrainatEntity.getIsImobil());
        declaratieActiveBunInstrainatInfo.setMarca(declaratieAvereBunInstrainatEntity.getMarca());
        declaratieActiveBunInstrainatInfo.setDataInstrainarii(declaratieAvereBunInstrainatEntity.getDataInstrainarii());
        declaratieActiveBunInstrainatInfo.setPersoanaBeneficiara(declaratieAvereBunInstrainatEntity.getPersoanaBeneficiara());
        declaratieActiveBunInstrainatInfo.setFormaInstrainarii(declaratieAvereBunInstrainatEntity.getFormaInstrainarii());
        declaratieActiveBunInstrainatInfo.setValoarea(declaratieAvereBunInstrainatEntity.getValoarea());
        declaratieActiveBunInstrainatInfo.setMoneda(declaratieAvereBunInstrainatEntity.getMoneda());

        return declaratieActiveBunInstrainatInfo;
    }

    private DeclaratieAvereCadouInfo getDeclaratieActiveCadouInfo(DeclaratieAvereCadouEntity declaratieAvereCadouEntity) {
        DeclaratieAvereCadouInfo declaratieActiveCadouInfo = new DeclaratieAvereCadouInfo();
        declaratieActiveCadouInfo.setId(declaratieAvereCadouEntity.getId());
        declaratieActiveCadouInfo.setTitular(declaratieAvereCadouEntity.getTitular());
        declaratieActiveCadouInfo.setSursaVenit(declaratieAvereCadouEntity.getSursaVenit());
        declaratieActiveCadouInfo.setServiciulPrestat(declaratieAvereCadouEntity.getServiciulPrestat());
        declaratieActiveCadouInfo.setVenit(declaratieAvereCadouEntity.getVenit());
        declaratieActiveCadouInfo.setMoneda(declaratieAvereCadouEntity.getMoneda());

        return declaratieActiveCadouInfo;
    }

    private DeclaratieAvereVenitInfo getDeclaratieActiveVenitInfo(DeclaratieAvereVenitEntity declaratieAvereVenitEntity) {
        DeclaratieAvereVenitInfo declaratieActiveVenitInfo = new DeclaratieAvereVenitInfo();
        declaratieActiveVenitInfo.setId(declaratieAvereVenitEntity.getId());
        declaratieActiveVenitInfo.setTip(declaratieAvereVenitEntity.getTip());
        declaratieActiveVenitInfo.setTitular(declaratieAvereVenitEntity.getTitular());
        declaratieActiveVenitInfo.setSursaVenit(declaratieAvereVenitEntity.getSursaVenit());
        declaratieActiveVenitInfo.setServiciulPrestat(declaratieAvereVenitEntity.getServiciulPrestat());
        declaratieActiveVenitInfo.setVenitAnual(declaratieAvereVenitEntity.getVenitAnual());
        declaratieActiveVenitInfo.setMoneda(declaratieAvereVenitEntity.getMoneda());

        return declaratieActiveVenitInfo;
    }

    private DeclaratieAvereBijuterieInfo getDeclaratieActiveBijuterieInfo(DeclaratieAvereBijuterieEntity declaratieAvereBijuterieEntity) {
        DeclaratieAvereBijuterieInfo declaratieActiveBijuterieInfo = new DeclaratieAvereBijuterieInfo();
        declaratieActiveBijuterieInfo.setId(declaratieAvereBijuterieEntity.getId());
        declaratieActiveBijuterieInfo.setDescriere(declaratieAvereBijuterieEntity.getDescriere());
        declaratieActiveBijuterieInfo.setAnDobandire(declaratieAvereBijuterieEntity.getAnDobandire());
        declaratieActiveBijuterieInfo.setValoareEstimate(declaratieAvereBijuterieEntity.getValoareEstimate());
        declaratieActiveBijuterieInfo.setMoneda(declaratieAvereBijuterieEntity.getMoneda());

        return declaratieActiveBijuterieInfo;
    }

    private DeclaratieAverePlasamentInfo getDeclaratieActivePlasamentInfo(DeclaratieAverePlasamentEntity declaratieAverePlasamentEntity) {
        DeclaratieAverePlasamentInfo declaratieActivePlasamentInfo = new DeclaratieAverePlasamentInfo();
        declaratieActivePlasamentInfo.setId(declaratieAverePlasamentEntity.getId());
        declaratieActivePlasamentInfo.setTitular(declaratieAverePlasamentEntity.getTitular());
        declaratieActivePlasamentInfo.setEmitentTitlu(declaratieAverePlasamentEntity.getEmitentTitlu());
        declaratieActivePlasamentInfo.setTipulPlasamentului(declaratieAverePlasamentEntity.getTipulPlasamentului());
        declaratieActivePlasamentInfo.setNumarTitluriSauCotaParte(declaratieAverePlasamentEntity.getNumarTitluriSauCotaParte());
        declaratieActivePlasamentInfo.setValoare(declaratieAverePlasamentEntity.getValoare());
        declaratieActivePlasamentInfo.setMoneda(declaratieAverePlasamentEntity.getMoneda());

        return declaratieActivePlasamentInfo;
    }

    private DeclaratieAvereDatorieInfo getDeclaratieActiveDatorieInfo(DeclaratieAvereDatorieEntity declaratieAvereDatorieEntity) {
        DeclaratieAvereDatorieInfo declaratieActiveDatorieInfo = new DeclaratieAvereDatorieInfo();
        declaratieActiveDatorieInfo.setId(declaratieAvereDatorieEntity.getId());
        declaratieActiveDatorieInfo.setCreditor(declaratieAvereDatorieEntity.getCreditor());
        declaratieActiveDatorieInfo.setAnContractare(declaratieAvereDatorieEntity.getAnContractare());
        declaratieActiveDatorieInfo.setScadenta(declaratieAvereDatorieEntity.getScadenta());
        declaratieActiveDatorieInfo.setValoare(declaratieAvereDatorieEntity.getValoare());
        declaratieActiveDatorieInfo.setMoneda(declaratieAvereDatorieEntity.getMoneda());

        return declaratieActiveDatorieInfo;
    }

    private DeclaratieAvereContInfo getDeclaratieActiveContInfo(DeclaratieAvereContEntity declaratieAvereContEntity) {
        DeclaratieAvereContInfo declaratieActiveContInfo = new DeclaratieAvereContInfo();
        declaratieActiveContInfo.setId(declaratieAvereContEntity.getId());
        declaratieActiveContInfo.setTitular(declaratieAvereContEntity.getTitular());
        declaratieActiveContInfo.setInstitutieBancara(declaratieAvereContEntity.getInstitutieBancara());
        declaratieActiveContInfo.setTipCont(declaratieAvereContEntity.getTipCont());
        declaratieActiveContInfo.setSoldCont(declaratieAvereContEntity.getSoldCont());
        declaratieActiveContInfo.setMoneda(declaratieAvereContEntity.getMoneda());
        declaratieActiveContInfo.setAnDeschidereCont(declaratieAvereContEntity.getAnDeschidereCont());

        return declaratieActiveContInfo;
    }

    private DeclaratieAvereAlteActiveInfo getDeclaratieActiveAlteActiveInfo(DeclaratieAvereAlteActiveEntity declaratieAvereAlteActiveEntity) {
        DeclaratieAvereAlteActiveInfo declaratieActiveAlteActiveInfo = new DeclaratieAvereAlteActiveInfo();
        declaratieActiveAlteActiveInfo.setId(declaratieAvereAlteActiveEntity.getId());
        declaratieActiveAlteActiveInfo.setDescriere(declaratieAvereAlteActiveEntity.getDescriere());

        return declaratieActiveAlteActiveInfo;
    }

    /**
     * Saves a declaratieAvere.
     *
     * @param declaratieAvereInfo The DeclaratieAvereInfo
     * @return The saved DeclaratieAvereInfo
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public DeclaratieAvereInfo saveDeclaratieAvere(DeclaratieAvereInfo declaratieAvereInfo) {

        validateDeclaratieAvere(declaratieAvereInfo);

        DeclaratieAvereEntity declaratieAvereEntity = populateDeclaratieAvereEntity(declaratieAvereInfo);

        return getDeclaratieAvereInfo(demnitarEAO.saveDeclaratieAvere(declaratieAvereEntity));
    }

    private void validateDeclaratieAvere(DeclaratieAvereInfo declaratieAvereInfo) {
        if (declaratieAvereInfo.getDemnitarId() == null) {
            throw new ValidationException("demnitarId este obligatoriu");
        }

        if (demnitarEAO.getDemnitar(declaratieAvereInfo.getDemnitarId()) == null) {
            throw new ValidationException("demnitar nu exista");
        }

//        if (Utilities.isEmptyOrNull(declaratieAvereInfo.getFunctie())) {
//            throw new ValidationException("functie este obligatorie");
//        }

//        if (Utilities.isEmptyOrNull(declaratieAvereInfo.getInstitutie())) {
//            throw new ValidationException("institutie este obligatorie");
//        }

        if (declaratieAvereInfo.getDataDeclaratiei() == null) {
            throw new ValidationException("dataDeclaratiei este obligatorie");
        }

        validateAlteActive(declaratieAvereInfo);
        validateBunuriImobile(declaratieAvereInfo);
    }

    private void validateBunuriImobile(DeclaratieAvereInfo declaratieAvereInfo) {
        if (declaratieAvereInfo.getDeclaratieAvereBunImobilInfoList() != null) {
            for (DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo : declaratieAvereInfo.getDeclaratieAvereBunImobilInfoList()) {
                if (declaratieAvereBunImobilInfo.getIsTeren() == null) {
                    throw new ValidationException("isTeren este obligatorie");
                }

//                if (Utilities.isEmptyOrNull(declaratieAvereBunImobilInfo.getAdresaImobil())) {
//                    throw new ValidationException("adresaImobil este obligatorie");
//                }

                if (declaratieAvereBunImobilInfo.getAdresaImobil() != null && declaratieAvereBunImobilInfo.getAdresaImobil().length() > 500) {
                    throw new ValidationException("lungimea maxima pt adresaImobil este 500");
                }

                if (declaratieAvereBunImobilInfo.getIsTeren() && declaratieAvereBunImobilInfo.getTerenCategorie() == null) {
                    throw new ValidationException("terenCategorie este obligatorie");
                }

                if (!declaratieAvereBunImobilInfo.getIsTeren() && declaratieAvereBunImobilInfo.getCladireCategorie() == null) {
                    throw new ValidationException("cladireCategorie este obligatorie");
                }

//                if (Utilities.isEmptyOrNull(declaratieAvereBunImobilInfo.getAnDobandire())) {
//                    throw new ValidationException("anDobandire este obligatoriu");
//                }

                if (declaratieAvereBunImobilInfo.getSuprafata() == null) {
                    throw new ValidationException("suprafata este obligatorie");
                }

//                if (Utilities.isEmptyOrNull(declaratieAvereBunImobilInfo.getUnitateMasura())) {
//                    throw new ValidationException("unitateMasura este obligatorie");
//                }

                if (declaratieAvereBunImobilInfo.getUnitateMasura() != null && declaratieAvereBunImobilInfo.getUnitateMasura().length() > 10) {
                    throw new ValidationException("lungimea maxima pt unitateMasura este 10");
                }

//                if (Utilities.isEmptyOrNull(declaratieAvereBunImobilInfo.getCotaParte())) {
//                    throw new ValidationException("cotaParte este obligatorie");
//                }

                if (declaratieAvereBunImobilInfo.getCotaParte() != null && declaratieAvereBunImobilInfo.getCotaParte().length() > 100) {
                    throw new ValidationException("lungimea maxima pt cotaParte este 100");
                }

//                if (Utilities.isEmptyOrNull(declaratieAvereBunImobilInfo.getModDobandire())) {
//                    throw new ValidationException("modDobandire este obligatoriu");
//                }

//                if (declaratieAvereBunImobilInfo.getModDobandire() != null && declaratieAvereBunImobilInfo.getModDobandire().length() > 100) {
//                    throw new ValidationException("lungimea maxima pt modDobandire este 100");
//                }

//                if (Utilities.isEmptyOrNull(declaratieAvereBunImobilInfo.getTitular())) {
//                    throw new ValidationException("titular este obligatoriu");
//                }

//                if (declaratieAvereBunImobilInfo.getTitular() != null && declaratieAvereBunImobilInfo.getTitular().length() > 100) {
//                    throw new ValidationException("lungimea maxima pt titular este 100");
//                }
            }
        }
    }

    private void validateAlteActive(DeclaratieAvereInfo declaratieAvereInfo) {
        if (declaratieAvereInfo.getDeclaratieAvereAlteActiveInfoList() != null) {
            for (DeclaratieAvereAlteActiveInfo declaratieAvereAlteActiveInfo : declaratieAvereInfo.getDeclaratieAvereAlteActiveInfoList()) {
                if (Utilities.isEmptyOrNull(declaratieAvereAlteActiveInfo.getDescriere())) {
                    throw new ValidationException("descriere este obligatorie");
                }
            }
        }
    }

    private DeclaratieAvereEntity populateDeclaratieAvereEntity(DeclaratieAvereInfo declaratieAvereInfo) {
        DeclaratieAvereEntity declaratieAvereEntity;

        if (declaratieAvereInfo.getId() != null) {
            declaratieAvereEntity = demnitarEAO.getDeclaratieAvere(declaratieAvereInfo.getId());

            if (declaratieAvereEntity == null) {
                throw new ValidationException("declaratie avere nu exista");
            }
        } else {
            declaratieAvereEntity = new DeclaratieAvereEntity();
        }

        declaratieAvereEntity.setDemnitarId(declaratieAvereInfo.getDemnitarId());
        declaratieAvereEntity.setDataDeclaratiei(declaratieAvereInfo.getDataDeclaratiei());
        declaratieAvereEntity.setFunctie(declaratieAvereInfo.getFunctie());
        declaratieAvereEntity.setFunctie2(declaratieAvereInfo.getFunctie2());
        declaratieAvereEntity.setInstitutie(declaratieAvereInfo.getInstitutie());
        declaratieAvereEntity.setInstitutie2(declaratieAvereInfo.getInstitutie2());

        populateDeclaratieAvereAlteActiveEntitySet(declaratieAvereEntity.getDeclaratieAvereAlteActiveEntitySet(),
                declaratieAvereEntity, declaratieAvereInfo);
        populateDeclaratieAvereBunImobilEntitySet(declaratieAvereEntity.getDeclaratieAvereBunImobilEntitySet(),
                declaratieAvereEntity, declaratieAvereInfo);
        populateDeclaratieAvereBunMobilEntitySet(declaratieAvereEntity.getDeclaratieAvereBunMobilEntitySet(),
                declaratieAvereEntity, declaratieAvereInfo);
        populateDeclaratieAvereBijuterieEntitySet(declaratieAvereEntity.getDeclaratieAvereBijuterieEntitySet(),
                declaratieAvereEntity, declaratieAvereInfo);
        populateDeclaratieAverePlasamentEntitySet(declaratieAvereEntity.getDeclaratieAverePlasamentEntitySet(),
                declaratieAvereEntity, declaratieAvereInfo);
        populateDeclaratieAvereContEntitySet(declaratieAvereEntity.getDeclaratieAvereContEntitySet(),
                declaratieAvereEntity, declaratieAvereInfo);
        populateDeclaratieAvereBunInstrainatEntitySet(declaratieAvereEntity.getDeclaratieAvereBunInstrainatEntitySet(),
                declaratieAvereEntity, declaratieAvereInfo);
        populateDeclaratieAvereCadouEntitySet(declaratieAvereEntity.getDeclaratieAvereCadouEntitySet(),
                declaratieAvereEntity, declaratieAvereInfo);
        populateDeclaratieAvereVenitEntitySet(declaratieAvereEntity.getDeclaratieAvereVenitEntitySet(),
                declaratieAvereEntity, declaratieAvereInfo);

        return declaratieAvereEntity;
    }

    private void populateDeclaratieAvereVenitEntitySet(Set<DeclaratieAvereVenitEntity> declaratieAvereVenitEntitySet,
                                                       DeclaratieAvereEntity declaratieAvereEntity, DeclaratieAvereInfo declaratieAvereInfo) {
        if (declaratieAvereVenitEntitySet == null) {
            declaratieAvereVenitEntitySet = new HashSet<>();
            declaratieAvereEntity.setDeclaratieAvereVenitEntitySet(declaratieAvereVenitEntitySet);
        }

        // prepare maps that are needed in order to identify what entities news to be added, modified and deleted
        Map<Integer, DeclaratieAvereVenitInfo> declaratieAvereVenitInfoByIdHashMap = new HashMap<>();

        if (declaratieAvereInfo.getDeclaratieAvereVenitInfoList() != null) {
            for (DeclaratieAvereVenitInfo declaratieAvereVenitInfo : declaratieAvereInfo.getDeclaratieAvereVenitInfoList()) {
                declaratieAvereVenitInfoByIdHashMap.put(declaratieAvereVenitInfo.getId(), declaratieAvereVenitInfo);
            }
        }

        Map<Integer, DeclaratieAvereVenitEntity> declaratieAvereVenitEntityByIdMap = new HashMap<>();

        for (DeclaratieAvereVenitEntity declaratieAvereVenitEntity : declaratieAvereVenitEntitySet) {
            declaratieAvereVenitEntityByIdMap.put(declaratieAvereVenitEntity.getId(), declaratieAvereVenitEntity);
        }


        if (declaratieAvereInfo.getDeclaratieAvereVenitInfoList() != null) {
            for (DeclaratieAvereVenitInfo declaratieAvereVenitInfo : declaratieAvereInfo.getDeclaratieAvereVenitInfoList()) {
                DeclaratieAvereVenitEntity declaratieAvereVenitEntity = declaratieAvereVenitEntityByIdMap.get(declaratieAvereVenitInfo.getId());

                if (declaratieAvereVenitEntity == null) {
                    declaratieAvereVenitEntity = new DeclaratieAvereVenitEntity();
                    declaratieAvereVenitEntitySet.add(declaratieAvereVenitEntity);
                }

                populateDeclaratieAvereVenitEntity(declaratieAvereVenitEntity, declaratieAvereEntity,
                        declaratieAvereVenitInfo);
            }
        }

        // delete the relevant DeclaratieAvereVenitEntities
        Iterator<DeclaratieAvereVenitEntity> declaratieAvereVenitEntitySetIterator =
                declaratieAvereVenitEntitySet.iterator();

        while (declaratieAvereVenitEntitySetIterator.hasNext()) {
            DeclaratieAvereVenitEntity declaratieAvereVenitEntity = declaratieAvereVenitEntitySetIterator.next();

            if (!declaratieAvereVenitInfoByIdHashMap.containsKey(declaratieAvereVenitEntity.getId())) {
                declaratieAvereVenitEntitySetIterator.remove();
            }
        }
    }

    private void populateDeclaratieAvereVenitEntity(DeclaratieAvereVenitEntity declaratieAvereVenitEntity, DeclaratieAvereEntity declaratieAvereEntity,
                                                    DeclaratieAvereVenitInfo declaratieAvereVenitInfo) {
        declaratieAvereVenitEntity.setDeclaratieAvereEntity(declaratieAvereEntity);
        declaratieAvereVenitEntity.setTip(declaratieAvereVenitInfo.getTip());
        declaratieAvereVenitEntity.setTitular(declaratieAvereVenitInfo.getTitular());
        declaratieAvereVenitEntity.setSursaVenit(declaratieAvereVenitInfo.getSursaVenit());
        declaratieAvereVenitEntity.setServiciulPrestat(declaratieAvereVenitInfo.getServiciulPrestat());
        declaratieAvereVenitEntity.setMoneda(declaratieAvereVenitInfo.getMoneda());
        declaratieAvereVenitEntity.setVenitAnual(declaratieAvereVenitInfo.getVenitAnual());
    }

    private void populateDeclaratieAvereCadouEntitySet(Set<DeclaratieAvereCadouEntity> declaratieAvereCadouEntitySet,
                                                       DeclaratieAvereEntity declaratieAvereEntity, DeclaratieAvereInfo declaratieAvereInfo) {
        if (declaratieAvereCadouEntitySet == null) {
            declaratieAvereCadouEntitySet = new HashSet<>();
            declaratieAvereEntity.setDeclaratieAvereCadouEntitySet(declaratieAvereCadouEntitySet);
        }

        // prepare maps that are needed in order to identify what entities news to be added, modified and deleted
        Map<Integer, DeclaratieAvereCadouInfo> declaratieAvereCadouInfoByIdHashMap = new HashMap<>();

        if (declaratieAvereInfo.getDeclaratieAvereCadouInfoList() != null) {
            for (DeclaratieAvereCadouInfo declaratieAvereCadouInfo : declaratieAvereInfo.getDeclaratieAvereCadouInfoList()) {
                declaratieAvereCadouInfoByIdHashMap.put(declaratieAvereCadouInfo.getId(), declaratieAvereCadouInfo);
            }
        }

        Map<Integer, DeclaratieAvereCadouEntity> declaratieAvereCadouEntityByIdMap = new HashMap<>();

        for (DeclaratieAvereCadouEntity declaratieAvereCadouEntity : declaratieAvereCadouEntitySet) {
            declaratieAvereCadouEntityByIdMap.put(declaratieAvereCadouEntity.getId(), declaratieAvereCadouEntity);
        }


        if (declaratieAvereInfo.getDeclaratieAvereCadouInfoList() != null) {
            for (DeclaratieAvereCadouInfo declaratieAvereCadouInfo : declaratieAvereInfo.getDeclaratieAvereCadouInfoList()) {
                DeclaratieAvereCadouEntity declaratieAvereCadouEntity = declaratieAvereCadouEntityByIdMap.get(declaratieAvereCadouInfo.getId());

                if (declaratieAvereCadouEntity == null) {
                    declaratieAvereCadouEntity = new DeclaratieAvereCadouEntity();
                    declaratieAvereCadouEntitySet.add(declaratieAvereCadouEntity);
                }

                populateDeclaratieAvereCadouEntity(declaratieAvereCadouEntity, declaratieAvereEntity,
                        declaratieAvereCadouInfo);
            }
        }

        // delete the relevant DeclaratieAvereCadouEntities
        Iterator<DeclaratieAvereCadouEntity> declaratieAvereCadouEntitySetIterator =
                declaratieAvereCadouEntitySet.iterator();

        while (declaratieAvereCadouEntitySetIterator.hasNext()) {
            DeclaratieAvereCadouEntity declaratieAvereCadouEntity = declaratieAvereCadouEntitySetIterator.next();

            if (!declaratieAvereCadouInfoByIdHashMap.containsKey(declaratieAvereCadouEntity.getId())) {
                declaratieAvereCadouEntitySetIterator.remove();
            }
        }
    }

    private void populateDeclaratieAvereBunInstrainatEntitySet(Set<DeclaratieAvereBunInstrainatEntity> declaratieAvereBunInstrainatEntitySet,
                                                               DeclaratieAvereEntity declaratieAvereEntity,
                                                               DeclaratieAvereInfo declaratieAvereInfo) {
        if (declaratieAvereBunInstrainatEntitySet == null) {
            declaratieAvereBunInstrainatEntitySet = new HashSet<>();
            declaratieAvereEntity.setDeclaratieAvereBunInstrainatEntitySet(declaratieAvereBunInstrainatEntitySet);
        }

        // prepare maps that are needed in order to identify what entities news to be added, modified and deleted
        Map<Integer, DeclaratieAvereBunInstrainatInfo> declaratieAvereBunInstrainatInfoByIdHashMap = new HashMap<>();

        if (declaratieAvereInfo.getDeclaratieAvereBunInstrainatInfoList() != null) {
            for (DeclaratieAvereBunInstrainatInfo declaratieAvereBunInstrainatInfo : declaratieAvereInfo.getDeclaratieAvereBunInstrainatInfoList()) {
                declaratieAvereBunInstrainatInfoByIdHashMap.put(declaratieAvereBunInstrainatInfo.getId(), declaratieAvereBunInstrainatInfo);
            }
        }

        Map<Integer, DeclaratieAvereBunInstrainatEntity> declaratieAvereBunInstrainatEntityByIdMap = new HashMap<>();

        for (DeclaratieAvereBunInstrainatEntity declaratieAvereBunInstrainatEntity : declaratieAvereBunInstrainatEntitySet) {
            declaratieAvereBunInstrainatEntityByIdMap.put(declaratieAvereBunInstrainatEntity.getId(), declaratieAvereBunInstrainatEntity);
        }


        if (declaratieAvereInfo.getDeclaratieAvereBunInstrainatInfoList() != null) {
            for (DeclaratieAvereBunInstrainatInfo declaratieAvereBunInstrainatInfo : declaratieAvereInfo.getDeclaratieAvereBunInstrainatInfoList()) {
                DeclaratieAvereBunInstrainatEntity declaratieAvereBunInstrainatEntity = declaratieAvereBunInstrainatEntityByIdMap.get(declaratieAvereBunInstrainatInfo.getId());

                if (declaratieAvereBunInstrainatEntity == null) {
                    declaratieAvereBunInstrainatEntity = new DeclaratieAvereBunInstrainatEntity();
                    declaratieAvereBunInstrainatEntitySet.add(declaratieAvereBunInstrainatEntity);
                }

                populateDeclaratieAvereBunInstrainatEntity(declaratieAvereBunInstrainatEntity, declaratieAvereEntity,
                        declaratieAvereBunInstrainatInfo);
            }
        }

        // delete the relevant DeclaratieAvereBunInstrainatEntities
        Iterator<DeclaratieAvereBunInstrainatEntity> declaratieAvereBunInstrainatEntitySetIterator =
                declaratieAvereBunInstrainatEntitySet.iterator();

        while (declaratieAvereBunInstrainatEntitySetIterator.hasNext()) {
            DeclaratieAvereBunInstrainatEntity declaratieAvereBunInstrainatEntity = declaratieAvereBunInstrainatEntitySetIterator.next();

            if (!declaratieAvereBunInstrainatInfoByIdHashMap.containsKey(declaratieAvereBunInstrainatEntity.getId())) {
                declaratieAvereBunInstrainatEntitySetIterator.remove();
            }
        }
    }

    private void populateDeclaratieAvereCadouEntity(DeclaratieAvereCadouEntity declaratieAvereCadouEntity, DeclaratieAvereEntity declaratieAvereEntity,
                                                    DeclaratieAvereCadouInfo declaratieAvereCadouInfo) {
        declaratieAvereCadouEntity.setDeclaratieAvereEntity(declaratieAvereEntity);
        declaratieAvereCadouEntity.setTitular(declaratieAvereCadouInfo.getTitular());
        declaratieAvereCadouEntity.setSursaVenit(declaratieAvereCadouInfo.getSursaVenit());
        declaratieAvereCadouEntity.setServiciulPrestat(declaratieAvereCadouInfo.getServiciulPrestat());
        declaratieAvereCadouEntity.setVenit(declaratieAvereCadouInfo.getVenit());
        declaratieAvereCadouEntity.setMoneda(declaratieAvereCadouInfo.getMoneda());
    }

    private void populateDeclaratieAvereBunInstrainatEntity(DeclaratieAvereBunInstrainatEntity declaratieAvereBunInstrainatEntity,
                                                            DeclaratieAvereEntity declaratieAvereEntity,
                                                            DeclaratieAvereBunInstrainatInfo declaratieAvereBunInstrainatInfo) {
        declaratieAvereBunInstrainatEntity.setDeclaratieAvereEntity(declaratieAvereEntity);
        declaratieAvereBunInstrainatEntity.setTip(declaratieAvereBunInstrainatInfo.getTip());
        declaratieAvereBunInstrainatEntity.setIsImobil(declaratieAvereBunInstrainatInfo.getIsImobil());
        declaratieAvereBunInstrainatEntity.setMarca(declaratieAvereBunInstrainatInfo.getMarca());
        declaratieAvereBunInstrainatEntity.setDataInstrainarii(declaratieAvereBunInstrainatInfo.getDataInstrainarii());
        declaratieAvereBunInstrainatEntity.setPersoanaBeneficiara(declaratieAvereBunInstrainatInfo.getPersoanaBeneficiara());
        declaratieAvereBunInstrainatEntity.setFormaInstrainarii(declaratieAvereBunInstrainatInfo.getFormaInstrainarii());
        declaratieAvereBunInstrainatEntity.setValoarea(declaratieAvereBunInstrainatInfo.getValoarea());
        declaratieAvereBunInstrainatEntity.setMoneda(declaratieAvereBunInstrainatInfo.getMoneda());
    }

    private void populateDeclaratieAvereContEntitySet(Set<DeclaratieAvereContEntity> declaratieAvereContEntitySet,
                                                      DeclaratieAvereEntity declaratieAvereEntity,
                                                      DeclaratieAvereInfo declaratieAvereInfo) {
        if (declaratieAvereContEntitySet == null) {
            declaratieAvereContEntitySet = new HashSet<>();
            declaratieAvereEntity.setDeclaratieAvereContEntitySet(declaratieAvereContEntitySet);
        }

        // prepare maps that are needed in order to identify what entities news to be added, modified and deleted
        Map<Integer, DeclaratieAvereContInfo> declaratieAvereContInfoByIdHashMap = new HashMap<>();

        if (declaratieAvereInfo.getDeclaratieAvereContInfoList() != null) {
            for (DeclaratieAvereContInfo declaratieAvereContInfo : declaratieAvereInfo.getDeclaratieAvereContInfoList()) {
                declaratieAvereContInfoByIdHashMap.put(declaratieAvereContInfo.getId(), declaratieAvereContInfo);
            }
        }

        Map<Integer, DeclaratieAvereContEntity> declaratieAvereContEntityByIdMap = new HashMap<>();

        for (DeclaratieAvereContEntity declaratieAvereContEntity : declaratieAvereContEntitySet) {
            declaratieAvereContEntityByIdMap.put(declaratieAvereContEntity.getId(), declaratieAvereContEntity);
        }


        if (declaratieAvereInfo.getDeclaratieAvereContInfoList() != null) {
            for (DeclaratieAvereContInfo declaratieAvereContInfo : declaratieAvereInfo.getDeclaratieAvereContInfoList()) {
                DeclaratieAvereContEntity declaratieAvereContEntity = declaratieAvereContEntityByIdMap.get(declaratieAvereContInfo.getId());

                if (declaratieAvereContEntity == null) {
                    declaratieAvereContEntity = new DeclaratieAvereContEntity();
                    declaratieAvereContEntitySet.add(declaratieAvereContEntity);
                }

                populateDeclaratieAvereContEntity(declaratieAvereContEntity, declaratieAvereEntity,
                        declaratieAvereContInfo);
            }
        }

        // delete the relevant DeclaratieAvereContEntities
        Iterator<DeclaratieAvereContEntity> declaratieAvereContEntitySetIterator =
                declaratieAvereContEntitySet.iterator();

        while (declaratieAvereContEntitySetIterator.hasNext()) {
            DeclaratieAvereContEntity declaratieAvereContEntity = declaratieAvereContEntitySetIterator.next();

            if (!declaratieAvereContInfoByIdHashMap.containsKey(declaratieAvereContEntity.getId())) {
                declaratieAvereContEntitySetIterator.remove();
            }
        }
    }

    private void populateDeclaratieAvereContEntity(DeclaratieAvereContEntity declaratieAvereContEntity,
                                                   DeclaratieAvereEntity declaratieAvereEntity,
                                                   DeclaratieAvereContInfo declaratieAvereContInfo) {
        declaratieAvereContEntity.setDeclaratieAvereEntity(declaratieAvereEntity);
        declaratieAvereContEntity.setTitular(declaratieAvereContInfo.getTitular());
        declaratieAvereContEntity.setInstitutieBancara(declaratieAvereContInfo.getInstitutieBancara());
        declaratieAvereContEntity.setTipCont(declaratieAvereContInfo.getTipCont());
        declaratieAvereContEntity.setMoneda(declaratieAvereContInfo.getMoneda());
        declaratieAvereContEntity.setAnDeschidereCont(declaratieAvereContInfo.getAnDeschidereCont());
        declaratieAvereContEntity.setSoldCont(declaratieAvereContInfo.getSoldCont());
    }

    private void populateDeclaratieAverePlasamentEntitySet(Set<DeclaratieAverePlasamentEntity> declaratieAverePlasamentEntitySet,
                                                           DeclaratieAvereEntity declaratieAvereEntity,
                                                           DeclaratieAvereInfo declaratieAvereInfo) {
        if (declaratieAverePlasamentEntitySet == null) {
            declaratieAverePlasamentEntitySet = new HashSet<>();
            declaratieAvereEntity.setDeclaratieAverePlasamentEntitySet(declaratieAverePlasamentEntitySet);
        }

        // prepare maps that are needed in order to identify what entities news to be added, modified and deleted
        Map<Integer, DeclaratieAverePlasamentInfo> declaratieAverePlasamentInfoByIdHashMap = new HashMap<>();

        if (declaratieAvereInfo.getDeclaratieAverePlasamentInfoList() != null) {
            for (DeclaratieAverePlasamentInfo declaratieAverePlasamentInfo : declaratieAvereInfo.getDeclaratieAverePlasamentInfoList()) {
                declaratieAverePlasamentInfoByIdHashMap.put(declaratieAverePlasamentInfo.getId(), declaratieAverePlasamentInfo);
            }
        }

        Map<Integer, DeclaratieAverePlasamentEntity> declaratieAverePlasamentEntityByIdMap = new HashMap<>();

        for (DeclaratieAverePlasamentEntity declaratieAverePlasamentEntity : declaratieAverePlasamentEntitySet) {
            declaratieAverePlasamentEntityByIdMap.put(declaratieAverePlasamentEntity.getId(), declaratieAverePlasamentEntity);
        }


        if (declaratieAvereInfo.getDeclaratieAverePlasamentInfoList() != null) {
            for (DeclaratieAverePlasamentInfo declaratieAverePlasamentInfo : declaratieAvereInfo.getDeclaratieAverePlasamentInfoList()) {
                DeclaratieAverePlasamentEntity declaratieAverePlasamentEntity = declaratieAverePlasamentEntityByIdMap.get(declaratieAverePlasamentInfo.getId());

                if (declaratieAverePlasamentEntity == null) {
                    declaratieAverePlasamentEntity = new DeclaratieAverePlasamentEntity();
                    declaratieAverePlasamentEntitySet.add(declaratieAverePlasamentEntity);
                }

                populateDeclaratieAverePlasamentEntity(declaratieAverePlasamentEntity, declaratieAvereEntity,
                        declaratieAverePlasamentInfo);
            }
        }

        // delete the relevant DeclaratieAverePlasamentEntities
        Iterator<DeclaratieAverePlasamentEntity> declaratieAverePlasamentEntitySetIterator =
                declaratieAverePlasamentEntitySet.iterator();

        while (declaratieAverePlasamentEntitySetIterator.hasNext()) {
            DeclaratieAverePlasamentEntity declaratieAverePlasamentEntity = declaratieAverePlasamentEntitySetIterator.next();

            if (!declaratieAverePlasamentInfoByIdHashMap.containsKey(declaratieAverePlasamentEntity.getId())) {
                declaratieAverePlasamentEntitySetIterator.remove();
            }
        }
    }

    private void populateDeclaratieAverePlasamentEntity(DeclaratieAverePlasamentEntity declaratieAverePlasamentEntity,
                                                        DeclaratieAvereEntity declaratieAvereEntity,
                                                        DeclaratieAverePlasamentInfo declaratieAverePlasamentInfo) {
        declaratieAverePlasamentEntity.setDeclaratieAvereEntity(declaratieAvereEntity);
        declaratieAverePlasamentEntity.setTitular(declaratieAverePlasamentInfo.getTitular());
        declaratieAverePlasamentEntity.setEmitentTitlu(declaratieAverePlasamentInfo.getEmitentTitlu());
        declaratieAverePlasamentEntity.setTipulPlasamentului(declaratieAverePlasamentInfo.getTipulPlasamentului());
        declaratieAverePlasamentEntity.setNumarTitluriSauCotaParte(declaratieAverePlasamentInfo.getNumarTitluriSauCotaParte());
        declaratieAverePlasamentEntity.setValoare(declaratieAverePlasamentInfo.getValoare());
        declaratieAverePlasamentEntity.setMoneda(declaratieAverePlasamentInfo.getMoneda());
    }

    private void populateDeclaratieAvereBijuterieEntitySet(Set<DeclaratieAvereBijuterieEntity> declaratieAvereBijuterieEntitySet,
                                                           DeclaratieAvereEntity declaratieAvereEntity,
                                                           DeclaratieAvereInfo declaratieAvereInfo) {
        if (declaratieAvereBijuterieEntitySet == null) {
            declaratieAvereBijuterieEntitySet = new HashSet<>();
            declaratieAvereEntity.setDeclaratieAvereBijuterieEntitySet(declaratieAvereBijuterieEntitySet);
        }

        // prepare maps that are needed in order to identify what entities news to be added, modified and deleted
        Map<Integer, DeclaratieAvereBijuterieInfo> declaratieAvereBijuterieInfoByIdHashMap = new HashMap<>();

        if (declaratieAvereInfo.getDeclaratieAvereBijuterieInfoList() != null) {
            for (DeclaratieAvereBijuterieInfo declaratieAvereBijuterieInfo : declaratieAvereInfo.getDeclaratieAvereBijuterieInfoList()) {
                declaratieAvereBijuterieInfoByIdHashMap.put(declaratieAvereBijuterieInfo.getId(), declaratieAvereBijuterieInfo);
            }
        }

        Map<Integer, DeclaratieAvereBijuterieEntity> declaratieAvereBijuterieEntityByIdMap = new HashMap<>();

        for (DeclaratieAvereBijuterieEntity declaratieAvereBijuterieEntity : declaratieAvereBijuterieEntitySet) {
            declaratieAvereBijuterieEntityByIdMap.put(declaratieAvereBijuterieEntity.getId(), declaratieAvereBijuterieEntity);
        }


        if (declaratieAvereInfo.getDeclaratieAvereBijuterieInfoList() != null) {
            for (DeclaratieAvereBijuterieInfo declaratieAvereBijuterieInfo : declaratieAvereInfo.getDeclaratieAvereBijuterieInfoList()) {
                DeclaratieAvereBijuterieEntity declaratieAvereBijuterieEntity = declaratieAvereBijuterieEntityByIdMap.get(declaratieAvereBijuterieInfo.getId());

                if (declaratieAvereBijuterieEntity == null) {
                    declaratieAvereBijuterieEntity = new DeclaratieAvereBijuterieEntity();
                    declaratieAvereBijuterieEntitySet.add(declaratieAvereBijuterieEntity);
                }

                populateDeclaratieAvereBijuterieEntity(declaratieAvereBijuterieEntity, declaratieAvereEntity,
                        declaratieAvereBijuterieInfo);
            }
        }

        // delete the relevant DeclaratieAvereBijuterieEntities
        Iterator<DeclaratieAvereBijuterieEntity> declaratieAvereBijuterieEntitySetIterator =
                declaratieAvereBijuterieEntitySet.iterator();

        while (declaratieAvereBijuterieEntitySetIterator.hasNext()) {
            DeclaratieAvereBijuterieEntity declaratieAvereBijuterieEntity = declaratieAvereBijuterieEntitySetIterator.next();

            if (!declaratieAvereBijuterieInfoByIdHashMap.containsKey(declaratieAvereBijuterieEntity.getId())) {
                declaratieAvereBijuterieEntitySetIterator.remove();
            }
        }
    }

    private void populateDeclaratieAvereBijuterieEntity(DeclaratieAvereBijuterieEntity declaratieAvereBijuterieEntity,
                                                        DeclaratieAvereEntity declaratieAvereEntity,
                                                        DeclaratieAvereBijuterieInfo declaratieAvereBijuterieInfo) {
        declaratieAvereBijuterieEntity.setDeclaratieAvereEntity(declaratieAvereEntity);
        declaratieAvereBijuterieEntity.setDescriere(declaratieAvereBijuterieInfo.getDescriere());
        declaratieAvereBijuterieEntity.setAnDobandire(declaratieAvereBijuterieInfo.getAnDobandire());
        declaratieAvereBijuterieEntity.setValoareEstimate(declaratieAvereBijuterieInfo.getValoareEstimate());
        declaratieAvereBijuterieEntity.setMoneda(declaratieAvereBijuterieInfo.getMoneda());
    }

    private void populateDeclaratieAvereBunMobilEntitySet(Set<DeclaratieAvereBunMobilEntity> declaratieAvereBunMobilEntitySet,
                                                          DeclaratieAvereEntity declaratieAvereEntity,
                                                          DeclaratieAvereInfo declaratieAvereInfo) {
        if (declaratieAvereBunMobilEntitySet == null) {
            declaratieAvereBunMobilEntitySet = new HashSet<>();
            declaratieAvereEntity.setDeclaratieAvereBunMobilEntitySet(declaratieAvereBunMobilEntitySet);
        }

        // prepare maps that are needed in order to identify what entities news to be added, modified and deleted
        Map<Integer, DeclaratieAvereBunMobilInfo> declaratieAvereBunMobilInfoByIdHashMap = new HashMap<>();

        if (declaratieAvereInfo.getDeclaratieAvereBunMobilInfoList() != null) {
            for (DeclaratieAvereBunMobilInfo declaratieAvereBunMobilInfo : declaratieAvereInfo.getDeclaratieAvereBunMobilInfoList()) {
                declaratieAvereBunMobilInfoByIdHashMap.put(declaratieAvereBunMobilInfo.getId(), declaratieAvereBunMobilInfo);
            }
        }

        Map<Integer, DeclaratieAvereBunMobilEntity> declaratieAvereBunMobilEntityByIdMap = new HashMap<>();

        for (DeclaratieAvereBunMobilEntity declaratieAvereBunMobilEntity : declaratieAvereBunMobilEntitySet) {
            declaratieAvereBunMobilEntityByIdMap.put(declaratieAvereBunMobilEntity.getId(), declaratieAvereBunMobilEntity);
        }


        if (declaratieAvereInfo.getDeclaratieAvereBunMobilInfoList() != null) {
            for (DeclaratieAvereBunMobilInfo declaratieAvereBunMobilInfo : declaratieAvereInfo.getDeclaratieAvereBunMobilInfoList()) {
                DeclaratieAvereBunMobilEntity declaratieAvereBunMobilEntity = declaratieAvereBunMobilEntityByIdMap.get(declaratieAvereBunMobilInfo.getId());

                if (declaratieAvereBunMobilEntity == null) {
                    declaratieAvereBunMobilEntity = new DeclaratieAvereBunMobilEntity();
                    declaratieAvereBunMobilEntitySet.add(declaratieAvereBunMobilEntity);
                }

                populateDeclaratieAvereBunMobilEntity(declaratieAvereBunMobilEntity, declaratieAvereEntity,
                        declaratieAvereBunMobilInfo);
            }
        }

        // delete the relevant DeclaratieAvereBunMobilEntities
        Iterator<DeclaratieAvereBunMobilEntity> declaratieAvereBunMobilEntitySetIterator =
                declaratieAvereBunMobilEntitySet.iterator();

        while (declaratieAvereBunMobilEntitySetIterator.hasNext()) {
            DeclaratieAvereBunMobilEntity declaratieAvereBunMobilEntity = declaratieAvereBunMobilEntitySetIterator.next();

            if (!declaratieAvereBunMobilInfoByIdHashMap.containsKey(declaratieAvereBunMobilEntity.getId())) {
                declaratieAvereBunMobilEntitySetIterator.remove();
            }
        }
    }

    private void populateDeclaratieAvereBunMobilEntity(DeclaratieAvereBunMobilEntity declaratieAvereBunMobilEntity,
                                                       DeclaratieAvereEntity declaratieAvereEntity,
                                                       DeclaratieAvereBunMobilInfo declaratieAvereBunMobilInfo) {
        declaratieAvereBunMobilEntity.setDeclaratieAvereEntity(declaratieAvereEntity);
        declaratieAvereBunMobilEntity.setTip(declaratieAvereBunMobilInfo.getTip());
        declaratieAvereBunMobilEntity.setMarca(declaratieAvereBunMobilInfo.getMarca());
        declaratieAvereBunMobilEntity.setCantitate(declaratieAvereBunMobilInfo.getCantitate());
        declaratieAvereBunMobilEntity.setAnFabricare(declaratieAvereBunMobilInfo.getAnFabricare());
        declaratieAvereBunMobilEntity.setModDobandire(declaratieAvereBunMobilInfo.getModDobandire());
    }

    private void populateDeclaratieAvereBunImobilEntitySet(Set<DeclaratieAvereBunImobilEntity> declaratieAvereBunImobilEntitySet,
                                                           DeclaratieAvereEntity declaratieAvereEntity,
                                                           DeclaratieAvereInfo declaratieAvereInfo) {
        if (declaratieAvereBunImobilEntitySet == null) {
            declaratieAvereBunImobilEntitySet = new HashSet<>();
            declaratieAvereEntity.setDeclaratieAvereBunImobilEntitySet(declaratieAvereBunImobilEntitySet);
        }

        // prepare maps that are needed in order to identify what entities news to be added, modified and deleted
        Map<Integer, DeclaratieAvereBunImobilInfo> declaratieAvereBunImobilInfoByIdHashMap = new HashMap<>();

        if (declaratieAvereInfo.getDeclaratieAvereBunImobilInfoList() != null) {
            for (DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo : declaratieAvereInfo.getDeclaratieAvereBunImobilInfoList()) {
                declaratieAvereBunImobilInfoByIdHashMap.put(declaratieAvereBunImobilInfo.getId(), declaratieAvereBunImobilInfo);
            }
        }

        Map<Integer, DeclaratieAvereBunImobilEntity> declaratieAvereBunImobilEntityByIdMap = new HashMap<>();

        for (DeclaratieAvereBunImobilEntity declaratieAvereBunImobilEntity : declaratieAvereBunImobilEntitySet) {
            declaratieAvereBunImobilEntityByIdMap.put(declaratieAvereBunImobilEntity.getId(), declaratieAvereBunImobilEntity);
        }


        if (declaratieAvereInfo.getDeclaratieAvereBunImobilInfoList() != null) {
            for (DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo : declaratieAvereInfo.getDeclaratieAvereBunImobilInfoList()) {
                DeclaratieAvereBunImobilEntity declaratieAvereBunImobilEntity = declaratieAvereBunImobilEntityByIdMap.get(declaratieAvereBunImobilInfo.getId());

                if (declaratieAvereBunImobilEntity == null) {
                    declaratieAvereBunImobilEntity = new DeclaratieAvereBunImobilEntity();
                    declaratieAvereBunImobilEntitySet.add(declaratieAvereBunImobilEntity);
                }

                populateDeclaratieAvereBunImobilEntity(declaratieAvereBunImobilEntity, declaratieAvereEntity,
                        declaratieAvereBunImobilInfo);
            }
        }

        // delete the relevant DeclaratieAvereBunImobilEntities
        Iterator<DeclaratieAvereBunImobilEntity> declaratieAvereBunImobilEntitySetIterator =
                declaratieAvereBunImobilEntitySet.iterator();

        while (declaratieAvereBunImobilEntitySetIterator.hasNext()) {
            DeclaratieAvereBunImobilEntity declaratieAvereBunImobilEntity = declaratieAvereBunImobilEntitySetIterator.next();

            if (!declaratieAvereBunImobilInfoByIdHashMap.containsKey(declaratieAvereBunImobilEntity.getId())) {
                declaratieAvereBunImobilEntitySetIterator.remove();
            }
        }
    }

    private void populateDeclaratieAvereAlteActiveEntitySet(Set<DeclaratieAvereAlteActiveEntity> declaratieAvereAlteActiveEntitySet,
                                                            DeclaratieAvereEntity declaratieAvereEntity,
                                                            DeclaratieAvereInfo declaratieAvereInfo) {
        if (declaratieAvereAlteActiveEntitySet == null) {
            declaratieAvereAlteActiveEntitySet = new HashSet<>();
            declaratieAvereEntity.setDeclaratieAvereAlteActiveEntitySet(declaratieAvereAlteActiveEntitySet);
        }

        // prepare maps that are needed in order to identify what entities news to be added, modified and deleted
        Map<Integer, DeclaratieAvereAlteActiveInfo> declaratieAvereAlteActiveInfoByIdHashMap = new HashMap<>();

        if (declaratieAvereInfo.getDeclaratieAvereAlteActiveInfoList() != null) {
            for (DeclaratieAvereAlteActiveInfo declaratieAvereAlteActiveInfo : declaratieAvereInfo.getDeclaratieAvereAlteActiveInfoList()) {
                declaratieAvereAlteActiveInfoByIdHashMap.put(declaratieAvereAlteActiveInfo.getId(), declaratieAvereAlteActiveInfo);
            }
        }

        Map<Integer, DeclaratieAvereAlteActiveEntity> declaratieAvereAlteActiveEntityByIdMap = new HashMap<>();

        for (DeclaratieAvereAlteActiveEntity declaratieAvereAlteActiveEntity : declaratieAvereAlteActiveEntitySet) {
            declaratieAvereAlteActiveEntityByIdMap.put(declaratieAvereAlteActiveEntity.getId(), declaratieAvereAlteActiveEntity);
        }


        if (declaratieAvereInfo.getDeclaratieAvereAlteActiveInfoList() != null) {
            for (DeclaratieAvereAlteActiveInfo declaratieAvereAlteActiveInfo : declaratieAvereInfo.getDeclaratieAvereAlteActiveInfoList()) {
                DeclaratieAvereAlteActiveEntity declaratieAvereAlteActiveEntity = declaratieAvereAlteActiveEntityByIdMap.get(declaratieAvereAlteActiveInfo.getId());

                if (declaratieAvereAlteActiveEntity == null) {
                    declaratieAvereAlteActiveEntity = new DeclaratieAvereAlteActiveEntity();
                    declaratieAvereAlteActiveEntitySet.add(declaratieAvereAlteActiveEntity);
                }

                populateDeclaratieAvereAlteActiveEntity(declaratieAvereAlteActiveEntity, declaratieAvereEntity,
                        declaratieAvereAlteActiveInfo);
            }
        }

        // delete the relevant DeclaratieAvereAlteActiveEntities
        Iterator<DeclaratieAvereAlteActiveEntity> declaratieAvereAlteActiveEntitySetIterator =
                declaratieAvereAlteActiveEntitySet.iterator();

        while (declaratieAvereAlteActiveEntitySetIterator.hasNext()) {
            DeclaratieAvereAlteActiveEntity declaratieAvereAlteActiveEntity = declaratieAvereAlteActiveEntitySetIterator.next();

            if (!declaratieAvereAlteActiveInfoByIdHashMap.containsKey(declaratieAvereAlteActiveEntity.getId())) {
                declaratieAvereAlteActiveEntitySetIterator.remove();
            }
        }
    }

    private void populateDeclaratieAvereBunImobilEntity(DeclaratieAvereBunImobilEntity declaratieAvereBunImobilEntity,
                                                        DeclaratieAvereEntity declaratieAvereEntity,
                                                        DeclaratieAvereBunImobilInfo declaratieAvereBunImobilInfo) {
        declaratieAvereBunImobilEntity.setDeclaratieAvereEntity(declaratieAvereEntity);
        declaratieAvereBunImobilEntity.setIsTeren(declaratieAvereBunImobilInfo.getIsTeren());
        declaratieAvereBunImobilEntity.setAdresaImobil(declaratieAvereBunImobilInfo.getAdresaImobil());
        declaratieAvereBunImobilEntity.setTerenCategorie(declaratieAvereBunImobilInfo.getTerenCategorie());
        declaratieAvereBunImobilEntity.setCladireCategorie(declaratieAvereBunImobilInfo.getCladireCategorie());
        declaratieAvereBunImobilEntity.setAnDobandire(declaratieAvereBunImobilInfo.getAnDobandire());
        declaratieAvereBunImobilEntity.setSuprafata(declaratieAvereBunImobilInfo.getSuprafata());
        declaratieAvereBunImobilEntity.setUnitateMasura(declaratieAvereBunImobilInfo.getUnitateMasura());
        declaratieAvereBunImobilEntity.setCotaParte(declaratieAvereBunImobilInfo.getCotaParte());
        declaratieAvereBunImobilEntity.setModDobandire(declaratieAvereBunImobilInfo.getModDobandire());
        declaratieAvereBunImobilEntity.setTitular(declaratieAvereBunImobilInfo.getTitular());
    }

    private void populateDeclaratieAvereAlteActiveEntity(DeclaratieAvereAlteActiveEntity declaratieAvereAlteActiveEntity,
                                                         DeclaratieAvereEntity declaratieAvereEntity,
                                                         DeclaratieAvereAlteActiveInfo declaratieAvereAlteActiveInfo) {
        declaratieAvereAlteActiveEntity.setDeclaratieAvereEntity(declaratieAvereEntity);
        declaratieAvereAlteActiveEntity.setDescriere(declaratieAvereAlteActiveInfo.getDescriere());
    }

    /**
     * Gets a declaratieAvere.
     *
     * @param id The declaratieAvere id
     * @return The DeclaratieAvereInfo
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED, rollbackFor = Exception.class)
    public DeclaratieAvereInfo getDeclaratieAvere(Integer id) {
        if (id == null) {
            throw new ValidationException("id is required");
        }

        DeclaratieAvereEntity declaratieAvereEntity = demnitarEAO.getDeclaratieAvere(id);

        if (declaratieAvereEntity == null) {
            throw new ValidationException("declaratie avere nu exista");
        }

        return getDeclaratieAvereInfo(declaratieAvereEntity);
    }

    /**
     * Finds declaratii avere by the specified search criteria.
     *
     * @param searchDeclaratieAvereCriteria The SearchDeclaratieAvereCriteria object
     * @return The List of DeclaratieAvereInfo objects
     */
    @Transactional(readOnly = true)
    public List<DeclaratieAvereInfo> findDeclaratiiAvere(SearchDeclaratieAvereCriteria searchDeclaratieAvereCriteria) {
        List<DeclaratieAvereInfo> declaratieAvereInfoList = new ArrayList<>();

        DeclaratieAvereEntitySearchCriteria declaratieAvereEntitySearchCriteria = new DeclaratieAvereEntitySearchCriteria();
        declaratieAvereEntitySearchCriteria.setDemnitarId(searchDeclaratieAvereCriteria.getDemnitarId());
        declaratieAvereEntitySearchCriteria.setDataDeclaratiei(searchDeclaratieAvereCriteria.getDataDeclaratiei());
        declaratieAvereEntitySearchCriteria.setEagerLoadAllRelations(true);

        List<DeclaratieAvereEntity> declaratieAvereEntityList = demnitarEAO.findDeclaratiiAvere(declaratieAvereEntitySearchCriteria);

        for (DeclaratieAvereEntity declaratieAvereEntity : declaratieAvereEntityList) {
            declaratieAvereInfoList.add(getDeclaratieAvereInfo(declaratieAvereEntity));
        }

        return declaratieAvereInfoList;
    }
}
