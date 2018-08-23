package com.imuliar.uzTicketsBot.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import javax.persistence.Table;

/**
 * <p>The entity for transferring the data about requested tickets (ticket searching criteria).</p>
 *
 * @author imuliar
 * @since 1.0
 */
@Entity
@Table(name = "TicketRequest")
public class TicketRequest extends EntityFrame {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_ID",
            foreignKey = @ForeignKey(name = "FK_Request_User"))
    private TelegramUser telegramUser;

    @Column
    private LocalDate departureDate;

    private Station departureStation;

    private Station arrivalStation;

    @Enumerated(EnumType.STRING)
    private TicketRequestStatus requestStatus = TicketRequestStatus.INACTIVE;

    public TicketRequest() {
    }

    public TelegramUser getTelegramUser() {
        return telegramUser;
    }

    public void setTelegramUser(TelegramUser telegramUser) {
        this.telegramUser = telegramUser;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public Station getDepartureStation() {
        return departureStation;
    }

    public void setDepartureStation(Station departureStation) {
        this.departureStation = departureStation;
    }

    public Station getArrivalStation() {
        return arrivalStation;
    }

    public void setArrivalStation(Station arrivalStation) {
        this.arrivalStation = arrivalStation;
    }

    public TicketRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(TicketRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    @Override
    public String toString() {
        return departureStation.getTitle() + " - " + arrivalStation.getTitle() + " [" + departureDate + "]";
    }
}
