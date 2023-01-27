package com.ticketing.api.model.response;

import com.ticketing.api.enums.PriorityEnums;
import com.ticketing.api.enums.StatusEnums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponse
{

    private String title;
    private String description;
    private PriorityEnums priority;
    private StatusEnums status;
    private UserNameResponse assignedTo;
    private String createdBy;
    private String type;
    private Date closedAt;
    private boolean isDeleted;
}
