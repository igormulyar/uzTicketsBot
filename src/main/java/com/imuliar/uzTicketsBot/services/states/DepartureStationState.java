package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.services.StationCodeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

/**
 * <p>The state when user choosing the station of departure.</p>
 *
 * @author imuliar
 * @since 1.0
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DepartureStationState extends AbstractState {

    private StationCodeResolver stationCodeResolver;

    private AbstractState arrivalStationState;

    @Override
    public void processUpdate(Update update) {
        Long chatId = resolveChatId(update);
        if (update.hasCallbackQuery()) {
            String callbackString = update.getCallbackQuery().getData();
            if (callbackString.equals(ADD_TASK_CALLBACK)) {
                bot.sendBotResponse(new SendMessage()
                        .enableMarkdown(true)
                        .setChatId(chatId)
                        .setText("Please enter the station of departure."));
            }
            if (STATION_CALLBACK_REGEXP_PATTERN.matcher(callbackString).matches()) {
                String selectedId = callbackString.split(":")[1];
                proposedStations.stream()
                        .filter(proposed -> proposed.getValue().equals(selectedId))
                        .findAny()
                        .ifPresent(station -> {
                            context.getTicketRequest().setDepartureStation(station);
                            arrivalStationState.setContext(context);
                            context.setState(arrivalStationState);
                            context.processUpdate(update);
                        });
            }
            if (callbackString.equals(TO_BEGGINNING_CALBACK)) {
                goToBeginning(update);
            }
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String userInput = update.getMessage().getText();
            proposedStations = stationCodeResolver.resolveProposedStations(userInput);
            publishStationSearchResults(chatId);
        }
    }

    @Autowired
    public void setStationCodeResolver(StationCodeResolver stationCodeResolver) {
        this.stationCodeResolver = stationCodeResolver;
    }

    @Autowired
    @Qualifier("arrivalStationState")
    public void setArrivalStationState(AbstractState arrivalStationState) {
        this.arrivalStationState = arrivalStationState;
    }
}
