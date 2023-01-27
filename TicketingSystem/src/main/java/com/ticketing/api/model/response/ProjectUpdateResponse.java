package com.ticketing.api.model.response;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;


    @Getter
    @Setter
    public class ProjectUpdateResponse
    {

        private String projectId;
        private String projectName;
        private String managerUserId;
        private String type;
    }

