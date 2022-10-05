package com.brunofelix.api.votes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Associate not found!")
public class AssociateNotFoundException extends RuntimeException {
    public static final String reasonMessage = "Associate not found!";
}
