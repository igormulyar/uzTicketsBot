package com.imuliar.uzTicketsBot.services.impl;

import com.imuliar.uzTicketsBot.UzTicketsBot;
import com.imuliar.uzTicketsBot.services.HttpTicketsInfoRetriever;
import com.imuliar.uzTicketsBot.services.ScheduledProcessRunner;
import com.imuliar.uzTicketsBot.services.TicketRequestService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
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
    public Map<Long, List<String>> searchTicketsForAllUsers() {
        LOGGER.info("Scheduled task started.");
        Map<Long, List<String>> availableTickets = ticketRequestService.findActiveTicketRequests().stream()
                .collect(Collectors.toMap(tr -> tr.getTelegramUser().getChatId(),
                        t -> new ArrayList<>(Collections.singletonList(ticketsInfoRetriever.requestTickets(t))),
                        (o1, o2) -> {
                            o1.addAll(o2);
                            return o1;
                        }));

        availableTickets.entrySet().stream()
                .peek(e -> e.getValue().removeIf(Objects::isNull))
                .filter(e -> !CollectionUtils.isEmpty(e.getValue()))
                .forEach(e -> notifyUser(e.getKey(), e.getValue()));
        LOGGER.info("Scheduled task ended.");
        return availableTickets;
    }

    private void notifyUser(Long chatId, List<String> urls) {
        LOGGER.info("Notifying users.");
        urls.forEach(url -> {
            try {
                bot.execute(new SendMessage()
                        .enableMarkdown(true)
                        .setChatId(chatId)
                        .setText("Tickets for your request are available, you can watch and buy here: " + url));
            } catch (Exception e) {
                LOGGER.error("Bot execute method exception!", e);
            }
        });
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
