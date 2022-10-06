package com.brunofelix.api.votes.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteSessionRequestDto {

    @NotNull
    private Long agendaId;

    private LocalDateTime closingAt = LocalDateTime.now().plusMinutes(1);
}
