package Controllers;

import Entities.CommentEvent;
import Controllers.util.JsfUtil;
import Controllers.util.JsfUtil.PersistAction;
import Entities.Event;
import Entities.RegisteredUser;
import Utils.CommentEventFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("commentEventController")
@SessionScoped
public class CommentEventController implements Serializable {

    @EJB
    private Utils.CommentEventFacade ejbFacade;
    private List<CommentEvent> items = null;
    private CommentEvent selected;
    private Event eventSelected;

    public Event getEventSelected() {
        return eventSelected;
    }

    public CommentEventController() {
    }

    public CommentEvent getSelected() {
        return selected;
    }

    public void setSelected(CommentEvent selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private CommentEventFacade getFacade() {
        return ejbFacade;
    }

    public CommentEvent prepareCreate(RegisteredUser registeredUser, Event event) {
        selected = new CommentEvent();
        selected.setEvent(event);
        selected.setRegisteredUser(registeredUser);
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("CommentEventCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("CommentEventUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("CommentEventDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void setEventSelected(Event event) {
        eventSelected = event;
    }

    public List<CommentEvent> getItems() {
        items = getFacade().getByEventName(eventSelected.getName());
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public CommentEvent getCommentEvent(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<CommentEvent> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<CommentEvent> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = CommentEvent.class)
    public static class CommentEventControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CommentEventController controller = (CommentEventController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "commentEventController");
            return controller.getCommentEvent(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof CommentEvent) {
                CommentEvent o = (CommentEvent) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), CommentEvent.class.getName()});
                return null;
            }
        }

    }

}
