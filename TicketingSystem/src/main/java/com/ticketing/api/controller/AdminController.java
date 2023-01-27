package com.ticketing.api.controller;

import com.ticketing.api.exception.ConstraintViolationException;
import com.ticketing.api.model.request.RemoveUserRequest;
import com.ticketing.api.model.request.ResetUserPasswordRequest;
import com.ticketing.api.model.request.UserCreateRequestModel;
import com.ticketing.api.model.response.ResponseModel;
import com.ticketing.api.model.response.UIBean;
import com.ticketing.api.service.AdminService;
import com.ticketing.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.ValidationException;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("api/v1/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    private static final String READ_WRITE = "hasPermission('manage_users', 'view & edit')";

    private static final String READ = "hasPermission('manage_users', 'view')";



    @PreAuthorize(READ_WRITE)
    @PostMapping(path = "/createUser")
    public ResponseEntity<ResponseModel> createUser(@RequestBody final UserCreateRequestModel userDetails) {
        ResponseModel responseModel = null;
        try {
            adminService.addUser(userDetails);
            responseModel = new ResponseModel(CREATED, "User created successfully.", null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.CREATED);
        } catch (ValidationException exception) {
            responseModel = new ResponseModel(1001, exception.getMessage(), null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize(READ)
    @GetMapping(value = "/getAllUsers")
    public ResponseEntity<ResponseModel> getAllUsers(@RequestParam(required = false) String userId, final Pageable pageable) {
        final UIBean data = userService.getAllUsers(userId, pageable);
        final ResponseModel responseModel = new ResponseModel(OK, "Operation completed successfully.", null,
                data);

        return new ResponseEntity<>(responseModel, OK);
    }

    @PreAuthorize(READ_WRITE)
    @PostMapping(path = "/resetUserPassword")
    public ResponseEntity<?> resetUserPassword(@RequestBody ResetUserPasswordRequest resetUserPasswordRequest) {
        ResponseModel responseModel = null;
        try {
            return adminService.resetUserPassword(resetUserPasswordRequest);
        } catch (EntityNotFoundException exception) {
            responseModel = new ResponseModel(404, exception.getMessage(), null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize(READ_WRITE)
    @PutMapping(path = "/removeUser", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity removeUser(@Valid @RequestBody RemoveUserRequest removeUserRequest) {
        ResponseModel responseModel;
        try {
            UIBean returnValue = userService.removeUser(removeUserRequest);
            return new ResponseEntity<>(returnValue, HttpStatus.OK);
        } catch (ConstraintViolationException exception) {
            responseModel = new ResponseModel(400, exception.getMessage(), null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
        }
    }
}
