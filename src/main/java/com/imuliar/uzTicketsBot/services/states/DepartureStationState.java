package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.UzTicketsBot;
import com.imuliar.uzTicketsBot.services.UserState;
import org.telegram.telegrambots.api.objects.Update;

/**
 * @author imuliar
 * 22.07.2018
 */

public class DepartureStationState extends AbstractState implements UserState {

    public DepartureStationState(UzTicketsBot bot, UserContext context) {
        super(bot, context);
    }

    @Override
    public void processUpdate(Update update) {

    }

    @Override
    public void publishMessage(Update update) {

    }

    @Override
    public void publishValidationMessage() {

    }
}
