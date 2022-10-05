package com.brunofelix.api.votes.service;

import com.brunofelix.api.votes.controller.dto.VoteResponseDto;
import com.brunofelix.api.votes.controller.dto.VoteResultDto;
import com.brunofelix.api.votes.controller.dto.VoteSessionRequestDto;
import com.brunofelix.api.votes.controller.dto.VoteSessionResponseDto;
import com.brunofelix.api.votes.exception.VoteSessionAlreadyRegisteredException;
import com.brunofelix.api.votes.exception.VoteSessionClosingAtInvalidException;
import com.brunofelix.api.votes.exception.VoteSessionNotFoundException;
import com.brunofelix.api.votes.model.Agenda;
import com.brunofelix.api.votes.model.Vote;
import com.brunofelix.api.votes.model.VoteSession;
import com.brunofelix.api.votes.repository.VoteSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

        VoteSession voteSession = new VoteSession(voteSessionRequestDto.getClosingAt(), agenda);

        if (voteSession.checkVotingSessionFinished())
            throw new VoteSessionClosingAtInvalidException();

        return new VoteSessionResponseDto(voteSessionRepository.save(voteSession), null, null);
    }

    public Page<VoteSessionResponseDto> getAll(Pageable pageable) {
        return voteSessionRepository.findAll(pageable).map(
                voteSession -> new VoteSessionResponseDto(voteSession, voteSession.getVotes().stream().map(VoteResponseDto::new).collect(Collectors.toList()), this.getVoteResult(voteSession))
        );
    }

    public VoteSessionResponseDto getById(Long id) {
        VoteSession voteSession = this.findById(id);
        return new VoteSessionResponseDto(voteSession, voteSession.getVotes().stream().map(VoteResponseDto::new).collect(Collectors.toList()), this.getVoteResult(voteSession));
    }

    protected VoteSession findById(Long id) {
        return voteSessionRepository.findById(id).orElseThrow(VoteSessionNotFoundException::new);
    }

    private List<VoteResultDto> getVoteResult(VoteSession voteSession) {
        return Arrays.stream(Vote.Value.values()).map(value -> {
            Long count = voteSession.getVotes().stream().filter(vote -> vote.getValue() == value).count();
            return new VoteResultDto(value, count);
        }).collect(Collectors.toList());
    }

    public void closeVoteSessions() {
        voteSessionRepository.findByClosingAtBeforeAndFinished(LocalDateTime.now(), false).stream().forEach(voteSession -> {
                voteSession.setFinished(true);
                voteSessionRepository.save(voteSession);
        });
    }
}
