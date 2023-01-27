package com.ticketing.api.model.request;

import com.ticketing.api.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveMemberRequest {
    private String ticketId;
    private UserEntity members;
}