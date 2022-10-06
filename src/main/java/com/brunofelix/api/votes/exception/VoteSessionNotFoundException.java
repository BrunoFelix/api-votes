package com.brunofelix.api.votes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Vote session not found!")
public class VoteSessionNotFoundException extends RuntimeException {
    public static final String reasonMessage = "Vote session not found!";
}


