package com.brunofelix.api.votes;

import com.brunofelix.api.votes.controller.dto.VoteSessionRequestDto;
import com.brunofelix.api.votes.model.Agenda;
import com.brunofelix.api.votes.model.VoteSession;
import com.brunofelix.api.votes.repository.AgendaRepository;
import com.brunofelix.api.votes.repository.VoteSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VoteSessionIntegrationTest extends DatabaseContainerConfiguration {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public AgendaRepository agendaRepository;

    @Autowired
    public VoteSessionRepository voteSessionRepository;

    @Autowired
    public ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        voteSessionRepository.deleteAll();
        agendaRepository.deleteAll();
    }

    @Test
    @DisplayName("When I try to create vote session valid Then vote session is created")
    public void createVoteSessionValid() throws Exception {
        Agenda agenda = new Agenda("Agenda test");
        agendaRepository.save(agenda);

        LocalDateTime closingAt = LocalDateTime.now().plusHours(1);
        VoteSessionRequestDto voteSessionRequestDto = new VoteSessionRequestDto(agenda.getId(), closingAt);

        mockMvc.perform(post("/v1/vote-session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteSessionRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.agenda_id").value(agenda.getId()))
                .andExpect(jsonPath("$.closing_at").value(closingAt.toString()))
                .andExpect(jsonPath("$.finished").value(false))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.votes.length()").value(0));

        List<VoteSession> voteSessions = voteSessionRepository.findAll();
        assertEquals(voteSessions.size(), 1);
        assertEquals(voteSessions.get(0).getAgenda().getId(), agenda.getId());
        assertEquals(voteSessions.get(0).getClosingAt(), closingAt);
        assertEquals(voteSessions.get(0).getFinished(), false);
        assertEquals(voteSessions.get(0).getCreatedAt(), LocalDateTime.now());
        assertEquals(voteSessions.get(0).getVotes().size(), 0);

    }

    @Test
    @DisplayName("When I try to get all vote sessions Then it should list of all vote sessions")
    public void getById() throws Exception {
        Agenda agenda = new Agenda("Agenda test");
        agendaRepository.save(agenda);

        VoteSession voteSession = voteSessionRepository.save(
                new VoteSession(LocalDateTime.now().plusHours(1), agenda)
        );

        mockMvc.perform(get("/v1/vote-session/{id}", voteSession.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(voteSession.getId()))
                .andExpect(jsonPath("$.agenda_id").value(agenda.getId()))
                .andExpect(jsonPath("$.closing_at").value(voteSession.getClosingAt().toString()))
                .andExpect(jsonPath("$.finished").value(voteSession.getFinished()))
                .andExpect(jsonPath("$.created_at").value(voteSession.getCreatedAt().toString()))
                .andExpect(jsonPath("$.votes.size").value(voteSession.getVotes().size()));

        VoteSession dbVoteSession = voteSessionRepository.findById(voteSession.getId()).orElse(new VoteSession());
        assertEquals(dbVoteSession.getAgenda().getId(), voteSession.getAgenda().getId());
        assertEquals(dbVoteSession.getClosingAt(), voteSession.getClosingAt());
        assertEquals(dbVoteSession.getFinished(), voteSession.getFinished());
        assertEquals(dbVoteSession.getCreatedAt(), voteSession.getCreatedAt());
        assertEquals(dbVoteSession.getVotes().size(), voteSession.getVotes().size());
    }

}
