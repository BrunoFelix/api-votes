package com.brunofelix.api.votes.service;

import com.brunofelix.api.votes.controller.dto.AgendaRequestDto;
import com.brunofelix.api.votes.controller.dto.AgendaResponseDto;
import com.brunofelix.api.votes.exception.AssociateNotFoundException;
import com.brunofelix.api.votes.model.Agenda;
import com.brunofelix.api.votes.repository.AgendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AgendaService {

    @Autowired
    private AgendaRepository agendaRepository;

    public AgendaResponseDto create(AgendaRequestDto agendaRequestDto) {
        Agenda agenda = new Agenda(agendaRequestDto.getDescription());

        return new AgendaResponseDto(agendaRepository.save(agenda));
    }

    public Page<AgendaResponseDto> getAll(Pageable pageable) {
        return agendaRepository.findAll(pageable).map(AgendaResponseDto::new);
    }

    public AgendaResponseDto getById(Long id) {
        return new AgendaResponseDto(this.findById(id));
    }

    protected Agenda findById(Long id) {
        return agendaRepository.findById(id).orElseThrow(AssociateNotFoundException::new);
    }
}
