package com.ticketing.api.model.request;

import com.ticketing.api.entity.UserEntity;
import com.ticketing.api.enums.PriorityEnums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReopenTicketRequest
{
    @NotBlank(message = "Please enter ticket id")
    private String ticketId;
    private PriorityEnums priority;
    private String description;
    private UserEntity assignedTo;
    private Timestamp dueDate;
}
