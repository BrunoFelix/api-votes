package com.brunofelix.api.votes.service.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CpfResponseDto {

    public Status status;

    public enum Status {
        ABLE_TO_VOTE("ABLE_TO_VOTE"),
        UNABLE_TO_VOTE("UNABLE_TO_VOTE");

        private String description;

        Status(String description) {
            this.description = description;
        }

        public String toString() {
            return description;
        }
    }
}