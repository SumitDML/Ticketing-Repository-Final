package com.ticketing.api.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ticketing.api.enums.PriorityEnums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketCreationRequest
{
    @NotNull(message = "Title cannot be empty")
    @NotBlank
    private String title;
    @NotNull(message = "Description cannot be empty")
    private String description;
    @NotNull(message = "Select priority")
    private PriorityEnums priority;
    @NotNull(message = "Assign ticket to some one")
    private TicketAssignedUserRequest assignedTo;
    //    @NotNull(message = "enter email address of the customer")
    private String createdBy;
    @NotNull(message = "Type of ticket")
    private String type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dueDate;

    @NotNull(message = "project")
    private TicketAssignedProjectRequest project;


}
