package com.brunofelix.api.votes.controller;

import com.brunofelix.api.votes.controller.dto.VoteSessionRequestDto;
import com.brunofelix.api.votes.controller.dto.VoteSessionResponseDto;
import com.brunofelix.api.votes.service.VoteSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<VoteSessionResponseDto> getAll(@PageableDefault(sort = "id", direction = Sort.Direction.ASC, size = Integer.MAX_VALUE) Pageable pageable) {
        return voteSessionService.getAll(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VoteSessionResponseDto getById(@PathVariable Long id) {
        return voteSessionService.getById(id);
    }

}
