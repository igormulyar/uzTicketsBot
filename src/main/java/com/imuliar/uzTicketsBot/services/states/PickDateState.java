package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.model.TicketRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.imuliar.uzTicketsBot.services.impl.OutputMessageServiceImpl.*;
import static java.lang.Math.toIntExact;

/**
 * @author imuliar
 *         21.07.2018
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PickDateState extends AbstractState {

    private static final String YEAR_BACK_CALLBACK = "year_back";

    private static final String YEAR_FORWARD_CALLBACK = "year_forward";

    private static final String MONTH_BACK_CALLBACK = "month_back";

    private static final String MONTH_FORWARD_CALLBACK = "month_forward";

    private static final Pattern DATE_MATCHING_PATTERN = Pattern.compile("^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$");

    private LocalDate calendarViewDate = LocalDate.now();

    private AbstractState trackTicketsState;

    /**
     * {@inheritDoc}
     */
    @Override
    public void processUpdate(Update update) {
        Long chatId = resolveChatId(update);
        if (update.hasCallbackQuery()) {
            String callbackString = update.getCallbackQuery().getData();
            if (STATION_CALLBACK_REGEXP_PATTERN.matcher(callbackString).matches()) {
                calendarViewDate = LocalDate.now();
                outputMessageService.printMessageWithKeyboard(chatId, context.getLocalizedMessage("message.chooseDepartureDate"), buildCalendarMarkup(calendarViewDate));
            }
            if (callbackString.equals(TO_BEGGINNING_CALBACK)) {
                goToBeginning(update);
            }
            if (callbackString.equals(CONFIRM_TICKETS_REQUEST)) {
                trackTicketsState.setContext(context);
                context.setState(trackTicketsState);
                context.processUpdate(update);
            }
            switch (callbackString) {
                case YEAR_BACK_CALLBACK:
                    calendarViewDate = calendarViewDate.minusYears(1);
                    displayCalendarUpdated(update);
                    break;
                case YEAR_FORWARD_CALLBACK:
                    calendarViewDate = calendarViewDate.plusYears(1);
                    displayCalendarUpdated(update);
                    break;
                case MONTH_BACK_CALLBACK:
                    calendarViewDate = calendarViewDate.minusMonths(1);
                    displayCalendarUpdated(update);
                    break;
                case MONTH_FORWARD_CALLBACK:
                    calendarViewDate = calendarViewDate.plusMonths(1);
                    displayCalendarUpdated(update);
                    break;
            }
            if (DATE_MATCHING_PATTERN.matcher(callbackString).matches()) {
                processReceivedDateInput(update, callbackString);
            }
        }
    }

    private void processReceivedDateInput(Update update, String callbackString) {
        LocalDate pickedDate = LocalDate.parse(callbackString, DateTimeFormatter.ISO_DATE);
        if (pickedDate.isBefore(LocalDate.now())) {
            outputMessageService.popUpNotify(update.getCallbackQuery().getId(), context.getLocalizedMessage("alert.priorDate"));
        } else {
            context.getTicketRequest().setDepartureDate(pickedDate);
            publishTicketRequestSummary(update);
        }
    }

    private void publishTicketRequestSummary(Update update) {
        TicketRequest ticketRequest = context.getTicketRequest();
        String date =
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
                        .withLocale(context.getLocale())
                        .format(ticketRequest.getDepartureDate());
        String ticketRequestSummary = context.getLocalizedMessage("message.confirmSearch",
                new String[]{ticketRequest.getDepartureStation().getTitle(), ticketRequest.getArrivalStation().getTitle(), date});

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        markupInline.setKeyboard(keyboard);
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton().setText(context.getLocalizedMessage("button.confirm")).setCallbackData(CONFIRM_TICKETS_REQUEST));
        buttons.add(new InlineKeyboardButton().setText(context.getLocalizedMessage("button.cancel")).setCallbackData(TO_BEGGINNING_CALBACK));
        keyboard.add(buttons);
        outputMessageService.printMessageWithKeyboard(resolveChatId(update),  ticketRequestSummary, markupInline);
    }

    private void displayCalendarUpdated(Update update) {
        Long chatId = resolveChatId(update);
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        outputMessageService.updateMessageWithMarkup(chatId, toIntExact(messageId), context.getLocalizedMessage("message.enterDate"), buildCalendarMarkup(calendarViewDate));
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
        InlineKeyboardButton calendarMonth = new InlineKeyboardButton().setText(date.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, context.getLocale())).setCallbackData(EMPTY_CALLBACK);
        InlineKeyboardButton monthToRight = new InlineKeyboardButton().setText(" >>>>> ").setCallbackData(MONTH_FORWARD_CALLBACK);
        List<InlineKeyboardButton> monthRow = new ArrayList<>(Arrays.asList(monthToLeft, calendarMonth, monthToRight));
        keyboard.add(monthRow);

        List<InlineKeyboardButton> daysOfWeekRow = new ArrayList<>();
        daysOfWeekRow.add(new InlineKeyboardButton().setText(context.getLocalizedMessage("calendar.mon")).setCallbackData(EMPTY_CALLBACK));
        daysOfWeekRow.add(new InlineKeyboardButton().setText(context.getLocalizedMessage("calendar.tue")).setCallbackData(EMPTY_CALLBACK));
        daysOfWeekRow.add(new InlineKeyboardButton().setText(context.getLocalizedMessage("calendar.wed")).setCallbackData(EMPTY_CALLBACK));
        daysOfWeekRow.add(new InlineKeyboardButton().setText(context.getLocalizedMessage("calendar.thu")).setCallbackData(EMPTY_CALLBACK));
        daysOfWeekRow.add(new InlineKeyboardButton().setText(context.getLocalizedMessage("calendar.fri")).setCallbackData(EMPTY_CALLBACK));
        daysOfWeekRow.add(new InlineKeyboardButton().setText(context.getLocalizedMessage("calendar.sat")).setCallbackData(EMPTY_CALLBACK));
        daysOfWeekRow.add(new InlineKeyboardButton().setText(context.getLocalizedMessage("calendar.sun")).setCallbackData(EMPTY_CALLBACK));
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

    @Autowired
    @Qualifier("trackTicketsState")
    public void setTrackTicketsState(AbstractState trackTicketsState) {
        this.trackTicketsState = trackTicketsState;
    }
}
