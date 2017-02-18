/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Entities.Event;
import Entities.RegisteredUser;
import Entities.Ticket;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author stevan
 */
@Stateless
public class RegisteredUserFacade extends AbstractFacade<RegisteredUser> {

    @PersistenceContext(unitName = "MusicFestivalParodyPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RegisteredUserFacade() {
        super(RegisteredUser.class);
    }
    
    public List<RegisteredUser> getLastLoggedInUsers(){
        return em.createQuery("select ru from RegisteredUser ru order by ru.lastLogin desc").setMaxResults(10).getResultList();
    }
    
    public List<Ticket> getTicketsByEvent(Event event){
        return em.createQuery("select t from Ticket t where t.event = :event")
                .setParameter("event", event).getResultList();
    }
}
