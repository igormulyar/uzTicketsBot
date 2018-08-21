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

    private Long chatId;

    public TelegramUser() {
    }

    public TelegramUser(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
