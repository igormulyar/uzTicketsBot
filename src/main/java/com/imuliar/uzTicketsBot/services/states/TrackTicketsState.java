package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.model.TicketRequest;
import com.imuliar.uzTicketsBot.services.HttpTicketsInfoRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Update;

/**
 * <p>The state when user should decide if he wants the bot to track the tickets.</p>
 *
 * @author imuliar
 * @since 1.1
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TrackTicketsState extends AbstractState {

    private HttpTicketsInfoRetriever ticketsInfoRetriever;

    @Override
    public void processUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            String callbackString = update.getCallbackQuery().getData();
            if (callbackString.equals(CONFIRM_TICKETS_REQUEST)) {
                TicketRequest ticketRequest = context.getTicketRequest();
                String resultUrl = ticketsInfoRetriever.requestTickets(ticketRequest);
                if (resultUrl != null) {
                    System.out.println(resultUrl);
                } else
                    System.out.println("NO RESULT");
            }

        }
    }

    @Autowired
    public void setTicketsInfoRetriever(HttpTicketsInfoRetriever ticketsInfoRetriever) {
        this.ticketsInfoRetriever = ticketsInfoRetriever;
    }
}
