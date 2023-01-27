package com.ticketing.api.model.response;

import com.ticketing.api.enums.PriorityEnums;
import com.ticketing.api.enums.StatusEnums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketUpdationResponse
{
    private String ticketId;
    private String subject;
    private String description;
    private String tag;
    private PriorityEnums priority;
    private StatusEnums status;
    private String assignedTo;
    private String createdBy;
    private String type;
}
