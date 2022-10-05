package com.brunofelix.api.votes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Associate has already voted in this session!")
public class VoteDuplicatedException extends RuntimeException {
    public static final String reasonMessage = "Associate has already voted in this session!";
}