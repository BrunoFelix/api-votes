package com.brunofelix.api.votes.controller;

import com.brunofelix.api.votes.controller.dto.AgendaRequestDto;
import com.brunofelix.api.votes.controller.dto.AgendaResponseDto;
import com.brunofelix.api.votes.service.AgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/agenda")
public class AgendaController {

    @Autowired
    private AgendaService agendaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgendaResponseDto create(@RequestBody @Valid AgendaRequestDto agendaRequestDto) {
        return agendaService.create(agendaRequestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<AgendaResponseDto> findAll(@PageableDefault(sort = "id", direction = Sort.Direction.ASC, size = Integer.MAX_VALUE) Pageable pageable) {
        return agendaService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AgendaResponseDto findById(@PathVariable Long id) {
        return agendaService.findById(id);
    }
}
