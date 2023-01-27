package com.ticketing.api.repository;

import com.ticketing.api.entity.TicketEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity,Long> {
    TicketEntity findByTicketIdAndIsDeleted(String ticketId,boolean isDeleted);
    TicketEntity findByTicketId(String ticketId);

    Page<TicketEntity> findByCreatedByAndIsDeleted(String createdBy,boolean isDeleted,Pageable pageable);
    TicketEntity findById(long id);
    Page<TicketEntity>  findAll(Specification<TicketEntity> specification, Pageable pageable);
}
