package com.ticketing.api.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private String name;

    private String email;

    private String userId;

    private String phoneNumber;

    private String address;

    private Date createdAt;

    private UserRoleResponse roles;

    private Set<ProjectResponse> projects;


}

