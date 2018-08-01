package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.services.StationCodeResolver;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * <p>The state when user choosing the station of departure.</p>
 *
 * @author imuliar
 * @since 1.0
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DepartureStationState extends AbstractState {

    private static final String ADD_TASK_CALLBACK = "add_task";

    private StationCodeResolver stationCodeResolver;

    private ArrivalStationState arrivalStationState;

    private List<Station> proposedStations;

    @Override
    public void processUpdate(Update update) {
        Long chatId = resolveChatId(update);
        if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().equals(ADD_TASK_CALLBACK)) {
                SendMessage sendMessage = new SendMessage()
                        .enableMarkdown(true)
                        .setChatId(chatId)
                        .setText("Please enter the station of departure.");
                try {
                    bot.execute(sendMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (update.getCallbackQuery().getData().matches(STATION_CALLBACK_REGEXP)) {
                String callbackString = update.getCallbackQuery().getData();
                String selectedId = callbackString.split(":")[1];
                proposedStations.stream()
                        .filter(proposed -> proposed.getValue().equals(selectedId))
                        .findAny()
                        .ifPresent(station -> publishSelectedStation(chatId, station));
            }
            if (update.getCallbackQuery().getData().equals(ENTER_ARRIVAL)) {
                arrivalStationState.setContext(context);
                context.setState(arrivalStationState);
                context.processUpdate(update);
            }
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String userInput = update.getMessage().getText();
            proposedStations = stationCodeResolver.resolveProposedStations(userInput);
            publishStationSearchResults(chatId);
        }
    }

    private void publishStationSearchResults(Long chatId) {
        if (CollectionUtils.isEmpty(proposedStations)) {
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            markupInline.setKeyboard(keyboard);

            List<InlineKeyboardButton> buttons = new ArrayList<>();
            buttons.add(new InlineKeyboardButton().setText("Enter again").setCallbackData(ADD_TASK_CALLBACK));
            buttons.add(new InlineKeyboardButton().setText("Cancel").setCallbackData(TO_BEGGINNING_CALBACK));
            keyboard.add(buttons);
            SendMessage sendMessage = new SendMessage()
                    .enableMarkdown(true)
                    .setChatId(chatId)
                    .setText("Sorry, we can't find any station.")
                    .setReplyMarkup(markupInline);
            try {
                bot.execute(sendMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            SendMessage sendMessage = new SendMessage()
                    .enableMarkdown(true)
                    .setChatId(chatId)
                    .setText("Please, choose one of proposed.")
                    .setReplyMarkup(markupInline);
            try {
                bot.execute(sendMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void publishSelectedStation(Long chatId, Station station) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        markupInline.setKeyboard(keyboard);

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton().setText("Enter again").setCallbackData(ADD_TASK_CALLBACK));
        buttons.add(new InlineKeyboardButton().setText("Next").setCallbackData(ENTER_ARRIVAL));
        keyboard.add(buttons);
        SendMessage sendMessage = new SendMessage()
                .enableMarkdown(true)
                .setChatId(chatId)
                .setText("Chosen station of departure: " + station.getTitle())
                .setReplyMarkup(markupInline);
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publishMessage(Update update) {
    }

    @Autowired
    public void setStationCodeResolver(StationCodeResolver stationCodeResolver) {
        this.stationCodeResolver = stationCodeResolver;
    }

    @Autowired
    public void setArrivalStationState(ArrivalStationState arrivalStationState) {
        this.arrivalStationState = arrivalStationState;
    }
}
