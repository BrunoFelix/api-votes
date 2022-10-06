package com.brunofelix.api.votes.listener;

import com.brunofelix.api.votes.event.KafkaEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventListener {

    @Autowired
    public ApplicationEventPublisher applicationEventPublisher;

    @KafkaListener(topics = "${kafka.topic.vote-events}", groupId = "vote")
    public void voteEvent(KafkaEvent payload){
        applicationEventPublisher.publishEvent(payload);
    }
}
