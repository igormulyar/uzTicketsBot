package com.imuliar.uzTicketsBot.services.impl;

import com.imuliar.uzTicketsBot.dao.TicketRequestDao;
import com.imuliar.uzTicketsBot.model.TicketRequest;
import com.imuliar.uzTicketsBot.model.TicketRequestStatus;
import com.imuliar.uzTicketsBot.services.TicketRequestService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>Service for {@link TicketRequest} manipulating</p>
 *
 * @author imuliar
 * @since 1.0
 */
@Service
public class TicketRequestServiceImpl implements TicketRequestService {

    private static final int ACTIVE_TASKS_LIMIT = 3;

    private TicketRequestDao ticketRequestDao;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAlreadySaved(Long chatId, TicketRequest ticketRequest) {
        return ticketRequestDao.findActiveByChatId(chatId).stream()
                .filter(req -> req.getDepartureStation().getValue().equals(ticketRequest.getDepartureStation().getValue()))
                .filter(req -> req.getArrivalStation().getValue().equals(ticketRequest.getArrivalStation().getValue()))
                .anyMatch(req -> req.getDepartureDate().equals(ticketRequest.getDepartureDate()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(TicketRequest ticketRequest) {
        ticketRequestDao.save(ticketRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TicketRequest> findActiveTicketRequests() {
        return ticketRequestDao.findByRequestStatus(TicketRequestStatus.ACTIVE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void markInactive(TicketRequest executedRequest) {
        ticketRequestDao.markInactive(executedRequest.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInTaskLimit(Long chatId) {
        return ticketRequestDao.countUpActiveTasksAmount(chatId) <= ACTIVE_TASKS_LIMIT;
    }

    @Autowired
    public void setTicketRequestDao(TicketRequestDao ticketRequestDao) {
        this.ticketRequestDao = ticketRequestDao;
    }
}
