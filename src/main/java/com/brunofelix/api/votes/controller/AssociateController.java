package com.brunofelix.api.votes.controller;

import com.brunofelix.api.votes.controller.dto.AssociateRequestDto;
import com.brunofelix.api.votes.controller.dto.AssociateResponseDto;
import com.brunofelix.api.votes.service.AssociateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/associate")
public class AssociateController {

    @Autowired
    private AssociateService associateService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssociateResponseDto create(@RequestBody @Valid AssociateRequestDto associateRequestDto) {
        return associateService.create(associateRequestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<AssociateResponseDto> getAll(@PageableDefault(sort = "name", direction = Sort.Direction.ASC, size = Integer.MAX_VALUE) Pageable pageable) {
        return associateService.getAll(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AssociateResponseDto getById(@PathVariable Long id) {
        return associateService.getById(id);
    }
}
