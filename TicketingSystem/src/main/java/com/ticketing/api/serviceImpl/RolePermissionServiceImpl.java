package com.ticketing.api.serviceImpl;

import com.ticketing.api.entity.RolePermissionEntity;
import com.ticketing.api.model.response.RolePermissionResponseModel;
import com.ticketing.api.model.response.RolePermissionViewResponseModel;
import com.ticketing.api.model.response.UIBean;
import com.ticketing.api.repository.RolePermissionRepository;
import com.ticketing.api.service.RolePermissionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    @Autowired
    private RolePermissionRepository permissionRepository;

    @Override
    public UIBean getAllPermissions(Pageable pageable) {
        UIBean data = new UIBean();
        final RolePermissionViewResponseModel permissionViewResponseModel = new RolePermissionViewResponseModel();
        final List<RolePermissionResponseModel> rolePermissionResponseModels = new ArrayList<>();
        final Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by("createdAt").descending());
        final Page<RolePermissionEntity> findPermissions = permissionRepository.findAll(page);
        if (findPermissions.hasContent()) {
            permissionViewResponseModel.setTotalElements(findPermissions.getTotalElements());
            permissionViewResponseModel.setTotalPages(findPermissions.getTotalPages());

            findPermissions.getContent().forEach(permission -> {
                rolePermissionResponseModels.add(prepareRoleViewResponse(permission));
            });
            permissionViewResponseModel.setRolePermissionResponseModel(rolePermissionResponseModels);
        }
        data.setData(permissionViewResponseModel);
        return data;
    }

    private RolePermissionResponseModel prepareRoleViewResponse(final RolePermissionEntity entity) {
        final ModelMapper mapper = new ModelMapper();
        RolePermissionResponseModel response = mapper.map(entity, RolePermissionResponseModel.class);

        return response;

    }

}

