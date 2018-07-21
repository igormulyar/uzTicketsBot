package com.imuliar.uzTicketsBot;

import com.imuliar.uzTicketsBot.services.InputUpdateHandler;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import static java.lang.Math.toIntExact;

/**
 * <p>The main bot class.</p>
 * <p>
 * <p><b>THE BOT INFO:</b></p>
 * <li><b>Bot's name </b>: uz_tickets_test_bot</li>
 * <li><b>Bot's username </b>: uz_tickets_test_bot</li>
 * <li><b>Use this token to access the HTTP API </b>: <i>559136986:AAGA8dMIaVrdts--oK7ebK3Nfm9HFgufppY</i></li>
 * <li><b>Link </b>: <a href='http://t.me/uz_tickets_test_bot'>Bot's address in telegram</a></li>
 *
 * @author imuliar
 * @since 1.0
 */
@Component
public class UzTicketsBot extends TelegramLongPollingBot {

    private static final String BOT_UZERNAME = "uz_tickets_test_bot";

    private static final String BOT_TOKEN = "559136986:AAGA8dMIaVrdts--oK7ebK3Nfm9HFgufppY";

    private static final Logger LOGGER = LoggerFactory.getLogger(UzTicketsBot.class);

    private InputUpdateHandler inputUpdateHandler;

    static {
        ApiContextInitializer.init();
    }

    @PostConstruct
    public void registerBot() {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {

        inputUpdateHandler.handle(update);

        //TODO manage processing the requests ans sending the responses
        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals("/start")) {
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();

            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();

            InlineKeyboardButton inlineButton1 = new InlineKeyboardButton().setText("Say 'Hello'").setCallbackData("get_hello");
            InlineKeyboardButton inlineButton2 = new InlineKeyboardButton().setText("Say 'Goodbye'").setCallbackData("get_goodbye");

            rowInline.add(inlineButton1);
            rowInline.add(inlineButton2);
            rowsInline.add(rowInline);
            markupInline.setKeyboard(rowsInline);

            SendMessage sendMessage = new SendMessage()
                    .enableMarkdown(true)
                    .setChatId(update.getMessage().getChatId().toString())
                    .setText("HELLO! \n Please chose the text to update in this message.")
                    .setReplyMarkup(markupInline);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                LOGGER.error("Exception: ", e.getMessage());
            }
        } else if (update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();
            if (call_data.equals("get_hello")) {
                String answer = "HELLO, MOTHERFUCKER!";
                EditMessageText new_message = new EditMessageText()
                        .setChatId(chat_id)
                        .setMessageId(toIntExact(message_id))
                        .setText(answer);
                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (call_data.equals("get_goodbye")) {
                String answer = "GOODBYE, MOTHERFUCKER!";
                EditMessageText new_message = new EditMessageText()
                        .setChatId(chat_id)
                        .setMessageId(toIntExact(message_id))
                        .setText(answer);
                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else
                throw new IllegalStateException("UNSUPPORTED CALLBACK DATA RECEIVED!");

        }
    }

    public InlineKeyboardMarkup buildCalendarPage() {
        //calculations
        LocalDate date = LocalDate.now();
        int daysInMonthAmount = date.getMonth().length(date.isLeapYear());
        LocalDate firstDayOfMonth = date.minusDays(date.getDayOfMonth() - 1L);
        int firstDayOffset = firstDayOfMonth.getDayOfWeek().getValue() - 1;
        int minimalDaysAmtInCalendar = daysInMonthAmount + firstDayOffset;
        int totalDayButtonsAmount = minimalDaysAmtInCalendar;
        while (totalDayButtonsAmount % 7 != 0) {
            totalDayButtonsAmount++;
        }

        //markup
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        markupInline.setKeyboard(keyboard);

        InlineKeyboardButton yearToLeft = new InlineKeyboardButton().setText(" <<< ").setCallbackData("minus_year");
        InlineKeyboardButton calendarYear = new InlineKeyboardButton().setText(String.valueOf(date.getYear()));
        InlineKeyboardButton yearToRight = new InlineKeyboardButton().setText(" >>> ").setCallbackData("plus_year");
        List<InlineKeyboardButton> yearRow = new ArrayList<>(Arrays.asList(yearToLeft, calendarYear, yearToRight));
        keyboard.add(yearRow);

        InlineKeyboardButton monthToLeft = new InlineKeyboardButton().setText(" <<<<< ").setCallbackData("minus_month");
        InlineKeyboardButton calendarMonth = new InlineKeyboardButton().setText(date.getMonth().toString());
        InlineKeyboardButton monthToRight = new InlineKeyboardButton().setText(" >>>>> ").setCallbackData("plus_month");
        List<InlineKeyboardButton> monthRow = new ArrayList<>(Arrays.asList(monthToLeft, calendarMonth, monthToRight));
        keyboard.add(monthRow);

        List<InlineKeyboardButton> daysOfWeekRow = new ArrayList<>();
        daysOfWeekRow.add(new InlineKeyboardButton().setText("mon"));
        daysOfWeekRow.add(new InlineKeyboardButton().setText("tue"));
        daysOfWeekRow.add(new InlineKeyboardButton().setText("wed"));
        daysOfWeekRow.add(new InlineKeyboardButton().setText("thu"));
        daysOfWeekRow.add(new InlineKeyboardButton().setText("fri"));
        daysOfWeekRow.add(new InlineKeyboardButton().setText("sat"));
        daysOfWeekRow.add(new InlineKeyboardButton().setText("sun"));
        keyboard.add(daysOfWeekRow);

        int dayOfWeekCounter = 1;
        List<InlineKeyboardButton> daysRow = new ArrayList<>(7);
        for (int i = 1; i < totalDayButtonsAmount + 1; i++) {
            if (i <= firstDayOffset || (i > daysInMonthAmount && dayOfWeekCounter != 1)) {
                daysRow.add(new InlineKeyboardButton().setText(" - "));
            } else {
                daysRow.add(new InlineKeyboardButton().setText(" " + i + " "). setCallbackData(String.valueOf(i)));
            }
            if (dayOfWeekCounter == 7) {
                keyboard.add(daysRow);
                daysRow = new ArrayList<>(7);
                dayOfWeekCounter = 1;
            } else {
                dayOfWeekCounter++;
            }
        }
        return markupInline;
    }

    @Override
    public String getBotUsername() {
        return BOT_UZERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Autowired
    public void setInputUpdateHandler(InputUpdateHandler inputUpdateHandler) {
        this.inputUpdateHandler = inputUpdateHandler;
    }
}
