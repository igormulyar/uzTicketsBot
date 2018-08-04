package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.model.TicketRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import static java.lang.Math.toIntExact;

/**
 * @author imuliar
 * 21.07.2018
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PickDateState extends AbstractState{

    private static final String MESSAGE_TEXT = "Please chose the departure date";

    private static final String YEAR_BACK_CALLBACK = "year_back";

    private static final String YEAR_FORWARD_CALLBACK = "year_forward";

    private static final String MONTH_BACK_CALLBACK = "month_back";

    private static final String MONTH_FORWARD_CALLBACK = "month_forward";

    private static final String DATE_MATCHING_PATTERN = "^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$";

    private LocalDate calendarViewDate = LocalDate.now();

    @Override
    public void processUpdate(Update update) {
        if(update.hasCallbackQuery()){
            String callbackString = update.getCallbackQuery().getData();
            if (callbackString.equals(ENTER_DATE)){
                calendarViewDate = LocalDate.now();
                displayCalendar(update);
            }
            if (callbackString.equals(YEAR_BACK_CALLBACK)){
                calendarViewDate = calendarViewDate.minusYears(1);
                displayCalendarUpdated(update);
            }
            if (callbackString.equals(YEAR_FORWARD_CALLBACK)){
                calendarViewDate = calendarViewDate.plusYears(1);
                displayCalendarUpdated(update);
            }
            if (callbackString.equals(MONTH_BACK_CALLBACK)){
                calendarViewDate = calendarViewDate.minusMonths(1);
                displayCalendarUpdated(update);
            }
            if (callbackString.equals(MONTH_FORWARD_CALLBACK)){
                calendarViewDate = calendarViewDate.plusMonths(1);
                displayCalendarUpdated(update);
            }
            if (callbackString.equals(TO_BEGGINNING_CALBACK)){
                context.setInitialState();
                context.processUpdate(update);
            }
            if(callbackString.matches(DATE_MATCHING_PATTERN)){
                LocalDate pickedDate = LocalDate.parse(callbackString, DateTimeFormatter.ISO_DATE);
                context.getTicketRequest().setDate(pickedDate);
                System.out.println(context.getTicketRequest().toString());
                publishTicketRequestSummary(update);
            }
        }


    }

    private void publishTicketRequestSummary(Update update) {
        TicketRequest ticketRequest = context.getTicketRequest();
        String ticketRequestSummary = "Confirm search the train: " +
                "FROM " + ticketRequest.getFrom().getTitle() + " " +
                "TO " + ticketRequest.getTo().getTitle() + ". " +
                "DEPARTURE DATE: " + ticketRequest.getDate();

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        markupInline.setKeyboard(keyboard);
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton().setText("Cancel").setCallbackData(TO_BEGGINNING_CALBACK));
        buttons.add(new InlineKeyboardButton().setText("Confirm").setCallbackData(ENTER_DATE));
        keyboard.add(buttons);

        sendBotResponse(new SendMessage()
                .enableMarkdown(true)
                .setChatId(resolveChatId(update))
                .setText(ticketRequestSummary)
                .setReplyMarkup(markupInline));
    }

    private void displayCalendarUpdated(Update update) {
        Long chatId = resolveChatId(update);
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        sendBotResponse(new EditMessageText()
                .setChatId(chatId)
                .setMessageId(toIntExact(messageId))
                .setText(MESSAGE_TEXT)
                .setReplyMarkup(buildCalendarMarkup(calendarViewDate)));
    }

    private void displayCalendar(Update update) {
        sendBotResponse(new SendMessage()
                .enableMarkdown(true)
                .setChatId(resolveChatId(update))
                .setText(MESSAGE_TEXT)
                .setReplyMarkup(buildCalendarMarkup(calendarViewDate)));
    }

    private InlineKeyboardMarkup buildCalendarMarkup(@Nullable LocalDate date) {
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

        InlineKeyboardButton yearToLeft = new InlineKeyboardButton().setText(" <<< ").setCallbackData(YEAR_BACK_CALLBACK);
        InlineKeyboardButton calendarYear = new InlineKeyboardButton().setText(String.valueOf(date.getYear())).setCallbackData(EMPTY_CALLBACK);
        InlineKeyboardButton yearToRight = new InlineKeyboardButton().setText(" >>> ").setCallbackData(YEAR_FORWARD_CALLBACK);
        List<InlineKeyboardButton> yearRow = new ArrayList<>(Arrays.asList(yearToLeft, calendarYear, yearToRight));
        keyboard.add(yearRow);

        InlineKeyboardButton monthToLeft = new InlineKeyboardButton().setText(" <<<<< ").setCallbackData(MONTH_BACK_CALLBACK);
        InlineKeyboardButton calendarMonth = new InlineKeyboardButton().setText(date.getMonth().toString()).setCallbackData(EMPTY_CALLBACK);
        InlineKeyboardButton monthToRight = new InlineKeyboardButton().setText(" >>>>> ").setCallbackData(MONTH_FORWARD_CALLBACK);
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
