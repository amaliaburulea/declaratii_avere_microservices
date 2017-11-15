package com.declaratiiavere.demnitarservice.demnitar;

import com.declaratiiavere.common.utils.DateUtilities;
import com.declaratiiavere.common.utils.ObjectComparator;
import com.declaratiiavere.common.utils.Utilities;
import com.declaratiiavere.iam.user.UserIdentity;
import com.declaratiiavere.iam.user.UserInfo;
import com.declaratiiavere.jpaframework.OrderByInfo;
import com.declaratiiavere.jpaframework.OrderType;
import com.declaratiiavere.restclient.RestException;
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

    @Autowired
    private UserServiceAdapter userServiceAdapter;

    private DemnitarInfo getDemnitarInfo(DemnitarEntity demnitarEntity) {
        DemnitarInfo demnitarInfo = new DemnitarInfo();
        demnitarInfo.setId(demnitarEntity.getId());
        demnitarInfo.setNume(demnitarEntity.getNume());
        demnitarInfo.setPrenume(demnitarEntity.getPrenume());
        demnitarInfo.setAnNastere(demnitarEntity.getAnNastere());

        demnitarInfo.setFunctieId(demnitarEntity.getFunctieId());
        demnitarInfo.setFunctie2Id(demnitarEntity.getFunctie2Id());
        demnitarInfo.setInstitutieId(demnitarEntity.getInstitutieId());
        demnitarInfo.setInstitutie2Id(demnitarEntity.getInstitutie2Id());

        if (demnitarEntity.getFunctieEntity() != null) {
            demnitarInfo.setFunctie(demnitarEntity.getFunctieEntity().getNume());
        }

        if (demnitarEntity.getFunctie2Entity() != null) {
            demnitarInfo.setFunctie2(demnitarEntity.getFunctie2Entity().getNume());
        }

        if (demnitarEntity.getInstitutieEntity() != null) {
            demnitarInfo.setInstitutie(demnitarEntity.getInstitutieEntity().getNume());
        }

        if (demnitarEntity.getInstitutie2Entity() != null) {
            demnitarInfo.setInstitutie2(demnitarEntity.getInstitutie2Entity().getNume());
        }

        UserInfo userInfo = UserIdentity.getLoginUser();

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

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public FunctieInfo saveFunctie(FunctieInfo functieInfo) {
        validateFunctie(functieInfo);

        FunctieEntity functieEntity = populateFunctieEntity(functieInfo);

        return getFunctieInfo(demnitarEAO.saveFunctie(functieEntity));
    }

    private FunctieEntity populateFunctieEntity(FunctieInfo functieInfo) {
        FunctieEntity functieEntity;

        if (functieInfo.getId() != null) {
            functieEntity = demnitarEAO.getFunctie(functieInfo.getId());

            if (functieEntity == null) {
                throw new ValidationException("Functie does not exist");
            }
        } else {
            functieEntity = new FunctieEntity();
        }

        functieEntity.setNume(functieInfo.getNume());

        return functieEntity;
    }

    private void validateFunctie(FunctieInfo functieInfo) {
        if (functieInfo.getNume() == null) {
            throw new ValidationException("nume is required");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public InstitutieInfo saveInstitutie(InstitutieInfo institutieInfo) {
        validateInstitutie(institutieInfo);

        InstitutieEntity institutieEntity = populateInstitutieEntity(institutieInfo);

        return getInstitutieInfo(demnitarEAO.saveInstitutie(institutieEntity));
    }

    private InstitutieEntity populateInstitutieEntity(InstitutieInfo institutieInfo) {
        InstitutieEntity institutieEntity;

        if (institutieInfo.getId() != null) {
            institutieEntity = demnitarEAO.getInstitutie(institutieInfo.getId());

            if (institutieEntity == null) {
                throw new ValidationException("Institutie does not exist");
            }
        } else {
            institutieEntity = new InstitutieEntity();
        }

        institutieEntity.setNume(institutieInfo.getNume());

        return institutieEntity;
    }

    private void validateInstitutie(InstitutieInfo institutieInfo) {
        if (institutieInfo.getNume() == null) {
            throw new ValidationException("nume is required");
        }
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
        demnitarEntity.setAnNastere(demnitarInfo.getAnNastere());

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

    @Transactional(readOnly = true)
    public InstitutieInfo getInstitutieByNume(String nume) {
        if (nume == null) {
            throw new ValidationException("nume is required");
        }

        InstitutieEntity institutieEntity = demnitarEAO.getInstitutieByNume(nume);

        if (institutieEntity == null) {
            throw new ValidationException("institutie does not exist");
        }

        return getInstitutieInfo(institutieEntity);
    }

    private InstitutieInfo getInstitutieInfo(InstitutieEntity institutieEntity) {
        InstitutieInfo institutieInfo = new InstitutieInfo();
        institutieInfo.setId(institutieEntity.getId());
        institutieInfo.setNume(institutieEntity.getNume());

        return institutieInfo;
    }

    @Transactional(readOnly = true)
    public FunctieInfo getFunctieByNume(String nume) {
        if (nume == null) {
            throw new ValidationException("nume is required");
        }

        FunctieEntity functieEntity = demnitarEAO.getFunctieByNume(nume);

        if (functieEntity == null) {
            throw new ValidationException("functie does not exist");
        }

        return getFunctieInfo(functieEntity);
    }

    private FunctieInfo getFunctieInfo(FunctieEntity functieEntity) {
        FunctieInfo functieInfo = new FunctieInfo();
        functieInfo.setId(functieEntity.getId());
        functieInfo.setNume(functieEntity.getNume());

        return functieInfo;
    }

    @Transactional(readOnly = true)
    public List<InstitutieInfo> findAllInstitutii() {
        InstitutieEntitySearchCriteria institutieEntitySearchCriteria = new InstitutieEntitySearchCriteria();
        List<InstitutieEntity> institutieEntityList = demnitarEAO.findInstitutii(institutieEntitySearchCriteria);

        List<InstitutieInfo> institutieInfoList = new ArrayList<>();

        for (InstitutieEntity institutieEntity : institutieEntityList) {
            InstitutieInfo institutieInfo = getInstitutieInfo(institutieEntity);
            institutieInfoList.add(institutieInfo);
        }

        return institutieInfoList;
    }

    @Transactional(readOnly = true)
    public List<FunctieInfo> findAllFunctii() {
        FunctieEntitySearchCriteria functieEntitySearchCriteria = new FunctieEntitySearchCriteria();
        List<FunctieEntity> functieEntityList = demnitarEAO.findFunctii(functieEntitySearchCriteria);

        List<FunctieInfo> functieInfoList = new ArrayList<>();

        for (FunctieEntity functieEntity : functieEntityList) {
            FunctieInfo functieInfo = getFunctieInfo(functieEntity);
            functieInfoList.add(functieInfo);
        }

        return functieInfoList;
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

        if (searchDemnitarCriteria.isSearchOnlyDemnitarsAssignedToMe() && UserIdentity.getLoginUser() != null) {
            demnitarEntitySearchCriteria.setVoluntarId(UserIdentity.getLoginUser().getUserId());
        }

        demnitarEntitySearchCriteria.setNume(searchDemnitarCriteria.getNume());
        demnitarEntitySearchCriteria.setPrenume(searchDemnitarCriteria.getPrenume());
        demnitarEntitySearchCriteria.setFunctieId(searchDemnitarCriteria.getFunctieId());
        demnitarEntitySearchCriteria.setInstitutieId(searchDemnitarCriteria.getInstitutieId());
        demnitarEntitySearchCriteria.setEagerLoadAllRelations(true);

        if (!Utilities.isEmptyOrNull(searchDemnitarCriteria.getNumeStartsWith())) {
            demnitarEntitySearchCriteria.setNumeStartsWith(searchDemnitarCriteria.getNumeStartsWith() + "%");
        }

        if (!Utilities.isEmptyOrNull(searchDemnitarCriteria.getPrenumeStartsWith())) {
            demnitarEntitySearchCriteria.setPrenumeStartsWith(searchDemnitarCriteria.getPrenumeStartsWith() + "%");
        }

        List<DemnitarEntity> demnitarEntityList = demnitarEAO.findDemnitars(demnitarEntitySearchCriteria);

        for (DemnitarEntity demnitarEntity : demnitarEntityList) {
            demnitarInfoList.add(getDemnitarInfo(demnitarEntity));
        }

        return demnitarInfoList;
    }

    private DeclaratieAvereInfo getDeclaratieAvereInfo(DeclaratieAvereEntity declaratieAvereEntity, boolean eagerLoadAllRelations) {
        DeclaratieAvereInfo declaratieAvereInfo = new DeclaratieAvereInfo();
        declaratieAvereInfo.setId(declaratieAvereEntity.getId());
        declaratieAvereInfo.setDemnitarId(declaratieAvereEntity.getDemnitarId());

        declaratieAvereInfo.setVoluntarId(declaratieAvereEntity.getVoluntarId());

        if (declaratieAvereEntity.getDemnitarEntity() != null) {
            declaratieAvereInfo.setDemnitarNume(declaratieAvereEntity.getDemnitarEntity().getNume());
            declaratieAvereInfo.setDemnitarPrenume(declaratieAvereEntity.getDemnitarEntity().getPrenume());
            declaratieAvereInfo.setAnNastere(declaratieAvereEntity.getDemnitarEntity().getAnNastere());
        }

        declaratieAvereInfo.setDataDeclaratiei(declaratieAvereEntity.getDataDeclaratiei());
        declaratieAvereInfo.setFunctieId(declaratieAvereEntity.getFunctieId());
        declaratieAvereInfo.setFunctie2Id(declaratieAvereEntity.getFunctie2Id());
        declaratieAvereInfo.setInstitutieId(declaratieAvereEntity.getInstitutieId());
        declaratieAvereInfo.setInstitutie2Id(declaratieAvereEntity.getInstitutie2Id());

        if (declaratieAvereEntity.getFunctieEntity() != null) {
            declaratieAvereInfo.setFunctie(declaratieAvereEntity.getFunctieEntity().getNume());
        }

        if (declaratieAvereEntity.getFunctie2Entity() != null) {
            declaratieAvereInfo.setFunctie2(declaratieAvereEntity.getFunctie2Entity().getNume());
        }

        if (declaratieAvereEntity.getInstitutieEntity() != null) {
            declaratieAvereInfo.setInstitutie(declaratieAvereEntity.getInstitutieEntity().getNume());
        }

        if (declaratieAvereEntity.getInstitutie2Entity() != null) {
            declaratieAvereInfo.setInstitutie2(declaratieAvereEntity.getInstitutie2Entity().getNume());
        }

        declaratieAvereInfo.setLinkDeclaratie(declaratieAvereEntity.getLinkDeclaratie());
        declaratieAvereInfo.setGrupPolitic(declaratieAvereEntity.getGrupPolitic());
        declaratieAvereInfo.setCircumscriptie(declaratieAvereEntity.getCircumscriptie());
        declaratieAvereInfo.setIsDone(declaratieAvereEntity.getIsDone());

        if (eagerLoadAllRelations) {
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
        }

        return declaratieAvereInfo;
    }

    private DeclaratieIntereseInfo getDeclaratieIntereseInfo(DeclaratieIntereseEntity declaratieIntereseEntity, boolean eagerLoadAllRelations) {
        DeclaratieIntereseInfo declaratieIntereseInfo = new DeclaratieIntereseInfo();
        declaratieIntereseInfo.setId(declaratieIntereseEntity.getId());
        declaratieIntereseInfo.setDemnitarId(declaratieIntereseEntity.getDemnitarId());

        declaratieIntereseInfo.setVoluntarId(declaratieIntereseEntity.getVoluntarId());

        if (declaratieIntereseEntity.getDemnitarEntity() != null) {
            declaratieIntereseInfo.setDemnitarNume(declaratieIntereseEntity.getDemnitarEntity().getNume());
            declaratieIntereseInfo.setDemnitarPrenume(declaratieIntereseEntity.getDemnitarEntity().getPrenume());
            declaratieIntereseInfo.setAnNastere(declaratieIntereseEntity.getDemnitarEntity().getAnNastere());
        }

        declaratieIntereseInfo.setDataDeclaratiei(declaratieIntereseEntity.getDataDeclaratiei());
        declaratieIntereseInfo.setFunctieId(declaratieIntereseEntity.getFunctieId());
        declaratieIntereseInfo.setFunctie2Id(declaratieIntereseEntity.getFunctie2Id());
        declaratieIntereseInfo.setInstitutieId(declaratieIntereseEntity.getInstitutieId());
        declaratieIntereseInfo.setInstitutie2Id(declaratieIntereseEntity.getInstitutie2Id());

        if (declaratieIntereseEntity.getFunctieEntity() != null) {
            declaratieIntereseInfo.setFunctie(declaratieIntereseEntity.getFunctieEntity().getNume());
        }

        if (declaratieIntereseEntity.getFunctie2Entity() != null) {
            declaratieIntereseInfo.setFunctie2(declaratieIntereseEntity.getFunctie2Entity().getNume());
        }

        if (declaratieIntereseEntity.getInstitutieEntity() != null) {
            declaratieIntereseInfo.setInstitutie(declaratieIntereseEntity.getInstitutieEntity().getNume());
        }

        if (declaratieIntereseEntity.getInstitutie2Entity() != null) {
            declaratieIntereseInfo.setInstitutie2(declaratieIntereseEntity.getInstitutie2Entity().getNume());
        }

        declaratieIntereseInfo.setLinkDeclaratie(declaratieIntereseEntity.getLinkDeclaratie());
        declaratieIntereseInfo.setGrupPolitic(declaratieIntereseEntity.getGrupPolitic());
        declaratieIntereseInfo.setCircumscriptie(declaratieIntereseEntity.getCircumscriptie());
        declaratieIntereseInfo.setIsDone(declaratieIntereseEntity.getIsDone());

        if (eagerLoadAllRelations) {
            List<DeclaratieIntereseAsociatInfo> declaratieIntereseAsociatInfoList = new ArrayList<>();

            if (declaratieIntereseEntity.getDeclaratieIntereseAsociatEntitySet() != null) {
                for (DeclaratieIntereseAsociatEntity declaratieIntereseAsociatEntity : declaratieIntereseEntity.getDeclaratieIntereseAsociatEntitySet()) {
                    DeclaratieIntereseAsociatInfo declaratieInterseAsociatInfo = getDeclaratieAsociatInfo(declaratieIntereseAsociatEntity);
                    declaratieIntereseAsociatInfoList.add(declaratieInterseAsociatInfo);
                }
            }

            declaratieIntereseInfo.setDeclaratieIntereseAsociatInfoList(declaratieIntereseAsociatInfoList);
        }

        return declaratieIntereseInfo;
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
        declaratieActiveBunImobilInfo.setExplicatieSuprafata(declaratieAvereBunImobilEntity.getExplicatieSuprafata());
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
        declaratieActiveBunInstrainatInfo.setDataInstrainarii(declaratieAvereBunInstrainatEntity.getDataInstrainarii());
        declaratieActiveBunInstrainatInfo.setPersoanaBeneficiara(declaratieAvereBunInstrainatEntity.getPersoanaBeneficiara());
        declaratieActiveBunInstrainatInfo.setFormaInstrainarii(declaratieAvereBunInstrainatEntity.getFormaInstrainarii());
        declaratieActiveBunInstrainatInfo.setValoarea(declaratieAvereBunInstrainatEntity.getValoarea());
        declaratieActiveBunInstrainatInfo.setExplicatie(declaratieAvereBunInstrainatEntity.getExplicatieSuma());
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
        declaratieActiveCadouInfo.setExplicatie(declaratieAvereCadouEntity.getExplicatieCadou());
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
        declaratieActiveVenitInfo.setExplicatieVenit(declaratieAvereVenitEntity.getExplicatieVenit());

        return declaratieActiveVenitInfo;
    }

    private DeclaratieAvereBijuterieInfo getDeclaratieActiveBijuterieInfo(DeclaratieAvereBijuterieEntity declaratieAvereBijuterieEntity) {
        DeclaratieAvereBijuterieInfo declaratieActiveBijuterieInfo = new DeclaratieAvereBijuterieInfo();
        declaratieActiveBijuterieInfo.setId(declaratieAvereBijuterieEntity.getId());
        declaratieActiveBijuterieInfo.setDescriere(declaratieAvereBijuterieEntity.getDescriere());
        declaratieActiveBijuterieInfo.setAnDobandire(declaratieAvereBijuterieEntity.getAnDobandire());
        declaratieActiveBijuterieInfo.setValoareEstimate(declaratieAvereBijuterieEntity.getValoareEstimate());
        declaratieActiveBijuterieInfo.setExplicatieBijuterie(declaratieAvereBijuterieEntity.getExplicatieBijuterie());
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
        declaratieActivePlasamentInfo.setExplicatie(declaratieAverePlasamentEntity.getExplicatiePlasament());
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
        declaratieActiveDatorieInfo.setExplicatie(declaratieAvereDatorieEntity.getExplicatieDatorie());

        return declaratieActiveDatorieInfo;
    }

    private DeclaratieAvereContInfo getDeclaratieActiveContInfo(DeclaratieAvereContEntity declaratieAvereContEntity) {
        DeclaratieAvereContInfo declaratieActiveContInfo = new DeclaratieAvereContInfo();
        declaratieActiveContInfo.setId(declaratieAvereContEntity.getId());
        declaratieActiveContInfo.setTitular(declaratieAvereContEntity.getTitular());
        declaratieActiveContInfo.setInstitutieBancara(declaratieAvereContEntity.getInstitutieBancara());
        declaratieActiveContInfo.setTipCont(declaratieAvereContEntity.getTipCont());
        declaratieActiveContInfo.setSoldCont(declaratieAvereContEntity.getSoldCont());
        declaratieActiveContInfo.setExplicatie(declaratieAvereContEntity.getExplicatieSold());
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

    private DeclaratieIntereseAsociatInfo getDeclaratieAsociatInfo(DeclaratieIntereseAsociatEntity declaratieIntereseAsociatEntity) {
        DeclaratieIntereseAsociatInfo declaratieAsociatInfo = new DeclaratieIntereseAsociatInfo();
        declaratieAsociatInfo.setId(declaratieIntereseAsociatEntity.getId());
        declaratieAsociatInfo.setUnitatea(declaratieIntereseAsociatEntity.getUnitatea());
        declaratieAsociatInfo.setRolul(declaratieIntereseAsociatEntity.getRolul());
        declaratieAsociatInfo.setPartiSociale(declaratieIntereseAsociatEntity.getPartiSociale());
        declaratieAsociatInfo.setValoare(declaratieIntereseAsociatEntity.getValoarea());
        declaratieAsociatInfo.setMoneda(declaratieIntereseAsociatEntity.getMoneda());
        declaratieAsociatInfo.setExplicatie(declaratieIntereseAsociatEntity.getExplicatieVenit());

        return declaratieAsociatInfo;
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
        declaratieAvereInfo = getDeclaratieAvereInfo(demnitarEAO.saveDeclaratieAvere(declaratieAvereEntity), true);

        DeclaratieAvereEntitySearchCriteria declaratieAvereEntitySearchCriteria = new DeclaratieAvereEntitySearchCriteria();
        declaratieAvereEntitySearchCriteria.setDemnitarId(declaratieAvereInfo.getDemnitarId());
        declaratieAvereEntitySearchCriteria.setOrderByInfoList(Arrays.asList(new OrderByInfo("dataDeclaratiei", OrderType.DESCENDING)));
        List<DeclaratieAvereEntity> allDeclaratiiAvereEntityList = demnitarEAO.findDeclaratiiAvere(declaratieAvereEntitySearchCriteria);
        DeclaratieAvereEntity mostRecentDeclaratieAvereEntity = allDeclaratiiAvereEntityList.get(0);

        DemnitarEntity demnitarEntity = demnitarEAO.getDemnitar(declaratieAvereInfo.getDemnitarId());
        demnitarEntity.setInstitutieId(mostRecentDeclaratieAvereEntity.getInstitutieId());
        demnitarEntity.setInstitutie2Id(mostRecentDeclaratieAvereEntity.getInstitutie2Id());
        demnitarEntity.setFunctieId(mostRecentDeclaratieAvereEntity.getFunctieId());
        demnitarEntity.setFunctie2Id(mostRecentDeclaratieAvereEntity.getFunctie2Id());
        demnitarEntity.setGrupPolitic(mostRecentDeclaratieAvereEntity.getGrupPolitic());
        demnitarEAO.saveDemnitar(demnitarEntity);

        return declaratieAvereInfo;
    }

    public DeclaratieIntereseInfo saveDeclaratieInterese(DeclaratieIntereseInfo declaratieIntereseInfo) {
        validateDeclaratieInterese(declaratieIntereseInfo);
        DeclaratieIntereseEntity declaratieIntereseEntity = populateDeclaratieIntereseEntity(declaratieIntereseInfo);
        declaratieIntereseInfo = getDeclaratieIntereseInfo(demnitarEAO.saveDeclaratieInterese(declaratieIntereseEntity), true);

        DeclaratieIntereseEntitySearchCriteria declaratieIntereseEntitySearchCriteria = new DeclaratieIntereseEntitySearchCriteria();
        declaratieIntereseEntitySearchCriteria.setDemnitarId(declaratieIntereseInfo.getDemnitarId());
        declaratieIntereseEntitySearchCriteria.setOrderByInfoList(Arrays.asList(new OrderByInfo("dataDeclaratiei", OrderType.DESCENDING)));
        List<DeclaratieIntereseEntity> allDeclaratiiIntereseEntityList = demnitarEAO.findDeclaratiiInterese(declaratieIntereseEntitySearchCriteria);
        DeclaratieIntereseEntity mostRecentDeclaratieIntereseEntity = allDeclaratiiIntereseEntityList.get(0);

        DemnitarEntity demnitarEntity = demnitarEAO.getDemnitar(declaratieIntereseInfo.getDemnitarId());
        demnitarEntity.setInstitutieId(mostRecentDeclaratieIntereseEntity.getInstitutieId());
        demnitarEntity.setInstitutie2Id(mostRecentDeclaratieIntereseEntity.getInstitutie2Id());
        demnitarEntity.setFunctieId(mostRecentDeclaratieIntereseEntity.getFunctieId());
        demnitarEntity.setFunctie2Id(mostRecentDeclaratieIntereseEntity.getFunctie2Id());
        demnitarEntity.setGrupPolitic(mostRecentDeclaratieIntereseEntity.getGrupPolitic());
        demnitarEAO.saveDemnitar(demnitarEntity);

        return declaratieIntereseInfo;
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

    private void validateDeclaratieInterese(DeclaratieIntereseInfo declaratieIntereseInfo) {
        if (declaratieIntereseInfo.getDemnitarId() == null) {
            throw new ValidationException("demnitarId este obligatoriu");
        }

        if (demnitarEAO.getDemnitar(declaratieIntereseInfo.getDemnitarId()) == null) {
            throw new ValidationException("demnitar nu exista");
        }

//        if (Utilities.isEmptyOrNull(declaratieAvereInfo.getFunctie())) {
//            throw new ValidationException("functie este obligatorie");
//        }

//        if (Utilities.isEmptyOrNull(declaratieAvereInfo.getInstitutie())) {
//            throw new ValidationException("institutie este obligatorie");
//        }

        if (declaratieIntereseInfo.getDataDeclaratiei() == null) {
            throw new ValidationException("dataDeclaratiei este obligatorie");
        }

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
        UserInfo loginUserInfo = UserIdentity.getLoginUser();

        if (declaratieAvereInfo.getId() != null) {
            declaratieAvereEntity = demnitarEAO.getDeclaratieAvere(declaratieAvereInfo.getId());

            if (declaratieAvereEntity == null) {
                throw new ValidationException("declaratie avere nu exista");
            }

            if (loginUserInfo.isVolunteer() && !loginUserInfo.getUserId().equals(declaratieAvereEntity.getVoluntarId())) {
                throw new ValidationException("userul nu este autorizat sa editeze declaratia de avere");
            }
        } else {
            declaratieAvereEntity = new DeclaratieAvereEntity();
        }

        if (!loginUserInfo.isVolunteer()) {
            declaratieAvereEntity.setVoluntarId(declaratieAvereInfo.getVoluntarId());
        }

        declaratieAvereEntity.setDemnitarId(declaratieAvereInfo.getDemnitarId());
        declaratieAvereEntity.setDataDeclaratiei(declaratieAvereInfo.getDataDeclaratiei());
        declaratieAvereEntity.setDataDepunerii(declaratieAvereInfo.getDataDepunerii());
        declaratieAvereEntity.setFunctieId(declaratieAvereInfo.getFunctieId());
        declaratieAvereEntity.setFunctie2Id(declaratieAvereInfo.getFunctie2Id());
        declaratieAvereEntity.setInstitutieId(declaratieAvereInfo.getInstitutieId());
        declaratieAvereEntity.setInstitutie2Id(declaratieAvereInfo.getInstitutie2Id());
        declaratieAvereEntity.setLinkDeclaratie(declaratieAvereInfo.getLinkDeclaratie());
        declaratieAvereEntity.setGrupPolitic(declaratieAvereInfo.getGrupPolitic());
        declaratieAvereEntity.setCircumscriptie(declaratieAvereInfo.getCircumscriptie());

        declaratieAvereEntity.setIsDone(declaratieAvereInfo.getIsDone());

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
        populateDeclaratieAvereDatorieEntitySet(declaratieAvereEntity.getDeclaratieAvereDatorieEntitySet(),
                declaratieAvereEntity, declaratieAvereInfo);
        populateDeclaratieAvereVenitEntitySet(declaratieAvereEntity.getDeclaratieAvereVenitEntitySet(),
                declaratieAvereEntity, declaratieAvereInfo);

        return declaratieAvereEntity;
    }

        private DeclaratieIntereseEntity populateDeclaratieIntereseEntity(DeclaratieIntereseInfo declaratieIntereseInfo) {
            DeclaratieIntereseEntity DeclaratieIntereseEntity;
            UserInfo loginUserInfo = UserIdentity.getLoginUser();

            if (declaratieIntereseInfo.getId() != null) {
                DeclaratieIntereseEntity = demnitarEAO.getDeclaratieInterese(declaratieIntereseInfo.getId());

                if (DeclaratieIntereseEntity == null) {
                    throw new ValidationException("declaratie avere nu exista");
                }

                if (loginUserInfo.isVolunteer() && !loginUserInfo.getUserId().equals(DeclaratieIntereseEntity.getVoluntarId())) {
                    throw new ValidationException("userul nu este autorizat sa editeze declaratia de avere");
                }
            } else {
                DeclaratieIntereseEntity = new DeclaratieIntereseEntity();
            }

            if (!loginUserInfo.isVolunteer()) {
                DeclaratieIntereseEntity.setVoluntarId(declaratieIntereseInfo.getVoluntarId());
            }

            DeclaratieIntereseEntity.setDemnitarId(declaratieIntereseInfo.getDemnitarId());
            DeclaratieIntereseEntity.setDataDeclaratiei(declaratieIntereseInfo.getDataDeclaratiei());
            DeclaratieIntereseEntity.setDataDepunerii(declaratieIntereseInfo.getDataDepunerii());
            DeclaratieIntereseEntity.setFunctieId(declaratieIntereseInfo.getFunctieId());
            DeclaratieIntereseEntity.setFunctie2Id(declaratieIntereseInfo.getFunctie2Id());
            DeclaratieIntereseEntity.setInstitutieId(declaratieIntereseInfo.getInstitutieId());
            DeclaratieIntereseEntity.setInstitutie2Id(declaratieIntereseInfo.getInstitutie2Id());
            DeclaratieIntereseEntity.setLinkDeclaratie(declaratieIntereseInfo.getLinkDeclaratie());
            DeclaratieIntereseEntity.setGrupPolitic(declaratieIntereseInfo.getGrupPolitic());
            DeclaratieIntereseEntity.setCircumscriptie(declaratieIntereseInfo.getCircumscriptie());

            DeclaratieIntereseEntity.setIsDone(declaratieIntereseInfo.getIsDone());

            populateDeclaratieIntereseAsociatEntitySet(DeclaratieIntereseEntity.getDeclaratieIntereseAsociatEntitySet(),
                    DeclaratieIntereseEntity, declaratieIntereseInfo);


            return DeclaratieIntereseEntity;
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
        declaratieAvereVenitEntity.setExplicatieVenit(declaratieAvereVenitInfo.getExplicatieVenit());
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

    private void populateDeclaratieAvereCadouEntity(DeclaratieAvereCadouEntity declaratieAvereCadouEntity, DeclaratieAvereEntity declaratieAvereEntity,
                                                    DeclaratieAvereCadouInfo declaratieAvereCadouInfo) {
        declaratieAvereCadouEntity.setDeclaratieAvereEntity(declaratieAvereEntity);
        declaratieAvereCadouEntity.setTitular(declaratieAvereCadouInfo.getTitular());
        declaratieAvereCadouEntity.setSursaVenit(declaratieAvereCadouInfo.getSursaVenit());
        declaratieAvereCadouEntity.setServiciulPrestat(declaratieAvereCadouInfo.getServiciulPrestat());
        declaratieAvereCadouEntity.setVenit(declaratieAvereCadouInfo.getVenit());
        declaratieAvereCadouEntity.setMoneda(declaratieAvereCadouInfo.getMoneda());
        declaratieAvereCadouEntity.setExplicatieCadou(declaratieAvereCadouInfo.getExplicatie());
    }

    private void populateDeclaratieAvereDatorieEntitySet(Set<DeclaratieAvereDatorieEntity> declaratieAvereDatorieEntitySet,
                                                       DeclaratieAvereEntity declaratieAvereEntity, DeclaratieAvereInfo declaratieAvereInfo) {
        if (declaratieAvereDatorieEntitySet == null) {
            declaratieAvereDatorieEntitySet = new HashSet<>();
            declaratieAvereEntity.setDeclaratieAvereDatorieEntitySet(declaratieAvereDatorieEntitySet);
        }

        // prepare maps that are needed in order to identify what entities news to be added, modified and deleted
        Map<Integer, DeclaratieAvereDatorieInfo> declaratieAvereDatorieInfoByIdHashMap = new HashMap<>();

        if (declaratieAvereInfo.getDeclaratieAvereDatorieInfoList() != null) {
            for (DeclaratieAvereDatorieInfo declaratieAvereDatorieInfo : declaratieAvereInfo.getDeclaratieAvereDatorieInfoList()) {
                declaratieAvereDatorieInfoByIdHashMap.put(declaratieAvereDatorieInfo.getId(), declaratieAvereDatorieInfo);
            }
        }

        Map<Integer, DeclaratieAvereDatorieEntity> declaratieAvereDatorieEntityByIdMap = new HashMap<>();

        for (DeclaratieAvereDatorieEntity declaratieAvereDatorieEntity : declaratieAvereDatorieEntitySet) {
            declaratieAvereDatorieEntityByIdMap.put(declaratieAvereDatorieEntity.getId(), declaratieAvereDatorieEntity);
        }


        if (declaratieAvereInfo.getDeclaratieAvereDatorieInfoList() != null) {
            for (DeclaratieAvereDatorieInfo declaratieAvereDatorieInfo : declaratieAvereInfo.getDeclaratieAvereDatorieInfoList()) {
                DeclaratieAvereDatorieEntity declaratieAvereDatorieEntity = declaratieAvereDatorieEntityByIdMap.get(declaratieAvereDatorieInfo.getId());

                if (declaratieAvereDatorieEntity == null) {
                    declaratieAvereDatorieEntity = new DeclaratieAvereDatorieEntity();
                    declaratieAvereDatorieEntitySet.add(declaratieAvereDatorieEntity);
                }

                populateDeclaratieAvereDatorieEntity(declaratieAvereDatorieEntity, declaratieAvereEntity,
                        declaratieAvereDatorieInfo);
            }
        }

        // delete the relevant DeclaratieAvereDatorieEntities
        Iterator<DeclaratieAvereDatorieEntity> declaratieAvereDatorieEntitySetIterator =
                declaratieAvereDatorieEntitySet.iterator();

        while (declaratieAvereDatorieEntitySetIterator.hasNext()) {
            DeclaratieAvereDatorieEntity declaratieAvereDatorieEntity = declaratieAvereDatorieEntitySetIterator.next();

            if (!declaratieAvereDatorieInfoByIdHashMap.containsKey(declaratieAvereDatorieEntity.getId())) {
                declaratieAvereDatorieEntitySetIterator.remove();
            }
        }
    }

    private void populateDeclaratieAvereDatorieEntity(DeclaratieAvereDatorieEntity declaratieAvereDatorieEntity, DeclaratieAvereEntity declaratieAvereEntity,
                                                    DeclaratieAvereDatorieInfo declaratieAvereDatorieInfo) {
        declaratieAvereDatorieEntity.setDeclaratieAvereEntity(declaratieAvereEntity);
        declaratieAvereDatorieEntity.setCreditor(declaratieAvereDatorieInfo.getCreditor());
        declaratieAvereDatorieEntity.setAnContractare(declaratieAvereDatorieInfo.getAnContractare());
        declaratieAvereDatorieEntity.setScadenta(declaratieAvereDatorieInfo.getScadenta());
        declaratieAvereDatorieEntity.setValoare(declaratieAvereDatorieInfo.getValoare());
        declaratieAvereDatorieEntity.setMoneda(declaratieAvereDatorieInfo.getMoneda());
        declaratieAvereDatorieEntity.setExplicatieDatorie(declaratieAvereDatorieInfo.getExplicatie());
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

    private void populateDeclaratieAvereBunInstrainatEntity(DeclaratieAvereBunInstrainatEntity declaratieAvereBunInstrainatEntity,
                                                            DeclaratieAvereEntity declaratieAvereEntity,
                                                            DeclaratieAvereBunInstrainatInfo declaratieAvereBunInstrainatInfo) {
        declaratieAvereBunInstrainatEntity.setDeclaratieAvereEntity(declaratieAvereEntity);
        declaratieAvereBunInstrainatEntity.setTip(declaratieAvereBunInstrainatInfo.getTip());
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
        declaratieAvereContEntity.setExplicatieSold(declaratieAvereContInfo.getExplicatie());
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
        declaratieAverePlasamentEntity.setExplicatiePlasament(declaratieAverePlasamentInfo.getExplicatie());
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
        declaratieAvereBijuterieEntity.setExplicatieBijuterie(declaratieAvereBijuterieInfo.getExplicatieBijuterie());
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
        declaratieAvereBunImobilEntity.setExplicatieSuprafata(declaratieAvereBunImobilInfo.getExplicatieSuprafata());
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

    private void populateDeclaratieIntereseAsociatEntitySet(Set<DeclaratieIntereseAsociatEntity> declaratieIntereseAsociatEntitySet,
                                                            DeclaratieIntereseEntity declaratieIntereseEntity,
                                                            DeclaratieIntereseInfo declaratieIntereseInfo) {
        if (declaratieIntereseAsociatEntitySet == null) {
            declaratieIntereseAsociatEntitySet = new HashSet<>();
            declaratieIntereseEntity.setDeclaratieIntereseAsociatEntitySet(declaratieIntereseAsociatEntitySet);
        }

        // prepare maps that are needed in order to identify what entities news to be added, modified and deleted
        Map<Integer, DeclaratieIntereseAsociatInfo> declaratieIntereseAsociatInfoByIdHashMap = new HashMap<>();

        if (declaratieIntereseInfo.getDeclaratieIntereseAsociatInfoList() != null) {
            for (DeclaratieIntereseAsociatInfo declaratieIntereseAsociatInfo : declaratieIntereseInfo.getDeclaratieIntereseAsociatInfoList()) {
                declaratieIntereseAsociatInfoByIdHashMap.put(declaratieIntereseAsociatInfo.getId(), declaratieIntereseAsociatInfo);
            }
        }

        Map<Integer, DeclaratieIntereseAsociatEntity> declaratieIntereseAsociatEntityByIdMap = new HashMap<>();

        for (DeclaratieIntereseAsociatEntity declaratieAvereAlteActiveEntity : declaratieIntereseAsociatEntitySet) {
            declaratieIntereseAsociatEntityByIdMap.put(declaratieAvereAlteActiveEntity.getId(), declaratieAvereAlteActiveEntity);
        }


        if (declaratieIntereseInfo.getDeclaratieIntereseAsociatInfoList() != null) {
            for (DeclaratieIntereseAsociatInfo declaratieIntereseAsociatInfo : declaratieIntereseInfo.getDeclaratieIntereseAsociatInfoList()) {
                DeclaratieIntereseAsociatEntity declaratieIntereseAsociatEntity = declaratieIntereseAsociatEntityByIdMap.get(declaratieIntereseAsociatInfo.getId());

                if (declaratieIntereseAsociatEntity == null) {
                    declaratieIntereseAsociatEntity = new DeclaratieIntereseAsociatEntity();
                    declaratieIntereseAsociatEntitySet.add(declaratieIntereseAsociatEntity);
                }

                populateDeclaratieIntereseAsociatEntity(declaratieIntereseAsociatEntity, declaratieIntereseEntity,
                        declaratieIntereseAsociatInfo);
            }
        }

        // delete the relevant DeclaratieIntereseAsociatEntity
        Iterator<DeclaratieIntereseAsociatEntity> declaratieIntereseAsociatEntitySetIterator =
                declaratieIntereseAsociatEntitySet.iterator();

        while (declaratieIntereseAsociatEntitySetIterator.hasNext()) {
            DeclaratieIntereseAsociatEntity declaratieAvereAlteActiveEntity = declaratieIntereseAsociatEntitySetIterator.next();

            if (!declaratieIntereseAsociatInfoByIdHashMap.containsKey(declaratieAvereAlteActiveEntity.getId())) {
                declaratieIntereseAsociatEntitySetIterator.remove();
            }
        }
    }

    private void populateDeclaratieIntereseAsociatEntity(DeclaratieIntereseAsociatEntity declaratieIntereseAsociatEntity,
                                                         DeclaratieIntereseEntity declaratieIntereseEntity,
                                                         DeclaratieIntereseAsociatInfo declaratieIntereseAsociatInfo) {
        declaratieIntereseAsociatEntity.setDeclaratieIntereseEntity(declaratieIntereseEntity);
        declaratieIntereseAsociatEntity.setUnitatea(declaratieIntereseAsociatInfo.getUnitatea());
        declaratieIntereseAsociatEntity.setRolul(declaratieIntereseAsociatInfo.getRolul());
        declaratieIntereseAsociatEntity.setPartiSociale(declaratieIntereseAsociatInfo.getPartiSociale());
        declaratieIntereseAsociatEntity.setValoarea(declaratieIntereseAsociatInfo.getValoare());
        declaratieIntereseAsociatEntity.setMoneda(declaratieIntereseAsociatInfo.getMoneda());
        declaratieIntereseAsociatEntity.setExplicatieVenit(declaratieIntereseAsociatInfo.getExplicatie());
    }
    /**
     * Gets a declaratieAvere.
     *
     * @param id The declaratieAvere id
     * @return The DeclaratieAvereInfo
     */
    @Transactional(readOnly = true)
    public DeclaratieAvereInfo getDeclaratieAvere(Integer id) {
        if (id == null) {
            throw new ValidationException("id is required");
        }

        DeclaratieAvereEntity declaratieAvereEntity = demnitarEAO.getDeclaratieAvere(id);

        if (declaratieAvereEntity == null) {
            throw new ValidationException("declaratie avere nu exista");
        }

        return getDeclaratieAvereInfo(declaratieAvereEntity, true);
    }

    public DeclaratieIntereseInfo getDeclaratieInterese(Integer id) {
        if (id == null) {
            throw new ValidationException("id is required");
        }

        DeclaratieIntereseEntity declaratieIntereseEntity = demnitarEAO.getDeclaratieInterese(id);

        if (declaratieIntereseEntity == null) {
            throw new ValidationException("declaratie avere nu exista");
        }

        return getDeclaratieIntereseInfo(declaratieIntereseEntity, true);
    }

    /**
     * Finds declaratii avere by the specified search criteria.
     *
     * @param searchDeclaratieAvereCriteria The SearchDeclaratieAvereCriteria object
     * @return The List of DeclaratieAvereInfo objects
     */
    @Transactional(readOnly = true)
    public List<DeclaratieAvereInfo> findDeclaratiiAvere(SearchDeclaratieAvereCriteria searchDeclaratieAvereCriteria) throws RestException {
        List<DeclaratieAvereInfo> declaratieAvereInfoList = new ArrayList<>();

        DeclaratieAvereEntitySearchCriteria declaratieAvereEntitySearchCriteria = new DeclaratieAvereEntitySearchCriteria();
        declaratieAvereEntitySearchCriteria.setDemnitarId(searchDeclaratieAvereCriteria.getDemnitarId());
        declaratieAvereEntitySearchCriteria.setDataDeclaratiei(searchDeclaratieAvereCriteria.getDataDeclaratiei());
        declaratieAvereEntitySearchCriteria.setFunctieId(searchDeclaratieAvereCriteria.getFunctieId());
        declaratieAvereEntitySearchCriteria.setInstitutieId(searchDeclaratieAvereCriteria.getInstitutieId());

        if (UserIdentity.getLoginUser() != null && UserIdentity.getLoginUser().isVolunteer()) {
            declaratieAvereEntitySearchCriteria.setVoluntarId(UserIdentity.getLoginUser().getUserId());
        } else {
            declaratieAvereEntitySearchCriteria.setVoluntarId(searchDeclaratieAvereCriteria.getVoluntarId());
        }

        if (!Utilities.isEmptyOrNull(searchDeclaratieAvereCriteria.getDemnitarNumeLike())) {
            declaratieAvereEntitySearchCriteria.setDemnitarNumeLike(searchDeclaratieAvereCriteria.getDemnitarNumeLike() + "%");
        }

        if (!Utilities.isEmptyOrNull(searchDeclaratieAvereCriteria.getDemnitarPrenumeLike())) {
            declaratieAvereEntitySearchCriteria.setDemnitarPrenumeLike(searchDeclaratieAvereCriteria.getDemnitarPrenumeLike() + "%");
        }

        if (searchDeclaratieAvereCriteria.getAnulDeclaratiei() != null) {
            declaratieAvereEntitySearchCriteria.setStartDataDeclaratiei(DateUtilities.parseDate(searchDeclaratieAvereCriteria.getAnulDeclaratiei()
                    + "-01-01", "yyyy-MM-dd"));
            declaratieAvereEntitySearchCriteria.setEndDataDeclaratiei(DateUtilities.parseDate((searchDeclaratieAvereCriteria.getAnulDeclaratiei())
                    + "-12-31", "yyyy-MM-dd"));
        }

        declaratieAvereEntitySearchCriteria.setStatus(searchDeclaratieAvereCriteria.getStatus());

        if (searchDeclaratieAvereCriteria.getDemnitarId() != null) {
            declaratieAvereEntitySearchCriteria.setEagerLoadAllRelations(true);
        }

        List<DeclaratieAvereEntity> declaratieAvereEntityList = demnitarEAO.findDeclaratiiAvere(declaratieAvereEntitySearchCriteria);
        Set<Integer> voluntarIdSet = new HashSet<>();

        for (DeclaratieAvereEntity declaratieAvereEntity : declaratieAvereEntityList) {
            if (declaratieAvereEntity.getVoluntarId() != null) {
                voluntarIdSet.add(declaratieAvereEntity.getVoluntarId());
            }
        }

        for (DeclaratieAvereEntity declaratieAvereEntity : declaratieAvereEntityList) {
            declaratieAvereInfoList.add(getDeclaratieAvereInfo(declaratieAvereEntity, declaratieAvereEntitySearchCriteria.isEagerLoadAllRelations()));
        }

        if (!voluntarIdSet.isEmpty()) {
            Map<Integer, UserInfo> voluntarUserInfoByIdMap = new HashMap<>();
            voluntarUserInfoByIdMap = userServiceAdapter.getUserInfoByIdMap(voluntarIdSet);

            for (DeclaratieAvereInfo declaratieAvereInfo : declaratieAvereInfoList) {
                if (declaratieAvereInfo.getVoluntarId() != null) {
                    UserInfo voluntarUserInfo = voluntarUserInfoByIdMap.get(declaratieAvereInfo.getVoluntarId());
                    declaratieAvereInfo.setVoluntarUserName(voluntarUserInfo.getUsername());
                }
            }
        }

        ObjectComparator<DeclaratieAvereInfo> objectComparator = new ObjectComparator<>(DeclaratieAvereInfo.class,
                Arrays.asList("demnitarNume",
                        "demnitarPrenume", "negativeTimestamp"));
        declaratieAvereInfoList.sort(objectComparator);

        return declaratieAvereInfoList;
    }

    public List<DeclaratieIntereseInfo> findDeclaratiiInterese(SearchDeclaratieIntereseCriteria searchDeclaratieIntereseCriteria) throws RestException {
        List<DeclaratieIntereseInfo> declaratieIntereseInfoList = new ArrayList<>();

        DeclaratieIntereseEntitySearchCriteria declaratieIntereseEntitySearchCriteria = new DeclaratieIntereseEntitySearchCriteria();
        declaratieIntereseEntitySearchCriteria.setDemnitarId(searchDeclaratieIntereseCriteria.getDemnitarId());
        declaratieIntereseEntitySearchCriteria.setDataDeclaratiei(searchDeclaratieIntereseCriteria.getDataDeclaratiei());
        declaratieIntereseEntitySearchCriteria.setFunctieId(searchDeclaratieIntereseCriteria.getFunctieId());
        declaratieIntereseEntitySearchCriteria.setInstitutieId(searchDeclaratieIntereseCriteria.getInstitutieId());

        if (UserIdentity.getLoginUser() != null && UserIdentity.getLoginUser().isVolunteer()) {
            declaratieIntereseEntitySearchCriteria.setVoluntarId(UserIdentity.getLoginUser().getUserId());
        } else {
            declaratieIntereseEntitySearchCriteria.setVoluntarId(searchDeclaratieIntereseCriteria.getVoluntarId());
        }

        if (!Utilities.isEmptyOrNull(searchDeclaratieIntereseCriteria.getDemnitarNumeLike())) {
            declaratieIntereseEntitySearchCriteria.setDemnitarNumeLike(searchDeclaratieIntereseCriteria.getDemnitarNumeLike() + "%");
        }

        if (!Utilities.isEmptyOrNull(searchDeclaratieIntereseCriteria.getDemnitarPrenumeLike())) {
            declaratieIntereseEntitySearchCriteria.setDemnitarPrenumeLike(searchDeclaratieIntereseCriteria.getDemnitarPrenumeLike() + "%");
        }

        if (searchDeclaratieIntereseCriteria.getAnulDeclaratiei() != null) {
            declaratieIntereseEntitySearchCriteria.setStartDataDeclaratiei(DateUtilities.parseDate(searchDeclaratieIntereseCriteria.getAnulDeclaratiei()
                    + "-01-01", "yyyy-MM-dd"));
            declaratieIntereseEntitySearchCriteria.setEndDataDeclaratiei(DateUtilities.parseDate((searchDeclaratieIntereseCriteria.getAnulDeclaratiei())
                    + "-12-31", "yyyy-MM-dd"));
        }

        declaratieIntereseEntitySearchCriteria.setStatus(searchDeclaratieIntereseCriteria.getStatus());

        if (searchDeclaratieIntereseCriteria.getDemnitarId() != null) {
            declaratieIntereseEntitySearchCriteria.setEagerLoadAllRelations(true);
        }

        List<DeclaratieIntereseEntity> declaratieIntereseEntityList = demnitarEAO.findDeclaratiiInterese(declaratieIntereseEntitySearchCriteria);
        Set<Integer> voluntarIdSet = new HashSet<>();

        for (DeclaratieIntereseEntity declaratieIntereseEntity : declaratieIntereseEntityList) {
            if (declaratieIntereseEntity.getVoluntarId() != null) {
                voluntarIdSet.add(declaratieIntereseEntity.getVoluntarId());
            }
        }

        for (DeclaratieIntereseEntity declaratieIntereseEntity : declaratieIntereseEntityList) {
            declaratieIntereseInfoList.add(getDeclaratieIntereseInfo(declaratieIntereseEntity, declaratieIntereseEntitySearchCriteria.isEagerLoadAllRelations()));
        }

        if (!voluntarIdSet.isEmpty()) {
            Map<Integer, UserInfo> voluntarUserInfoByIdMap = new HashMap<>();
            voluntarUserInfoByIdMap = userServiceAdapter.getUserInfoByIdMap(voluntarIdSet);

            for (DeclaratieIntereseInfo declaratieIntereseInfo : declaratieIntereseInfoList) {
                if (declaratieIntereseInfo.getVoluntarId() != null) {
                    UserInfo voluntarUserInfo = voluntarUserInfoByIdMap.get(declaratieIntereseInfo.getVoluntarId());
                    declaratieIntereseInfo.setVoluntarUserName(voluntarUserInfo.getUsername());
                }
            }
        }

        ObjectComparator<DeclaratieIntereseInfo> objectComparator = new ObjectComparator<>(DeclaratieIntereseInfo.class,
                Arrays.asList("demnitarNume",
                        "demnitarPrenume", "negativeTimestamp"));
        declaratieIntereseInfoList.sort(objectComparator);

        return declaratieIntereseInfoList;
    }
}
