package com.brunofelix.api.votes;

import com.brunofelix.api.votes.controller.dto.VoteRequestDto;
import com.brunofelix.api.votes.exception.*;
import com.brunofelix.api.votes.model.Agenda;
import com.brunofelix.api.votes.model.Associate;
import com.brunofelix.api.votes.model.Vote;
import com.brunofelix.api.votes.model.VoteSession;
import com.brunofelix.api.votes.repository.AgendaRepository;
import com.brunofelix.api.votes.repository.AssociateRepository;
import com.brunofelix.api.votes.repository.VoteRepository;
import com.brunofelix.api.votes.repository.VoteSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VoteIntegrationTest extends DatabaseContainerConfiguration {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public ObjectMapper objectMapper;

    //repositories
    @Autowired
    public AssociateRepository associateRepository;

    @Autowired
    public AgendaRepository agendaRepository;

    @Autowired
    public VoteSessionRepository voteSessionRepository;

    @Autowired
    public VoteRepository voteRepository;

    //Models
    private Associate associate;

    private Agenda agenda;

    @BeforeEach
    public void setup() {
        voteRepository.deleteAll();
        voteSessionRepository.deleteAll();
        agendaRepository.deleteAll();
        associateRepository.deleteAll();

        this.associate = new Associate("20535141084", "Associate test");
        this.agenda = new Agenda("Agenda test");
    }

    @Test
    @DisplayName("When I try to create vote valid Then vote is created")
    public void createVoteValid() throws Exception {
        Associate savedAssociate = associateRepository.save(this.associate);
        Agenda savedAgenda = agendaRepository.save(this.agenda);
        VoteSession savedVoteSession = voteSessionRepository.save(new VoteSession(LocalDateTime.now().plusHours(1), savedAgenda));

        VoteRequestDto voteRequestDto = new VoteRequestDto(savedAssociate.getId(), savedVoteSession.getId(), Vote.Value.YES);

        mockMvc.perform(post("/v1/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.associate_id").value(savedAssociate.getId()))
                .andExpect(jsonPath("$.agenda_id").value(savedAgenda.getId()))
                .andExpect(jsonPath("$.vote_session_id").value(savedVoteSession.getId()))
                .andExpect(jsonPath("$.value").value(voteRequestDto.getValue().toString()))
                .andExpect(jsonPath("$.created_at").exists());

        List<Vote> votes = voteRepository.findAll();
        assertEquals(votes.size(), 1);
        assertEquals(votes.get(0).getAssociate().getId(), savedAssociate.getId());
        assertEquals(votes.get(0).getAssociate().getCpf(), savedAssociate.getCpf());
        assertEquals(votes.get(0).getVoteSession().getAgenda().getId(), savedAgenda.getId());
        assertEquals(votes.get(0).getVoteSession().getId(), savedVoteSession.getId());
        assertEquals(votes.get(0).getValue(), voteRequestDto.getValue());
    }

    @Test
    @DisplayName("When I try to create vote with associate not found Then return an exception")
    public void createVoteWithAssociateNotFound() throws Exception {
        Agenda savedAgenda = agendaRepository.save(this.agenda);
        VoteSession savedVoteSession = voteSessionRepository.save(new VoteSession(LocalDateTime.now().plusHours(1), savedAgenda));

        VoteRequestDto voteRequestDto = new VoteRequestDto(Long.parseLong("999"), savedVoteSession.getId(), Vote.Value.YES);

        mockMvc.perform(post("/v1/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(status().reason(AssociateNotFoundException.reasonMessage));

        List<Vote> dbVotes = voteRepository.findAll();
        assertEquals(dbVotes.size(), 0);
    }

    @Test
    @DisplayName("When I try to create vote with vote session not found Then return an exception")
    public void createVoteWithVoteSessionNotFound() throws Exception {
        Associate savedAssociate = associateRepository.save(this.associate);

        VoteRequestDto voteRequestDto = new VoteRequestDto(savedAssociate.getId(), Long.parseLong("999"), Vote.Value.YES);

        mockMvc.perform(post("/v1/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(status().reason(VoteSessionNotFoundException.reasonMessage));

        List<Vote> dbVotes = voteRepository.findAll();
        assertEquals(dbVotes.size(), 0);
    }

    @Test
    @DisplayName("When I try to create vote with vote session closed Then return an exception")
    public void createVoteWithVoteSessionClosed() throws Exception {
        Associate savedAssociate = associateRepository.save(this.associate);

        Agenda savedAgenda = agendaRepository.save(this.agenda);
        VoteSession savedVoteSession = voteSessionRepository.save(new VoteSession(LocalDateTime.now().minusHours(1), savedAgenda));

        VoteRequestDto voteRequestDto = new VoteRequestDto(savedAssociate.getId(), savedVoteSession.getId(), Vote.Value.YES);

        mockMvc.perform(post("/v1/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(VoteSessionClosedException.reasonMessage));

        List<Vote> dbVotes = voteRepository.findAll();
        assertEquals(dbVotes.size(), 0);
    }

    @Test
    @DisplayName("When I try to create duplicated vote Then return an exception")
    public void createDuplicateVote() throws Exception {
        Associate savedAssociate = associateRepository.save(this.associate);

        Agenda savedAgenda = agendaRepository.save(this.agenda);
        VoteSession savedVoteSession = voteSessionRepository.save(new VoteSession(LocalDateTime.now().plusHours(1), savedAgenda));

        VoteRequestDto voteRequestDto = new VoteRequestDto(savedAssociate.getId(), savedVoteSession.getId(), Vote.Value.YES);

        voteRepository.save(new Vote(voteRequestDto.getValue(), savedAssociate, savedVoteSession));

        mockMvc.perform(post("/v1/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteRequestDto)))
                .andExpect(status().isConflict())
                .andExpect(status().reason(VoteAlreadyRegisteredException.reasonMessage));

        List<Vote> dbVotes = voteRepository.findAll();
        assertEquals(dbVotes.size(), 1);

    }

    @Test
    @DisplayName("When I try to get vote by id Then the vote is returned")
    public void getById() throws Exception {
        Associate savedAssociate = associateRepository.save(this.associate);

        Agenda savedAgenda = agendaRepository.save(this.agenda);
        VoteSession savedVoteSession = voteSessionRepository.save(new VoteSession(LocalDateTime.now().minusHours(1), savedAgenda));

        VoteRequestDto voteRequestDto = new VoteRequestDto(savedAssociate.getId(), savedVoteSession.getId(), Vote.Value.YES);

        Vote vote = voteRepository.save(new Vote(voteRequestDto.getValue(), savedAssociate, savedVoteSession));

        mockMvc.perform(get("/v1/vote/{id}", vote.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(vote.getId()))
                .andExpect(jsonPath("$.associate_id").value(savedAssociate.getId()))
                .andExpect(jsonPath("$.agenda_id").value(savedAgenda.getId()))
                .andExpect(jsonPath("$.vote_session_id").value(savedVoteSession.getId()))
                .andExpect(jsonPath("$.value").value(voteRequestDto.getValue().toString()))
                .andExpect(jsonPath("$.created_at").exists());

        Vote dbVote = voteRepository.findById(vote.getId()).orElse(new Vote());
        assertEquals(dbVote.getId(), vote.getId());
        assertEquals(dbVote.getAssociate().getId(), savedAssociate.getId());
        assertEquals(dbVote.getVoteSession().getAgenda().getId(), savedAgenda.getId());
        assertEquals(dbVote.getVoteSession().getId(), savedVoteSession.getId());
        assertEquals(dbVote.getValue(), voteRequestDto.getValue());
    }

    @Test
    @DisplayName("When I try to get vote by id not found Then return a exception")
    public void getByIdNotFound() throws Exception {
        mockMvc.perform(get("/v1/vote/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(status().reason(VoteNotFoundException.reasonMessage));

        Vote dbVote = voteRepository.findById(Long.parseLong("999")).orElse(null);
        assertNull(dbVote);
    }

    @Test
    @DisplayName("When I try to get vote by associate Then the list is returned")
    public void getByAssociate() throws Exception {
        Associate savedAssociate = associateRepository.save(new Associate("20535141084", "Associate test"));

        Agenda savedAgenda = agendaRepository.save(new Agenda("Agenda test"));
        VoteSession savedVoteSession = voteSessionRepository.save(new VoteSession(LocalDateTime.now().minusHours(1), savedAgenda));

        VoteRequestDto voteRequestDto = new VoteRequestDto(savedAssociate.getId(), savedVoteSession.getId(), Vote.Value.YES);

        Vote vote = voteRepository.save(new Vote(voteRequestDto.getValue(), savedAssociate, savedVoteSession));

        mockMvc.perform(get("/v1/vote/associate/{id}", savedAssociate.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.content[0]").exists())
                .andExpect(jsonPath("$.content[0].id").value(vote.getId()))
                .andExpect(jsonPath("$.content[0].value").value(vote.getValue().toString()))
                .andExpect(jsonPath("$.total_elements").value(1));

        Page<Vote> dbVotes = voteRepository.findByAssociate(savedAssociate, Pageable.ofSize(1));
        assertEquals(dbVotes.getContent().size(), 1);
        assertEquals(dbVotes.getContent().get(0).getId(), vote.getId());
        assertEquals(dbVotes.getContent().get(0).getValue(), vote.getValue());
    }
}
