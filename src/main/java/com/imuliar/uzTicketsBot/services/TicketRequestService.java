package com.imuliar.uzTicketsBot.services;

import com.imuliar.uzTicketsBot.model.TicketRequest;
import java.util.List;

/**
 * <p>//TODO Description</p>
 *
 * @author imuliar
 * @since //TODO Insert version
 */
public interface TicketRequestService {

    /**
     * <p>Checks if ticket request with the same date, direction and chatId is already saved.</p>
     *
     * @param chatId        chat id
     * @param ticketRequest ticket request to be compared with db data
     * @return true is already saved, otherwise false
     */
    boolean isAlreadySaved(Long chatId, TicketRequest ticketRequest);

    /**
     * <p>Simply save the passed object to the persistent storage</p>
     *
     * @param ticketRequest ticket request to be saved
     */
    void save(TicketRequest ticketRequest);

    /**
     * <p>Retrieve all active ticket requests from db</p>
     *
     * @return list of ticket requests
     */
    List<TicketRequest> findActiveTicketRequests();
}
