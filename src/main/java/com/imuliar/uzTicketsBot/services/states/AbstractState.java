package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.UzTicketsBot;
import com.imuliar.uzTicketsBot.model.Station;
import com.imuliar.uzTicketsBot.services.UserState;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * <p>Encapsulates properties and behaviour which is common for child states.</p>
 *
 * @author imuliar
 * @since 1.0
 */

public abstract class AbstractState implements UserState {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractState.class);

    protected static final String EMPTY_CALLBACK = "do_nothing";

    protected static final String TO_BEGGINNING_CALBACK = "to_beginning";

    protected static final String ADD_TASK_CALLBACK = "add_task";

    protected static final String STATION_CALLBACK_PATTERN = "stationId:%s";

    protected static final Pattern STATION_CALLBACK_REGEXP_PATTERN = Pattern.compile("^stationId\\:\\d{7}$");

    protected static final String CONFIRM_TICKETS_REQUEST = "confirm_tickets_req";

    protected List<Station> proposedStations;

    protected UzTicketsBot bot;

    protected UserContext context;

    protected Long resolveChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        }
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        throw new IllegalStateException("Can't resolve chatId!!!");
    }

    protected <T extends Serializable, M extends BotApiMethod<T>> void sendBotResponse(M method) {
        try {
            bot.execute(method);
        } catch (Exception e) {
            LOGGER.error("Bot execute method exception!", e);
        }
    }

    protected void publishStationSearchResults(Long chatId) {
        if (CollectionUtils.isEmpty(proposedStations)) {
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            markupInline.setKeyboard(keyboard);

            List<InlineKeyboardButton> buttons = new ArrayList<>();
            buttons.add(new InlineKeyboardButton().setText("Enter again").setCallbackData(ADD_TASK_CALLBACK));
            buttons.add(new InlineKeyboardButton().setText("Cancel").setCallbackData(TO_BEGGINNING_CALBACK));
            keyboard.add(buttons);
            sendBotResponse(new SendMessage()
                    .enableMarkdown(true)
                    .setChatId(chatId)
                    .setText("Sorry, we can't find any station.")
                    .setReplyMarkup(markupInline));
        } else {
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            markupInline.setKeyboard(keyboard);

            List<List<Station>> partitions = ListUtils.partition(proposedStations, 2);
            for (List<Station> partition : partitions) {
                List<InlineKeyboardButton> buttonLine = new ArrayList<>();
                partition.forEach(station -> buttonLine.add(new InlineKeyboardButton()
                        .setText(station.getTitle()).setCallbackData(String.format(STATION_CALLBACK_PATTERN, station.getValue()))));
                keyboard.add(buttonLine);
            }
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            buttons.add(new InlineKeyboardButton().setText("Enter again").setCallbackData(ADD_TASK_CALLBACK));
            buttons.add(new InlineKeyboardButton().setText("Cancel").setCallbackData(TO_BEGGINNING_CALBACK));
            keyboard.add(buttons);
            sendBotResponse(new SendMessage()
                    .enableMarkdown(true)
                    .setChatId(chatId)
                    .setText("Please, choose one of proposed.")
                    .setReplyMarkup(markupInline));
        }
    }

    protected void goToBeginning(Update update) {
        context.setInitialState();
        context.processUpdate(update);
    }

    public UserContext getContext() {
        return context;
    }

    @Lazy
    @Autowired
    public void setContext(UserContext context) {
        this.context = context;
    }

    @Autowired
    public void setBot(UzTicketsBot bot) {
        this.bot = bot;
    }
}
