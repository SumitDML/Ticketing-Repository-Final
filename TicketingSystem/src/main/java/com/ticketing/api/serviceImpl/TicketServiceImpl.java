package com.ticketing.api.serviceImpl;

import com.ticketing.api.Specs.SearchSpecification;
import com.ticketing.api.entity.ImageEntity;
import com.ticketing.api.entity.ProjectEntity;
import com.ticketing.api.entity.TicketEntity;
import com.ticketing.api.entity.UserEntity;
import com.ticketing.api.enums.PriorityEnums;
import com.ticketing.api.enums.StatusEnums;
import com.ticketing.api.exception.ConstraintViolationException;
import com.ticketing.api.exception.ImageConversionException;
import com.ticketing.api.exception.ItemNotFoundException;
import com.ticketing.api.exception.MailException;
import com.ticketing.api.exception.ValidationException;
import com.ticketing.api.model.request.GetTicketRequest;
import com.ticketing.api.model.request.ReopenTicketRequest;
import com.ticketing.api.model.request.TicketCreationRequest;
import com.ticketing.api.model.request.TicketDeletionRequest;
import com.ticketing.api.model.request.TicketStatusChangeRequest;
import com.ticketing.api.model.request.TicketUpdationRequest;
import com.ticketing.api.model.response.ResponseModel;
import com.ticketing.api.model.response.TicketResponse;
import com.ticketing.api.model.response.UIBean;
import com.ticketing.api.repository.ImageRepository;
import com.ticketing.api.repository.ProjectRepository;
import com.ticketing.api.repository.TicketRepository;
import com.ticketing.api.repository.UserRepository;
import com.ticketing.api.service.TicketService;
import com.ticketing.api.util.ImageUtils;
import com.ticketing.api.util.MailSenderService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    TicketRepository ticketRepository;
    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private MailSenderService mailSenderService;

    @Override
    public ResponseEntity<?> createNewTicket(TicketCreationRequest ticketCreationRequest, MultipartFile[] file) {
        TicketEntity ticketEntity = new TicketEntity();
        ticketEntity = modelMapper.map(ticketCreationRequest, TicketEntity.class);

        ticketEntity.setStatus(StatusEnums.Assigned);
        UserEntity userEntity = userRepository.findById(ticketCreationRequest.getAssignedTo().getId()).orElse(null);
        if (userEntity == null) {
            throw new ValidationException("User with the UserId: " + ticketCreationRequest.getAssignedTo().getId() + " does not exist. Please check the user");
        } else {
            ticketEntity.setAssignedTo(userEntity);
        }

        if (ticketCreationRequest.getProject().getId() == null) {
            throw new ValidationException("Enter Valid Project Details");
        }
        ProjectEntity projectEntity = projectRepository.findById(ticketCreationRequest.getProject().getId()).orElse(null);
        if (projectEntity != null) {

            ticketEntity.setProject(projectEntity);
        } else {
            throw new EntityNotFoundException("Unable to find Project which you want to assign.");
        }
        List<ImageEntity> imageEntityList = new ArrayList<>();
        List<MultipartFile> images = Arrays.stream(file).toList();
        images.forEach(i -> {
            ImageEntity imageEntity = null;
            try {
                imageEntity = ImageEntity.builder()
                        .name(i.getOriginalFilename())
                        .type(i.getContentType())
                        .imageData(ImageUtils.compressImage(i.getBytes())).build();
            } catch (IOException e) {
                throw new ImageConversionException("Error Occurred While compressing/decompressing images!");
            }
            imageEntityList.add(imageEntity);
        });

        ticketEntity.setTicketImage(imageEntityList);
        TicketEntity saveTicket = ticketRepository.save(ticketEntity);

        try {
            String to = ticketCreationRequest.getCreatedBy();
            String subject = "Your Ticket id is:" + saveTicket.getTicketId();
            String text = "Dear Customer\n Thank you for writing to us!\n We have received your query, " +
                    "and our team will reach out to you in 2 business days. Please note the ticket id"
                    + saveTicket.getTicketId() + " for future references";
            mailSenderService.setMailSender(to, subject, text);

        } catch (Exception e) {
            e.getStackTrace();
        }
        if (ticketCreationRequest.getAssignedTo() == null) {
            throw new ConstraintViolationException("To whom you want to assign ticket");
        }
        try {
            String to = userEntity.getEmail();
            String subject = "Your have been assigned with ticket id: " + saveTicket.getTicketId();
            String text = "You have been the with assigned the ticket id: " + saveTicket.getTicketId() + " and the priority of the ticket is " + saveTicket.getPriority();
            mailSenderService.setMailSender(to, subject, text);


        } catch (Exception e) {
            throw new MailException("Something went wrong while sending mail.");
        }


        return ResponseEntity.ok(new ResponseModel<>(HttpStatus.OK, "Ticket created Successfully", null, null));

    }

    public ResponseEntity<?> updateTicket(TicketUpdationRequest ticketUpdationRequest) {
        TicketEntity ticket = ticketRepository.findById(ticketUpdationRequest.getId()).orElse(null);

        if (ticket != null && ticketUpdationRequest != null) {
            ticket.setTitle(ticketUpdationRequest.getTitle());
            ticket.setDescription(ticketUpdationRequest.getDescription());
            ticket.setDueDate(ticketUpdationRequest.getDueDate());
            ticket.setPriority(ticketUpdationRequest.getPriority());
            ticket.setType(ticketUpdationRequest.getType());
            ticket.setStatus(ticketUpdationRequest.getStatus());
//            ticket = modelMapper.map(ticketUpdationRequest, TicketEntity.class);
            if (ticketUpdationRequest.getStatus() == StatusEnums.Resolved) {
                Date date=new Date();
                ticket.setClosedAt(date);
                try {
                    String to = ticket.getCreatedBy();
                    String subject = "Your Ticket with ticket id"  + ticketUpdationRequest.getId();
                    String text = "Dear Customer\n  We wish to inform you that your ticket with ticket Id " + ticketUpdationRequest.getId() +
                            " is resolved.\nIf you still have any concern feel free to contact us for any query.\n Regards,\n Sourabh Choudhary\nSKETCHWIRE";
                    mailSenderService.setMailSender(to, subject, text);
                } catch (Exception e) {
                    throw new MailException("Something went wrong while sending mail.");
                }
            }
            ticketRepository.save(ticket);

              return ResponseEntity.ok(new ResponseModel<>(HttpStatus.OK, "Ticket updated Successfully", null, null));
        }
        else {
            throw new EntityNotFoundException("Ticket not found!");
        }

    }

    @Override
    public ResponseModel<?> deleteTicket(TicketDeletionRequest ticketDeletionRequest) {
        TicketEntity ticketEntity = ticketRepository.findByTicketIdAndIsDeleted(ticketDeletionRequest.getTicketId(), false);
        if (ticketEntity != null) {
            ticketEntity.setDeleted(true);
            ticketRepository.save(ticketEntity);
            return new ResponseModel<>(HttpStatus.OK, "Ticket with ticket id:" + ticketDeletionRequest.getTicketId() + " is deleted", null, null);
        } else {
            throw new ValidationException("Ticket Not Found");
        }
    }

    @Override
    public UIBean<?> getTicket(GetTicketRequest getTicketRequest, Integer pageNumber, Integer pageSize) {
        Pageable p = PageRequest.of(pageNumber, pageSize);

        TicketEntity ticketEntity = ticketRepository.findByTicketIdAndIsDeleted(getTicketRequest.getTicketId(), false);

        Page<TicketEntity> pageItems = ticketRepository.findByCreatedByAndIsDeleted(getTicketRequest.getCreatedBy(), false, p);
        List<TicketEntity> ticketEntities = pageItems.getContent();

        if (ticketEntity != null) {
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            TicketResponse ticketResponse = modelMapper.map(ticketEntity, TicketResponse.class);
            return new UIBean<>(ticketResponse);
        } else if (ticketEntities != null && ticketEntities.size() != 0) {

            Set<TicketResponse> ticketResponseSet = new HashSet<>();
            ticketEntities.forEach(ticket ->
            {
                ModelMapper modelMapper = new ModelMapper();
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                TicketResponse ticketResponse = modelMapper.map(ticket, TicketResponse.class);
                ticketResponseSet.add(ticketResponse);

            });

            return new UIBean<>(ticketResponseSet);
        } else {
            throw new ValidationException("Ticket isn't available");
        }
    }

    @Override
    public UIBean filter(Integer pageNumber, Integer pageSize, PriorityEnums priority, StatusEnums status) {

        Pageable p = PageRequest.of(pageNumber, pageSize);

        List<TicketResponse> ticketList = new ArrayList<>();

        Specification<TicketEntity> specification = SearchSpecification.getSpec(priority,status);
        Page<TicketEntity> allTickets = ticketRepository.findAll(specification,p);
        List<TicketEntity> tickets = allTickets.getContent();

        if (tickets.isEmpty()) {
            throw new ItemNotFoundException("No Tickets Available!");
        } else {
            tickets.forEach(t -> {
                if (!t.isDeleted()) {
                    ticketList.add(modelMapper.map(t, TicketResponse.class));
                }
            });

        }

        return new UIBean(ticketList);
    }


    @Override
    public UIBean changeTicketStatus(TicketStatusChangeRequest ticketStatusChangeRequest) {
        String ticketId = ticketStatusChangeRequest.getTicketId();
        StatusEnums ticketStatus = ticketStatusChangeRequest.getStatus();
        TicketEntity ticketEntity = ticketRepository.findByTicketId(ticketId);
        if (ticketEntity != null && ticketStatusChangeRequest != null) {
            ticketEntity.setStatus(ticketStatus);
            if (ticketStatusChangeRequest.getStatus() == StatusEnums.Resolved) {
                Date date=new Date();
                ticketEntity.setClosedAt(date);
                resolvedTicketEmail(ticketEntity.getCreatedBy(),ticketStatusChangeRequest.getTicketId());
            }
            ticketRepository.save(ticketEntity);
            return new UIBean("Status of ticket With Ticket Id " + ticketStatusChangeRequest.getTicketId() + " is now changed to " + ticketStatusChangeRequest.getStatus());
        } else {
            throw new EntityNotFoundException("Unable to find ticket. Please provide valid information");
        }
    }

    @Override
    public UIBean reopenTicket(ReopenTicketRequest reopenTicketRequest)
    {
        String ticketId = reopenTicketRequest.getTicketId();
        TicketEntity ticketEntity = ticketRepository.findByTicketId(ticketId);
        if (ticketEntity != null && reopenTicketRequest != null)
        {
            if (ticketEntity.getStatus() == StatusEnums.Resolved) {
                ticketEntity.setStatus(StatusEnums.Pending);
                ticketEntity.setAssignedTo(reopenTicketRequest.getAssignedTo());
                ticketEntity.setPriority(reopenTicketRequest.getPriority());
                ticketEntity.setDueDate(reopenTicketRequest.getDueDate());
                ticketEntity.setDescription(reopenTicketRequest.getDescription());
                ticketEntity.setStatus(StatusEnums.Reopened);
                ticketRepository.save(ticketEntity);
                reopenTicketCustomerEmail(ticketEntity.getCreatedBy(), ticketEntity.getTicketId());
                reopenTicketAssignedToEmail(ticketEntity.getAssignedTo().getEmail(),ticketEntity.getTicketId(),reopenTicketRequest.getPriority());
                return new UIBean("Ticket is now reopened.");
            }
            else
            {
                return new UIBean("Ticket is not yet resolved.");
            }
        }
            else
            {
                throw new EntityNotFoundException("Unable to find ticket. Please enter valid ticket id");
            }
    }

    public void resolvedTicketEmail(String to,String ticketId)
    {
        try {
            String subject = "Your Ticket with ticket id " + ticketId;
            String text = "Dear Customer\n  We wish to inform you that your ticket with ticket Id " + ticketId +
                    " is resolved.\nIf you still have any concern feel free to contact us for any query.\n Regards,\n Sourabh Choudhary\nSKETCHWIRE";
            mailSenderService.setMailSender(to, subject, text);
        } catch (Exception e) {
            throw new MailException("Something went wrong while sending mail.");
        }

    }
    public void reopenTicketCustomerEmail(String to,String ticketId)
    {
        try {
            String subject = "Your Ticket with ticket id " + ticketId+" is opened again";
            String text = "Dear Customer\n  We wish to inform you that your ticket with ticket Id " + ticketId +
                    " is opened again. Our executive will be in contact with yo soon.\n If you still have any concern feel free to contact us for any query.\n Regards,\n Sourabh Choudhary\nSKETCHWIRE";
            mailSenderService.setMailSender(to, subject, text);
        } catch (Exception e) {
            throw new MailException("Something went wrong while sending mail.");
        }

    }
    public void reopenTicketAssignedToEmail(String to,String ticketId,PriorityEnums priorityEnums)
    {
        try {
            String subject = "Your have been assigned with ticket id: " + ticketId;
            String text = "You have been the with assigned the ticket id: " + ticketId + " and the priority of the ticket is " + priorityEnums;
            mailSenderService.setMailSender(to, subject, text);
        }
        catch (Exception e) {
            throw new MailException("Something went wrong while sending mail.");
        }

    }


}

