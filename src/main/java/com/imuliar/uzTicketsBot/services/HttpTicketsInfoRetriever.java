package com.imuliar.uzTicketsBot.services;

import com.imuliar.uzTicketsBot.model.TicketRequest;

/**
 * <p>Service for getting tickets info via HTTP.</p>
 *
 * @author imuliar
 * @since 1.0
 */
public interface HttpTicketsInfoRetriever {

    /**
     * <p>Makes a HTTP request and get info about available tickets.</p>
     *
     * @param ticketRequest carries all search criteria
     * @return info about available tickets for specified date and direction
     */
    String requestTickets(TicketRequest ticketRequest);
}
