package com.brunofelix.api.votes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Closing date must be greater than the current date!")
public class VoteSessionClosingAtInvalidException extends RuntimeException {}
