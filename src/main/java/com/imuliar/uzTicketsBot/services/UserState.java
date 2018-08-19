package com.imuliar.uzTicketsBot.services;

import org.telegram.telegrambots.api.objects.Update;

/**
 * <p>User State interface</p>
 *
 * @author imuliar
 * @since 1.0
 */
public interface UserState {

    /**
     * <p>Performs the action, which is specific for particular user state implementation.</p>
     * @param update input update
     */
    void processUpdate(Update update);
}
