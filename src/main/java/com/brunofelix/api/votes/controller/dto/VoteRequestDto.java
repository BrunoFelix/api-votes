package com.brunofelix.api.votes.controller.dto;

import com.brunofelix.api.votes.model.Vote;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class VoteRequestDto {

    @NotNull
    private Long associateId;

    @NotNull
    private Long voteSessionId;

    @NotNull
    private Vote.Value value;
}
