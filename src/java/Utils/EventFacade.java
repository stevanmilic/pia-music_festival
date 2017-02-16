/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Entities.Event;
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
    
    public List<Event> getTopRatedEvents(){
        return em.createQuery("select e from Event e order by e.rating desc").setMaxResults(5).getResultList();
    }
    
    public List<Event> getMostRecentEvents(){
        Query query = em.createNativeQuery("SELECT * FROM event WHERE NOW() < end_date ORDER BY ABS(DATEDIFF(start_date, NOW())) LIMIT 5", Event.class);
        return query.getResultList();
    }
    
}
