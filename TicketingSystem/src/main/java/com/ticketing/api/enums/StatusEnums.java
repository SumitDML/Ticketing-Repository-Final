package com.ticketing.api.enums;

public enum StatusEnums {
    New("NEW"),
    Assigned("ASSIGNED"),
    Pending("PENDING"),
    Resolved("RESOLVED"),
    Reopened("REOPENED");

    StatusEnums(String value) {
    }
}
