package com.imuliar.uzTicketsBot.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imuliar.uzTicketsBot.services.StationCodeResolver;
import com.imuliar.uzTicketsBot.services.states.StationDto;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
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
 * <p>Default {@link StationCodeResolver} implementation.</p>
 * <p>Resolves station code via HTTP.</p>
 *
 * @author imuliar
 * @since 1.0
 */
@Service
public class StationCodeResolverImpl implements StationCodeResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(StationCodeResolverImpl.class);

    private static final String URL_PATTERN = "https://booking.uz.gov.ua/ru/train_search/station/?term=%s";

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StationDto> resolveProposedStations(String userInput) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        String url = String.format(URL_PATTERN, userInput);

        HttpHeaders headers = new HttpHeaders();
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        HttpEntity<LinkedMultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        String body = response.getBody();
        try {
            return new ObjectMapper().readValue(body, new TypeReference<List<StationDto>>() {
            });
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return Collections.emptyList();
    }
}
