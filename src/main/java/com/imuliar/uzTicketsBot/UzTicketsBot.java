package com.imuliar.uzTicketsBot;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * <p>The main bot class.</p>
 *
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

    static {
        ApiContextInitializer.init();
    }

    @PostConstruct
    public void registerBot(){
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            LOGGER.error(e.getMessage());
        }
    }


    @Override
    public void onUpdateReceived(Update update) {
        //TODO manage processing the requests ans sending the responses
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText("HELLO MOTHERFUCKER!");
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            LOGGER.error("Exception: ", e.getMessage());
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

}