package com.brunofelix.api.votes.service;

import com.brunofelix.api.votes.controller.dto.VoteSessionRequestDto;
import com.brunofelix.api.votes.controller.dto.VoteSessionResponseDto;
import com.brunofelix.api.votes.exception.VoteSessionAlreadyRegisteredException;
import com.brunofelix.api.votes.exception.VoteSessionClosingAtInvalidException;
import com.brunofelix.api.votes.exception.VoteSessionNotFoundException;
import com.brunofelix.api.votes.model.Agenda;
import com.brunofelix.api.votes.model.VoteSession;
import com.brunofelix.api.votes.repository.VoteSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VoteSessionService {

    @Autowired
    private VoteSessionRepository voteSessionRepository;

    @Autowired
    private AgendaService agendaService;

    public VoteSessionResponseDto create(VoteSessionRequestDto voteSessionRequestDto) {
        //The agenda service throws an exception if the record does not exist
        Agenda agenda = agendaService.findById(voteSessionRequestDto.getAgendaId());

        if (voteSessionRepository.findByAgenda(agenda).isPresent())
            throw new VoteSessionAlreadyRegisteredException();

        if (voteSessionRequestDto.getClosingAt().isBefore(LocalDateTime.now()) || voteSessionRequestDto.getClosingAt().isEqual(LocalDateTime.now()))
            throw new VoteSessionClosingAtInvalidException();

        VoteSession voteSession = new VoteSession(voteSessionRequestDto.getClosingAt(), agenda);

        return new VoteSessionResponseDto(voteSessionRepository.save(voteSession));
    }

    public Page<VoteSessionResponseDto> getAll(Pageable pageable) {
        return voteSessionRepository.findAll(pageable).map(VoteSessionResponseDto::new);
    }

    public VoteSessionResponseDto getById(Long id) {
        return new VoteSessionResponseDto(this.findById(id));
    }

    protected VoteSession findById(Long id) {
        return voteSessionRepository.findById(id).orElseThrow(VoteSessionNotFoundException::new);
    }
}
