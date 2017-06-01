package com.declaratiiavere.demnitarservice.demnitar;

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

    /**
     * Finds declaratieAveres.
     *
     * @param searchDeclaratieAvereCrtieria    The search criteria
     * @return                          The list of DeclaratieAvereInfo objects
     */
    @RequestMapping(value = "/declaratieavere/find", method = RequestMethod.POST)
    public List<DeclaratieAvereInfo> findDeclaratieAveres(@RequestBody SearchDeclaratieAvereCriteria searchDeclaratieAvereCrtieria) {
        return demnitarService.findDeclaratiiAvere(searchDeclaratieAvereCrtieria);
    }
}
