package com.imuliar.uzTicketsBot.services.states;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
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

    private static final String LANG_UA = "LANG_UA";

    private static final String LANG_RU = "LANG_RU";

    private static final String LANG_EN = "LANG_EN";

    private static final String CHANGE_LANG_CALLBACK = "LANG";

    private DepartureStationState departureStationState;

    private AbstractState viewTasksState;

    @Override
    public void processUpdate(Update update) {
        Long chatId = resolveChatId(update);
        if(update.hasCallbackQuery()){
            switch(update.getCallbackQuery().getData()){
                case TO_BEGGINNING_CALBACK:
                    printInitialMessage(chatId);
                    break;
                case ADD_TASK_CALLBACK:
                    departureStationState.setContext(context);
                    context.setState(departureStationState);
                    context.processUpdate(update);
                    break;
                case VIEW_TASKS_CALLBACK:
                    viewTasksState.setContext(context);
                    context.setState(viewTasksState);
                    context.processUpdate(update);
                    break;
                case CHANGE_LANG_CALLBACK:
                    publishLangList(update);
                    break;
                case LANG_UA:
                    userDao.updateLanguage(chatId, "uk-UA");
                    context.setLocale(Locale.forLanguageTag("uk-UA"));
                    printInitialMessage(chatId);
                    break;
                case LANG_RU:
                    userDao.updateLanguage(chatId, "ru-RU");
                    context.setLocale(Locale.forLanguageTag("ru-RU"));
                    printInitialMessage(chatId);
                    break;
                case LANG_EN:
                    userDao.updateLanguage(chatId, "en-US");
                    context.setLocale(Locale.forLanguageTag("en-US"));
                    printInitialMessage(chatId);
                    break;
                default:
                    printInitialMessage(chatId);
            }
        } else {
            printInitialMessage(chatId);
        }
    }

    private void printInitialMessage(Long chatId) {
        context.setInitialState();
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        markupInline.setKeyboard(keyboard);

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton().setText(context.getLocalizedMessage("button.searchTickets")).setCallbackData(ADD_TASK_CALLBACK));
        buttons.add(new InlineKeyboardButton().setText(context.getLocalizedMessage("lang.lang")).setCallbackData(CHANGE_LANG_CALLBACK));
        keyboard.add(buttons);
        keyboard.add(Collections.singletonList(new InlineKeyboardButton().setText(context.getLocalizedMessage("button.currentSearchTasks")).setCallbackData(VIEW_TASKS_CALLBACK)));
        outputMessageService.printMessageWithKeyboard(chatId, context.getLocalizedMessage("message.chooseAction"), markupInline);
    }

    private void publishLangList(Update update) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        markupInline.setKeyboard(keyboard);

        keyboard.add(Collections.singletonList(new InlineKeyboardButton().setText(context.getLocalizedMessage("lang.ua")).setCallbackData(LANG_UA)));
        keyboard.add(Collections.singletonList(new InlineKeyboardButton().setText(context.getLocalizedMessage("lang.ru")).setCallbackData(LANG_RU)));
        keyboard.add(Collections.singletonList(new InlineKeyboardButton().setText(context.getLocalizedMessage("lang.en")).setCallbackData(LANG_EN)));
        outputMessageService.printMessageWithKeyboard(resolveChatId(update), context.getLocalizedMessage("message.selectLang"), markupInline);
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
