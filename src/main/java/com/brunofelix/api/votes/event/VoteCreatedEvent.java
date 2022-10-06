package com.brunofelix.api.votes.event;

import com.brunofelix.api.votes.controller.dto.VoteResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteCreatedEvent implements KafkaEvent {

    public VoteResponseDto payload;
}
