package com.brunofelix.api.votes.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssociateRequestDto {

    @NotBlank @NotNull
    private String cpf;

    @NotBlank @NotNull @Size(min = 3, max = 150)
    private String name;
}
