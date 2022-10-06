package com.brunofelix.api.votes.controller.dto;

import com.brunofelix.api.votes.model.Agenda;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgendaResponseDto {

    private Long id;
    private String description;
    private LocalDateTime createdAt;

    private List<VoteResultDto> ResultVotes;

    public AgendaResponseDto (Agenda agenda, List<VoteResultDto> resultVotes) {
        this.id = agenda.getId();
        this.description = agenda.getDescription();
        this.createdAt = agenda.getCreatedAt();
        this.ResultVotes = resultVotes;
    }
}