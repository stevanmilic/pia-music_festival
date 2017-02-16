/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Entities.Event;
import Entities.VideoEvent;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author stevan
 */
@Stateless
public class VideoEventFacade extends AbstractFacade<VideoEvent> {

    @PersistenceContext(unitName = "MusicFestivalParodyPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VideoEventFacade() {
        super(VideoEvent.class);
    }

    public List<VideoEvent> getByEvent(Event event) {
        return em.createQuery("select ve from VideoEvent ve where ve.event = :event")
                .setParameter("event", event).getResultList();
    }

}
