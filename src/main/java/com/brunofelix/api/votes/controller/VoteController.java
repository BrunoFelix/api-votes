package com.brunofelix.api.votes.controller;

import com.brunofelix.api.votes.controller.dto.VoteRequestDto;
import com.brunofelix.api.votes.controller.dto.VoteResponseDto;
import com.brunofelix.api.votes.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/vote")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VoteResponseDto create(@RequestBody @Valid VoteRequestDto voteRequestDto) {
        return voteService.create(voteRequestDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VoteResponseDto getById(@PathVariable Long id) {
        return voteService.getById(id);
    }

    @GetMapping("/associate/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Page<VoteResponseDto> getByAssociate(@PathVariable Long id, @PageableDefault(sort = "id", direction = Sort.Direction.ASC, size = Integer.MAX_VALUE) Pageable pageable) {
        return voteService.getByAssociate(id, pageable);
    }


}
