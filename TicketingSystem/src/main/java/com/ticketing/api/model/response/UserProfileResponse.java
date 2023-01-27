package com.ticketing.api.model.response;

import com.ticketing.api.entity.DepartmentEntity;
import com.ticketing.api.entity.RoleEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
public class UserProfileResponse {
    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private RoleEntity roles;
    private DepartmentEntity department;
    private Set<UserAssignedTicketsResponse> tickets;


}
