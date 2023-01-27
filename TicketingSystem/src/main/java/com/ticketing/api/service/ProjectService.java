package com.ticketing.api.service;

import com.ticketing.api.model.request.AssignUserToProjectRequest;
import com.ticketing.api.model.request.RemoveUserFromProjectRequest;
import com.ticketing.api.model.request.ProjectCreationRequest;
import com.ticketing.api.model.request.ProjectUpdateRequest;
import com.ticketing.api.model.response.UIBean;
import org.springframework.http.ResponseEntity;

public interface ProjectService
{
    ResponseEntity addProject(ProjectCreationRequest projectCreationRequest);
    UIBean<?> updateProject(ProjectUpdateRequest projectUpdateRequest);

    UIBean<?> addUsersInProject(AssignUserToProjectRequest assignUserToProjectRequest);

    UIBean removeUserFromProject(RemoveUserFromProjectRequest removeUserFromProjectRequest);
}
