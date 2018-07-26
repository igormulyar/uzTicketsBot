package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.services.StationCodeResolver;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * <p>The state when user choosing the station of departure.</p>
 *
 * @author imuliar
 * @since 1.0
 */
@Component
@Scope("prototype")
public class DepartureStationState extends AbstractState {

    private static final String ADD_TASK_CALLBACK = "add_task";

    private StationCodeResolver stationCodeResolver;

    @Override
    public void processUpdate(Update update) {
        Long chatId = resolveChatId(update);
        if (update.hasCallbackQuery() && update.getCallbackQuery().getData().equals(ADD_TASK_CALLBACK)) {
            SendMessage sendMessage = new SendMessage()
                    .enableMarkdown(true)
                    .setChatId(chatId)
                    .setText("Please enter the station of departure.");
            try {
                bot.execute(sendMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String userInput = update.getMessage().getText();
            List<StationDto> proposedStations = stationCodeResolver.resolveProposedStations(userInput);
            publishStationSearchResults(chatId, proposedStations);
        }


    }

    private void publishStationSearchResults(Long chatId, List<StationDto> proposedStations) {
        if (CollectionUtils.isEmpty(proposedStations)) {
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            markupInline.setKeyboard(keyboard);

            List<InlineKeyboardButton> buttons = new ArrayList<>();
            buttons.add(new InlineKeyboardButton().setText("Enter again").setCallbackData(ADD_TASK_CALLBACK));
            buttons.add(new InlineKeyboardButton().setText("Cancel").setCallbackData(TO_BEGGINNING_CALBACK));
            keyboard.add(buttons);
            SendMessage sendMessage = new SendMessage()
                    .enableMarkdown(true)
                    .setChatId(chatId)
                    .setText("Sorry, we can't find any station.")
                    .setReplyMarkup(markupInline);
            try {
                bot.execute(sendMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            
        }
    }

    @Override
    public void publishMessage(Update update) {


    }

    @Autowired
    public void setStationCodeResolver(StationCodeResolver stationCodeResolver) {
        this.stationCodeResolver = stationCodeResolver;
    }
}
