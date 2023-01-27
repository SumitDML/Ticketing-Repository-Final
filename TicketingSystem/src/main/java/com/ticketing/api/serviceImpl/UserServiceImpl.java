package com.ticketing.api.serviceImpl;

import com.ticketing.api.entity.DeviceEntity;
import com.ticketing.api.entity.LoginEntity;
import com.ticketing.api.entity.UserEntity;
import com.ticketing.api.exception.ConstraintViolationException;
import com.ticketing.api.exception.ValidationException;
import com.ticketing.api.model.request.ForgotPasswordRequest;
import com.ticketing.api.model.request.LoginRequest;
import com.ticketing.api.model.request.PasswordChangeRequestModel;
import com.ticketing.api.model.request.RemoveUserRequest;
import com.ticketing.api.model.request.ResetPasswordRequest;
import com.ticketing.api.model.request.UserInfo;
import com.ticketing.api.model.response.LoginResponse;
import com.ticketing.api.model.response.LogoutResponse;
import com.ticketing.api.model.response.ResponseModel;
import com.ticketing.api.model.response.UIBean;
import com.ticketing.api.model.response.UserAssignedTicketsResponse;
import com.ticketing.api.model.response.UserProfileResponse;
import com.ticketing.api.model.response.UserResponse;
import com.ticketing.api.model.response.UserViewResponseModel;
import com.ticketing.api.model.response.UsersViewResponseModel;
import com.ticketing.api.redis.RedisUtility;
import com.ticketing.api.repository.DeviceRepository;
import com.ticketing.api.repository.UserRepository;
import com.ticketing.api.service.UserService;
import com.ticketing.api.util.JwtUtil;
import com.ticketing.api.util.MailSenderService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RedisUtility redisUtility;
    @Value("${twilio.account.sid}")
    private String sid;

    @Value("${twilio.auth.token}")
    private String token;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Value("${auth.token.secret.key}")
    private String authSecret;

    @Autowired
    private Random random;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    MailSenderService mailSenderService;

    @Value("${master.otp}")
    private String masterOtp;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    DeviceRepository deviceRepository;

    @Override
    public ResponseEntity<ResponseModel<?>> sendOtp(ForgotPasswordRequest forgotPasswordRequest) {
        UserEntity userEntity = userRepository.findByEmail(forgotPasswordRequest.getEmail());
        if (userEntity != null) {
            Random rnd = new Random();
            int number = rnd.nextInt(9999);
            String otp = String.format("%06d", number);

            String key = forgotPasswordRequest.getEmail() + "_OTP";
            String email = forgotPasswordRequest.getEmail();
            redisUtility.setData(key, otp);

            try {
                mailSenderService.setMailSender(email, "OTP", otp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new EntityNotFoundException("Invalid Email");
        }
        return new ResponseEntity(new ResponseModel<>(HttpStatus.OK, "Otp send successfully", null, null), HttpStatus.OK);
    }


    @Override
    public ResponseEntity<ResponseModel<String>> setPassword(ResetPasswordRequest resetPasswordRequest) {
        UserEntity userEntity = userRepository.findByEmail(resetPasswordRequest.getEmail());
        if (userEntity != null && userEntity.isDeleted() == false) {
            String email = resetPasswordRequest.getEmail();
            String otp = resetPasswordRequest.getOtp();
            String key = email + "_OTP";
            String dbOTP = redisUtility.getData(key);

            if (dbOTP != null || otp != null) {

                if (otp.equals(dbOTP) || (otp.equals(masterOtp))) {
                    redisUtility.delete(key);
                    userEntity.setPassword(bCryptPasswordEncoder.encode(resetPasswordRequest.getNewPassword()));
                    userRepository.save(userEntity);
                } else {
                    throw new ValidationException("Invalid Otp");
                }
            }

            return ResponseEntity.ok(new ResponseModel<>(HttpStatus.OK, "Password Updated Successfully", null, null));
        } else {
            throw new EntityNotFoundException("Invalid Email");
        }
    }


    @Override
    public ResponseEntity loginUser(LoginRequest loginRequest, MultiValueMap<String, String> headers) {
        UIBean<LoginResponse> ui = new UIBean<LoginResponse>();
        UserEntity userEntity = userRepository.findByEmail(loginRequest.getEmail());
        if (userEntity != null && userEntity.isDeleted() == false) {
            if (bCryptPasswordEncoder.matches(loginRequest.getPassword(), userEntity.getPassword())) {
                String token = generateAdminToken(userEntity, headers);

                LoginEntity login = userEntity.getLoginEntity();
                LoginResponse loginResponse = new LoginResponse();
                if (login == null) {
                    loginResponse.setExistingUser(false);
                    login = new LoginEntity();
                } else {
                    loginResponse.setExistingUser(true);
                }
                loginResponse.setToken(token);
                ui.setData(loginResponse);
                userEntity.setLoginEntity(login);
                login.setUserEntity(userEntity);


                List<DeviceEntity> deviceDetailsEntityList = login.getDeviceDetails();

                DeviceEntity deviceDetails = new DeviceEntity();
                if (deviceDetailsEntityList == null) {
                    deviceDetailsEntityList = new ArrayList<DeviceEntity>();
                }
                deviceDetails.setUserId(userEntity.getUserId());
                deviceDetails.setIsActive(true);
                deviceDetails.setToken(token);
                deviceDetails.setLoginEntity(login);
                deviceDetailsEntityList.add(deviceDetails);
                login.setDeviceDetails(deviceDetailsEntityList);

                try {
                    userRepository.save(userEntity);
                } catch (HttpClientErrorException.BadRequest exception) {
                    exception.printStackTrace();
                }
            } else {

                return new ResponseEntity(new ResponseModel<>(HttpStatus.UNAUTHORIZED, "Incorrect password ", null, null), HttpStatus.UNAUTHORIZED);
            }
        } else
            return new ResponseEntity(new ResponseModel<>(HttpStatus.BAD_REQUEST, "Email doesn't exists ", null, null), HttpStatus.BAD_REQUEST);
        return new ResponseEntity(new ResponseModel<>(HttpStatus.OK, "Login successfully", null, ui), HttpStatus.OK);
    }


    private String generateAdminToken(UserEntity userEntity, MultiValueMap<String, String> headers) {
        final Map<String, Object> claims = new ConcurrentHashMap<>();
        final List<String> pagePermissions = new ArrayList<>();
        if (userEntity.getRoles() != null && !CollectionUtils.isEmpty(userEntity.getRoles().getPermissions())) {
            userEntity.getRoles().getPermissions().forEach(permission -> {
                pagePermissions.add(permission.getPageName() + "_" + permission.getPagePermission());
            });
        }
        String userAgent = headers.getFirst("USER-AGENT");
        if (!StringUtils.isEmpty(userAgent)) {
            claims.put("USER-AGENT", userAgent);
        }
        claims.put("iat", new Date());
        claims.put("issuer", "sketchwire");
        claims.put("permissions", pagePermissions);
        claims.put("userId", userEntity.getUserId());
        if (userEntity.getRoles() != null)
            claims.put("role", userEntity.getRoles().getRoleName());
        return Jwts.builder().setSubject((userEntity.getEmail())).addClaims(claims).signWith(SignatureAlgorithm.HS512, authSecret).compact();
    }


    public void changePassword(PasswordChangeRequestModel passChangeRequest, UserInfo userInfo) {
        UserEntity userEntity = userRepository.findByEmail(userInfo.getEmail());
        if (userEntity == null || userEntity.isDeleted()) {
            throw new ValidationException("User not found");
        }
        String oldPass = userEntity.getPassword();
        if (bCryptPasswordEncoder.matches(passChangeRequest.getOldPassword(), oldPass)) {
            try {
                userEntity.setPassword(bCryptPasswordEncoder.encode(passChangeRequest.getNewPassword()));
                userRepository.save(userEntity);
            } catch (ValidationException e) {
                throw new ValidationException("Something went wrong");
            }
        } else {
            throw new ValidationException("Incorrect Password");
        }
    }

    @Override
    public UIBean getAllUsers(String userId, Pageable pageable) {
        final UsersViewResponseModel usersViewResponseModel = new UsersViewResponseModel();
        final UserViewResponseModel userViewResponseModel = new UserViewResponseModel();
        UIBean data = new UIBean();

        if (userId == null || userId.isEmpty()) {
            final List<UserEntity> users = new ArrayList<>();
            final Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
            List<UserResponse> userRes = new ArrayList<>();
            final Page<UserEntity> userEntities = userRepository.findAll(page);
            if (userEntities.hasContent()) {
                usersViewResponseModel.setTotalElements(userEntities.getTotalElements());
                usersViewResponseModel.setTotalPages(userEntities.getTotalPages());
                userEntities.getContent().forEach(user -> {
                    if(user.isDeleted() == false){
                        userRes.add(prepareUserViewResponse(user));
                    }
                });
                usersViewResponseModel.setUsers(userRes);

                data.setData(usersViewResponseModel);
            }
        } else {
            UserEntity user = userRepository.findByUserId(userId);
            if (user != null && user.isDeleted() == false) {

                UserResponse userResponse = new UserResponse();
                userResponse = mapper.map(user, UserResponse.class);
                userViewResponseModel.setUserEntity(userResponse);
                data.setData(userViewResponseModel);

            }
            else {
                throw new ConstraintViolationException("User Not Found! Please enter valid userId!");
            }
        }

        return data;
    }

    @Override
    public LogoutResponse logout(String token, UserInfo userInfo) {
        String[] Token = token.split(" ");
        token = String.valueOf(Token[1]);
        DeviceEntity device = deviceRepository.getByUserId(userInfo.getUserId(),token);
        if (device != null) {
            if (Token.length == 2 && device.getToken().equals(token)) {

                device.setIsActive(false);
                deviceRepository.save(device);
                return getLogoutResponse(true, "User Logged out");
            } else
                return getLogoutResponse(false, "Invalid token");
        }

        throw new EntityNotFoundException("Invalid UserId");
    }

    private LogoutResponse getLogoutResponse(boolean successStatus, String message) {
        LogoutResponse logoutResponse = new LogoutResponse();
        logoutResponse.setMessage(message);
        logoutResponse.setSuccess(successStatus);
        return logoutResponse;
    }

    @Override
    public UIBean removeUser(RemoveUserRequest removeUserRequest) {
        UserEntity existingUser = userRepository.findByUserId(removeUserRequest.getUserId());
        if(existingUser != null && existingUser.isDeleted()==false){
            existingUser.setDeleted(true);
            userRepository.save(existingUser);
            return new UIBean("User Successfully Deleted!");
        }
        else {
            throw new ConstraintViolationException("User Not Found! Please enter valid userId!");
        }
    }

    @Override
    public UIBean userProfile(String tokenHeader) {

        String jwtToken = tokenHeader.substring(7);
        String email = jwtUtil.extractEmailFromToken(jwtToken);

        UserEntity user = userRepository.findByEmail(email);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Set<UserAssignedTicketsResponse> userAssignedTicketsResponses=new HashSet<>();
        user.getTickets().forEach(ticketEntity -> {
            UserAssignedTicketsResponse userAssignedTicketsResponse=modelMapper.map(ticketEntity, UserAssignedTicketsResponse.class);
            userAssignedTicketsResponses.add(userAssignedTicketsResponse);
        });

        UserProfileResponse userProfileResponse = modelMapper.map(user,UserProfileResponse.class);
        userProfileResponse.setTickets(userAssignedTicketsResponses);
        return  new UIBean(userProfileResponse);
    }

    private UserResponse prepareUserViewResponse(final UserEntity entity) {
        final ModelMapper mapper = new ModelMapper();
        UserResponse response = mapper.map(entity, UserResponse.class);

        return response;

    }


    //
}
