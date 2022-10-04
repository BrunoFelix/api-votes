package com.brunofelix.api.votes.controller.dto;

import com.brunofelix.api.votes.model.Agenda;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AgendaResponseDto {

    private Long id;
    private String description;
    private LocalDateTime createdAt;

    public AgendaResponseDto (Agenda agenda) {
        this.id = agenda.getId();
        this.description = agenda.getDescription();
        this.createdAt = agenda.getCreatedAt();
    }
}