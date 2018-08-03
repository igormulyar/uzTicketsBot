package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.services.StationCodeResolver;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * <p>The state when user is asked for the station of destination.</p>
 *
 * @author imuliar
 * @since 1.0
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ArrivalStationState extends AbstractState {

    private StationCodeResolver stationCodeResolver;

    private List<Station> proposedStations;

    private AbstractState pickDateState;

    @Override
    public void processUpdate(Update update) {
        Long chatId = resolveChatId(update);
        if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().equals(ENTER_ARRIVAL)) {
                sendBotResponse(new SendMessage()
                        .enableMarkdown(true)
                        .setChatId(chatId)
                        .setText("Please enter the station of arrival."));
            }



        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String userInput = update.getMessage().getText();
            proposedStations = stationCodeResolver.resolveProposedStations(userInput);
            publishStationSearchResults(chatId);
        }

    }

    @Override
    public void publishMessage(Update update) {

    }

    private void publishStationSearchResults(Long chatId) {
        if (CollectionUtils.isEmpty(proposedStations)) {
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            markupInline.setKeyboard(keyboard);

            List<InlineKeyboardButton> buttons = new ArrayList<>();
            buttons.add(new InlineKeyboardButton().setText("Enter again").setCallbackData(ENTER_ARRIVAL));
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
            buttons.add(new InlineKeyboardButton().setText("Enter again").setCallbackData(ENTER_ARRIVAL));
            buttons.add(new InlineKeyboardButton().setText("Cancel").setCallbackData(TO_BEGGINNING_CALBACK));
            keyboard.add(buttons);
            sendBotResponse(new SendMessage()
                    .enableMarkdown(true)
                    .setChatId(chatId)
                    .setText("Please, choose one of proposed.")
                    .setReplyMarkup(markupInline));
        }
    }

    @Autowired
    public void setStationCodeResolver(StationCodeResolver stationCodeResolver) {
        this.stationCodeResolver = stationCodeResolver;
    }

    @Autowired
    @Qualifier("pickDateState")
    public void setPickDateState(AbstractState pickDateState) {
        this.pickDateState = pickDateState;
    }


}
