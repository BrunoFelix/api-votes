package com.brunofelix.api.votes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "vote")
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Vote {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "value")
    private Option value;

    @OneToOne
    private Associate associate;

    @ManyToOne
    @JoinColumn(name = "vote_session_id")
    private VoteSession voteSession;

    public enum Option {
        YES("yes"),
        NO("no");

        private String description;

        Option(String description) {
            this.description = description;
        }

        public String toString() {
            return description;
        }
    }
}
