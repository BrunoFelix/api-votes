package com.brunofelix.api.votes;

import com.brunofelix.api.votes.controller.dto.AgendaRequestDto;
import com.brunofelix.api.votes.exception.AgendaNotFoundException;
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
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AgendaIntegrationTest extends DatabaseContainerConfiguration {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public AgendaRepository agendaRepository;

    @Autowired
    public AssociateRepository associateRepository;

    @Autowired
    public VoteSessionRepository voteSessionRepository;

    @Autowired
    public VoteRepository voteRepository;

    @Autowired
    public ObjectMapper objectMapper;

    private Agenda agenda;

    private AgendaRequestDto agendaRequestDto;

    @Value("${api.path.version.agenda}")
    private String pathVersionEndpointApi;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        voteRepository.deleteAll();
        voteSessionRepository.deleteAll();
        agendaRepository.deleteAll();
        associateRepository.deleteAll();
        this.agendaRequestDto = new AgendaRequestDto("Agenda test");
        this.agenda = new Agenda("Agenda test");
    }

    @Test
    @DisplayName("When I try to create agenda valid Then agenda is created")
    public void createAgendaValid() throws Exception {
        mockMvc.perform(post(this.pathVersionEndpointApi)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(this.agendaRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value(agendaRequestDto.getDescription()))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.result_votes").doesNotExist());
    }

    @Test
    @DisplayName("When I try to get agenda by id not found Then return a exception")
    public void getByIdNotFound() throws Exception {
        mockMvc.perform(get(this.pathVersionEndpointApi + "/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(status().reason(AgendaNotFoundException.reasonMessage));

        Agenda dbAgenda = agendaRepository.findById(Long.parseLong("999")).orElse(null);
        assertNull(dbAgenda);
    }

    @Test
    @DisplayName("When I try to get agenda by id with result votes Then agenda is returned")
    public void getByIdWithResultVotes() throws Exception {
        Agenda savedAgenda = agendaRepository.save(this.agenda);

        Associate associate1 = associateRepository.save(new Associate("79440319070", "Test 1"));
        Associate associate2 = associateRepository.save(new Associate("20535141084", "Test 2"));
        Associate associate3 = associateRepository.save(new Associate("19367290080", "Test 3"));

        VoteSession voteSession = voteSessionRepository.save(new VoteSession(LocalDateTime.now().plusMinutes(1), savedAgenda));

        voteRepository.save(new Vote(Vote.Value.YES, associate1, voteSession));
        voteRepository.save(new Vote(Vote.Value.YES, associate2, voteSession));
        voteRepository.save(new Vote(Vote.Value.NO, associate3, voteSession));

        mockMvc.perform(get(this.pathVersionEndpointApi + "/{id}", savedAgenda.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(savedAgenda.getId()))
                .andExpect(jsonPath("$.description").value(savedAgenda.getDescription()))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.result_votes.length()").value(2))
                .andExpect(jsonPath("$.result_votes[0].value").value(Vote.Value.YES.toString()))
                .andExpect(jsonPath("$.result_votes[0].count").value(2))
                .andExpect(jsonPath("$.result_votes[1].value").value(Vote.Value.NO.toString()))
                .andExpect(jsonPath("$.result_votes[1].count").value(1));
    }

    @Test
    @DisplayName("When I try to get agenda by id not found Then agenda is returned")
    public void getById() throws Exception {
        Agenda savedAgenda = agendaRepository.save(this.agenda);

        mockMvc.perform(get(this.pathVersionEndpointApi + "/{id}", savedAgenda.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(savedAgenda.getId()))
                .andExpect(jsonPath("$.description").value(savedAgenda.getDescription()))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.result_votes.length()").value(2))
                .andExpect(jsonPath("$.result_votes[0].value").value(Vote.Value.YES.toString()))
                .andExpect(jsonPath("$.result_votes[0].count").value(0))
                .andExpect(jsonPath("$.result_votes[1].value").value(Vote.Value.NO.toString()))
                .andExpect(jsonPath("$.result_votes[1].count").value(0));
    }

    @Test
    @DisplayName("When I try to get all agendas Then it should list of all agendas")
    public void getAll() throws Exception {
        Agenda savedAgenda = agendaRepository.save(this.agenda);

        mockMvc.perform(get(this.pathVersionEndpointApi)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.content[0]").exists())
                .andExpect(jsonPath("$.content[0].id").value(savedAgenda.getId()))
                .andExpect(jsonPath("$.content[0].description").value(savedAgenda.getDescription()))
                .andExpect(jsonPath("$.content[0].created_at").exists())
                .andExpect(jsonPath("$.content[0].result_votes.length()").value(2))
                .andExpect(jsonPath("$.content[0].result_votes[0].value").value(Vote.Value.YES.toString()))
                .andExpect(jsonPath("$.content[0].result_votes[0].count").value(0))
                .andExpect(jsonPath("$.content[0].result_votes[1].value").value(Vote.Value.NO.toString()))
                .andExpect(jsonPath("$.content[0].result_votes[1].count").value(0))
                .andExpect(jsonPath("$.total_elements").value(1));
    }



}
