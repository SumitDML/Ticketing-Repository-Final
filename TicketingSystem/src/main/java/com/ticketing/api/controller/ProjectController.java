package com.ticketing.api.controller;


import com.ticketing.api.exception.ConstraintViolationException;
import com.ticketing.api.exception.ValidationException;
import com.ticketing.api.model.request.AssignUserToProjectRequest;
import com.ticketing.api.model.request.RemoveUserFromProjectRequest;
import com.ticketing.api.model.request.ProjectCreationRequest;
import com.ticketing.api.model.request.ProjectUpdateRequest;
import com.ticketing.api.model.response.ResponseModel;
import com.ticketing.api.model.response.UIBean;
import com.ticketing.api.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @PostMapping("/createProject")
    public ResponseEntity<ResponseModel> addProject(@RequestBody @Valid ProjectCreationRequest projectCreationRequest) {
        ResponseModel responseModel = null;
        try {
            projectService.addProject(projectCreationRequest);
            responseModel = new ResponseModel(OK, "Project created successfully.", null, null);

            return new ResponseEntity<>(responseModel, HttpStatus.CREATED);
        } catch (ValidationException | ConstraintViolationException e) {
            return new ResponseEntity(new ResponseModel<>(HttpStatus.BAD_REQUEST, e.getMessage(), null, null), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/updateProject")
    public ResponseEntity<UIBean<?>> updateProject(@RequestBody @Valid ProjectUpdateRequest projectUpdateRequest) {
        try {
            UIBean<?> projectCreation = projectService.updateProject(projectUpdateRequest);
            return new ResponseEntity<>(projectCreation, HttpStatus.OK);
        } catch (ValidationException | ConstraintViolationException e) {
            return new ResponseEntity(new ResponseModel<>(HttpStatus.NOT_FOUND, e.getMessage(), null, null), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/assignUsers")
    public ResponseEntity<?> addUsersInProject(@RequestBody @Valid AssignUserToProjectRequest assignUserToProjectRequest) {
        ResponseModel responseModel = null;
        try {
            UIBean returnValue = projectService.addUsersInProject(assignUserToProjectRequest);
            return new ResponseEntity<>(returnValue, HttpStatus.OK);
        } catch (ValidationException | ConstraintViolationException e) {
            return new ResponseEntity(new ResponseModel<>(HttpStatus.NOT_FOUND, e.getMessage(), null, null), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/removeUser")
    public ResponseEntity<?> removeUserFromProject(@RequestBody @Valid RemoveUserFromProjectRequest removeUserFromProjectRequest) {
        ResponseModel responseModel = null;
        try {
            UIBean returnValue = projectService.removeUserFromProject(removeUserFromProjectRequest);
            return new ResponseEntity<>(returnValue, HttpStatus.OK);
        }
        catch (ValidationException | ConstraintViolationException e)
        {
            return new ResponseEntity(new ResponseModel<>(HttpStatus.NOT_FOUND, e.getMessage(), null, null), HttpStatus.NOT_FOUND);
        }
    }

}
