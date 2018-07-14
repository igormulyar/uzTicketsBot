package com.imuliar.uzTicketsBot.services;

/**
 * <p>Service for starting tasks based on configured schedule.</p>
 *
 * @author imuliar
 * @since 1.0
 */
public interface ScheduledProcessRunner {

    /**
     * <p>This method is supposed to be run periodically by configured schedule.</p>
     * <p>All scheduled service calls has to be defined within this method.</p>
     */
    void searchTicketsForAllUsers();
}
