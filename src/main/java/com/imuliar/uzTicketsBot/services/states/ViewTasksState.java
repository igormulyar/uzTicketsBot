package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.model.TicketRequest;
import com.imuliar.uzTicketsBot.services.HttpTicketsInfoRetriever;
import com.imuliar.uzTicketsBot.services.TicketRequestService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.api.objects.Update;

import static com.imuliar.uzTicketsBot.services.impl.OutputMessageServiceImpl.*;

/**
 * @author imuliar
 * 22.07.2018
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ViewTasksState extends AbstractState {

    private TicketRequestService ticketRequestService;

    private List<TicketRequest> activeTicketRequests;

    private HttpTicketsInfoRetriever httpTicketsInfoRetriever;

    @Override
    public void processUpdate(Update update) {
        Long chatId = resolveChatId(update);
        if (update.hasCallbackQuery()) {
            String callbackString = update.getCallbackQuery().getData();
            if (callbackString.equals(VIEW_TASKS_CALLBACK)) {
                processSearchActiveTasksRequest(update, chatId);
            } else if (VIEW_TASK_CALLBACK_REGEXP_PATTERN.matcher(callbackString).matches()) {
                showTaskMenuToUser(chatId, parseTicketRequestId(callbackString));
            } else if (DELETE_TASK_CALLBACK_REGEXP_PATTERN.matcher(callbackString).matches()) {
                processDeleteTicketRequest(update, parseTicketRequestId(callbackString));
            } else if (SEARCH_NOW_CALLBACK_REGEXP_PATTERN.matcher(callbackString).matches()) {
                processInstantSearch(update, callbackString);
            } else {
                goToBeginning(update);
            }
        }
    }

    private void processInstantSearch(Update update, String callbackString) {
        TicketRequest ticketRequest = getTicketRequestById(parseTicketRequestId(callbackString));
        String resultUrl = httpTicketsInfoRetriever.requestTickets(ticketRequest);
        if(resultUrl != null){
            outputMessageService.notifyTicketsSearchSuccess(ticketRequest, resultUrl);
        } else {
            outputMessageService.popUpNotify(update.getCallbackQuery().getId(),
                    "No tickets found");
        }
        goToBeginning(update);
    }

    private Long parseTicketRequestId(String callbackString) {
        return Long.valueOf(callbackString.split(":")[1]);
    }

    private void processDeleteTicketRequest(Update update, Long id) {
        ticketRequestService.deleteById(id);
        outputMessageService.popUpNotify(update.getCallbackQuery().getId(), "Task deleted");
        goToBeginning(update);
    }

    private void showTaskMenuToUser(Long chatId, Long id) {
        outputMessageService.printTaskOverview(chatId, getTicketRequestById(id));
    }

    private TicketRequest getTicketRequestById(Long id) {
        return activeTicketRequests.stream()
                .filter(tr -> tr.getId().doubleValue() == id.doubleValue())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Wrong callback data received: no ticket request with such id."));
    }

    private void processSearchActiveTasksRequest(Update update, Long chatId) {
        activeTicketRequests = ticketRequestService.findActiveRequestsByChatId(chatId);
        if (CollectionUtils.isEmpty(activeTicketRequests)) {
            outputMessageService.popUpNotify(update.getCallbackQuery().getId(),
                    "You don't have any active search tasks.");
        } else {
            outputMessageService.printListOfUserTasks(chatId, activeTicketRequests);
        }
    }

    @Autowired
    public void setTicketRequestService(TicketRequestService ticketRequestService) {
        this.ticketRequestService = ticketRequestService;
    }

    @Autowired
    public void setHttpTicketsInfoRetriever(HttpTicketsInfoRetriever httpTicketsInfoRetriever) {
        this.httpTicketsInfoRetriever = httpTicketsInfoRetriever;
    }
}
