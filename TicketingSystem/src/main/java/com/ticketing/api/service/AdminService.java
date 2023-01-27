package com.ticketing.api.service;

import com.ticketing.api.model.request.ResetUserPasswordRequest;
import com.ticketing.api.model.request.UserCreateRequestModel;
import org.springframework.http.ResponseEntity;

public interface AdminService {

    public void addUser(UserCreateRequestModel userCreateRequest);
    public ResponseEntity<?> resetUserPassword(ResetUserPasswordRequest resetUserPasswordRequest);

}
