package com.brunofelix.api.votes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Agenda not found!")
public class AgendaNotFoundException extends RuntimeException {}
