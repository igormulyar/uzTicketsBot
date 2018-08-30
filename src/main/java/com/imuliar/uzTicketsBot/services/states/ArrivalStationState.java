package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.model.Station;
import com.imuliar.uzTicketsBot.services.StationCodeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.api.objects.Update;

import static com.imuliar.uzTicketsBot.services.impl.OutputMessageServiceImpl.*;

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
            if (callbackString.equals(ADD_TASK_CALLBACK)) {
                outputMessageService.printSimpleMessage(chatId, "Please enter the station of arrival.");
            }
            if (STATION_CALLBACK_REGEXP_PATTERN.matcher(callbackString).matches()) {
                String selectedId = callbackString.split(":")[1];
                if (CollectionUtils.isEmpty(proposedStations)) {
                    outputMessageService.printSimpleMessage(chatId, "Please enter the station of arrival.");
                } else {
                    Station arrivalStation = proposedStations.stream()
                            .filter(proposed -> proposed.getValue().equals(selectedId))
                            .findAny()
                            .orElseThrow(() -> new IllegalStateException("Wrong callback data. Can't find station."));

                    if(context.getTicketRequest().getDepartureStation().getValue().equals(arrivalStation.getValue())){
                        outputMessageService.popUpNotify(update.getCallbackQuery().getId(),
                                "Arrival station can not be the same as departure station!");
                    } else {
                        context.getTicketRequest().setArrivalStation(arrivalStation);
                        pickDateState.setContext(context);
                        context.setState(pickDateState);
                        context.processUpdate(update);
                    }
                }
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
    @Qualifier("pickDateState")
    public void setPickDateState(AbstractState pickDateState) {
        this.pickDateState = pickDateState;
    }
}
