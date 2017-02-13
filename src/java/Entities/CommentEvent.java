/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Type;

/**
 *
 * @author stevan
 */
@Entity
@Table(name="event_comment")
public class CommentEvent implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @Column(name="data_text")
    @Type(type="text")
    private String text;
    
    @ManyToOne
    @JoinColumn(name="fk_registered_user_comment")
    private RegisteredUser registeredUser;
    
    @ManyToOne
    @JoinColumn(name="fk_event_comment")
    private Event event;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public RegisteredUser getRegisteredUser() {
        return registeredUser;
    }

    public String getText() {
        return text;
    }

    public Event getEvent() {
        return event;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setRegisteredUser(RegisteredUser registeredUser) {
        this.registeredUser = registeredUser;
    }

    public void setEvent(Event event) {
        this.event = event;
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
        if (!(object instanceof CommentEvent)) {
            return false;
        }
        CommentEvent other = (CommentEvent) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.CommentEvent[ id=" + id + " ]";
    }
    
}
