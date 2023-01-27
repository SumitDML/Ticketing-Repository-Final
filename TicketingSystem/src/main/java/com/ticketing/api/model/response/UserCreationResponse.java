package com.ticketing.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationResponse
{



    @NotBlank(message = " name cannot be null")
    private String name;

    @NotBlank(message = "Email cannot be null")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Phone number shouldn't be empty")
    private String phoneNumber;


}
