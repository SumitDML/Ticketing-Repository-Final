package com.ticketing.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAssignedTicketsResponse
{
    private String ticketId;
    private String subject;
    private String description;
    private String priority;
    private String status;}
