package com.imuliar.uzTicketsBot.services.impl;

import com.imuliar.uzTicketsBot.services.ScheduledProcessRunner;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * <p>Default {@link ScheduledProcessRunner} implementation.</p>
 *
 * @author imuliar
 * @since 1.0
 */
public class ScheduledProcessRunnerImpl implements ScheduledProcessRunner {

    private static final Integer INITIAL_START_DELAY = 10_000; // milliseconds

    private static final Integer BULK_SEARCH_INTERVAL = 60_000; // milliseconds


    /**
     * {@inheritDoc}
     */
    @Override
    @Scheduled(
            initialDelay = INITIAL_START_DELAY,
            fixedDelay = BULK_SEARCH_INTERVAL)
    public void searchTicketsForAllUsers() {

        /*
        * 1. Search active ticket requests in DB
        *
        * 2. For all active ticket request - run search async task
        *
        * */
    }
}
