package com.brunofelix.api.votes.service;

import com.brunofelix.api.votes.controller.dto.AssociateRequestDto;
import com.brunofelix.api.votes.controller.dto.AssociateResponseDto;
import com.brunofelix.api.votes.exception.AssociateNotFoundException;
import com.brunofelix.api.votes.exception.CpfAlreadyRegisteredException;
import com.brunofelix.api.votes.exception.CpfInvalidException;
import com.brunofelix.api.votes.model.Associate;
import com.brunofelix.api.votes.repository.AssociateRepository;
import com.brunofelix.api.votes.service.client.CpfServiceClient;
import com.brunofelix.api.votes.service.client.dto.CpfResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

public class AssociateServiceTest {

    @Mock
    private AssociateRepository associateRepository;
    @Mock
    private CpfServiceClient cpfServiceClient;
    @Mock
    private KafkaService kafkaService;

    @InjectMocks
    private AssociateService associateService;

    private AssociateRequestDto associateRequestDto;
    private Associate associate;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        this.associateRequestDto = new AssociateRequestDto("62566743088", "Test");
        this.associate = new Associate(this.associateRequestDto.getCpf(), this.associateRequestDto.getName());
        this.associate.setId(1L);
        this.associate.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldRegisterNewAssociate() {
        Mockito.when(associateRepository.findByCpf(any())).thenReturn(Optional.empty());
        Mockito.when(cpfServiceClient.getValidateCpf(any())).thenReturn(new CpfResponseDto(CpfResponseDto.Status.ABLE_TO_VOTE));
        Mockito.when(associateRepository.save(any())).thenReturn(this.associate);

        AssociateResponseDto associateResponseDto = associateService.create(this.associateRequestDto);

        Assertions.assertEquals(associateResponseDto.getId(), this.associate.getId());
        Assertions.assertEquals(associateResponseDto.getCpf(), this.associate.getCpf());
        Assertions.assertEquals(associateResponseDto.getName(), this.associate.getName());
        Assertions.assertEquals(associateResponseDto.getCreatedAt(), this.associate.getCreatedAt());
    }

    @Test
    public void shouldThrowCpfAlreadyRegisteredOnCreateAssociate() {
        Mockito.when(associateRepository.findByCpf(any())).thenReturn(Optional.of(this.associate));

        Assertions.assertThrows(CpfAlreadyRegisteredException.class, () -> { associateService.create(this.associateRequestDto); });
    }

    @Test
    public void shouldThrowCpfInvalidRegisteredOnCreateAssociate() {
        Mockito.when(associateRepository.findByCpf(any())).thenReturn(Optional.empty());
        Mockito.when(cpfServiceClient.getValidateCpf(any())).thenReturn(new CpfResponseDto(CpfResponseDto.Status.UNABLE_TO_VOTE));

        Assertions.assertThrows(CpfInvalidException.class, () -> { associateService.create(this.associateRequestDto); });
    }

    @Test
    public void shouldThrowAssociateNotFoundOnGetAssociateById() {
        Mockito.when(associateRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(AssociateNotFoundException.class, () -> { associateService.getById(1L); });
    }
}
