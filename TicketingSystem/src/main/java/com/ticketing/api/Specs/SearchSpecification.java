package com.ticketing.api.Specs;


import com.ticketing.api.entity.TicketEntity;


import com.ticketing.api.enums.PriorityEnums;
import com.ticketing.api.enums.StatusEnums;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
@Service
public class SearchSpecification {

    public static Specification<TicketEntity> getSpec(PriorityEnums priority, StatusEnums status){
        return (((root, query, criteriaBuilder) -> {
            List<Predicate> predicate = new ArrayList<>();
            if(priority!=null)
                predicate.add(criteriaBuilder.equal(root.get("priority"),priority));
            if(status!=null)
                predicate.add(criteriaBuilder.equal(root.get("status"),status));
            return criteriaBuilder.and(predicate.toArray(new Predicate[0]));
        }));
    }

}
