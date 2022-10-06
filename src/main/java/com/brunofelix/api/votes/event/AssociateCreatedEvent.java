package com.brunofelix.api.votes.event;

import com.brunofelix.api.votes.controller.dto.AssociateResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssociateCreatedEvent implements KafkaEvent{

    public AssociateResponseDto payload;
}
