package com.brunofelix.api.votes.event;

import com.brunofelix.api.votes.controller.dto.VoteResultDto;
import com.brunofelix.api.votes.controller.dto.VoteSessionResponseDto;
import com.brunofelix.api.votes.model.VoteSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteSessionFinishedEvent implements KafkaEvent {

    public VoteSessionResponseDto payload;
    public List<VoteResultDto> result;
}
