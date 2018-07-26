package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.UzTicketsBot;
import com.imuliar.uzTicketsBot.services.UserState;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import static java.lang.Math.toIntExact;

/**
 * @author imuliar
 * 21.07.2018
 */
@Component
@Scope("prototype")
public class PickDateState extends AbstractState{

    private static final String MESSAGE_TEXT = "Please chose the departure date";

    @Override
    public void processUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();

            EditMessageText new_message = null;


            if (call_data.equals("plus_year")) {

                new_message = buildEditMessageTextObject(message_id, chat_id);

            }

            try {
                bot.execute(new_message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private EditMessageText buildEditMessageTextObject(long message_id, long chat_id) {
        EditMessageText new_message;
        new_message = new EditMessageText()
                .setChatId(chat_id)
                .setMessageId(toIntExact(message_id))
                .setText(MESSAGE_TEXT)
                .setReplyMarkup(buildCalendarPage(LocalDate.now().plusYears(1)));
        return new_message;
    }

    @Override
    public void publishMessage(Update update) {
        SendMessage sendMessage = new SendMessage()
                .enableMarkdown(true)
                .setChatId(update.getMessage().getChatId().toString())
                .setText(MESSAGE_TEXT)
                .setReplyMarkup(buildCalendarPage(null));
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public InlineKeyboardMarkup buildCalendarPage(@Nullable LocalDate date) {
        //calculations
        date = Optional.ofNullable(date).orElse(LocalDate.now());
        int daysInMonthAmount = date.getMonth().length(date.isLeapYear());
        LocalDate firstDayOfMonth = date.minusDays(date.getDayOfMonth() - 1L);
        int firstDayOffset = firstDayOfMonth.getDayOfWeek().getValue() - 1;
        int totalDayButtonsAmount = daysInMonthAmount + firstDayOffset;
        while (totalDayButtonsAmount % 7 != 0) {
            totalDayButtonsAmount++;
        }

        //markup
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        markupInline.setKeyboard(keyboard);

        InlineKeyboardButton yearToLeft = new InlineKeyboardButton().setText(" <<< ").setCallbackData("minus_year");
        InlineKeyboardButton calendarYear = new InlineKeyboardButton().setText(String.valueOf(date.getYear())).setCallbackData(EMPTY_CALLBACK);
        InlineKeyboardButton yearToRight = new InlineKeyboardButton().setText(" >>> ").setCallbackData("plus_year");
        List<InlineKeyboardButton> yearRow = new ArrayList<>(Arrays.asList(yearToLeft, calendarYear, yearToRight));
        keyboard.add(yearRow);

        InlineKeyboardButton monthToLeft = new InlineKeyboardButton().setText(" <<<<< ").setCallbackData("minus_month");
        InlineKeyboardButton calendarMonth = new InlineKeyboardButton().setText(date.getMonth().toString()).setCallbackData(EMPTY_CALLBACK);
        InlineKeyboardButton monthToRight = new InlineKeyboardButton().setText(" >>>>> ").setCallbackData("plus_month");
        List<InlineKeyboardButton> monthRow = new ArrayList<>(Arrays.asList(monthToLeft, calendarMonth, monthToRight));
        keyboard.add(monthRow);

        List<InlineKeyboardButton> daysOfWeekRow = new ArrayList<>();
        daysOfWeekRow.add(new InlineKeyboardButton().setText("mon").setCallbackData(EMPTY_CALLBACK));
        daysOfWeekRow.add(new InlineKeyboardButton().setText("tue").setCallbackData(EMPTY_CALLBACK));
        daysOfWeekRow.add(new InlineKeyboardButton().setText("wed").setCallbackData(EMPTY_CALLBACK));
        daysOfWeekRow.add(new InlineKeyboardButton().setText("thu").setCallbackData(EMPTY_CALLBACK));
        daysOfWeekRow.add(new InlineKeyboardButton().setText("fri").setCallbackData(EMPTY_CALLBACK));
        daysOfWeekRow.add(new InlineKeyboardButton().setText("sat").setCallbackData(EMPTY_CALLBACK));
        daysOfWeekRow.add(new InlineKeyboardButton().setText("sun").setCallbackData(EMPTY_CALLBACK));
        keyboard.add(daysOfWeekRow);

        int dayOfWeekCounter = 1;
        List<InlineKeyboardButton> daysRow = new ArrayList<>(7);
        for (int i = 1 - firstDayOffset; i < totalDayButtonsAmount + 1; i++) {
            if (i <= 0 || (i > daysInMonthAmount && dayOfWeekCounter != 1)) {
                daysRow.add(new InlineKeyboardButton().setText(" . ").setCallbackData(EMPTY_CALLBACK));
            } else if (i <= daysInMonthAmount) {
                LocalDate buttonDate = date.withDayOfMonth(i);
                daysRow.add(new InlineKeyboardButton().setText(" " + i + " ").setCallbackData(buttonDate.toString()));
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
}
