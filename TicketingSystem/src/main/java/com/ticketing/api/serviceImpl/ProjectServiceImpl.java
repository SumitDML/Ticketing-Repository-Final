package com.ticketing.api.serviceImpl;

import com.ticketing.api.entity.ProjectEntity;
import com.ticketing.api.entity.UserEntity;
import com.ticketing.api.exception.ConstraintViolationException;
import com.ticketing.api.exception.MailException;
import com.ticketing.api.exception.ValidationException;
import com.ticketing.api.model.request.AssignUserToProjectRequest;
import com.ticketing.api.model.request.ProjectCreationRequest;
import com.ticketing.api.model.request.ProjectUpdateRequest;
import com.ticketing.api.model.request.RemoveUserFromProjectRequest;
import com.ticketing.api.model.response.ProjectUpdateResponse;
import com.ticketing.api.model.response.ResponseModel;
import com.ticketing.api.model.response.UIBean;
import com.ticketing.api.repository.ProjectRepository;
import com.ticketing.api.repository.UserRepository;
import com.ticketing.api.service.ProjectService;
import com.ticketing.api.util.MailSenderService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.Set;

@Service
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailSenderService mailSenderService;

    @Override
    public ResponseEntity addProject(ProjectCreationRequest projectCreationRequest) {
        if (projectCreationRequest.getManager().getId() == null) {
            throw new ConstraintViolationException("To whom you want to assign the project ");
        }
        UserEntity manager= userRepository.findById(projectCreationRequest.getManager().getId()).orElse(null);
        if (manager==null) {
            throw new ConstraintViolationException("Enter valid userId of manager");
        }
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ProjectEntity projectEntity = modelMapper.map(projectCreationRequest, ProjectEntity.class);
        try {
            projectRepository.save(projectEntity);
        }
        catch (DataIntegrityViolationException e)
        {
            throw new ValidationException("Project already exist");
        }
        try {
            String to = manager.getEmail();
            mailSenderService.setMailSender(to,
                    "Your have been assigned with project name: " + projectCreationRequest.getProjectName(),
                    "Your have been assigned with project name: " + projectCreationRequest.getProjectName());
        } catch (Exception e) {
            throw new MailException("Something went wrong while sending mail.");
        }
        return new ResponseEntity(new ResponseModel<>(HttpStatus.CREATED, "Project Created Successfully ", null, null), HttpStatus.CREATED);

    }

    public UIBean<?> updateProject(ProjectUpdateRequest projectUpdateRequest) {
        ProjectEntity projectEntity = projectRepository.findByProjectId(projectUpdateRequest.getProjectId());
        String userId=projectUpdateRequest.getManagerUserId();
        UserEntity userEntity=userRepository.findByUserId(userId);
        if (projectEntity != null&&projectUpdateRequest!=null) {
            if(userEntity!=null){
                projectEntity.setProjectName(projectUpdateRequest.getProjectName());
                projectEntity.setType(projectUpdateRequest.getType());
                projectEntity.setManager(userEntity);
                projectRepository.save(projectEntity);
                ModelMapper modelMapper = new ModelMapper();
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                ProjectUpdateResponse projectUpdateResponse = modelMapper.map(projectEntity, ProjectUpdateResponse.class);
                projectUpdateResponse.setManagerUserId(projectEntity.getManager().getUserId());
                return new UIBean<>(projectUpdateResponse);
            }
            else
            {
                throw new EntityNotFoundException("Unable to find user. Please enter valid user id");
            }
        } else {
            throw new EntityNotFoundException("Project not found!");}
    }

    @Override
    public UIBean<?> addUsersInProject(AssignUserToProjectRequest assignUserToProjectRequest) {
        ProjectEntity existingProject = projectRepository.findByProjectId(assignUserToProjectRequest.getProjectId());
        if(existingProject == null){
            throw new ConstraintViolationException("Project Does not exists or Invalid Project Id");
        }
        else {
            Set<UserEntity> userSet = new HashSet<>();

            assignUserToProjectRequest.getUsers().forEach(u -> {
                 UserEntity existingUser = userRepository.findById(u.getId()).orElse(null);
                 if(existingUser==null){
                     throw new ConstraintViolationException("User with Id :"+u.getId()+" does not exist !");
                 }else {
                     userSet.add(existingUser);
                 }
            });
            existingProject.setUsers(userSet);
            projectRepository.save(existingProject);
        }
        return new UIBean<>("User Assigned Successfully!!");
    }

    @Override
    public UIBean removeUserFromProject(RemoveUserFromProjectRequest removeUserFromProjectRequest) {
        ProjectEntity existingProject = projectRepository.findByProjectId(removeUserFromProjectRequest.getProjectId());
        if(existingProject == null){
            throw new ConstraintViolationException("Project Does not exists or Invalid Project Id");
        }
        UserEntity userEntity=userRepository.findByUserId(removeUserFromProjectRequest.getUserId());
        if(existingProject.getUsers().contains(userEntity))
        {
            existingProject.getUsers().remove(userEntity);
            projectRepository.save(existingProject);
        }
        else
        {
            throw new ConstraintViolationException("User is not assigned in this project. Please enter valid user id");
        }

        return new UIBean<>("User removed Successfully from Project!!");
    }


}
