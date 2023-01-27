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
public class ResetPasswordRequest
{
    @NotNull
    private String otp;
    @NotNull
    private String newPassword;
    @NotNull
    private String email;

}
