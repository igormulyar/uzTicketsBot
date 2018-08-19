package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.dao.TicketRequestDao;
import com.imuliar.uzTicketsBot.model.TicketRequest;
import com.imuliar.uzTicketsBot.services.HttpTicketsInfoRetriever;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * <p>The state when user should decide if he wants the bot to track the tickets.</p>
 *
 * @author imuliar
 * @since 1.1
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TrackTicketsState extends AbstractState {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackTicketsState.class);

    private HttpTicketsInfoRetriever ticketsInfoRetriever;

    private TicketRequestDao ticketRequestDao;

    @Override
    public void processUpdate(Update update) {
        Long chatId = resolveChatId(update);
        if (update.hasCallbackQuery()) {
            String callbackString = update.getCallbackQuery().getData();
            if (callbackString.equals(CONFIRM_TICKETS_REQUEST)) {
                TicketRequest ticketRequest = context.getTicketRequest();
                String resultUrl = ticketsInfoRetriever.requestTickets(ticketRequest);
                if (resultUrl != null) {
                    publishSearchResults(chatId, resultUrl);
                } else
                    System.out.println("NO RESULT");
                    //TODO propose to track tickets
            }
        }
    }

    void publishSearchResults (Long chatId, String url){
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        markupInline.setKeyboard(keyboard);

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton().setText("Go to beginning.").setCallbackData(TO_BEGGINNING_CALBACK));
        keyboard.add(buttons);
        sendBotResponse(new SendMessage()
                .enableMarkdown(true)
                .setChatId(chatId)
                .setText("Tickets for your request are available, you can watch and buy here: " + url)
                .setReplyMarkup(markupInline));
    }

    @Autowired
    public void setTicketsInfoRetriever(HttpTicketsInfoRetriever ticketsInfoRetriever) {
        this.ticketsInfoRetriever = ticketsInfoRetriever;
    }

    @Autowired
    public void setTicketRequestDao(TicketRequestDao ticketRequestDao) {
        this.ticketRequestDao = ticketRequestDao;
    }
}
