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

    List<TicketRequest> findByRequestStatus(TicketRequestStatus ticketRequestStatus);

    @Query("SELECT req FROM TicketRequest req " +
            "JOIN req.telegramUser telegramUser " +
            "WHERE telegramUser.chatId = :chatId AND req.requestStatus = 'ACTIVE'")
    List<TicketRequest> findActiveByChatId(@Param("chatId") Long chatId);

    @Modifying
    @Query("UPDATE TicketRequest req SET req.requestStatus = 'INACTIVE' WHERE req.id = :id")
    void markInactive(@Param("id") Long id);

    @Query("SELECT COUNT(req.id) FROM TicketRequest req " +
            "JOIN req.telegramUser telegramUser " +
            "WHERE telegramUser.chatId = :chatId AND req.requestStatus = 'ACTIVE'")
    long countUpActiveTasksAmount(@Param("chatId") Long chatId);
}
