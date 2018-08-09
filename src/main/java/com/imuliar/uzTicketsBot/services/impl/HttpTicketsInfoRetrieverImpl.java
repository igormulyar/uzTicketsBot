package com.imuliar.uzTicketsBot.services.impl;

import com.imuliar.uzTicketsBot.model.TicketRequest;
import com.imuliar.uzTicketsBot.services.HttpTicketsInfoRetriever;
import com.imuliar.uzTicketsBot.services.states.Station;
import java.nio.charset.Charset;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * <p>Default implementation of {@link HttpTicketsInfoRetriever}</p>
 *
 * @author imuliar
 * @since 1.0
 */
@Service
public class HttpTicketsInfoRetrieverImpl implements HttpTicketsInfoRetriever {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpTicketsInfoRetrieverImpl.class);

    private static final String DEFAULT_SEARCH_TIME_OFFSET = "00:00";

    private static final String UZ_SEARCH_URL = "https://booking.uz.gov.ua/ru/train_search/";

    private static final String UZ_RESULT_URL_TEMPLATE = "https://booking.uz.gov.ua/ru/?from=%s&to=%s&date=%s&time=00%%3A00&url=train-list";

    /**
     * {@inheritDoc}
     */
    @Override
    public String requestTickets(TicketRequest ticketRequest) {
        LocalDate date = ticketRequest.getDate();
        Station fromStation = ticketRequest.getFrom();
        Station toStation = ticketRequest.getTo();

        //TODO replace this with proper code
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        HttpHeaders headers = new HttpHeaders();
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("date", date.toString());
        params.add("from", fromStation.getValue());
        params.add("time", DEFAULT_SEARCH_TIME_OFFSET);
        params.add("to", toStation.getValue());
        HttpEntity<LinkedMultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(UZ_SEARCH_URL, HttpMethod.POST, requestEntity, String.class);

        if (!response.getBody().matches("warning")) {
            return String.format(UZ_RESULT_URL_TEMPLATE, fromStation.getValue(), toStation.getValue(), date.toString());
        } else {
            return null;
        }
    }
}
