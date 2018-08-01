package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.UzTicketsBot;
import com.imuliar.uzTicketsBot.services.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.api.objects.Update;

/**
 * @author imuliar
 * 22.07.2018
 */

public abstract class AbstractState implements UserState {

    protected static final String EMPTY_CALLBACK = "do_nothing";

    protected static final String TO_BEGGINNING_CALBACK = "to_beginning";

    protected static final String STATION_CALLBACK_PATTERN = "stationId:%s";

    protected static final String STATION_CALLBACK_REGEXP = "^stationId\\:\\d{7}$";

    protected static final String ENTER_ARRIVAL = "enter_arrival";

    protected UzTicketsBot bot;

    protected UserContext context;

    protected Long resolveChatId(Update update){
        if(update.hasMessage()){
            return update.getMessage().getChatId();
        }
        if(update.hasCallbackQuery()){
            return update.getCallbackQuery().getMessage().getChatId();
        }
        throw new IllegalStateException("Can't resolve chatId!!!");
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
