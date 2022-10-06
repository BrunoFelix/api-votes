package com.brunofelix.api.votes;

import com.brunofelix.api.votes.controller.dto.AssociateRequestDto;
import com.brunofelix.api.votes.event.AgendaCreatedEvent;
import com.brunofelix.api.votes.event.AssociateCreatedEvent;
import com.brunofelix.api.votes.exception.AssociateNotFoundException;
import com.brunofelix.api.votes.exception.CpfAlreadyRegisteredException;
import com.brunofelix.api.votes.exception.CpfInvalidException;
import com.brunofelix.api.votes.model.Associate;
import com.brunofelix.api.votes.repository.AssociateRepository;
import com.brunofelix.api.votes.service.KafkaService;
import com.brunofelix.api.votes.service.client.CpfServiceClient;
import com.brunofelix.api.votes.service.client.dto.CpfResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssociateIntegrationTest extends DatabaseContainerConfiguration {

    @Autowired
    public AssociateRepository associateRepository;

    @MockBean
    public CpfServiceClient CpfServiceClient;

    @MockBean
    private KafkaService kafkaService;

    @Autowired
    public ObjectMapper objectMapper;

    private Associate associate;

    private AssociateRequestDto associateRequestDto;

    @Value("${api.path.version.associate}")
    private String pathVersionEndpointApi;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        associateRepository.deleteAll();

        associateRequestDto = new AssociateRequestDto("20535141084", "Associate test");
        associate = new Associate("79440319070", "Associate test");
    }

    @Test
    @DisplayName("When I try to create associate valid Then associate is created")
    public void createAssociateValid() throws Exception {

        when(CpfServiceClient.getValidateCpf(anyString())).thenReturn(new CpfResponseDto(CpfResponseDto.Status.ABLE_TO_VOTE));

        mockMvc.perform(post(this.pathVersionEndpointApi)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(this.associateRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.cpf").value(this.associateRequestDto.getCpf()))
                .andExpect(jsonPath("$.name").value(this.associateRequestDto.getName()))
                .andExpect(jsonPath("$.created_at").exists());

        verify(kafkaService).send(any(AssociateCreatedEvent.class));
    }

    @Test
    @DisplayName("When I try to create associate with CPF invalid Then return an exception")
    public void createAssociateInvalid() throws Exception {
        when(CpfServiceClient.getValidateCpf(anyString())).thenReturn(new CpfResponseDto(CpfResponseDto.Status.UNABLE_TO_VOTE));

        mockMvc.perform(post(this.pathVersionEndpointApi)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(this.associateRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(CpfInvalidException.reasonMessage));

        this.associateRequestDto = new AssociateRequestDto("123", "Associate test");

        mockMvc.perform(post("/v1/associate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(this.associateRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(CpfInvalidException.reasonMessage));
    }

    @Test
    @DisplayName("When I try to create associate with CPF duplicate Then return an exception")
    public void createAssociateDuplicated() throws Exception {
        when(CpfServiceClient.getValidateCpf(anyString())).thenReturn(new CpfResponseDto(CpfResponseDto.Status.ABLE_TO_VOTE));
        associateRepository.save(new Associate(this.associateRequestDto.getCpf(), this.associateRequestDto.getName()));

        mockMvc.perform(post(this.pathVersionEndpointApi)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(this.associateRequestDto)))
                .andExpect(status().isConflict())
                .andExpect(status().reason(CpfAlreadyRegisteredException.reasonMessage));
    }

    @Test
    @DisplayName("When I try to get associate by id not found Then return a exception")
    public void getByIdNotFound() throws Exception {
        mockMvc.perform(get(this.pathVersionEndpointApi + "/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(status().reason(AssociateNotFoundException.reasonMessage));

        Associate dbAssociate = associateRepository.findById(Long.parseLong("999")).orElse(null);
        assertNull(dbAssociate);
    }

    @Test
    @DisplayName("When I try to get associate by id Then associate is returned")
    public void getById() throws Exception {
        Associate savedAssociate = associateRepository.save(this.associate);

        mockMvc.perform(get(this.pathVersionEndpointApi + "/{id}", savedAssociate.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(savedAssociate.getId()))
                .andExpect(jsonPath("$.cpf").value(savedAssociate.getCpf()))
                .andExpect(jsonPath("$.name").value(savedAssociate.getName()));
    }

    @Test
    @DisplayName("When I try to get all associates Then it should list of all associates")
    public void getAll() throws Exception {
        Associate savedAssociate = associateRepository.save(this.associate);

        mockMvc.perform(get(this.pathVersionEndpointApi)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0]").exists())
                .andExpect(jsonPath("$.content[0].id").value(savedAssociate.getId()))
                .andExpect(jsonPath("$.content[0].cpf").value(savedAssociate.getCpf()))
                .andExpect(jsonPath("$.content[0].name").value(savedAssociate.getName()))
                .andExpect(jsonPath("$.total_elements").value(1));
    }

}
