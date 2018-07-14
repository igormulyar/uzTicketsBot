package com.imuliar.uzTicketsBot.model;

import javax.persistence.Entity;

/**
 * <p>The Entity for representing the telegram user in DB.</p>
 *
 * @author imuliar
 * @since 1.0
 */
@Entity
public class TelegramUser extends EntityFrame {

    private String chatId;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
