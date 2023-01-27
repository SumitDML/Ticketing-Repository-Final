package com.ticketing.api.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginRequest {

    @Email
    @NotBlank(message ="Email cannot be null or Empty")
    private String email;
    @NotBlank(message ="Password cannot be null or Empty")
    private String password;
}
