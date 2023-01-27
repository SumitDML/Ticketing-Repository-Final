package com.ticketing.api.service;

import com.ticketing.api.enums.PriorityEnums;
import com.ticketing.api.enums.StatusEnums;
import com.ticketing.api.model.request.GetTicketRequest;
import com.ticketing.api.model.request.ReopenTicketRequest;
import com.ticketing.api.model.request.TicketCreationRequest;
import com.ticketing.api.model.request.TicketDeletionRequest;
import com.ticketing.api.model.request.TicketStatusChangeRequest;
import com.ticketing.api.model.request.TicketUpdationRequest;
import com.ticketing.api.model.response.ResponseModel;
import com.ticketing.api.model.response.UIBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface TicketService
{
    public ResponseEntity<?> createNewTicket(TicketCreationRequest ticketCreationRequest, MultipartFile[] file);
   ResponseEntity<?> updateTicket(TicketUpdationRequest ticketUpdationRequest);
   ResponseModel<?> deleteTicket(TicketDeletionRequest ticketDeletionRequest);

    UIBean<?> getTicket(GetTicketRequest getTicketRequest,Integer pageNumber , Integer pageSize);

    UIBean filter(Integer pageNumber, Integer pageSize, PriorityEnums priority, StatusEnums status);

    UIBean changeTicketStatus(TicketStatusChangeRequest ticketStatusChangeRequest);

    UIBean reopenTicket(ReopenTicketRequest reopenTicketRequest);
}
