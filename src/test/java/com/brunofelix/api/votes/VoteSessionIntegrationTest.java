package com.brunofelix.api.votes;

import com.brunofelix.api.votes.controller.dto.VoteSessionRequestDto;
import com.brunofelix.api.votes.exception.AgendaNotFoundException;
import com.brunofelix.api.votes.exception.VoteSessionClosingAtInvalidException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VoteSessionIntegrationTest extends DatabaseContainerConfiguration {

    @Autowired
    public AssociateRepository associateRepository;
    @Autowired
    public AgendaRepository agendaRepository;
    @Autowired
    public VoteSessionRepository voteSessionRepository;
    @Autowired
    public VoteRepository voteRepository;

    @Autowired
    public ObjectMapper objectMapper;

    private Agenda agenda;

    @Value("${api.path.version.vote-session}")
    private String pathVersionEndpointApi;

    @BeforeEach
    public void setup() {
        voteRepository.deleteAll();
        voteSessionRepository.deleteAll();
        agendaRepository.deleteAll();
        associateRepository.deleteAll();

        this.agenda = new Agenda("Agenda test");
    }

    @Test
    @DisplayName("When I try to create vote session valid Then vote session is created")
    public void createVoteSessionValid() throws Exception {
        agendaRepository.save(this.agenda);

        LocalDateTime closingAt = LocalDateTime.now().plusHours(1);
        VoteSessionRequestDto voteSessionRequestDto = new VoteSessionRequestDto(this.agenda.getId(), closingAt);

        mockMvc.perform(post(this.pathVersionEndpointApi)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteSessionRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.agenda_id").value(this.agenda.getId()))
                .andExpect(jsonPath("$.closing_at").exists())
                .andExpect(jsonPath("$.finished").value(false))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.votes").doesNotExist())
                .andExpect(jsonPath("$.result_votes").doesNotExist());

        List<VoteSession> voteSessions = voteSessionRepository.findAll();
        assertEquals(voteSessions.size(), 1);
        assertEquals(voteSessions.get(0).getAgenda().getId(), this.agenda.getId());
        assertEquals(voteSessions.get(0).getClosingAt().getHour(), closingAt.getHour());
        assertEquals(voteSessions.get(0).getClosingAt().getMinute(), closingAt.getMinute());
        assertEquals(voteSessions.get(0).getFinished(), false);
        assertEquals(voteSessions.get(0).getCreatedAt().getHour(), LocalDateTime.now().getHour());
        assertEquals(voteSessions.get(0).getCreatedAt().getMinute(), LocalDateTime.now().getMinute());

    }

    @Test
    @DisplayName("When I try to create vote session without closingAt Then vote session is created")
    public void createVoteSessionWithoutClosingAt() throws Exception {
        agendaRepository.save(this.agenda);

        VoteSessionRequestDto voteSessionRequestDto = new VoteSessionRequestDto();
        voteSessionRequestDto.setAgendaId(this.agenda.getId());

        mockMvc.perform(post(this.pathVersionEndpointApi)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteSessionRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.agenda_id").value(this.agenda.getId()))
                .andExpect(jsonPath("$.closing_at").exists())
                .andExpect(jsonPath("$.finished").value(false))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.votes").doesNotExist())
                .andExpect(jsonPath("$.result_votes").doesNotExist());


        List<VoteSession> voteSessions = voteSessionRepository.findAll();
        assertEquals(voteSessions.size(), 1);
        assertEquals(voteSessions.get(0).getAgenda().getId(), this.agenda.getId());
        assertEquals(voteSessions.get(0).getClosingAt().getHour(), LocalDateTime.now().plusMinutes(1).getHour());
        assertEquals(voteSessions.get(0).getClosingAt().getMinute(), LocalDateTime.now().plusMinutes(1).getMinute());
        assertEquals(voteSessions.get(0).getFinished(), false);
        assertEquals(voteSessions.get(0).getCreatedAt().getHour(), LocalDateTime.now().getHour());
        assertEquals(voteSessions.get(0).getCreatedAt().getMinute(), LocalDateTime.now().getMinute());

    }

    @Test
    @DisplayName("When I try to create vote session with agenda invalid Then return an exception")
    public void createVoteSessionWithAgendaInvalid() throws Exception {
        VoteSessionRequestDto voteSessionRequestDto = new VoteSessionRequestDto(1L, null);

        mockMvc.perform(post(this.pathVersionEndpointApi)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteSessionRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(status().reason(AgendaNotFoundException.reasonMessage));

        List<VoteSession> voteSessions = voteSessionRepository.findAll();
        assertEquals(voteSessions.size(), 0);
    }

    @Test
    @DisplayName("When I try to create vote session with closing time before now Then return an exception")
    public void createVoteSessionWithClosingTimeBeforeNow() throws Exception {
        agendaRepository.save(this.agenda);
        VoteSessionRequestDto voteSessionRequestDto = new VoteSessionRequestDto(this.agenda.getId(), LocalDateTime.now().minusMinutes(1));

        mockMvc.perform(post(this.pathVersionEndpointApi)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteSessionRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(VoteSessionClosingAtInvalidException.reasonMessage));

        List<VoteSession> voteSessions = voteSessionRepository.findAll();
        assertEquals(voteSessions.size(), 0);
    }

    @Test
    @DisplayName("When I try to get vote sessions by id Then vote sessions is returned")
    public void getById() throws Exception {
        agendaRepository.save(this.agenda);

        VoteSession voteSession = voteSessionRepository.save(
                new VoteSession(LocalDateTime.now().plusHours(1), this.agenda)
        );

        mockMvc.perform(get(this.pathVersionEndpointApi + "/{id}", voteSession.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(voteSession.getId()))
                .andExpect(jsonPath("$.agenda_id").value(this.agenda.getId()))
                .andExpect(jsonPath("$.closing_at").exists())
                .andExpect(jsonPath("$.finished").value(voteSession.getFinished()))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.votes.length()").value(voteSession.getVotes().size()))
                .andExpect(jsonPath("$.result_votes.length()").value(2))
                .andExpect(jsonPath("$.result_votes[0].value").value(Vote.Value.YES.toString()))
                .andExpect(jsonPath("$.result_votes[0].count").value(0))
                .andExpect(jsonPath("$.result_votes[1].value").value(Vote.Value.NO.toString()))
                .andExpect(jsonPath("$.result_votes[1].count").value(0));

        VoteSession dbVoteSession = voteSessionRepository.findById(voteSession.getId()).orElse(new VoteSession());
        assertEquals(dbVoteSession.getAgenda().getId(), voteSession.getAgenda().getId());
        assertEquals(dbVoteSession.getClosingAt().getHour(), voteSession.getClosingAt().getHour());
        assertEquals(dbVoteSession.getClosingAt().getMinute(), voteSession.getClosingAt().getMinute());
        assertEquals(dbVoteSession.getFinished(), voteSession.getFinished());
        assertEquals(dbVoteSession.getCreatedAt().getHour(), voteSession.getCreatedAt().getHour());
        assertEquals(dbVoteSession.getCreatedAt().getMinute(), voteSession.getCreatedAt().getMinute());
    }

    @Test
    @DisplayName("When I try to get all vote sessions Then it should list of all vote sessions")
    public void getAll() throws Exception {
        agendaRepository.save(this.agenda);

        VoteSession voteSession = voteSessionRepository.save(
                new VoteSession(LocalDateTime.now().plusHours(1), this.agenda)
        );

        mockMvc.perform(get(this.pathVersionEndpointApi)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(voteSession.getId()))
                .andExpect(jsonPath("$.content[0].agenda_id").value(this.agenda.getId()))
                .andExpect(jsonPath("$.content[0].closing_at").exists())
                .andExpect(jsonPath("$.content[0].finished").value(voteSession.getFinished()))
                .andExpect(jsonPath("$.content[0].created_at").exists())
                .andExpect(jsonPath("$.content[0].votes.length()").value(0))
                .andExpect(jsonPath("$.content[0].result_votes.length()").value(2))
                .andExpect(jsonPath("$.content[0].result_votes[0].value").value(Vote.Value.YES.toString()))
                .andExpect(jsonPath("$.content[0].result_votes[0].count").value(0))
                .andExpect(jsonPath("$.content[0].result_votes[1].value").value(Vote.Value.NO.toString()))
                .andExpect(jsonPath("$.content[0].result_votes[1].count").value(0))
                .andExpect(jsonPath("$.total_elements").value(1));

        VoteSession dbVoteSession = voteSessionRepository.findById(voteSession.getId()).orElse(new VoteSession());
        assertEquals(dbVoteSession.getAgenda().getId(), voteSession.getAgenda().getId());
        assertEquals(dbVoteSession.getClosingAt().getHour(), voteSession.getClosingAt().getHour());
        assertEquals(dbVoteSession.getClosingAt().getMinute(), voteSession.getClosingAt().getMinute());
        assertEquals(dbVoteSession.getFinished(), voteSession.getFinished());
        assertEquals(dbVoteSession.getCreatedAt().getHour(), voteSession.getCreatedAt().getHour());
        assertEquals(dbVoteSession.getCreatedAt().getMinute(), voteSession.getCreatedAt().getMinute());
    }

    @Test
    @DisplayName("When I try to get vote session by id with result votes Then vote session is returned")
    public void getByIdWithResultVotes() throws Exception {
        Agenda savedAgenda = agendaRepository.save(this.agenda);

        Associate associate1 = associateRepository.save(new Associate("79440319070", "Test 1"));
        Associate associate2 = associateRepository.save(new Associate("20535141084", "Test 2"));
        Associate associate3 = associateRepository.save(new Associate("19367290080", "Test 3"));

        VoteSession voteSession = voteSessionRepository.save(new VoteSession(LocalDateTime.now().plusMinutes(1), savedAgenda));

        voteRepository.save(new Vote(Vote.Value.YES, associate1, voteSession));
        voteRepository.save(new Vote(Vote.Value.YES, associate2, voteSession));
        voteRepository.save(new Vote(Vote.Value.NO, associate3, voteSession));

        mockMvc.perform(get(this.pathVersionEndpointApi + "/{id}", voteSession.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(voteSession.getId()))
                .andExpect(jsonPath("$.agenda_id").value(this.agenda.getId()))
                .andExpect(jsonPath("$.closing_at").exists())
                .andExpect(jsonPath("$.finished").value(voteSession.getFinished()))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.votes.length()").value(3))
                .andExpect(jsonPath("$.result_votes.length()").value(2))
                .andExpect(jsonPath("$.result_votes[0].value").value(Vote.Value.YES.toString()))
                .andExpect(jsonPath("$.result_votes[0].count").value(2))
                .andExpect(jsonPath("$.result_votes[1].value").value(Vote.Value.NO.toString()))
                .andExpect(jsonPath("$.result_votes[1].count").value(1));

        VoteSession dbVoteSession = voteSessionRepository.findById(voteSession.getId()).orElse(new VoteSession());
        assertEquals(dbVoteSession.getAgenda().getId(), voteSession.getAgenda().getId());
        assertEquals(dbVoteSession.getClosingAt().getHour(), voteSession.getClosingAt().getHour());
        assertEquals(dbVoteSession.getClosingAt().getMinute(), voteSession.getClosingAt().getMinute());
        assertEquals(dbVoteSession.getFinished(), voteSession.getFinished());
        assertEquals(dbVoteSession.getCreatedAt().getHour(), voteSession.getCreatedAt().getHour());
        assertEquals(dbVoteSession.getCreatedAt().getMinute(), voteSession.getCreatedAt().getMinute());
    }

}
