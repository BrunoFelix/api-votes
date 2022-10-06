package com.brunofelix.api.votes.event;

import com.brunofelix.api.votes.controller.dto.VoteSessionResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteSessionCreatedEvent implements KafkaEvent {

    public VoteSessionResponseDto payload;
}
