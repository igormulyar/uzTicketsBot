package com.imuliar.uzTicketsBot;

import com.imuliar.uzTicketsBot.model.TelegramUser;
import com.imuliar.uzTicketsBot.model.TicketRequest;
import com.imuliar.uzTicketsBot.services.HttpTicketsInfoRetriever;
import com.imuliar.uzTicketsBot.services.OutputMessageService;
import com.imuliar.uzTicketsBot.services.TicketRequestService;
import com.imuliar.uzTicketsBot.services.impl.ScheduledProcessRunnerImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * <p>Unit test for {@link ScheduledProcessRunnerImpl}</p>
 *
 * @author imuliar
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class ScheduledProcessRunnerImplTest {

    @InjectMocks
    private ScheduledProcessRunnerImpl scheduledProcessRunner;

    @Mock
    private HttpTicketsInfoRetriever ticketsInfoRetriever;

    @Mock
    private TicketRequestService ticketRequestService;

    @Mock
    private OutputMessageService outputMessageService;

    @Captor
    private ArgumentCaptor<SendMessage> sendMessageCaptor;

    @Ignore
    @Test
    public void searchTicketsForAllUsers() throws TelegramApiException {
        Long chatId1 = 1L;
        String url1 = "URL1";
        TelegramUser telegramUser1 = new TelegramUser(chatId1);
        TicketRequest ticketRequest1 = new TicketRequest();
        ticketRequest1.setTelegramUser(telegramUser1);

        Long chatId2 = 2L;
        String url2 = "URL2";
        TelegramUser telegramUser2 = new TelegramUser(chatId2);
        TicketRequest ticketRequest2 = new TicketRequest();
        ticketRequest2.setTelegramUser(telegramUser2);

        Long chatId3 = chatId2;
        String url3 = "URL3";
        TelegramUser telegramUser3 = new TelegramUser(chatId3);
        TicketRequest ticketRequest3 = new TicketRequest();
        ticketRequest3.setTelegramUser(telegramUser3);

        Long chatId4 = 4L;
        String url4 = null;
        TelegramUser telegramUser4 = new TelegramUser(chatId4);
        TicketRequest ticketRequest4 = new TicketRequest();
        ticketRequest4.setTelegramUser(telegramUser4);

        List<TicketRequest> activeTicketRequests = new ArrayList<>(Arrays.asList(ticketRequest1, ticketRequest2, ticketRequest3, ticketRequest4));

        Mockito.when(ticketRequestService.findActiveTicketRequests()).thenReturn(activeTicketRequests);
        /*Mockito.when(ticketsInfoRetriever.requestTickets(ticketRequest1)).thenReturn(url1);
        Mockito.when(ticketsInfoRetriever.requestTickets(ticketRequest2)).thenReturn(url2);
        Mockito.when(ticketsInfoRetriever.requestTickets(ticketRequest3)).thenReturn(url3);
        Mockito.when(ticketsInfoRetriever.requestTickets(ticketRequest4)).thenReturn(url4);*/

        scheduledProcessRunner.searchTicketsForAllUsers();

/*        Mockito.verify(outputMessageService, Mockito.times(3)).notifyTicketsSearchSuccess(sendMessageCaptor.capture());
        List<SendMessage> sendMessageList = sendMessageCaptor.getAllValues();
        sendMessageList.sort(Comparator.comparing(SendMessage::getText));
        SendMessage sendMessage1 = sendMessageList.get(0);
        SendMessage sendMessage2 = sendMessageList.get(1);
        SendMessage sendMessage3 = sendMessageList.get(2);
        Assert.assertTrue(sendMessage1.getText().contains(url1));
        Assert.assertTrue(sendMessage2.getText().contains(url2));
        Assert.assertTrue(sendMessage3.getText().contains(url3));
        Assert.assertEquals(chatId1.toString(), sendMessage1.getChatId());
        Assert.assertEquals(chatId2.toString(), sendMessage2.getChatId());
        Assert.assertEquals(chatId2.toString(), sendMessage3.getChatId());*/
    }
}
