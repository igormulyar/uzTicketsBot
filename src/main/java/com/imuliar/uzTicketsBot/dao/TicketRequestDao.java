package com.imuliar.uzTicketsBot.dao;

import com.imuliar.uzTicketsBot.model.TicketRequest;
import com.imuliar.uzTicketsBot.model.TicketRequestStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author imuliar
 * 19.08.2018
 */
@Repository
public interface TicketRequestDao extends JpaRepository<TicketRequest, Long> {

    @Query("SELECT req FROM TicketRequest req " +
            "JOIN req.telegramUser telegramUser " +
            "WHERE telegramUser.chatId = :chatId AND req.requestStatus = 'ACTIVE'")
    List<TicketRequest> findByChatId(@Param("chatId") Long chatId);

    List<TicketRequest> findByRequestStatus(TicketRequestStatus ticketRequestStatus);
}
