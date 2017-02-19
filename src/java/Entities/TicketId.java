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
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author stevan
 */
@Embeddable
public class TicketId implements Serializable {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timestamp", nullable = false,
            columnDefinition = "TIMESTAMP default CURRENT_TIMESTAMP")
    private Date timestamp = new Date();

    @Column(name = "id_event_ticket")
    private long eventId;

    @Column(name = "id_registered_user_ticket")
    private long userId;

    public long getEventId() {
        return eventId;
    }

    public long getUserId() {
        return userId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TicketId{" + "timestamp=" + timestamp + ", eventId=" + eventId + ", userId=" + userId + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.timestamp);
        hash = 59 * hash + (int) (this.eventId ^ (this.eventId >>> 32));
        hash = 59 * hash + (int) (this.userId ^ (this.userId >>> 32));
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
        final TicketId other = (TicketId) obj;
        if (this.eventId != other.eventId) {
            return false;
        }
        if (this.userId != other.userId) {
            return false;
        }
        if (!Objects.equals(this.timestamp, other.timestamp)) {
            return false;
        }
        return true;
    }
    
    

}
