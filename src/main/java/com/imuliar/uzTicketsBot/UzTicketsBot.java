package com.imuliar.uzTicketsBot;

import com.imuliar.uzTicketsBot.services.InputUpdateHandler;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

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
            LOGGER.error("Can't register bot!", e);
        }
    }

    public <T extends Serializable, M extends BotApiMethod<T>> void sendBotResponse(M method) {
        try {
            execute(method);
        } catch (Exception e) {
            LOGGER.error("Bot execute method exception!", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        inputUpdateHandler.handle(update);
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
