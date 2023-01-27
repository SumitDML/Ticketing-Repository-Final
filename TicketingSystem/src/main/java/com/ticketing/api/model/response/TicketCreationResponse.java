package com.ticketing.api.model.response;

import com.ticketing.api.enums.PriorityEnums;
import com.ticketing.api.enums.StatusEnums;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;


@Setter
@Getter
@Data
public class TicketCreationResponse implements Serializable
{
        private String ticketId;
        private String title;
        private String description;
        private String tag;
        private PriorityEnums priority;
        private StatusEnums status;
        private String AssignedTo;
        private String createdBy;
        private String type;
        private Date createdAt;
        private Set<UserCreationResponse> members;


    }

