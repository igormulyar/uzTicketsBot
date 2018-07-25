package com.imuliar.uzTicketsBot.services.states;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imuliar.uzTicketsBot.UzTicketsBot;
import com.imuliar.uzTicketsBot.services.UserState;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.api.objects.Update;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

/**
 * <p>The state when user choosing the station of departure.</p>
 *
 * @author imuliar
 * @since 1.0
 */
public class DepartureStationState extends AbstractState implements UserState {

    public DepartureStationState(UzTicketsBot bot, UserContext context) {
        super(bot, context);
    }

    @Override
    public void processUpdate(Update update) {

    }

    @Override
    public void publishMessage(Update update) {

    }

    @Override
    public void publishValidationMessage() {

    }

    private List<StationDto> resolvePossibleStations(String userInput){

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        String url = String.format("https://booking.uz.gov.ua/ru/train_search/station/?term=%s", userInput);

        HttpHeaders headers = new HttpHeaders();
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        HttpEntity<LinkedMultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        String body = response.getBody();
        try {
            return new ObjectMapper().readValue(body, new TypeReference<List<StationDto>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
