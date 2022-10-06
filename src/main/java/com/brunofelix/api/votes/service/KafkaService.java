package com.brunofelix.api.votes.service;

import com.brunofelix.api.votes.event.KafkaEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaService {

    @Value("${kafka.topic.vote-events}")
    private String topic;

    private final KafkaTemplate<String, KafkaEvent> kafkaTemplate;

    public void send(KafkaEvent event) {
        kafkaTemplate.send(topic, event);
    }
}
