package com.brunofelix.api.votes;

import com.brunofelix.api.votes.controller.dto.AssociateRequestDto;
import com.brunofelix.api.votes.model.Associate;
import com.brunofelix.api.votes.repository.AssociateRepository;
import com.brunofelix.api.votes.service.client.CpfServiceClient;
import com.brunofelix.api.votes.service.client.dto.CpfResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssociateIntegrationTest extends DatabaseContainerConfiguration {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public AssociateRepository associateRepository;

    @MockBean
    public CpfServiceClient CpfServiceClient;

    @Autowired
    public ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        associateRepository.deleteAll();
    }

    @Test
    @DisplayName("When I try to create associate valid Then associate is created")
    public void createAssociateValid() throws Exception {
        AssociateRequestDto associateRequestDto = new AssociateRequestDto("20535141084", "Associate test");

        when(CpfServiceClient.getValidateCpf(anyString())).thenReturn(new CpfResponseDto(CpfResponseDto.Status.ABLE_TO_VOTE));

        mockMvc.perform(post("/v1/associate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(associateRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.cpf").value(associateRequestDto.getCpf()))
                .andExpect(jsonPath("$.name").value(associateRequestDto.getName()))
                .andExpect(jsonPath("$.created_at").exists());
    }

    @Test
    @DisplayName("When I try to create associate with CPF invalid Then Then return an error")
    public void createAssociateInvalid() throws Exception {
        AssociateRequestDto associateRequestDto = new AssociateRequestDto("00000000000", "Associate test");

        when(CpfServiceClient.getValidateCpf(anyString())).thenReturn(new CpfResponseDto(CpfResponseDto.Status.UNABLE_TO_VOTE));

        mockMvc.perform(post("/v1/associate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(associateRequestDto)))
                .andExpect(status().isBadRequest());

        associateRequestDto = new AssociateRequestDto("123", "Associate test");

        mockMvc.perform(post("/v1/associate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(associateRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("When I try to get all associates Then it should list of all associates")
    public void findAll() throws Exception {
        Associate associate = new Associate("79440319070", "Associate test");
        associateRepository.save(associate);

        mockMvc.perform(get("/v1/associate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.content[0]").exists())
                .andExpect(jsonPath("$.total_elements").value(1));
    }

}
