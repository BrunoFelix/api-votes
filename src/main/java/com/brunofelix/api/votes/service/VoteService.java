package com.brunofelix.api.votes.service;

import com.brunofelix.api.votes.controller.dto.VoteRequestDto;
import com.brunofelix.api.votes.controller.dto.VoteResponseDto;
import com.brunofelix.api.votes.event.VoteCreatedEvent;
import com.brunofelix.api.votes.exception.VoteAlreadyRegisteredException;
import com.brunofelix.api.votes.exception.VoteNotFoundException;
import com.brunofelix.api.votes.exception.VoteSessionClosedException;
import com.brunofelix.api.votes.model.Associate;
import com.brunofelix.api.votes.model.Vote;
import com.brunofelix.api.votes.model.VoteSession;
import com.brunofelix.api.votes.repository.VoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VoteSessionService voteSessionService;

    @Autowired
    private AssociateService associateService;

    @Autowired
    private KafkaService kafkaService;

    public VoteResponseDto create(VoteRequestDto voteRequestDto) {
        //The associate service throws an exception if the record does not exist
        Associate associate = associateService.findById(voteRequestDto.getAssociateId());

        //The vote session service throws an exception if the record does not exist
        VoteSession voteSession = voteSessionService.findById(voteRequestDto.getVoteSessionId());

        if (voteSession.checkVotingSessionFinished())
            throw new VoteSessionClosedException();

        if (voteRepository.existsByAssociateAndVoteSession(associate, voteSession))
            throw new VoteAlreadyRegisteredException();

        Vote vote = new Vote(voteRequestDto.getValue(), associate, voteSession);
        VoteResponseDto voteResponseDto = new VoteResponseDto(voteRepository.save(vote));

        kafkaService.send(new VoteCreatedEvent(voteResponseDto));

        return voteResponseDto;
    }

    public VoteResponseDto getById(Long id) {
        return new VoteResponseDto(this.findById(id));
    }

    public Page<VoteResponseDto> getByAssociate(Long id, Pageable pageable) {
        Associate associate = associateService.findById(id);
        return voteRepository.findByAssociate(associate, pageable).map(VoteResponseDto::new);
    }

    protected Vote findById(Long id) {
        return voteRepository.findById(id).orElseThrow(VoteNotFoundException::new);
    }

    @EventListener
    private void kafkaVoteCreatedEvent(VoteCreatedEvent event) {
        CompletableFuture.runAsync(() -> log.info(String.format("-- Vote received via Kafka with [id=%s, value=%s]", event.payload.getId(), event.payload.getValue().toString())));
    }
}
