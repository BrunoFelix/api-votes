package com.brunofelix.api.votes.controller.dto;

import com.brunofelix.api.votes.model.Vote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteResultDto {

    private Vote.Value value;

    private Long count;
}
