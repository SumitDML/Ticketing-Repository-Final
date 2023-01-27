package com.ticketing.api.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ProjectUpdateRequest
{
    @NotBlank
    private String projectId;
    private String projectName;
    private String managerUserId;
    private String type;
}
