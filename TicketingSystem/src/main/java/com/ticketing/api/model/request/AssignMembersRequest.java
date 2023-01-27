package com.ticketing.api.model.request;

import com.ticketing.api.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssignMembersRequest {
    private Long ticketId;
    private List<UserEntity> members;
}

