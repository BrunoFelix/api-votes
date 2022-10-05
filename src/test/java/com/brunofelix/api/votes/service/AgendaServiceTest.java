package com.brunofelix.api.votes.service;

import com.brunofelix.api.votes.controller.dto.AgendaRequestDto;
import com.brunofelix.api.votes.controller.dto.AgendaResponseDto;
import com.brunofelix.api.votes.exception.AgendaNotFoundException;
import com.brunofelix.api.votes.model.Agenda;
import com.brunofelix.api.votes.repository.AgendaRepository;
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

public class AgendaServiceTest {

    @Mock
    private AgendaRepository agendaRepository;

    @InjectMocks
    private AgendaService agendaService;

    //models
    private AgendaRequestDto agendaRequestDto;

    private Agenda agenda;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.agendaRequestDto = new AgendaRequestDto("Agenda Test");
        this.agenda = new Agenda(this.agendaRequestDto.getDescription());
        this.agenda.setId(1L);
        this.agenda.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldRegisterNewAgenda() {
        Mockito.when(agendaRepository.save(any())).thenReturn(this.agenda);
        AgendaResponseDto agendaResponseDto = agendaService.create(this.agendaRequestDto);

        Assertions.assertEquals(agendaResponseDto.getId(), this.agenda.getId());
        Assertions.assertEquals(agendaResponseDto.getDescription(), this.agenda.getDescription());
        Assertions.assertEquals(agendaResponseDto.getCreatedAt(), this.agenda.getCreatedAt());
    }

    @Test
    public void shouldThrowAgendaNotFoundOnGetAgendaById() {
        Mockito.when(agendaRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(AgendaNotFoundException.class, () -> { agendaService.getById(1L); });
    }
}
