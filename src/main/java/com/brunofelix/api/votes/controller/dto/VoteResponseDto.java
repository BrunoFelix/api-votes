package com.brunofelix.api.votes.controller.dto;

import com.brunofelix.api.votes.model.Vote;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class VoteResponseDto {

    private Long id;

    private Long associateId;

    private Long agendaId;

    private Long voteSessionId;

    private Vote.Value value;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    public VoteResponseDto (Vote vote) {
        this.id = vote.getId();
        this.associateId = vote.getAssociate().getId();
        this.agendaId = vote.getVoteSession().getAgenda().getId();
        this.voteSessionId = vote.getVoteSession().getId();
        this.value = vote.getValue();
        this.createdAt = vote.getCreatedAt();
    }

}
