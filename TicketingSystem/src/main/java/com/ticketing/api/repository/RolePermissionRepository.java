package com.ticketing.api.repository;

import com.ticketing.api.entity.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionRepository extends JpaRepository<RolePermissionEntity,Long> {
}