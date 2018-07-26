package com.imuliar.uzTicketsBot.services.states;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Update;

/**
 * @author imuliar
 * 22.07.2018
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ViewTasksState extends AbstractState {

    @Override
    public void processUpdate(Update update) {

    }

    @Override
    public void publishMessage(Update update) {

    }
}
