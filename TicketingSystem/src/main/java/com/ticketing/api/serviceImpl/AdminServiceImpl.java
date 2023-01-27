package com.ticketing.api.serviceImpl;


import com.ticketing.api.entity.UserEntity;
import com.ticketing.api.exception.ValidationException;
import com.ticketing.api.model.request.ResetUserPasswordRequest;
import com.ticketing.api.model.request.UserCreateRequestModel;
import com.ticketing.api.model.response.ResponseModel;
import com.ticketing.api.repository.UserRepository;
import com.ticketing.api.service.AdminService;
import com.ticketing.api.util.MailSenderService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.EntityNotFoundException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Random random;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminServiceImpl.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    MailSenderService mailSenderService;

    @Override
    public void addUser(UserCreateRequestModel userCreateRequest) {
        final UserEntity userEntity = userRepository.findByEmail(userCreateRequest.getEmail());
        if (userEntity != null && userEntity.isDeleted() == false) {
            throw new ValidationException("Email invalid or already exists");
        }
        final ModelMapper mapper = new ModelMapper();
        UserEntity entity = mapper.map(userCreateRequest, UserEntity.class);
        validatePhoneNumber(userCreateRequest.getPhoneNumber());
        entity.setDeleted(false);
        final String generatedPassword = generatePassword();
        entity.setPassword(bCryptPasswordEncoder.encode(generatedPassword));
        try {
            userRepository.save(entity);
        } catch (HttpClientErrorException.BadRequest exception) {
            exception.printStackTrace();
        }
        try {
            mailSenderService.setMailSender(userCreateRequest.getEmail(), "Login Credentials", "Email : "
                    + userCreateRequest.getEmail() + " , Password : " + generatedPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public ResponseEntity<?> resetUserPassword(ResetUserPasswordRequest resetUserPasswordRequest) {
        UserEntity userEntity = userRepository.findByEmail(resetUserPasswordRequest.getEmail());
        if (userEntity != null) {
            final String generatedPassword = generatePassword();
            userEntity.setPassword(bCryptPasswordEncoder.encode(generatedPassword));
            try {
                userRepository.save(userEntity);
            } catch (HttpClientErrorException.BadRequest exception) {
                exception.printStackTrace();
            }
            try {
                mailSenderService.setMailSender(resetUserPasswordRequest.getEmail(), "Login Credentials", "Email : "
                        + resetUserPasswordRequest.getEmail() + " , Password : " + generatedPassword);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new EntityNotFoundException("User Not Found");
        }


        return new ResponseEntity(new ResponseModel<>(HttpStatus.OK, "Password Reset Successfully", null, null), HttpStatus.OK);

    }


    private void validatePhoneNumber(final String phoneNo) {
        final Pattern pattern = Pattern.compile("^((91)|(\\+91)|0?)[6-9]{1}\\d{9}$");
        final Matcher matcher = pattern.matcher(phoneNo);
        if (!(matcher.find() && matcher.group().equals(phoneNo))) {
            throw new ValidationException("Invalid phone number.");
        }
    }


    private String generatePassword() {
        final String upperCases = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String lowerCases = "abcdefghijklmnopqrstuvwxyz";
        final String numbers = "0123456789";
        final String symbols = "!@#$%^&*_=+-/.?<>)";

        final String values = upperCases + lowerCases + numbers + symbols;

        final int length = 8;
        final char[] passwordChar = new char[length];

        for (int i = 0; i < length; i++) {
            passwordChar[i] = values.charAt(this.random.nextInt(values.length()));

        }
        String password = new String(passwordChar);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("password is =" + password);
        }
        return password;
    }

}