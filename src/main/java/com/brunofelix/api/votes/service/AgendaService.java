package com.brunofelix.api.votes.service;

import com.brunofelix.api.votes.controller.dto.AgendaRequestDto;
import com.brunofelix.api.votes.controller.dto.AgendaResponseDto;
import com.brunofelix.api.votes.controller.dto.VoteResultDto;
import com.brunofelix.api.votes.exception.AgendaNotFoundException;
import com.brunofelix.api.votes.model.Agenda;
import com.brunofelix.api.votes.model.Vote;
import com.brunofelix.api.votes.repository.AgendaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgendaService {

    @Autowired
    private AgendaRepository agendaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public AgendaResponseDto create(AgendaRequestDto agendaRequestDto) {
        Agenda agenda = new Agenda(agendaRequestDto.getDescription());
        return new AgendaResponseDto(agendaRepository.save(agenda), null);
    }

    public Page<AgendaResponseDto> getAll(Pageable pageable) {
        return agendaRepository.findAll(pageable).map(agenda -> new AgendaResponseDto(agenda, this.getVoteResult(agenda)));
    }

    public AgendaResponseDto getById(Long id) {
        Agenda agenda = this.findById(id);
        return new AgendaResponseDto(agenda, this.getVoteResult(agenda));
    }

    protected Agenda findById(Long id) {
        return agendaRepository.findById(id).orElseThrow(AgendaNotFoundException::new);
    }

    private List<VoteResultDto> getVoteResult(Agenda agenda) {
        return agenda.getVotes().stream().collect(Collectors.groupingBy(Vote::getValue, Collectors.counting())).entrySet()
                .stream()
                .map(e -> new VoteResultDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
