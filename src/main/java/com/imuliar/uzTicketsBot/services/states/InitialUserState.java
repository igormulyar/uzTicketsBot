package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.services.UserContext;
import com.imuliar.uzTicketsBot.services.UserState;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

/**
 * <p>Initial (DEFAULT) user state.</p>
 *
 * @author imuliar
 * @since 1.0
 */
public class InitialUserState implements UserState {

    private UserContext context;

    public InitialUserState(UserContext context) {
        this.context = context;
    }

    @Override
    public void consumeInput(Update update) {
        publishMessage();
        if(update.hasMessage() && verify(update.getMessage())){

        }
    }

    @Override
    public void publishMessage() {


    }

    @Override
    public void publishValidationMessage() {

    }

    private boolean verify(Message message){
        if(message != null){
            return true;
        } else {
            publishValidationMessage();
        }
        return false;
    }
}
