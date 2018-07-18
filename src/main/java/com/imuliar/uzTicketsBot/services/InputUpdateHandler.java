package com.imuliar.uzTicketsBot.services;

import org.telegram.telegrambots.api.objects.Update;

/**
 * <p>Service for processing all input updates, received by the bot.</p>
 *
 * @author imuliar
 * @since 1.0
 */
public interface InputUpdateHandler {

    /**
     * <p>Update processing main method.</p>
     *
     * @param update input update, received by the bot
     */
    void handle(Update update);
}
