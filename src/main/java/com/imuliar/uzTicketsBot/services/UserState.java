package com.imuliar.uzTicketsBot.services;

import org.telegram.telegrambots.api.objects.Update;

/**
 * <p>User State interface</p>
 *
 * @author imuliar
 * @since 1.0
 */
public interface UserState {

    void processUpdate(Update update);
}
