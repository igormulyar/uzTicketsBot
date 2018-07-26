package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.UzTicketsBot;
import com.imuliar.uzTicketsBot.services.UserState;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * <p>Initial (DEFAULT) user state.</p>
 *
 * @author imuliar
 * @since 1.0
 */
@Component
@Scope("prototype")
public class InitialUserState extends AbstractState {

    private static final String ADD_TASK_CALLBACK = "add_task";

    private static final String VIEW_TASKS_CALLBACK = "view_tasks";

    private DepartureStationState departureStationState;

    private ViewTasksState viewTasksState;

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
            String msg = String.format("WRONG APPLICATION STATE. WRONG QUERY %s FOR STATE: %s",
                    update.getCallbackQuery(), this.getClass().getName());
            throw new IllegalStateException(msg);
        }
    }

    @Override
    public void publishMessage(Update update) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        markupInline.setKeyboard(keyboard);

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton().setText("Add task").setCallbackData(ADD_TASK_CALLBACK));
        buttons.add(new InlineKeyboardButton().setText("View my tasks").setCallbackData(VIEW_TASKS_CALLBACK));
        buttons.add(new InlineKeyboardButton().setText("Cancel").setCallbackData(TO_BEGGINNING_CALBACK));
        keyboard.add(buttons);

        Long chatId = resolveChatId(update);
        SendMessage sendMessage = new SendMessage()
                .enableMarkdown(true)
                .setChatId(chatId)
                .setText("HELLO! \n Please, chose the action.")
                .setReplyMarkup(markupInline);
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public void setDepartureStationState(DepartureStationState departureStationState) {
        this.departureStationState = departureStationState;
    }

    @Autowired
    public void setViewTasksState(ViewTasksState viewTasksState) {
        this.viewTasksState = viewTasksState;
    }
}
