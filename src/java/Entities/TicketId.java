/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author stevan
 */
@Embeddable
public class TicketId implements Serializable {

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

}
