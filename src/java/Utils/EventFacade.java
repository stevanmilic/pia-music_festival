/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Entities.CommentEvent;
import Entities.DetailEvent;
import Entities.Event;
import Entities.RegisteredUser;
import Entities.Ticket;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author stevan
 */
@Stateless
public class EventFacade extends AbstractFacade<Event> {

    @PersistenceContext(unitName = "MusicFestivalParodyPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventFacade() {
        super(Event.class);
    }

    public List<Event> getTopRatedEvents() {
        return em.createQuery("select e from Event e order by e.rating desc").setMaxResults(5).getResultList();
    }

    public List<Event> getRecentEvents() {
        Query nativeQuery = em.createNativeQuery("SELECT * FROM event WHERE NOW() < end_date ORDER BY ABS(DATEDIFF(start_date, NOW())) LIMIT 5", Event.class);
        return nativeQuery.getResultList();
    }

    public List<Event> getAllEvents() {
        return em.createQuery("select e from Event e").getResultList();
    }

    public void persistDetailEvent(DetailEvent detailEvent) {
        em.persist(detailEvent);
    }

    public List<Ticket> getTicketsByEvent(Event event) {
        return em.createQuery("select t from Ticket t where t.event = :event")
                .setParameter("event", event).getResultList();
    }

    public List<CommentEvent> getCommentsByUser(RegisteredUser registeredUser) {
        return em.createQuery("select ce from CommentEvent ce where ce.registeredUser = :registeredUser")
                .setParameter("registeredUser", registeredUser).getResultList();
    }

}
