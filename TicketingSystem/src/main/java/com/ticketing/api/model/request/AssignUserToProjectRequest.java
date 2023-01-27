package com.ticketing.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssignUserToProjectRequest {
    @NotNull(message = "Project ID Cannot be null!")
    private String projectId;
    @NotNull(message = "Atleast add 1 user!")
    private Set<UserRequest> users;
}
