package com.imuliar.uzTicketsBot.model;

import com.imuliar.uzTicketsBot.services.states.Station;
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

    private Station from;

    private Station to;

    @Enumerated(EnumType.STRING)
    private TicketRequestStatus status ;

    public TicketRequest() {
    }

    public TicketRequest(LocalDate date, Station from, Station to) {
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

    public Station getFrom() {
        return from;
    }

    public void setFrom(Station from) {
        this.from = from;
    }

    public Station getTo() {
        return to;
    }

    public void setTo(Station to) {
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

    @Override
    public String toString() {
        return "TicketRequest{" +
                "user=" + user +
                ", date=" + date +
                ", from=" + from +
                ", to=" + to +
                ", status=" + status +
                "} " + super.toString();
    }
}
