package com.imuliar.uzTicketsBot.services.impl;

import com.imuliar.uzTicketsBot.services.states.UserContext;
import java.time.LocalDateTime;

/**
 * @author imuliar
 * 22.07.2018
 */

public class UserSession {

    private UserContext context;

    private LocalDateTime creationTime;

    public UserSession(UserContext context, LocalDateTime creationTime) {
        this.context = context;
        this.creationTime = creationTime;
    }

    public UserContext getContext() {
        return context;
    }

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
