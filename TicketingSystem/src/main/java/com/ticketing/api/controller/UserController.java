package com.ticketing.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketing.api.model.request.ForgotPasswordRequest;
import com.ticketing.api.model.request.LoginRequest;
import com.ticketing.api.model.request.PasswordChangeRequestModel;
import com.ticketing.api.model.request.ResetPasswordRequest;
import com.ticketing.api.model.request.UserInfo;
import com.ticketing.api.model.response.LogoutResponse;
import com.ticketing.api.model.response.ResponseModel;
import com.ticketing.api.model.response.UIBean;
import com.ticketing.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.ValidationException;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;

    private static final String READ_WRITE = "hasPermission('manage_users', 'view & edit')";

    private static final String READ = "hasPermission('manage_users', 'view')";


    @PostMapping("/sendOtp")
    public ResponseEntity<ResponseModel<?>> genOtp(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        ResponseModel responseModel = null;
        try {
            return userService.sendOtp(forgotPasswordRequest);
        } catch (EntityNotFoundException exception) {
            responseModel = new ResponseModel(404, exception.getMessage(), null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<ResponseModel<String>> forgotPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        ResponseModel responseModel = null;
        try {
            return userService.setPassword(resetPasswordRequest);
        } catch (EntityNotFoundException exception) {
            responseModel = new ResponseModel(404, exception.getMessage(), null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.NOT_FOUND);
        } catch (ValidationException exception) {
            responseModel = new ResponseModel(400, exception.getMessage(), null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/login", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseModel> login(@Valid @RequestBody LoginRequest loginRequest, @RequestHeader MultiValueMap<String, String> headers) {
        ResponseModel responseModel = null;
        try {
            return userService.loginUser(loginRequest, headers);
        } catch (ValidationException exception) {
            responseModel = new ResponseModel(400, exception.getMessage(), null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = true) String authorization, @RequestBody UserInfo userInfo) {
        ResponseModel responseModel = null;
        try {
            LogoutResponse logoutResponse = userService.logout(authorization, userInfo);
            if (logoutResponse.isSuccess() == true) {
                return ResponseEntity.ok(logoutResponse);
            }
            return new ResponseEntity<>(logoutResponse, HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException exception) {
            responseModel = new ResponseModel(404, exception.getMessage(), null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping(path = "/changePassword")
    public ResponseEntity<ResponseModel> changePassword(@Valid @RequestBody PasswordChangeRequestModel passChangeDto, @RequestHeader("user-info") String userInfo) {

        UserInfo user = null;
        try {

            user = new ObjectMapper().readValue(userInfo, UserInfo.class);

            userService.changePassword(passChangeDto, user);
            ResponseModel<?> responseModel = new ResponseModel(OK, "Password Changed successfully", null, null);
            return new ResponseEntity<>(responseModel, OK);

        } catch (ValidationException exception) {
            ResponseModel responseModel = new ResponseModel(1001, "Incorrect Password", null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.UNAUTHORIZED);

        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping(path = "/userProfile")
    public ResponseEntity getUserProfile(@RequestHeader("Authorization") String tokenHeader) {

        try {
            UIBean returnValue = userService.userProfile(tokenHeader);
            return new ResponseEntity(returnValue, HttpStatus.OK);
        }
        catch (EntityNotFoundException exception)
        {
            throw new EntityNotFoundException("SomeThing went wrong");
        }

    }
}


