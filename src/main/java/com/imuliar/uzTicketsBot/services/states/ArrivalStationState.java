package com.imuliar.uzTicketsBot.services.states;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

/**
 * <p>The state when user is asked for the station of destination.</p>
 *
 * @author imuliar
 * @since 1.0
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ArrivalStationState extends AbstractState {

    @Override
    public void processUpdate(Update update) {
        Long chatId = resolveChatId(update);
        if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().equals(ENTER_ARRIVAL)) {
                SendMessage sendMessage = new SendMessage()
                        .enableMarkdown(true)
                        .setChatId(chatId)
                        .setText("Please enter the station of arrival.");
                try {
                    bot.execute(sendMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void publishMessage(Update update) {

    }
}
