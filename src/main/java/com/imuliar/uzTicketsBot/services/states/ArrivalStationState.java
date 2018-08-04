package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.services.StationCodeResolver;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
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

    private AbstractState pickDateState;

    @Override
    public void processUpdate(Update update) {
        Long chatId = resolveChatId(update);
        if (update.hasCallbackQuery()) {
            String callbackString = update.getCallbackQuery().getData();
            if (callbackString.equals(ENTER_ARRIVAL)) {
                sendBotResponse(new SendMessage()
                        .enableMarkdown(true)
                        .setChatId(chatId)
                        .setText("Please enter the station of arrival."));
            }
            if (callbackString.matches(STATION_CALLBACK_REGEXP)) {
                String selectedId = callbackString.split(":")[1];
                proposedStations.stream()
                        .filter(proposed -> proposed.getValue().equals(selectedId))
                        .findAny()
                        .ifPresent(station -> {
                            context.getTicketRequest().setTo(station);
                            publishSelectedStation(chatId, station);
                        });
            }
            if (callbackString.equals(TO_BEGGINNING_CALBACK)) {
                context.setInitialState();
                context.processUpdate(update);
            }
            if (callbackString.equals(ENTER_DATE)) {
                pickDateState.setContext(context);
                context.setState(pickDateState);
                context.processUpdate(update);
            }
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String userInput = update.getMessage().getText();
            proposedStations = stationCodeResolver.resolveProposedStations(userInput);
            publishStationSearchResults(chatId);
        }
    }

    private void publishSelectedStation(Long chatId, Station station) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        markupInline.setKeyboard(keyboard);

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton().setText("Enter again").setCallbackData(ADD_TASK_CALLBACK));
        buttons.add(new InlineKeyboardButton().setText("Next").setCallbackData(ENTER_DATE));
        keyboard.add(buttons);
        sendBotResponse(new SendMessage()
                .enableMarkdown(true)
                .setChatId(chatId)
                .setText("Chosen station: " + station.getTitle())
                .setReplyMarkup(markupInline));
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
