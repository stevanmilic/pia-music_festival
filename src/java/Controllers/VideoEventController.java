package Controllers;

import Entities.VideoEvent;
import Controllers.util.JsfUtil;
import Controllers.util.JsfUtil.PersistAction;
import Entities.Event;
import Entities.ImageEvent;
import Utils.VideoEventFacade;
import java.io.ByteArrayInputStream;
import java.io.IOException;

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
import javax.faces.event.PhaseId;
import javax.servlet.http.HttpSession;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named("videoEventController")
@SessionScoped
public class VideoEventController implements Serializable {

    @EJB
    private Utils.VideoEventFacade ejbFacade;
    private List<VideoEvent> items = null;
    private Event eventSelected;
    private VideoEvent selected;

    HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

    public VideoEvent getSelected() {
        return selected;
    }

    public void setSelected(VideoEvent selected) {
        this.selected = selected;
    }

    public Event getEventSelected() {
        return eventSelected;
    }

    public VideoEventController() {
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private VideoEventFacade getFacade() {
        return ejbFacade;
    }

    public boolean isEditDisabled() {
        if (selected == null) {
            return true;
        }
        return selected.isActivated();
    }

    public StreamedContent getVideo() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
            // So, browser is requesting the video. Return a real StreamedContent with the video bytes.
            String itemId = context.getExternalContext().getRequestParameterMap().get("itemId");
            VideoEvent videoEvent = getFacade().find(Long.valueOf(itemId));
            return new DefaultStreamedContent(new ByteArrayInputStream(videoEvent.getData()), "video/quicktime");
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        VideoEvent videoEvent = new VideoEvent();
        videoEvent.setEvent(eventSelected);
        videoEvent.setData(event.getFile().getContents());
        if (session.getAttribute("user").getClass().getSimpleName().equals("RegisteredUser")) {
            videoEvent.setActivated(false);
        } else {
            videoEvent.setActivated(true);
        }
        persist(videoEvent, PersistAction.CREATE, message.getSummary());
        items = null;
    }

    public void setEventSelected(Event event) {
        eventSelected = event;
    }

    public List<VideoEvent> getItems() {
        if (session.getAttribute("user") != null
                && session.getAttribute("user").getClass().getSimpleName().equals("RegisteredUser")) {
            items = getFacade().getActiveByEvent(eventSelected);

        } else {
            items = getFacade().getByEvent(eventSelected);
        }
        return items;
    }

    public void destroy() {
        persist(selected, PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("EventDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(selected, PersistAction.UPDATE, "Video status is updated!");
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
