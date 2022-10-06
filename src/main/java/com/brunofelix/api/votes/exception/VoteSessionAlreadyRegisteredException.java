package com.brunofelix.api.votes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Vote session already registered for the agenda!")
public class VoteSessionAlreadyRegisteredException extends RuntimeException {
    public static final String reasonMessage = "Associate has already voted in this session!";
}
