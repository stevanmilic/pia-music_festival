package Controllers;

import Entities.ImageEvent;
import Controllers.util.JsfUtil;
import Controllers.util.JsfUtil.PersistAction;
import Entities.Event;
import Utils.ImageEventFacade;
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

@Named("imageEventController")
@SessionScoped
public class ImageEventController implements Serializable {

    @EJB
    private Utils.ImageEventFacade ejbFacade;
    private List<ImageEvent> items = null;
    private Event eventSelected;
    private ImageEvent selected;

    HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

    public ImageEventController() {
    }

    public Event getEventSelected() {
        return eventSelected;
    }

    public void setSelected(ImageEvent selected) {
        this.selected = selected;
    }

    public ImageEvent getSelected() {
        return selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ImageEventFacade getFacade() {
        return ejbFacade;
    }

    public boolean isEditDisabled() {
        if (selected == null) {
            return true;
        }
        return selected.isActivated();
    }

    public StreamedContent getImage() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
            // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
            String itemId = context.getExternalContext().getRequestParameterMap().get("itemId");
            ImageEvent imageEvent = getFacade().find(Long.valueOf(itemId));
            return new DefaultStreamedContent(new ByteArrayInputStream(imageEvent.getData()));
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        ImageEvent imageEvent = new ImageEvent();
        imageEvent.setEvent(eventSelected);
        imageEvent.setData(event.getFile().getContents());
        if (session.getAttribute("user").getClass().getSimpleName().equals("RegisteredUser")) {
            imageEvent.setActivated(false);
        } else {
            imageEvent.setActivated(true);
        }
        persist(imageEvent, PersistAction.CREATE, message.getSummary());
        items = null;
    }

    public void setEventSelected(Event event) {
        eventSelected = event;
    }

    public List<ImageEvent> getItems() {
        if (session.getAttribute("user") != null
                && session.getAttribute("user").getClass().getSimpleName().equals("RegisteredUser")) {
            items = getFacade().getActiveByEvent(eventSelected);

        } else {
            items = getFacade().getByEvent(eventSelected);
        }

        return items;
    }

    public void update() {
        persist(selected, PersistAction.UPDATE, "Image status updated!");
    }

    public void destroy() {
        persist(selected, PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("EventDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    private void persist(ImageEvent selected, PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction == PersistAction.UPDATE) {
                    getFacade().edit(selected);
                } else if (persistAction == PersistAction.CREATE) {
                    getFacade().create(selected);
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

    public ImageEvent getImageEvent(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<ImageEvent> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<ImageEvent> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = ImageEvent.class)
    public static class ImageEventControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ImageEventController controller = (ImageEventController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "imageEventController");
            return controller.getImageEvent(getKey(value));
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
            if (object instanceof ImageEvent) {
                ImageEvent o = (ImageEvent) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), ImageEvent.class.getName()});
                return null;
            }
        }

    }

}
