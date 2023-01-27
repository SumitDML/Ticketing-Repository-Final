package com.ticketing.api.model.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class UIBean<T> implements Serializable
    {
        private T data;
    }

