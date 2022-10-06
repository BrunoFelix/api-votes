package com.brunofelix.api.votes.event;

import com.brunofelix.api.votes.controller.dto.AgendaResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgendaCreatedEvent implements KafkaEvent{

    public AgendaResponseDto payload;
}
