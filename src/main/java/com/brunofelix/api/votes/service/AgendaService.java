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
        agenda = agendaRepository.save(agenda);

        return new AgendaResponseDto(agenda);
    }

    public Page<AgendaResponseDto> findAll(Pageable pageable) {
        return agendaRepository.findAll(pageable).map(AgendaResponseDto::new);
    }

    public AgendaResponseDto findById(Long id) {
        return new AgendaResponseDto(agendaRepository.findById(id).orElseThrow(AssociateNotFoundException::new));
    }
}
