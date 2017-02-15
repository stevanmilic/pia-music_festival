/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 *
 * @author stevan
 */
@Entity
@Table(name="registered_user")
@PrimaryKeyJoinColumn(name="ID")
public class RegisteredUser extends User {
    
    @OneToMany(mappedBy = "registeredUser")
    private List<Ticket> tickets;
    
    @OneToMany(mappedBy="registeredUser")
    private List<CommentEvent> comments;
        
    @Column(name="activated")
    private boolean activated = false;

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public List<CommentEvent> getComments() {
        return comments;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public void setComments(List<CommentEvent> comments) {
        this.comments = comments;
    }
}
