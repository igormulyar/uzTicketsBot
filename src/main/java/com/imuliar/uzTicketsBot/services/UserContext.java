package com.imuliar.uzTicketsBot.services;

import org.telegram.telegrambots.api.objects.Update;

/**
 * <p>The user context that stores the current user state.</p>
 *
 * @author imuliar
 * @since 1.0
 */
public class UserContext {

    UserState state;

    public void processUpdate(Update update){
        state.consumeInput(update);
    }

    public void publishMessage(){
        state.publishMessage();
    }

    public UserContext() {
    }

    public UserContext(UserState state) {
        this.state = state;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }
}
