package com.imuliar.uzTicketsBot.services;

import com.imuliar.uzTicketsBot.model.Station;
import com.imuliar.uzTicketsBot.services.states.UserContext;

import java.util.List;

/**
 * <p>Service for fetching the station code.</p>
 *
 * @author imuliar
 * @since 1.0
 */
public interface StationCodeResolver {

    /**
     * <p>Resolves station code using given station name.</p>
     *
     * @param userInput station name entered by user
     * @return station code
     */
    List<Station> resolveProposedStations(String userInput, UserContext context);
}
