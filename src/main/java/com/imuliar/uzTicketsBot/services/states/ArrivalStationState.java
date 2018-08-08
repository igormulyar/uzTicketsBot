package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.services.StationCodeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

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
            if (callbackString.matches(STATION_CALLBACK_REGEXP)) {
                String selectedId = callbackString.split(":")[1];
                if (CollectionUtils.isEmpty(proposedStations)) {
                    sendBotResponse(new SendMessage()
                            .enableMarkdown(true)
                            .setChatId(chatId)
                            .setText("Please enter the station of arrival."));
                } else {
                    proposedStations.stream()
                            .filter(proposed -> proposed.getValue().equals(selectedId))
                            .findAny()
                            .ifPresent(station -> {
                                context.getTicketRequest().setTo(station);
                                pickDateState.setContext(context);
                                context.setState(pickDateState);
                                context.processUpdate(update);
                            });
                }
            }
            if (callbackString.equals(TO_BEGGINNING_CALBACK)) {
                context.setInitialState();
                context.processUpdate(update);
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
    @Qualifier("pickDateState")
    public void setPickDateState(AbstractState pickDateState) {
        this.pickDateState = pickDateState;
    }
}
