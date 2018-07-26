package com.imuliar.uzTicketsBot.services.impl;

import com.imuliar.uzTicketsBot.services.InputUpdateHandler;
import com.imuliar.uzTicketsBot.services.states.UserContext;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
public abstract class InputUpdateHandlerImpl implements InputUpdateHandler {

    private static final Duration sessionLifeDuration = Duration.ofMinutes(1);

    private Map<Long, UserSession> userSessionPool = new HashMap<>();

    /**
     * prototype scoped bean
     */
    private UserSession userSession;

    @Override
    public synchronized void handle(Update update) {
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
        userSessionPool = userSessionPool.entrySet().stream()
                .filter(entry -> Duration.between(entry.getValue().getCreationTime(), LocalDateTime.now()).compareTo(sessionLifeDuration) < 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Autowired
    public void setSession(UserSession userSession) {
        this.userSession = userSession;
    }

    @Lookup
    abstract UserSession initSession();
}
