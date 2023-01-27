package com.ticketing.api.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoleViewResponseModel {
    private Long totalElements;

    private Integer totalPages;

    private List<RoleResponseModel> roleResponseModel;

}


