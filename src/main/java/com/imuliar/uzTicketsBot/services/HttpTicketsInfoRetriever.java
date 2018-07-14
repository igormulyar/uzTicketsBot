package com.imuliar.uzTicketsBot.services;

import java.time.LocalDate;

/**
 * <p>Service for getting tickets info via HTTP.</p>
 *
 * @author imuliar
 * @since 1.0
 */
public interface HttpTicketsInfoRetriever {

    /**
     * <p>Makes a HTTP request and get info about available tickets.</p>
     * @param date date of desired trip
     * @param fromStation departure station
     * @param toStation arrival station
     * @return info about available tickets for specified date and direction
     */
    String requestTickets(LocalDate date, String fromStation, String toStation);
}
