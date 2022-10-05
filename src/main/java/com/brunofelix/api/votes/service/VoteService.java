package com.brunofelix.api.votes.service;

import com.brunofelix.api.votes.controller.dto.VoteRequestDto;
import com.brunofelix.api.votes.controller.dto.VoteResponseDto;
import com.brunofelix.api.votes.exception.VoteAlreadyRegisteredException;
import com.brunofelix.api.votes.exception.VoteNotFoundException;
import com.brunofelix.api.votes.exception.VoteSessionClosedException;
import com.brunofelix.api.votes.model.Associate;
import com.brunofelix.api.votes.model.Vote;
import com.brunofelix.api.votes.model.VoteSession;
import com.brunofelix.api.votes.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VoteSessionService voteSessionService;

    @Autowired
    private AssociateService associateService;

    public VoteResponseDto create(VoteRequestDto voteRequestDto) {
        //The associate service throws an exception if the record does not exist
        Associate associate = associateService.findById(voteRequestDto.getAssociateId());

        //The vote session service throws an exception if the record does not exist
        VoteSession voteSession = voteSessionService.findById(voteRequestDto.getVoteSessionId());

        if (voteSession.checkVotingFinished())
            throw new VoteSessionClosedException();

        if (voteRepository.existsByAssociateAndVoteSession(associate, voteSession))
            throw new VoteAlreadyRegisteredException();

        Vote vote = new Vote(voteRequestDto.getValue(), associate, voteSession);

        return new VoteResponseDto(voteRepository.save(vote));
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
}
