package com.ticketing.api.model.request;

import com.ticketing.api.enums.StatusEnums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketStatusChangeRequest
{
    @NotBlank(message = "Please enter a valid ticket Id")
    private String ticketId;
    @NotNull(message = "Please select the status")
    private StatusEnums status;
}
