package com.brunofelix.api.votes.job;

import com.brunofelix.api.votes.service.VoteSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VoteSessionJob {

    @Autowired
    private VoteSessionService voteSessionService;

    @Scheduled(cron = "0 * * * * *")
    private void closeVoteSessions() {
        log.info("-- Start closeVoteSessions job");
        voteSessionService.closeVoteSessions();
        log.info("-- Finished closeVoteSessions job");
    }

}
