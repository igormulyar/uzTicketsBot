package com.imuliar.uzTicketsBot;

import com.imuliar.uzTicketsBot.services.InputUpdateHandler;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import static java.lang.Math.toIntExact;

/**
 * <p>The main bot class.</p>
 * <p>
 * <p><b>THE BOT INFO:</b></p>
 * <li><b>Bot's name </b>: uz_tickets_test_bot</li>
 * <li><b>Bot's username </b>: uz_tickets_test_bot</li>
 * <li><b>Use this token to access the HTTP API </b>: <i>559136986:AAGA8dMIaVrdts--oK7ebK3Nfm9HFgufppY</i></li>
 * <li><b>Link </b>: <a href='http://t.me/uz_tickets_test_bot'>Bot's address in telegram</a></li>
 *
 * @author imuliar
 * @since 1.0
 */
@Component
public class UzTicketsBot extends TelegramLongPollingBot {

    private static final String BOT_UZERNAME = "uz_tickets_test_bot";

    private static final String BOT_TOKEN = "559136986:AAGA8dMIaVrdts--oK7ebK3Nfm9HFgufppY";

    private static final Logger LOGGER = LoggerFactory.getLogger(UzTicketsBot.class);

    private static final String EMPTY_CALLBACK = "do_nothing";

    private InputUpdateHandler inputUpdateHandler;

    static {
        ApiContextInitializer.init();
    }

    @PostConstruct
    public void registerBot() {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {

        inputUpdateHandler.handle(update);

        //TODO manage processing the requests ans sending the responses
        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals("/start")) {
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();


            SendMessage sendMessage = new SendMessage()
                    .enableMarkdown(true)
                    .setChatId(update.getMessage().getChatId().toString())
                    .setText("HELLO! \n Please, chose the departure date.")
                    //.setReplyMarkup(buildCalendarPage(null))
            ;
            try {
                execute(sendMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();
            if (call_data.equals("plus_year")) {
                EditMessageText new_message = new EditMessageText()
                        .setChatId(chat_id)
                        .setMessageId(toIntExact(message_id))
                        .setText("+1 year")
                        //.setReplyMarkup(buildCalendarPage(LocalDate.now().plusYears(1)))
                ;
                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (call_data.equals("get_goodbye")) {
                String answer = "GOODBYE, MOTHERFUCKER!";
                EditMessageText new_message = new EditMessageText()
                        .setChatId(chat_id)
                        .setMessageId(toIntExact(message_id))
                        .setText(answer);
                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else
                throw new IllegalStateException("UNSUPPORTED CALLBACK DATA RECEIVED!");

        }
    }



    @Override
    public String getBotUsername() {
        return BOT_UZERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Autowired
    public void setInputUpdateHandler(InputUpdateHandler inputUpdateHandler) {
        this.inputUpdateHandler = inputUpdateHandler;
    }
}
