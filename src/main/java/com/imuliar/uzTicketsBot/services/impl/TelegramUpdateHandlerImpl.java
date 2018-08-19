package com.imuliar.uzTicketsBot.services.impl;

import com.imuliar.uzTicketsBot.services.InputUpdateHandler;
import com.imuliar.uzTicketsBot.services.states.UserContext;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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

    @Lookup
    abstract UserSession initSession();
}
