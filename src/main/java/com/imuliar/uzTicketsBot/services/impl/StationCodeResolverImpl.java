package com.imuliar.uzTicketsBot.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imuliar.uzTicketsBot.services.StationCodeResolver;
import com.imuliar.uzTicketsBot.model.Station;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import com.imuliar.uzTicketsBot.services.states.UserContext;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Station> resolveProposedStations(String userInput, UserContext context) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        String url = context.getLocalizedMessage("url.stationCodeResolveTemplate", new String[]{userInput});

        HttpHeaders headers = new HttpHeaders();
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        HttpEntity<LinkedMultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        String body = response.getBody();
        try {
            return new ObjectMapper().readValue(body, new TypeReference<List<Station>>() {
            });
        } catch (IOException e) {
            LOGGER.error("Can't read proposed stations list, retrieved from server", e);
        }
        return Collections.emptyList();
    }
}
