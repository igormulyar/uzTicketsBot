package com.imuliar.uzTicketsBot.services;

/**
 * <p>Service for fetching the station code.</p>
 *
 * @author imuliar
 * @since 1.0
 */
public interface StationCodeResolver {

    /**
     * <p>Resolves station code using given station name.</p>
     * @param stationName station name
     * @return station code
     */
    String resolveCode(String stationName);
}
