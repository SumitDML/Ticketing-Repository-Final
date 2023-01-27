package com.ticketing.api.service;

import com.ticketing.api.model.request.ForgotPasswordRequest;
import com.ticketing.api.model.request.LoginRequest;
import com.ticketing.api.model.request.PasswordChangeRequestModel;
import com.ticketing.api.model.request.RemoveUserRequest;
import com.ticketing.api.model.request.ResetPasswordRequest;
import com.ticketing.api.model.request.UserInfo;
import com.ticketing.api.model.response.LogoutResponse;
import com.ticketing.api.model.response.ResponseModel;
import com.ticketing.api.model.response.UIBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public interface UserService
{
    public ResponseEntity<ResponseModel<String>> setPassword(ResetPasswordRequest resetPasswordRequest);
    ResponseEntity<ResponseModel> loginUser(LoginRequest loginRequest, MultiValueMap<String, String> headers);
    void changePassword(PasswordChangeRequestModel passwordChange, UserInfo userInfo);
    public ResponseEntity<ResponseModel<?>> sendOtp(ForgotPasswordRequest forgotPasswordRequest);
    UIBean getAllUsers(String userId, Pageable pageable);

    public LogoutResponse logout(String token, UserInfo userInfo);

    UIBean removeUser(RemoveUserRequest removeUserRequest);
    public UIBean userProfile(String tokenHeader);

}
