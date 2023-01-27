package com.ticketing.api.repository;

import com.ticketing.api.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<ProjectEntity,Long>
{
  ProjectEntity findByProjectName(String projectName);
  ProjectEntity findByProjectId(String projectId);


}
