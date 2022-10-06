package com.brunofelix.api.votes.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "associate")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Associate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cpf", unique = true)
    @NotNull
    private String cpf;

    @Column(name = "name")
    private String name;

    @Column(name = "created_at")
    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    public Associate(String cpf, String name) {
        this.cpf = cpf;
        this.name = name;
    }
}
