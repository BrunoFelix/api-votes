package com.brunofelix.api.votes.service;

import com.brunofelix.api.votes.controller.dto.VoteSessionRequestDto;
import com.brunofelix.api.votes.controller.dto.VoteSessionResponseDto;
import com.brunofelix.api.votes.exception.VoteSessionAlreadyRegisteredException;
import com.brunofelix.api.votes.exception.VoteSessionClosingAtInvalidException;
import com.brunofelix.api.votes.exception.VoteSessionNotFoundException;
import com.brunofelix.api.votes.model.Agenda;
import com.brunofelix.api.votes.model.Associate;
import com.brunofelix.api.votes.model.VoteSession;
import com.brunofelix.api.votes.repository.VoteSessionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

public class VoteSessionServiceTest {

    @Mock
    private VoteSessionRepository voteSessionRepository;

    @Mock
    private AgendaService agendaService;

    @InjectMocks
    private VoteSessionService voteSessionService;

    //models
    private VoteSessionRequestDto voteSessionRequestDto;

    private VoteSession voteSession;

    private Agenda agenda;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime closingAt = LocalDateTime.now().plusMinutes(10);

        this.voteSessionRequestDto = new VoteSessionRequestDto(1L, closingAt);

        this.agenda = new Agenda("Agenda Test");
        this.agenda.setId(1L);
        this.agenda.setCreatedAt(createdAt);

        this.voteSession = new VoteSession(closingAt, agenda);
        this.voteSession.setId(1L);
        this.voteSession.setCreatedAt(createdAt);
    }

    @Test
    void shouldRegisterNewVoteSession() {
        Mockito.when(agendaService.findById(any())).thenReturn(this.agenda);
        Mockito.when(voteSessionRepository.findByAgenda(any())).thenReturn(Optional.empty());
        Mockito.when(voteSessionRepository.save(any())).thenReturn(this.voteSession);

        VoteSessionResponseDto voteSessionResponseDto = voteSessionService.create(this.voteSessionRequestDto);

        Assertions.assertEquals(voteSessionResponseDto.getId(), this.voteSession.getId());
        Assertions.assertEquals(voteSessionResponseDto.getAgendaId(), this.voteSession.getAgenda().getId());
        Assertions.assertEquals(voteSessionResponseDto.getClosingAt(), this.voteSession.getClosingAt());
    }

    @Test
    public void shouldThrowVoteSessionAlreadyRegisteredOnCreateVoteSession() {
        Mockito.when(agendaService.findById(any())).thenReturn(this.agenda);
        Mockito.when(voteSessionRepository.findByAgenda(any())).thenReturn(Optional.of(this.voteSession));

        Assertions.assertThrows(VoteSessionAlreadyRegisteredException.class, () -> { voteSessionService.create(this.voteSessionRequestDto); });
    }

    @Test
    public void shouldThrowVoteSessionClosingAtInvalidOnCreateVoteSession() {
        Mockito.when(agendaService.findById(any())).thenReturn(this.agenda);
        Mockito.when(voteSessionRepository.findByAgenda(any())).thenReturn(Optional.empty());
        Mockito.when(voteSessionRepository.save(any())).thenReturn(this.voteSession);
        this.voteSessionRequestDto.setClosingAt(LocalDateTime.now().minusMinutes(1));

        Assertions.assertThrows(VoteSessionClosingAtInvalidException.class, () -> { voteSessionService.create(this.voteSessionRequestDto); });
    }

    @Test
    public void shouldThrowVoteSessionNotFoundOnGetVoteSessionById() {
        Mockito.when(voteSessionRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(VoteSessionNotFoundException.class, () -> { voteSessionService.getById(1L); });
    }

    @Test
    void shouldCloseVoteSessions() {
        List<VoteSession> voteSessionList = new ArrayList<>();
        voteSessionList.add(this.voteSession);
        Mockito.when(voteSessionRepository.findByClosingAtBeforeAndFinished(any(), any())).thenReturn(voteSessionList);

        voteSessionService.closeVoteSessions();

        Assertions.assertEquals(this.voteSession.getFinished(), true);
    }
}
