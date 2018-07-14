package com.imuliar.uzTicketsBot.services.impl;

import com.imuliar.uzTicketsBot.services.HttpTicketsInfoRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.time.LocalDate;

/**
 * <p>Default implementation of {@link HttpTicketsInfoRetriever}</p>
 *
 * @author imuliar
 * @since 1.0
 */
public class HttpTicketsInfoRetrieverImpl implements HttpTicketsInfoRetriever {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpTicketsInfoRetrieverImpl.class);


    /**
     * {@inheritDoc}
     */
    @Override
    public String requestTickets(LocalDate date, String fromStation, String toStation) {

        //TODO replace this with proper code
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        String url = "https://booking.uz.gov.ua/ru/train_search/";
        HttpHeaders headers = new HttpHeaders();
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("date", "2018-08-24");
        params.add("from", "2208001");
        params.add("time", "00:00");
        params.add("to", "2200001");
        HttpEntity<LinkedMultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);


        LOGGER.info("ResponseEntity: \n {}", response);
        System.out.println(String.format("Response BODY: \n %s", response.getBody()));
        return null;
    }
}
