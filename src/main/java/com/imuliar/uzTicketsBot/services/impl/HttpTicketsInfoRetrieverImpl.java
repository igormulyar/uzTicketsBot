package com.imuliar.uzTicketsBot.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imuliar.uzTicketsBot.model.Station;
import com.imuliar.uzTicketsBot.model.TicketRequest;
import com.imuliar.uzTicketsBot.services.HttpTicketsInfoRetriever;
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

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        LocalDate date = ticketRequest.getDepartureDate();
        Station fromStation = ticketRequest.getDepartureStation();
        Station toStation = ticketRequest.getArrivalStation();

        LOGGER.info("Searching for tickets: ?", ticketRequest);
        ResponseEntity<String> response = askUzServerForJsonString(date, fromStation, toStation);


        if (response.hasBody() && evaluateJsonString(response.getBody(), Collections.emptySet())) { //TODO Correct after implementing selecting the seat/carriage type
            LOGGER.info("Tickets found: ?", ticketRequest);
            return String.format(UZ_RESULT_URL_TEMPLATE, fromStation.getValue(), toStation.getValue(), date.toString());
        }

        LOGGER.info("No tickets found: ?", ticketRequest);
        return null;
    }

    /**
     * <p>Requests json string of available tickets from UZ server</p>
     *
     * @param date        requested departure date
     * @param fromStation requested station of departure
     * @param toStation   requested station of arrival
     * @return ResponseEntity
     */
    private ResponseEntity<String> askUzServerForJsonString(LocalDate date, Station fromStation, Station toStation) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        HttpHeaders headers = new HttpHeaders();
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("date", date.toString());
        params.add("from", fromStation.getValue());
        params.add("time", DEFAULT_SEARCH_TIME_OFFSET);
        params.add("to", toStation.getValue());
        HttpEntity<LinkedMultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        return restTemplate.exchange(UZ_SEARCH_URL, HttpMethod.POST, requestEntity, String.class);
    }

    /**
     * <p>Parse json string and define if requested tickets are available.</p>
     *
     * @param jsonString                json string retrieved from uz server
     * @param seatTypeFilteringCriteria the list of requested seat types
     * @return {@code TRUE} if at leas one seat of requested type was found and {@code FALSE} if not.
     */
    private boolean evaluateJsonString(String jsonString, Set<String> seatTypeFilteringCriteria) {
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(jsonString);
            if (jsonNode != null && !jsonNode.path("data").path("list").isMissingNode()) {
                Set<String> foundSeatTypes = convertNode(jsonNode.path("data").path("list"), new TypeReference<List<JsonNode>>() {}).stream()
                        .map(node -> convertNode(node.path("types"), new TypeReference<List<JsonNode>>() {}))
                        .flatMap(Collection::stream)
                        .filter(node -> !node.isMissingNode())
                        .map(node -> convertNode(node.path("title"), new TypeReference<String>() {}))
                        //.filter(seatType -> seatTypeFilteringCriteria.contains(seatType)) // TODO uncomment after implementing selecting the seat/carriage type
                        .collect(Collectors.toSet());
                return foundSeatTypes.size() > 0;
            }
            return false;
        } catch (IOException e) {
            LOGGER.error("Exception was thrown during the JSON string parsing", e);
            return false;
        }
    }

    private <T> T convertNode(JsonNode node, TypeReference<T> typeReference) {
        T object = null;
        try {
            object = new ObjectMapper().readerFor(typeReference).readValue(node);
        } catch (IOException e) {
            LOGGER.error("Exception was thrown during the attempt of matching JsonNode to specified type", e);
        }
        return object;
    }
}
