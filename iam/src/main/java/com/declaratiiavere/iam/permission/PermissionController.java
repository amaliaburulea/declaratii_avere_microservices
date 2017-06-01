package com.declaratiiavere.iam.permission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Permission controller.
 *
 * @author Viorel Vesa
 */
@RestController
@RequestMapping(value = "/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @RequestMapping(value = "/{permissionId}/", method = RequestMethod.GET)
    public PermissionInfo getPermission(@PathVariable Integer permissionId) {
        return permissionService.getPermission(permissionId);
    }

    @RequestMapping(value = "/find", method = RequestMethod.POST)
    public List<PermissionInfo> findPermissions(@RequestBody SearchPermissionCriteria searchPermissionCriteria) {
        return permissionService.findPermissions(searchPermissionCriteria);
    }

    @RequestMapping(method = RequestMethod.POST)
    public PermissionInfo savePermission(@RequestBody PermissionInfo permissionInfo) {
        return permissionService.savePermission(permissionInfo);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public PermissionInfo updatePermission(@RequestBody PermissionInfo permissionInfo) {
        return permissionService.savePermission(permissionInfo);
    }

    @RequestMapping(value = "/{permissionId}/", method = RequestMethod.DELETE)
    public String deletePermission(@PathVariable Integer permissionId) {
        permissionService.deletePermission(permissionId);

        return "SUCCESS";
    }

    @RequestMapping(value = "/checkPermissionForRequest", method = RequestMethod.POST)
    public PermissionValidationResponseInfo checkPermissionForRequest(@RequestBody RestRequestPermissionValidationInfo restRequestPermissionValidationInfo) {
        return permissionService.checkPermissionForRequest(restRequestPermissionValidationInfo);
    }
}
