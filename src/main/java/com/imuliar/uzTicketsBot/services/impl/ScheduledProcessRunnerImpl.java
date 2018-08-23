package com.imuliar.uzTicketsBot.services.impl;

import com.imuliar.uzTicketsBot.UzTicketsBot;
import com.imuliar.uzTicketsBot.model.TicketRequest;
import com.imuliar.uzTicketsBot.services.HttpTicketsInfoRetriever;
import com.imuliar.uzTicketsBot.services.ScheduledProcessRunner;
import com.imuliar.uzTicketsBot.services.TicketRequestService;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;

/**
 * <p>Default {@link ScheduledProcessRunner} implementation.</p>
 *
 * @author imuliar
 * @since 1.0
 */
@Component
public class ScheduledProcessRunnerImpl implements ScheduledProcessRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledProcessRunnerImpl.class);

    private static final int INITIAL_START_DELAY = 10_000; // milliseconds

    private static final int BULK_SEARCH_INTERVAL = 900_000; // milliseconds

    private UzTicketsBot bot;

    private HttpTicketsInfoRetriever ticketsInfoRetriever;

    private TicketRequestService ticketRequestService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Scheduled(
            initialDelay = INITIAL_START_DELAY,
            fixedDelay = BULK_SEARCH_INTERVAL)
    public void searchTicketsForAllUsers() {
        LOGGER.info("Scheduled task started.");

        ticketRequestService.findActiveTicketRequests().stream()
                .collect(() -> new HashMap<TicketRequest, String>(), (map, req) -> map.put(req, ticketsInfoRetriever.requestTickets(req)), HashMap::putAll)
                .entrySet().stream()
                .filter(e -> e.getValue() != null)
                .peek(this::notifyUser)
                .forEach(e -> ticketRequestService.markInactive(e.getKey()));

        LOGGER.info("Scheduled task ended.");
    }

    private void notifyUser(Map.Entry<TicketRequest, String> urlForRequest) {
        TicketRequest ticketRequest = urlForRequest.getKey();
        String url = urlForRequest.getValue();
        bot.sendBotResponse(new SendMessage()
                .enableMarkdown(true)
                .setChatId(ticketRequest.getTelegramUser().getChatId())
                .setText(String.format("Tickets for [%s] are available, you can watch and buy here: %s . Tracking stopped.", ticketRequest, url)));
    }

    @Autowired
    public void setTicketsInfoRetriever(HttpTicketsInfoRetriever ticketsInfoRetriever) {
        this.ticketsInfoRetriever = ticketsInfoRetriever;
    }

    @Autowired
    public void setTicketRequestService(TicketRequestService ticketRequestService) {
        this.ticketRequestService = ticketRequestService;
    }

    @Autowired
    public void setBot(UzTicketsBot bot) {
        this.bot = bot;
    }
}
