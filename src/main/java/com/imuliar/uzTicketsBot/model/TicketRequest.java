package com.imuliar.uzTicketsBot.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

/**
 * <p>The entity for transferring the data about requested tickets (ticket searching criteria).</p>
 *
 * @author imuliar
 * @since 1.0
 */
@Entity
public class TicketRequest extends EntityFrame {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_ID",
            foreignKey = @ForeignKey(name = "FK_Request_User"))
    private TelegramUser user;

    private LocalDate date;

    private String from;

    private String to;

    @Enumerated(EnumType.STRING)
    private TicketRequestStatus status;

    public TicketRequest() {
    }

    public TicketRequest(LocalDate date, String from, String to) {
        this.date = date;
        this.from = from;
        this.to = to;
        this.status = TicketRequestStatus.ACTIVE;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public TicketRequestStatus getStatus() {
        return status;
    }

    public void setStatus(TicketRequestStatus status) {
        this.status = status;
    }

    public TelegramUser getUser() {
        return user;
    }

    public void setUser(TelegramUser user) {
        this.user = user;
    }
}
