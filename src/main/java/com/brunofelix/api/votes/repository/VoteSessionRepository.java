package com.brunofelix.api.votes.repository;

import com.brunofelix.api.votes.model.Agenda;
import com.brunofelix.api.votes.model.VoteSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoteSessionRepository extends JpaRepository<VoteSession, Long> {

    Optional<VoteSession> findByAgenda(Agenda agenda);
    List<VoteSession> findByClosingAtBeforeAndFinished(LocalDateTime date, Boolean finished);
}
