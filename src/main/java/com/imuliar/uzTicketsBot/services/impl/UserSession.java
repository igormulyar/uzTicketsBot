package com.imuliar.uzTicketsBot.services.impl;

import com.imuliar.uzTicketsBot.services.states.UserContext;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * <p>Represents the user's session.</p>
 *
 * @author imuliar
 * @since 1.0
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserSession {

    private UserContext context;

    private LocalDateTime creationTime = LocalDateTime.now();

    public UserContext getContext() {
        return context;
    }

    @Autowired
    public void setContext(UserContext context) {
        this.context = context;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }


}
