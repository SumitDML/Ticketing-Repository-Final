package com.ticketing.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RemoveUserFromProjectRequest
{
    @NotNull
    private String projectId;
    @NotNull
    private String userId;
}
