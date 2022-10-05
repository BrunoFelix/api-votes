package com.brunofelix.api.votes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Voting session closed!")
public class VoteSessionClosedException extends RuntimeException {
    public static final String reasonMessage = "Voting session closed!";
}