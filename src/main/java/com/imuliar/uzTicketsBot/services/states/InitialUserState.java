package com.imuliar.uzTicketsBot.services.states;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import static com.imuliar.uzTicketsBot.services.impl.OutputMessageServiceImpl.*;

/**
 * <p>Initial (DEFAULT) user state.</p>
 *
 * @author imuliar
 * @since 1.0
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InitialUserState extends AbstractState {

    private DepartureStationState departureStationState;

    private AbstractState viewTasksState;

    @Override
    public void processUpdate(Update update) {
        if ((update.hasMessage() && update.getMessage().getText().equals("/start"))
                || (update.hasCallbackQuery() && update.getCallbackQuery().getData().equals(TO_BEGGINNING_CALBACK))) {
            publishMessage(update);
        } else if (update.getCallbackQuery().getData().equals(ADD_TASK_CALLBACK)) {
            departureStationState.setContext(context);
            context.setState(departureStationState);
            context.processUpdate(update);
        } else if (update.getCallbackQuery().getData().equals(VIEW_TASKS_CALLBACK)) {
            viewTasksState.setContext(context);
            context.setState(viewTasksState);
            context.processUpdate(update);
        } else {
            publishMessage(update);
        }
    }

    private void publishMessage(Update update) {
        context.setInitialState();
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        markupInline.setKeyboard(keyboard);

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton().setText("Search").setCallbackData(ADD_TASK_CALLBACK));
        buttons.add(new InlineKeyboardButton().setText("View my search tasks").setCallbackData(VIEW_TASKS_CALLBACK));
        buttons.add(new InlineKeyboardButton().setText("Cancel").setCallbackData(TO_BEGGINNING_CALBACK));
        keyboard.add(buttons);

        Long chatId = resolveChatId(update);
        SendMessage sendMessage = new SendMessage()
                .enableMarkdown(true)
                .setChatId(chatId)
                .setText(context.getMessageSource().getMessage("message.chooseAction", null, context.getLocale()))
                .setReplyMarkup(markupInline);

        bot.sendBotResponse(sendMessage);
    }

    @Autowired
    public void setDepartureStationState(DepartureStationState departureStationState) {
        this.departureStationState = departureStationState;
    }

    @Autowired
    @Qualifier("viewTasksState")
    public void setViewTasksState(AbstractState viewTasksState) {
        this.viewTasksState = viewTasksState;
    }
}
