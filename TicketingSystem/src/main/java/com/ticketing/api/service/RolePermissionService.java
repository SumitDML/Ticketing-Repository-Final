package com.ticketing.api.service;

import com.ticketing.api.model.response.UIBean;
import org.springframework.data.domain.Pageable;

public interface RolePermissionService {

    UIBean getAllPermissions(Pageable pageable);
}
