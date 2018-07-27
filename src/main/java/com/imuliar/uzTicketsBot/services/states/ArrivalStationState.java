package com.imuliar.uzTicketsBot.services.states;

import org.telegram.telegrambots.api.objects.Update;

/**
 * <p>The state when user is asked for the station of destination.</p>
 *
 * @author imuliar
 * @since 1.0
 */
public class ArrivalStationState extends AbstractState {

    @Override
    public void processUpdate(Update update) {
        if(update.hasCallbackQuery()){
            if (update.getCallbackQuery().getData().equals("")) {

            }
        }

    }

    @Override
    public void publishMessage(Update update) {

    }
}
