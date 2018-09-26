package com.imuliar.uzTicketsBot.dao;

import com.imuliar.uzTicketsBot.model.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author imuliar
 * 31.08.2018
 */
@Repository
@Transactional
public interface TelegramUserDao extends JpaRepository<TelegramUser, Long> {

    /**
     * <p>Find user by chatId</p>
     *
     * @param chatId chat id
     * @return user or {@code NULL}
     */
    TelegramUser findByChatId(Long chatId);

    /**
     * <p>Set new language code value for user with specified chatId</p>
     *
     * @param chatId       chat id
     * @param languageCode new language code value
     */
    @Modifying
    @Query("UPDATE TelegramUser usr SET usr.language = :languageCode WHERE usr.chatId = :chatId")
    void updateLanguage(@Param("chatId") Long chatId, @Param("languageCode") String languageCode);
}
