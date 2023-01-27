package com.ticketing.api.controller;

import com.ticketing.api.model.request.RoleRequestModel;
import com.ticketing.api.model.response.ResponseModel;
import com.ticketing.api.model.response.UIBean;
import com.ticketing.api.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.ValidationException;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("api/v1/role")
public class RoleController {

    private static final String READ_WRITE = "hasPermission('manage_users', 'view & edit')";

    private static final String READ = "hasPermission('manage_users', 'view')";

    @Autowired
    private RoleService roleService;

    @PreAuthorize(READ_WRITE)
    @PostMapping(path = "/createRole")
    public ResponseEntity<ResponseModel> createNewRole(@RequestBody final RoleRequestModel roleDetails) {
        ResponseModel responseModel = null;
        try {
            roleService.createRole(roleDetails);
            responseModel = new ResponseModel(OK, "Role created successfully.", null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.CREATED);
        } catch (ValidationException exception) {
            responseModel = new ResponseModel(1001, exception.getMessage(), null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
        }
    }


    @PreAuthorize(READ)
    @GetMapping(value = "/getAll")
    public ResponseEntity<ResponseModel> getAllRoles(final Pageable pageable) {
        final UIBean data = roleService.getAllRoles(pageable);
        final ResponseModel responseModel = new ResponseModel(OK, "Operation completed successfully.", null,
                data);

        return new ResponseEntity<>(responseModel, OK);
    }


    @PreAuthorize(READ_WRITE)
    @PostMapping(path = "/update")
    public ResponseEntity<ResponseModel> updateRole(@RequestBody final RoleRequestModel roleDetails) {
        ResponseModel responseModel = null;
        try {
            roleService.updateRole(roleDetails);
            responseModel = new ResponseModel(OK, "Role updated successfully.", null, null);
            return new ResponseEntity<>(responseModel, OK);
        } catch (HttpClientErrorException.BadRequest exception) {
            responseModel = new ResponseModel(1001, exception.getMessage(), null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);

        }
    }
}
