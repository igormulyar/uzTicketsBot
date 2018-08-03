package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.model.TicketRequest;
import com.imuliar.uzTicketsBot.services.UserState;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
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
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class UserContext {

    private AbstractState state;

    private TicketRequest ticketRequest;

    @PostConstruct
    public void setInitialState() {
        ticketRequest = new TicketRequest();
        state = initState();
        state.setContext(this);
    }

    @Lookup("initialUserState")
    abstract AbstractState initState();

    public void processUpdate(Update update) {
        state.processUpdate(update);
    }

    public void publishMessage(Update update) {
        state.publishMessage(update);
    }

    public UserState getState() {
        return state;
    }

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
