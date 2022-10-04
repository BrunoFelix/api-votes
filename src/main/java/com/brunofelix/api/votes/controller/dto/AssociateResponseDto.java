package com.brunofelix.api.votes.controller.dto;

import com.brunofelix.api.votes.model.Associate;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class AssociateResponseDto {

    private Long id;
    private String cpf;
    private String name;
    private LocalDateTime createdAt;

    public AssociateResponseDto (Associate associate) {
        this.id = associate.getId();
        this.cpf = associate.getCpf();
        this.name = associate.getName();
        this.createdAt = associate.getCreatedAt();
    }
}
