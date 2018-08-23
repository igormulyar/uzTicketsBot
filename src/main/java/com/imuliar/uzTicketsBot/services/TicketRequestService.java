package com.imuliar.uzTicketsBot.services;

import com.imuliar.uzTicketsBot.model.TicketRequest;
import java.util.List;

/**
 * <p>Service for {@link TicketRequest} manipulating</p>
 *
 * @author imuliar
 * @since 1.0
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

    /**
     * <p>Mark executed TicketRequest as inactive</p>
     *
     * @param executedRequest TicketRequest to be marked inactive
     */
    void markInactive(TicketRequest executedRequest);

    /**
     * <p>Check if active task limit is not exceed for current user(chatId)</p>
     *
     * @param chatId chat id
     * @return {@code TRUE} if active tasks amount for specified chatId is within limit, otherwise - {@code FALSE}
     */
    boolean isInTaskLimit(Long chatId);
}
