package com.imuliar.uzTicketsBot.services.impl;

import com.imuliar.uzTicketsBot.dao.TelegramUserDao;
import com.imuliar.uzTicketsBot.model.TelegramUser;
import com.imuliar.uzTicketsBot.services.InputUpdateHandler;
import com.imuliar.uzTicketsBot.services.states.UserContext;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.objects.Update;

/**
 * <p>Implementation of {@link InputUpdateHandler}</p>
 *
 * @author imuliar
 * @since 1.0
 */
@Service
public abstract class TelegramUpdateHandlerImpl implements InputUpdateHandler {

    private static final Duration sessionAliveDuration = Duration.ofSeconds(45);

    private TelegramUserDao userDao;

    private Map<Long, UserSession> userSessionPool = new ConcurrentHashMap<>();

    @Override
    public void handle(Update update) {
        Long chatId = update.getMessage() != null
                ? update.getMessage().getChatId()
                : update.getCallbackQuery().getMessage().getChatId();

        clearExpiredSessions();

        UserSession session = userSessionPool.get(chatId);
        if (session != null) {
            UserContext userContext = session.getContext();
            userContext.processUpdate(update);
            session.setCreationTime(LocalDateTime.now());
        } else {
            UserSession newUserSession = initSession();
            userSessionPool.put(chatId, newUserSession);
            Locale sessionLocale = null;
            TelegramUser user = userDao.findByChatId(resolveChatId(update));
            if(user == null){
                userDao.save(new TelegramUser(chatId));
                sessionLocale = Locale.forLanguageTag("uk-UA");
            } else{
                sessionLocale = Locale.forLanguageTag(user.getLanguage());
            }

            newUserSession.getContext().setLocale(sessionLocale);
            newUserSession.getContext().processUpdate(update);
        }
    }

    private void clearExpiredSessions() {
        Predicate<Map.Entry<Long, UserSession>> notExpired = entry -> Duration.between(entry.getValue().getCreationTime(),
                LocalDateTime.now()).compareTo(sessionAliveDuration) < 0;
        userSessionPool = userSessionPool.entrySet().stream()
                .filter(notExpired)
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v1,
                        ConcurrentHashMap::new));
    }

    private Long resolveChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        }
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        throw new IllegalStateException("Can't resolve chatId!!!");
    }

    @Lookup
    abstract UserSession initSession();

    @Autowired
    public void setUserDao(TelegramUserDao userDao) {
        this.userDao = userDao;
    }
}
