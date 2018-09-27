package com.imuliar.uzTicketsBot.services.impl;

import com.imuliar.uzTicketsBot.UzTicketsBot;
import com.imuliar.uzTicketsBot.model.TicketRequest;
import com.imuliar.uzTicketsBot.services.OutputMessageService;

import java.util.*;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import static java.lang.Math.toIntExact;

/**
 * @author imuliar
 * 29.08.2018
 */
@Service
public class OutputMessageServiceImpl implements OutputMessageService {

    @Autowired
    private MessageSource messages;

    public static final String EMPTY_CALLBACK = "do_nothing";
    public static final String TO_BEGGINNING_CALBACK = "to_beginning";
    public static final String ADD_TASK_CALLBACK = "add_task";
    public static final String VIEW_TASKS_CALLBACK = "view_tasks";
    public static final String STATION_CALLBACK_PATTERN = "stationId:%s";
    public static final Pattern STATION_CALLBACK_REGEXP_PATTERN = Pattern.compile("^stationId\\:\\d{7}$");
    public static final String CONFIRM_TICKETS_REQUEST = "confirm_tickets_req";
    public static final Pattern VIEW_TASK_CALLBACK_REGEXP_PATTERN = Pattern.compile("^viewTask\\:\\d+$");
    public static final Pattern DELETE_TASK_CALLBACK_REGEXP_PATTERN = Pattern.compile("^deleteTask\\:\\d+$");
    public static final Pattern SEARCH_NOW_CALLBACK_REGEXP_PATTERN = Pattern.compile("^searchNow\\:\\d+$");
    private UzTicketsBot bot;

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyStopTrackingExpiredRequests(List<TicketRequest> ticketRequests) {
        ticketRequests.forEach(tr ->
                printSimpleMessage(tr.getTelegramUser().getChatId(), messages.getMessage("message.expiredTaskStopped",
                        new String[]{tr.getDepartureStation().getTitle(), tr.getArrivalStation().getTitle(), tr.getDepartureDate().toString()}, Locale.forLanguageTag(tr.getTelegramUser().getLanguage())))
        );
    }

    @Override
    public void notifyTicketsSearchSuccess(TicketRequest ticketRequest, String url) {
        Locale locale = Locale.forLanguageTag(ticketRequest.getTelegramUser().getLanguage());
        String message = messages.getMessage("message.ticketsFound", new String[]{url}, locale)
                + messages.getMessage("message.trackingStopped", new Object[]{}, locale);
        printSimpleMessage(ticketRequest.getTelegramUser().getChatId(), message);
    }

    @Override
    public void printSimpleMessage(Long chatId, String message) {
        bot.sendBotResponse(new SendMessage()
                .enableMarkdown(true)
                .setChatId(chatId)
                .setText(message));
    }

    @Override
        public void printMessageWithKeyboard(Long chatId, String message, InlineKeyboardMarkup keyboardMarkup) {
        bot.sendBotResponse(new SendMessage()
                .enableMarkdown(true)
                .setChatId(chatId)
                .setText(message)
                .setReplyMarkup(keyboardMarkup));
    }

    @Override
    public void printListOfUserTasks(Long chatId, List<TicketRequest> activeTicketRequests, Locale locale) {
        String messageText = messages.getMessage("message.yourTaskList", new Object[]{}, locale);
        String labelPattern = "[%s - %s : %s]";
        String callbackPattern = "viewTask:%d";

        List<List<InlineKeyboardButton>> keyboardLines = new ArrayList<>(activeTicketRequests.size());
        for (TicketRequest request : activeTicketRequests) {
            String label = String.format(labelPattern, request.getDepartureStation().getTitle(),
                    request.getArrivalStation().getTitle(),
                    request.getDepartureDate().toString());
            String callbackData = String.format(callbackPattern, request.getId());
            keyboardLines.add(createKeyBoardLine(createButton(label, callbackData)));
        }
        printMessageWithKeyboard(chatId, messageText, createKeyboardMarkup(keyboardLines));
    }

    @Override
    public void printTaskOverview(Long chatId, TicketRequest ticketRequest) {
        Locale locale = Locale.forLanguageTag(ticketRequest.getTelegramUser().getLanguage());
        String messageText = String.format("[%s - %s : %s]", ticketRequest.getDepartureStation().getTitle(),
                ticketRequest.getArrivalStation().getTitle(),
                ticketRequest.getDepartureDate().toString());
        String searchNowCallback = String.format("searchNow:%d", ticketRequest.getId());
        String deleteTaskCallback = String.format("deleteTask:%d", ticketRequest.getId());
        InlineKeyboardMarkup keyboardMarkup = createKeyboardMarkup(Collections.singletonList(createKeyBoardLine(
                createButton(messages.getMessage("button.searchRightNow", new Object[]{}, locale), searchNowCallback),
                createButton(messages.getMessage("button.delete", new Object[]{}, locale), deleteTaskCallback),
                createButton(messages.getMessage("button.cancel", new Object[]{}, locale), TO_BEGGINNING_CALBACK))));
        printMessageWithKeyboard(chatId, messageText, keyboardMarkup);
    }

    @Override
    public void popUpNotify(String callbackQueryId, String messageText) {
        bot.sendBotResponse(new AnswerCallbackQuery()
                .setCallbackQueryId(callbackQueryId)
                .setShowAlert(true)
                .setText(messageText));
    }

    @Override
    public void updateMessageWithMarkup(Long chatId, int messageId, String text, InlineKeyboardMarkup markup) {
        bot.sendBotResponse(new EditMessageText()
                .setChatId(chatId)
                .setMessageId(toIntExact(messageId))
                .setText(text)
                .setReplyMarkup(markup));
    }

    private InlineKeyboardMarkup createKeyboardMarkup(List<List<InlineKeyboardButton>> keyboardLines) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        inlineKeyboard.setKeyboard(keyboardLines);
        return inlineKeyboard;
    }

    private List<InlineKeyboardButton> createKeyBoardLine(InlineKeyboardButton... buttons) {
        List<InlineKeyboardButton> line = new ArrayList<>();
        line.addAll(Arrays.asList(buttons));
        return line;
    }

    private InlineKeyboardButton createButton(String label, String callbackData) {
        return new InlineKeyboardButton().setText(label).setCallbackData(callbackData);
    }

    @Autowired
    public void setBot(UzTicketsBot bot) {
        this.bot = bot;
    }
}
