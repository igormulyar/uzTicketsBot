package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.model.TelegramUser;
import com.imuliar.uzTicketsBot.model.TicketRequest;
import com.imuliar.uzTicketsBot.model.TicketRequestStatus;
import com.imuliar.uzTicketsBot.services.HttpTicketsInfoRetriever;
import com.imuliar.uzTicketsBot.services.TicketRequestService;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
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

    private static final String TRACK_TICKETS_CALLBACK = "track_tickets";

    private HttpTicketsInfoRetriever ticketsInfoRetriever;

    private TicketRequestService ticketRequestService;

    @Override
    public void processUpdate(Update update) {
        Long chatId = resolveChatId(update);
        if (update.hasCallbackQuery()) {
            String callbackString = update.getCallbackQuery().getData();
            if (callbackString.equals(CONFIRM_TICKETS_REQUEST)) {
                searchTicketsNow(chatId);
            }
            if (callbackString.equals(TO_BEGGINNING_CALBACK)) {
                goToBeginning(update);
            }
            if (callbackString.equals(TRACK_TICKETS_CALLBACK)) {
                addTaskForTracking(update, chatId);
            }
        }
    }

    private void searchTicketsNow(Long chatId) {
        TicketRequest ticketRequest = context.getTicketRequest();
        String resultUrl = ticketsInfoRetriever.requestTickets(ticketRequest);
        if (resultUrl != null) {
            LOGGER.info("Tickets found: ? ?", ticketRequest, resultUrl);
            publishSearchResults(chatId, resultUrl);
        } else {
            LOGGER.info("Tickets not found: ?", ticketRequest);
            proposeToTrack(chatId);
        }
    }

    private void addTaskForTracking(Update update, Long chatId) {
        TicketRequest ticketRequest = context.getTicketRequest();
        if(!ticketRequestService.isInTaskLimit(chatId)){
            notifyToMuchTasks(update.getCallbackQuery().getId());
        }
        else if (!ticketRequestService.isAlreadySaved(chatId, ticketRequest)) {
            ticketRequest.setRequestStatus(TicketRequestStatus.ACTIVE);
            ticketRequest.setTelegramUser(new TelegramUser(chatId));
            ticketRequestService.save(ticketRequest);
            publishTaskAdded(update.getCallbackQuery().getId());
            goToBeginning(update);
        } else {
            notifyIsAlreadySaved(update.getCallbackQuery().getId());
            goToBeginning(update);
        }
    }

    private void notifyToMuchTasks(String callbackQueryId) {
        bot.sendBotResponse(new AnswerCallbackQuery()
                .setCallbackQueryId(callbackQueryId)
                .setText("You can not have more than 3 active tasks running. This search request will not be tracked automatically."));
    }

    private void notifyIsAlreadySaved(String callbackQueryId) {
        bot.sendBotResponse(new AnswerCallbackQuery()
                .setCallbackQueryId(callbackQueryId)
                .setText("The task is already added for tracking."));
    }

    private void proposeToTrack(Long chatId) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        markupInline.setKeyboard(keyboard);

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton().setText("Track tickets").setCallbackData(TRACK_TICKETS_CALLBACK));
        buttons.add(new InlineKeyboardButton().setText("Go to beginning").setCallbackData(TO_BEGGINNING_CALBACK));
        keyboard.add(buttons);
        bot.sendBotResponse(new SendMessage()
                .enableMarkdown(true)
                .setChatId(chatId)
                .setText("No tickets available now. Do you want me to search the tickets for you?")
                .setReplyMarkup(markupInline));
    }

    private void publishTaskAdded(String callbackQueryId) {
        bot.sendBotResponse(new AnswerCallbackQuery()
                .setCallbackQueryId(callbackQueryId)
                .setText("The tickets request were added for tracking. I will notify you when available tickets appear."));
    }

    private void publishSearchResults(Long chatId, String url) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        markupInline.setKeyboard(keyboard);

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton().setText("Go to beginning.").setCallbackData(TO_BEGGINNING_CALBACK));
        keyboard.add(buttons);
        bot.sendBotResponse(new SendMessage()
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
    public void setTicketRequestService(TicketRequestService ticketRequestService) {
        this.ticketRequestService = ticketRequestService;
    }
}
