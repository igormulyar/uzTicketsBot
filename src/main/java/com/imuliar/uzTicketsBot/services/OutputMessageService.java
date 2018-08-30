package com.imuliar.uzTicketsBot.services;

import com.imuliar.uzTicketsBot.model.TicketRequest;
import java.util.List;
import java.util.Map;

/**
 * @author imuliar
 * 29.08.2018
 */

public interface OutputMessageService {

    /**
     * <p>Notify users that their ticket request tasks were expired and stopped.</p>
     *
     * @param ticketRequests list of expired ticketRequests
     */
    void notifyStopTrackingExpiredRequests(List<TicketRequest> ticketRequests);

    /**
     * <p>Notify user that tickets were found.</p>
     *
     * @param urlForRequest map whe key is the ticket request and
     *                      value is the link to the web page for choosing and buying the tickets
     */
    void notifyTicketsSearchSuccess(TicketRequest ticketRequest, String resultUrl);

    /**
     * <p>Send to user simple message without any keyboards.</p>
     *
     * @param chatId  chat id
     * @param message message text
     */
    void printSimpleMessage(Long chatId, String message);

    /**
     * <p>Displays active user's tasks to user.</p>
     *
     * @param chatId               chat id
     * @param activeTicketRequests list of active tasks
     */
    void printListOfUserTasks(Long chatId, List<TicketRequest> activeTicketRequests);

    void printTaskOverview(Long chatId, TicketRequest ticketRequest);

    void popUpNotify(String callbackQueryId, String messageText);
}
