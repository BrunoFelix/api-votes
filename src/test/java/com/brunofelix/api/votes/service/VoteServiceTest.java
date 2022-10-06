package com.brunofelix.api.votes.service;

import com.brunofelix.api.votes.controller.dto.VoteRequestDto;
import com.brunofelix.api.votes.controller.dto.VoteResponseDto;
import com.brunofelix.api.votes.exception.VoteAlreadyRegisteredException;
import com.brunofelix.api.votes.exception.VoteSessionClosedException;
import com.brunofelix.api.votes.model.Agenda;
import com.brunofelix.api.votes.model.Associate;
import com.brunofelix.api.votes.model.Vote;
import com.brunofelix.api.votes.model.VoteSession;
import com.brunofelix.api.votes.repository.VoteRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;

public class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;
    @Mock
    private AgendaService agendaService;
    @Mock
    private AssociateService associateService;
    @Mock
    private VoteSessionService voteSessionService;
    @Mock
    private KafkaService kafkaService;

    @InjectMocks
    private VoteService voteService;

    private VoteRequestDto voteRequestDto;
    private VoteSession voteSession;
    private Agenda agenda;
    private Associate associate;
    private Vote vote;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime closingAt = LocalDateTime.now().plusMinutes(10);

        this.voteRequestDto = new VoteRequestDto(1L, 1L, Vote.Value.YES);

        this.associate = new Associate("62566743088", "Associate Test");
        this.associate.setId(1L);
        this.associate.setCreatedAt(createdAt);

        this.agenda = new Agenda("Agenda Test");
        this.agenda.setId(1L);
        this.agenda.setCreatedAt(createdAt);

        this.voteSession = new VoteSession(closingAt, agenda);
        this.voteSession.setId(1L);
        this.voteSession.setAgenda(this.agenda);
        this.voteSession.setCreatedAt(createdAt);

        this.vote = new Vote(Vote.Value.YES, this.associate, this.voteSession);
        this.vote.setId(1L);
        this.vote.setCreatedAt(createdAt);
    }

    @Test
    void shouldRegisterNewVote() {
        Mockito.when(associateService.findById(any())).thenReturn(this.associate);
        Mockito.when(voteSessionService.findById(any())).thenReturn(this.voteSession);
        Mockito.when(voteRepository.existsByAssociateAndVoteSession(any(), any())).thenReturn(false);
        Mockito.when(voteRepository.save(any())).thenReturn(this.vote);

        VoteResponseDto voteResponseDto = voteService.create(this.voteRequestDto);

        Assertions.assertEquals(voteResponseDto.getId(), this.vote.getId());
        Assertions.assertEquals(voteResponseDto.getAssociateId(), this.associate.getId());
        Assertions.assertEquals(voteResponseDto.getVoteSessionId(), this.voteSession.getId());
        Assertions.assertEquals(voteResponseDto.getValue(), Vote.Value.YES);
    }

    @Test
    public void shouldThrowVoteSessionClosedOnCreateVoteSession() {
        Mockito.when(associateService.findById(any())).thenReturn(this.associate);
        this.voteSession.setFinished(true);
        Mockito.when(voteSessionService.findById(any())).thenReturn(this.voteSession);
        Mockito.when(voteRepository.existsByAssociateAndVoteSession(any(), any())).thenReturn(false);
        Mockito.when(voteRepository.save(any())).thenReturn(this.vote);

        Assertions.assertThrows(VoteSessionClosedException.class, () -> { voteService.create(this.voteRequestDto); });
    }

    @Test
    public void shouldThrowVoteAlreadyRegisteredExceptionOnCreateVoteSession() {
        Mockito.when(associateService.findById(any())).thenReturn(this.associate);
        Mockito.when(voteSessionService.findById(any())).thenReturn(this.voteSession);
        Mockito.when(voteRepository.existsByAssociateAndVoteSession(any(), any())).thenReturn(true);
        Mockito.when(voteRepository.save(any())).thenReturn(this.vote);

        Assertions.assertThrows(VoteAlreadyRegisteredException.class, () -> { voteService.create(this.voteRequestDto); });
    }
}
