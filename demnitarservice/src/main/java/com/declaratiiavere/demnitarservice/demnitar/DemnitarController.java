package com.declaratiiavere.demnitarservice.demnitar;

import com.declaratiiavere.restclient.RestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Demnitar controller.
 *
 * @author Razvan Dani
 */
@RestController
@RequestMapping(value = "/demnitar")
public class DemnitarController {
    @Autowired
    private DemnitarService demnitarService;

    /**
     * Gets a demnitar by id.
     *
     * @param id            The demnitar id
     * @return              The DemnitarInfo
     */
    @RequestMapping(value = "/{id}/", method = RequestMethod.GET)
    public DemnitarInfo getDemnitar(@PathVariable Integer id) {
        return demnitarService.getDemnitar(id);
    }

    /**
     * Creates a demnitar.
     *
     * @param demnitarInfo  The DemnitarInfo
     * @return              The created DemnitarInfo
     */
    @RequestMapping(method = RequestMethod.POST)
    public DemnitarInfo createDemnitar(@RequestBody DemnitarInfo demnitarInfo) {
        return demnitarService.saveDemnitar(demnitarInfo);
    }

    /**
     * Updates a demnitar.
     *
     * @param demnitarInfo  The DemnitarInfo
     * @return              The updated DemnitarInfo
     */
    @RequestMapping(method = RequestMethod.PUT)
    public DemnitarInfo updateDemnitar(@RequestBody DemnitarInfo demnitarInfo) {
        return demnitarService.saveDemnitar(demnitarInfo);
    }

    /**
     * Finds demnitars.
     *
     * @param searchDemnitarCrtieria    The search criteria
     * @return                          The list of DemnitarInfo objects
     */
    @RequestMapping(value = "/find", method = RequestMethod.POST)
    public List<DemnitarInfo> findDemnitars(@RequestBody SearchDemnitarCriteria searchDemnitarCrtieria) {
        return demnitarService.findDemnitars(searchDemnitarCrtieria);
    }

    /**
     * Gets a declaratie avere by id.
     *
     * @param id            The declaratie avere id
     * @return              The DeclaratieAvereInfo
     */
    @RequestMapping(value = "/declaratieavere/{id}/", method = RequestMethod.GET)
    public DeclaratieAvereInfo getDeclaratieAvere(@PathVariable Integer id) {
        return demnitarService.getDeclaratieAvere(id);
    }

    @RequestMapping(value = "/declaratieinterese/{id}/", method = RequestMethod.GET)
    public DeclaratieIntereseInfo getDeclaratieInterese(@PathVariable Integer id) {
        return demnitarService.getDeclaratieInterese(id);
    }

    /**
     * Creates a declaratieAvere.
     *
     * @param declaratieAvereInfo  The DeclaratieAvereInfo
     * @return              The created DeclaratieAvereInfo
     */
    @RequestMapping(value = "/declaratieavere", method = RequestMethod.POST)
    public DeclaratieAvereInfo createDeclaratieAvere(@RequestBody DeclaratieAvereInfo declaratieAvereInfo) {
        return demnitarService.saveDeclaratieAvere(declaratieAvereInfo);
    }

    @RequestMapping(value = "/declaratieinterese", method = RequestMethod.POST)
    public DeclaratieIntereseInfo createDeclaratieInterese(@RequestBody DeclaratieIntereseInfo declaratieIntereseInfo) {
        return demnitarService.saveDeclaratieInterese(declaratieIntereseInfo);
    }

    /**
     * Updates a declaratieAvere.
     *
     * @param declaratieAvereInfo  The DeclaratieAvereInfo
     * @return              The updated DeclaratieAvereInfo
     */
    @RequestMapping(value = "/declaratieavere", method = RequestMethod.PUT)
    public DeclaratieAvereInfo updateDeclaratieAvere(@RequestBody DeclaratieAvereInfo declaratieAvereInfo) {
        return demnitarService.saveDeclaratieAvere(declaratieAvereInfo);
    }

    @RequestMapping(value = "/declaratieinterese", method = RequestMethod.PUT)
    public DeclaratieIntereseInfo updateDeclaratieInterese(@RequestBody DeclaratieIntereseInfo declaratieIntereseInfo) {
        return demnitarService.saveDeclaratieInterese(declaratieIntereseInfo);
    }

    /**
     * Finds declaratieAveres.
     *
     * @param searchDeclaratieAvereCriteria    The search criteria
     * @return                          The list of DeclaratieAvereInfo objects
     */
    @RequestMapping(value = "/declaratieavere/find", method = RequestMethod.POST)
    public List<DeclaratieAvereInfo> findDeclaratieAveres(@RequestBody SearchDeclaratieAvereCriteria searchDeclaratieAvereCriteria) throws RestException {
        return demnitarService.findDeclaratiiAvere(searchDeclaratieAvereCriteria);
    }

    @RequestMapping(value = "/declaratieinterese/find", method = RequestMethod.POST)
    public List<DeclaratieIntereseInfo> findDeclaratieInterese(@RequestBody SearchDeclaratieIntereseCriteria searchDeclaratieIntereseCriteria) throws RestException {
        return demnitarService.findDeclaratiiInterese(searchDeclaratieIntereseCriteria);
    }

    @RequestMapping(value = "/functie/find", method = RequestMethod.POST)
    public List<FunctieInfo> findAllFunctii() {
        return demnitarService.findAllFunctii();
    }

    @RequestMapping(value = "/institutie/find", method = RequestMethod.POST)
    public List<InstitutieInfo> findAllInstitutii() {
        return demnitarService.findAllInstitutii();
    }
}
