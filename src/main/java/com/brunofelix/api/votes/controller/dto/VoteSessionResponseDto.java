package com.brunofelix.api.votes.controller.dto;

import com.brunofelix.api.votes.model.VoteSession;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoteSessionResponseDto {

    private Long id;
    private Long agendaId;
    private Boolean finished;
    private LocalDateTime closingAt;
    private LocalDateTime createdAt;
    private List<VoteResponseDto> votes;
    private List<VoteResultDto> ResultVotes;

    public VoteSessionResponseDto (VoteSession voteSession, List<VoteResponseDto> votes, List<VoteResultDto> resultVotes) {
        this.id = voteSession.getId();
        this.agendaId = voteSession.getAgenda().getId();
        this.finished = voteSession.getFinished();
        this.closingAt = voteSession.getClosingAt();
        this.createdAt = voteSession.getCreatedAt();
        this.votes = votes;
        this.ResultVotes = resultVotes;
    }

    public VoteSessionResponseDto (VoteSession voteSession) {
        this.id = voteSession.getId();
        this.agendaId = voteSession.getAgenda().getId();
        this.finished = voteSession.getFinished();
        this.closingAt = voteSession.getClosingAt();
        this.createdAt = voteSession.getCreatedAt();
    }
}
