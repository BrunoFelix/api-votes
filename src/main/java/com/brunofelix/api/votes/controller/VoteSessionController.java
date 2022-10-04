package com.brunofelix.api.votes.controller;

import com.brunofelix.api.votes.controller.dto.VoteSessionRequestDto;
import com.brunofelix.api.votes.controller.dto.VoteSessionResponseDto;
import com.brunofelix.api.votes.service.VoteSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/vote-session")
public class VoteSessionController {

    @Autowired
    private VoteSessionService voteSessionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VoteSessionResponseDto create(@RequestBody @Valid VoteSessionRequestDto voteSessionRequestDto) {
        return voteSessionService.create(voteSessionRequestDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VoteSessionResponseDto getById(@PathVariable Long id) {
        return voteSessionService.getById(id);
    }
}
