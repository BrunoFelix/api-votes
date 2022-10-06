package com.brunofelix.api.votes.service;

import com.brunofelix.api.votes.controller.dto.AgendaRequestDto;
import com.brunofelix.api.votes.controller.dto.AgendaResponseDto;
import com.brunofelix.api.votes.controller.dto.VoteResultDto;
import com.brunofelix.api.votes.event.AgendaCreatedEvent;
import com.brunofelix.api.votes.exception.AgendaNotFoundException;
import com.brunofelix.api.votes.model.Agenda;
import com.brunofelix.api.votes.model.Vote;
import com.brunofelix.api.votes.repository.AgendaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AgendaService {

    @Autowired
    private AgendaRepository agendaRepository;

    @Autowired
    private KafkaService kafkaService;

    public AgendaResponseDto create(AgendaRequestDto agendaRequestDto) {
        Agenda agenda = new Agenda(agendaRequestDto.getDescription());

        AgendaResponseDto agendaResponseDto = new AgendaResponseDto(agendaRepository.save(agenda));

        kafkaService.send(new AgendaCreatedEvent(agendaResponseDto));

        return agendaResponseDto;
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

    @EventListener
    private void kafkaAgendaCreatedEvent(AgendaCreatedEvent event) {
        CompletableFuture.runAsync(() -> log.info(String.format("-- Agenda received via Kafka with [id=%s, description=%s]", event.payload.getId(), event.payload.getDescription())));
    }

    private List<VoteResultDto> getVoteResult(Agenda agenda) {
        return Arrays.stream(Vote.Value.values()).map(value -> {
            Long count = (agenda.getVoteSession() != null) ? agenda.getVoteSession().getVotes().stream().filter(vote -> vote.getValue() == value).count() : 0L;
            return new VoteResultDto(value, count);
        }).collect(Collectors.toList());
    }
}
