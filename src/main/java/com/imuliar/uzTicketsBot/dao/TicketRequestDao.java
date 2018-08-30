package com.imuliar.uzTicketsBot.dao;

import com.imuliar.uzTicketsBot.model.TicketRequest;
import com.imuliar.uzTicketsBot.model.TicketRequestStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author imuliar
 * 19.08.2018
 */
@Repository
public interface TicketRequestDao extends JpaRepository<TicketRequest, Long> {

    /**
     * <p>Fetch all ticket requests with specified status.</p>
     *
     * @param ticketRequestStatus status as a search criterion
     * @return list of ticket requests or empty list
     */
    List<TicketRequest> findByRequestStatus(TicketRequestStatus ticketRequestStatus);

    /**
     * <p>Fetch active status requests by specified chatId</p>
     *
     * @param chatId chat id as a search criterion
     * @return list of ticket requests or empty list
     */
    @Query("SELECT req FROM TicketRequest req " +
            "JOIN req.telegramUser telegramUser " +
            "WHERE telegramUser.chatId = :chatId AND req.requestStatus = 'ACTIVE'")
    List<TicketRequest> findActiveByChatId(@Param("chatId") Long chatId);

    /**
     * <p>Marks ticket request with specified id as INACTIVE</p>
     *
     * @param id id of ticket request which is supposed to be marked INACTIVE
     */
    @Modifying
    @Query("UPDATE TicketRequest req SET req.requestStatus = 'INACTIVE' WHERE req.id = :id")
    void markInactive(@Param("id") Long id);

    /**
     * <p>Calculates the amount of active ticket requests which are related to specified chatId</p>
     *
     * @param chatId as a search criterion
     * @return amount of active requests for chat id
     */
    @Query("SELECT COUNT(req.id) FROM TicketRequest req " +
            "JOIN req.telegramUser telegramUser " +
            "WHERE telegramUser.chatId = :chatId AND req.requestStatus = 'ACTIVE'")
    long countUpActiveTasksAmount(@Param("chatId") Long chatId);

}
