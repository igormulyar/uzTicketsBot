package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.model.TicketRequest;
import com.imuliar.uzTicketsBot.services.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Update;

/**
 * <p>The user context stores the current user state.</p>
 *
 * @author imuliar
 * @since 1.0
 */
@Component
@Scope("prototype")
public class UserContext {

    private AbstractState state;

    private TicketRequest ticketRequest = new TicketRequest();

    public void processUpdate(Update update){
        state.processUpdate(update);
    }

    public void publishMessage(Update update){
        state.publishMessage(update);
    }

    public UserState getState() {
        return state;
    }

    @Autowired
    @Qualifier("initialUserState")
    public void setState(AbstractState state) {
        this.state = state;
        state.setContext(this);
    }

    public TicketRequest getTicketRequest() {
        return ticketRequest;
    }

    public void setTicketRequest(TicketRequest ticketRequest) {
        this.ticketRequest = ticketRequest;
    }
}
