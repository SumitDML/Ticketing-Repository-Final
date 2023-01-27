package com.ticketing.api.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class RemoveUserRequest {
    @NotBlank(message = "userId cannot be null!")
    private String userId;
}
