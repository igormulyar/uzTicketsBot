package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.UzTicketsBot;
import com.imuliar.uzTicketsBot.services.UserState;
import org.telegram.telegrambots.api.objects.Update;

/**
 * <p>The user context stores the current user state.</p>
 *
 * @author imuliar
 * @since 1.0
 */
public class UserContext {

    UserState state;

    private UzTicketsBot bot;

    public UserContext(UzTicketsBot bot) {
        this.bot = bot;
        this.state = new InitialUserState(bot, this);
    }

    public void processUpdate(Update update){
        state.processUpdate(update);
    }

    public void publishMessage(Update update){
        state.publishMessage(update);
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }
}
