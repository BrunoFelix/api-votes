package com.brunofelix.api.votes.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class VoteSessionModelTest {

    @Test
    public void testCheckVotingSessionFinished() {
        VoteSession voteSession = new VoteSession();
        voteSession.setClosingAt(LocalDateTime.now().plusMinutes(1));

        assertFalse(voteSession.checkVotingSessionFinished());

        voteSession = new VoteSession();
        voteSession.setClosingAt(LocalDateTime.now().minusMinutes(1));

        assertTrue(voteSession.checkVotingSessionFinished());

        voteSession = new VoteSession();
        voteSession.setFinished(false);

        assertFalse(voteSession.checkVotingSessionFinished());

        voteSession = new VoteSession();
        voteSession.setFinished(true);

        assertTrue(voteSession.checkVotingSessionFinished());
    }

}
