package com.brunofelix.api.votes.repository;

import com.brunofelix.api.votes.model.Associate;
import com.brunofelix.api.votes.model.Vote;
import com.brunofelix.api.votes.model.VoteSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    Boolean existsByAssociateAndVoteSession(Associate associate, VoteSession voteSession);

    Page<Vote> findByAssociate(Associate associate, Pageable pageable);
}
