package com.ticketing.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketing.api.enums.PriorityEnums;
import com.ticketing.api.enums.StatusEnums;
import com.ticketing.api.exception.ConstraintViolationException;
import com.ticketing.api.exception.ValidationException;
import com.ticketing.api.model.request.GetTicketRequest;
import com.ticketing.api.model.request.ReopenTicketRequest;
import com.ticketing.api.model.request.TicketCreationRequest;
import com.ticketing.api.model.request.TicketDeletionRequest;
import com.ticketing.api.model.request.TicketStatusChangeRequest;
import com.ticketing.api.model.request.TicketUpdationRequest;
import com.ticketing.api.model.response.ResponseModel;
import com.ticketing.api.model.response.UIBean;
import com.ticketing.api.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("api/v1/tickets")
public class TicketController
{
    @Autowired
    TicketService ticketService;

    @PostMapping("/createTicket")
    public ResponseEntity<ResponseModel> createTicket(@RequestParam("data") String data,@RequestParam("images") MultipartFile[] file) {
        ResponseModel responseModel = null;

        try
        {
            TicketCreationRequest ticketCreationRequest= new ObjectMapper().readValue(data,TicketCreationRequest.class);
            ticketService.createNewTicket(ticketCreationRequest,file);
            responseModel = new ResponseModel(OK, "Ticket created successfully.", null, null);
            return new ResponseEntity(responseModel,HttpStatus.CREATED);
        }
        catch (ValidationException | ConstraintViolationException  e)
        {
            return new ResponseEntity(new ResponseModel<>(HttpStatus.NOT_FOUND, e.getMessage(), null, null), HttpStatus.NOT_FOUND);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/updateTicket")
    public ResponseEntity<?>updateTicket(@RequestBody @Valid TicketUpdationRequest ticketUpdationRequest) {
        {
            ResponseModel responseModel = null;
            try {
                ticketService.updateTicket(ticketUpdationRequest);
                responseModel = new ResponseModel(OK, "Ticket updated successfully.", null, null);
                return new ResponseEntity(responseModel, OK);
            } catch (ValidationException | ConstraintViolationException e) {
                return new ResponseEntity(new ResponseModel<>(HttpStatus.NOT_FOUND, e.getMessage(), null, null), HttpStatus.NOT_FOUND);
            }
        }
    }

    @DeleteMapping("/deleteTicket")
    public ResponseEntity<ResponseModel<?>> deleteTicket(@RequestBody @Valid TicketDeletionRequest ticketDeletionRequest)
    {
        try
        {
            ResponseModel<?> responseModel=ticketService.deleteTicket(ticketDeletionRequest);
            return new ResponseEntity<>(responseModel,HttpStatus.OK);
        }
        catch (ValidationException | ConstraintViolationException e)
        {
            return new ResponseEntity(new ResponseModel<>(HttpStatus.NOT_FOUND, e.getMessage(), null, null), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getTicket")
    public ResponseEntity<UIBean<?>> getTicket(@RequestBody GetTicketRequest getTicketRequest,
                                               @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                               @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        try {
            UIBean<?> uiBean = ticketService.getTicket(getTicketRequest, pageNumber, pageSize);
            return new ResponseEntity<>(uiBean, HttpStatus.OK);
        } catch (ValidationException | ConstraintViolationException e) {
            return new ResponseEntity(new ResponseModel<>(HttpStatus.NOT_FOUND, e.getMessage(), null, null), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/listTickets",
            consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_ATOM_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_ATOM_XML_VALUE})
    public ResponseEntity listTickets(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                      @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize,
                                      @RequestParam(value = "priority",required = false) PriorityEnums priority,
                                      @RequestParam(value = "status",required = false) StatusEnums status) {
        UIBean returnValue = ticketService.filter(pageNumber, pageSize, priority,status);
        return new ResponseEntity(returnValue, HttpStatus.OK);

    }

    @PostMapping(path = "/changeTicketStatus")
    public ResponseEntity changeTicketStatus(@Valid @RequestBody TicketStatusChangeRequest ticketStatusChangeRequest)
    {
        try {
            UIBean changeStatus = ticketService.changeTicketStatus(ticketStatusChangeRequest);
            return new ResponseEntity(changeStatus, OK);
        }
        catch (ValidationException | ConstraintViolationException e)
        {
            return new ResponseEntity(new ResponseModel<>(HttpStatus.NOT_FOUND, e.getMessage(), null, null), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path = "/reopenTicket")
    public ResponseEntity reopenTicket(@Valid @RequestBody ReopenTicketRequest reopenTicketRequest)
    {
        try {
            UIBean reopenTicket = ticketService.reopenTicket(reopenTicketRequest);
            return new ResponseEntity(reopenTicket, OK);
        }
        catch (ValidationException | ConstraintViolationException e)
        {
            return new ResponseEntity(new ResponseModel<>(HttpStatus.NOT_FOUND, e.getMessage(), null, null), HttpStatus.NOT_FOUND);
        }
    }

}






