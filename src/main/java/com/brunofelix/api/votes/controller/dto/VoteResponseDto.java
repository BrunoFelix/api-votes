package com.brunofelix.api.votes.controller.dto;

import com.brunofelix.api.votes.model.Vote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteResponseDto {

    private Long id;
    private Long associateId;
    private Long voteSessionId;
    private Vote.Value value;

    private LocalDateTime createdAt;

    public VoteResponseDto (Vote vote) {
        this.id = vote.getId();
        this.associateId = vote.getAssociate().getId();
        this.voteSessionId = vote.getVoteSession().getId();
        this.value = vote.getValue();
        this.createdAt = vote.getCreatedAt();
    }

}
