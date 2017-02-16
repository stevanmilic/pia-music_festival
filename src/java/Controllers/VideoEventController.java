package Controllers;

import Entities.VideoEvent;
import Controllers.util.JsfUtil;
import Controllers.util.JsfUtil.PersistAction;
import Entities.Event;
import Entities.ImageEvent;
import Utils.VideoEventFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.primefaces.event.FileUploadEvent;

@Named("videoEventController")
@SessionScoped
public class VideoEventController implements Serializable {

    @EJB
    private Utils.VideoEventFacade ejbFacade;
    private List<VideoEvent> items = null;
    private Event eventSelected;

    public VideoEventController() {
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private VideoEventFacade getFacade() {
        return ejbFacade;
    }

    public void handleFileUpload(FileUploadEvent event) {
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        VideoEvent videoEvent = new VideoEvent();
        videoEvent.setEvent(eventSelected);
        videoEvent.setData(event.getFile().getContents());
        persist(videoEvent, PersistAction.CREATE, message.getSummary());
    }

    public void setEventSelected(Event event) {
        eventSelected = event;
    }

    public List<VideoEvent> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(VideoEvent selected, PersistAction persistAction, String successMessage) {
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

    public VideoEvent getVideoEvent(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<VideoEvent> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<VideoEvent> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = VideoEvent.class)
    public static class VideoEventControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            VideoEventController controller = (VideoEventController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "videoEventController");
            return controller.getVideoEvent(getKey(value));
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
            if (object instanceof VideoEvent) {
                VideoEvent o = (VideoEvent) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), VideoEvent.class.getName()});
                return null;
            }
        }

    }

}
