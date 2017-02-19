/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Entities.CommentEvent;
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
public class CommentEventFacade extends AbstractFacade<CommentEvent> {

    @PersistenceContext(unitName = "MusicFestivalParodyPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CommentEventFacade() {
        super(CommentEvent.class);
    }

    public List<CommentEvent> getByEventName(String eventName) {
        return em.createQuery("select ce from CommentEvent ce where ce.event.name = :eventName")
                .setParameter("eventName", eventName).getResultList();
    }

}
