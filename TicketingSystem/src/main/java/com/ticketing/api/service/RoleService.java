package com.ticketing.api.service;

import com.ticketing.api.entity.RoleEntity;
import com.ticketing.api.model.request.RoleRequestModel;
import com.ticketing.api.model.response.UIBean;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    public void createRole(RoleRequestModel roleDetails);
    public void updateRole(RoleRequestModel roleDetails);
    RoleEntity findRoleById(Long roleEntityId);
    UIBean getAllRoles(Pageable pageable);

}