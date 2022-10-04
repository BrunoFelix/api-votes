package com.brunofelix.api.votes.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class VoteSessionRequestDto {

    @NotNull
    private Long agendaId;

    @NotNull @FutureOrPresent
    private LocalDateTime closingAt = LocalDateTime.now().minusMinutes(1);
}
