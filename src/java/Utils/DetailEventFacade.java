/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Entities.DetailEvent;
import Entities.Event;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author stevan
 */
@Stateless
public class DetailEventFacade extends AbstractFacade<DetailEvent> {

    @PersistenceContext(unitName = "MusicFestivalParodyPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DetailEventFacade() {
        super(DetailEvent.class);
    }
    
    public List<DetailEvent> getByEvent(Event event){
        return em.createQuery("select de from DetailEvent de where de.event = :event")
                .setParameter("event", event).getResultList();
    }
}
