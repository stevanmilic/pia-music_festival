/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Entities.Event;
import Entities.ImageEvent;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author stevan
 */
@Stateless
public class ImageEventFacade extends AbstractFacade<ImageEvent> {

    @PersistenceContext(unitName = "MusicFestivalParodyPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ImageEventFacade() {
        super(ImageEvent.class);
    }

    public List<ImageEvent> getByEvent(Event event) {
        return em.createQuery("select ie from ImageEvent ie where ie.event = :event")
                .setParameter("event", event).getResultList();
    }

    public List<ImageEvent> getActiveByEvent(Event event) {
        return em.createQuery("select ie from ImageEvent ie where ie.event = :event and ie.activated = true")
                .setParameter("event", event).getResultList();
    }

}
