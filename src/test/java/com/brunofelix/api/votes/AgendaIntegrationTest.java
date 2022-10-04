package com.brunofelix.api.votes;

import com.brunofelix.api.votes.controller.dto.AgendaRequestDto;
import com.brunofelix.api.votes.model.Agenda;
import com.brunofelix.api.votes.repository.AgendaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
    public ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        agendaRepository.deleteAll();
    }

    @Test
    @DisplayName("When I try to create agenda valid Then agenda is created")
    public void createAssociateValid() throws Exception {
        AgendaRequestDto agendaRequestDto = new AgendaRequestDto("Agenda test");

        mockMvc.perform(post("/v1/agenda")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendaRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value(agendaRequestDto.getDescription()))
                .andExpect(jsonPath("$.created_at").exists());
    }

    @Test
    @DisplayName("When I try to get all agendas Then it should list of all agendas")
    public void findAll() throws Exception {
        Agenda agenda = new Agenda("Agenda test");
        agendaRepository.save(agenda);

        mockMvc.perform(get("/v1/agenda")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.content[0]").exists())
                .andExpect(jsonPath("$.total_elements").value(1));
    }

}
