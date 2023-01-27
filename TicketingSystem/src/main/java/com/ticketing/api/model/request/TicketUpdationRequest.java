package com.ticketing.api.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ticketing.api.enums.PriorityEnums;
import com.ticketing.api.enums.StatusEnums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketUpdationRequest
{

    @NotNull
    private Long id;
    private String title;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Timestamp dueDate;
    private PriorityEnums priority;
    private StatusEnums status;
//    private TicketAssignedUserRequest assignedTo;
//    private TicketAssignedProjectRequest project;
//    private String createdBy;
    private String type;
    //private ProjectEntity project;
}
