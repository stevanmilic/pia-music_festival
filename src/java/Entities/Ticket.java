/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author stevan
 *
 * Note: do a background check(query or list count) for number of reserved
 * tickets for specific user and for number of current tickets for the event.
 *
 */
@Entity
@Table(name = "ticket")
public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String STATUS_BOOKED = "booked";
    public static final String STATUS_SOLD = "sold";
    public static final String STATUS_DISABLED = "disabled";

    public static final String TYPE_ONE_DAY = "one_day";
    public static final String TYPE_WHOLE_TRIP = "whole_trip";

    @EmbeddedId
    TicketId ticketId;

    @ManyToOne
    @PrimaryKeyJoinColumn(name="id_event_ticket", referencedColumnName = "ID")
    private Event event;
    
    @ManyToOne
    @PrimaryKeyJoinColumn(name="id_registered_user_ticket", referencedColumnName = "ID")
    private RegisteredUser registeredUser;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "status", nullable = false)
    private String status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timestamp", nullable = false,
            columnDefinition = "TIMESTAMP default CURRENT_TIMESTAMP")
    private Date timestamp = new Date();

    public void setRegisteredUser(RegisteredUser registeredUser) {
        this.registeredUser = registeredUser;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public RegisteredUser getRegisteredUser() {
        return registeredUser;
    }

    public Event getEvent() {
        return event;
    }

    public TicketId getTicketId() {
        return ticketId;
    }

    public void setTicketId(TicketId ticketId) {
        this.ticketId = ticketId;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "event_day")
    private Date dayEvent;

    public Date getDayEvent() {
        return dayEvent;
    }

    public void setDayEvent(Date dayEvent) {
        this.dayEvent = dayEvent;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.ticketId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Ticket other = (Ticket) obj;
        if (!Objects.equals(this.ticketId, other.ticketId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Ticket{" + "ticketId=" + ticketId + ", type=" + type + ", status=" + status + ", timestamp=" + timestamp + ", dayEvent=" + dayEvent + '}';
    }

}
