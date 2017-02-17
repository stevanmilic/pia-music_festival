/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "type", nullable = false)
    //possible values
    //oneday - for one day
    //wholetrip - for whole trip :D
    private String type;

    @Column(name = "status", nullable = false)
    //possible values
    //Reserved - user has reserved the ticket
    //Sold - admin has approved the purchase
    //Disabled - admin has not approved the purchase for two days! omg stupid
    private String status;

    @ManyToOne
    @JoinColumn(name = "fk_registered_user_ticket")
    private RegisteredUser registeredUser;

    @ManyToOne
    @JoinColumn(name = "fk_event_ticket")
    private Event event;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timestamp", nullable = false,
            columnDefinition = "TIMESTAMP default CURRENT_TIMESTAMP")
    private Date timestamp = new Date();

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
    
    

    public RegisteredUser getRegisteredUser() {
        return registeredUser;
    }

    public Event getEvent() {
        return event;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setRegisteredUser(RegisteredUser registeredUser) {
        this.registeredUser = registeredUser;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ticket)) {
            return false;
        }
        Ticket other = (Ticket) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Ticket[ id=" + id + " ]";
    }

}
