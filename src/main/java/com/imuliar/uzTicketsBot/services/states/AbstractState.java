package com.imuliar.uzTicketsBot.services.states;

import com.imuliar.uzTicketsBot.UzTicketsBot;

/**
 * @author imuliar
 * 22.07.2018
 */

public class AbstractState {

    protected static final String EMPTY_CALLBACK = "do_nothing";

    protected static final String TO_BEGGINNING_CALBACK = "to_beginning";

    protected UserContext context;

    protected UzTicketsBot bot;

    public AbstractState(UzTicketsBot bot, UserContext context) {
        this.bot = bot;
        this.context = context;
    }
}
