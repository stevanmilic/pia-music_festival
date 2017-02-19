package Controllers;

import Entities.Event;
import Controllers.util.JsfUtil;
import Controllers.util.JsfUtil.PersistAction;
import Entities.DetailEvent;
import Entities.util.HibernateUtil;
import Utils.DateHelper;
import Utils.EventFacade;
import Utils.InputHelper;
import java.io.IOException;

import java.io.Serializable;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
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
import javax.servlet.http.HttpSession;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.primefaces.event.FileUploadEvent;

@Named("eventController")
@SessionScoped
public class EventController implements Serializable {

    @EJB
    private Utils.EventFacade ejbFacade;
    private List<Event> items = null;
    private List<Event> filteredItems = null;
    private Event selected;
    private boolean initialized = false;

    public static final int MAX_RESERVATIONS_PER_DAY = 5;
    public static final int MAX_TICKETS_PER_DAY = 50;

    HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

    public EventController() {
        //HibernateUtil.getSessionFactory().openSession();
    }

    public void handleFileUpload(FileUploadEvent fileUploadEvent) {
        try {
            JsfUtil.addSuccessMessage(fileUploadEvent.getFile().getFileName() + " is uploaded.");

            String[] tables = InputHelper.convertStreamToString(fileUploadEvent.getFile().getInputstream()).split("\n\n");

            if (tables.length != 4) {
                JsfUtil.addErrorMessage("Wrong formated csv file, not enough tables!");
            } else {
                boolean isEventCreated = false;

                for (int index = 0; index < tables.length; index++) {
                    Iterable<CSVRecord> records = CSVParser.parse(tables[index].replace("\", ", "\","), CSVFormat.RFC4180.withFirstRecordAsHeader());
                    for (CSVRecord record : records) {
                        switch (index) {
                            case 0:
                                eventCsv(record);
                                break;
                            case 1:
                                priceEventCsv(record);
                                break;
                            case 2:
                                if (!isEventCreated) {
                                    create();
                                    isEventCreated = true;
                                }
                                detailEventCsv(record);
                                break;
                            case 3:
                                socialEventCsv(record);
                                break;
                        }
                    }

                }
                update();
                JsfUtil.addSuccessMessage("Successfully imported csv file!");
            }
        } catch (IOException | ParseException exception) {
            JsfUtil.addErrorMessage(exception, "Wrong formated csv file!");
        } catch (Exception exception) {
            JsfUtil.addErrorMessage(exception, "Wrong formated csv file, " + exception.getMessage());
        }

    }

    private void eventCsv(CSVRecord record) throws ParseException {
        selected.setName(record.get("Festival"));
        selected.setPlace(record.get("Place"));
        selected.setStartDate(DateHelper.getDateFromString(record.get("StartDate")));
        selected.setEndDate(DateHelper.getDateFromString(record.get("EndDate")));
        selected.setMaxReservations(MAX_RESERVATIONS_PER_DAY);
        selected.setMaxTicketsPerDay(MAX_TICKETS_PER_DAY);
    }

    private void priceEventCsv(CSVRecord record) throws Exception {
        switch (record.get("TicketType")) {
            case "per day":
                selected.setPricePerDay(Integer.parseInt(record.get("Price")));
                break;
            case "whole festival":
                selected.setPriceForWhole(Integer.parseInt(record.get("Price")));
                break;
            default:
                throw new Exception("Wrong formated csv file, social network table");
        }
    }

    private void detailEventCsv(CSVRecord record) throws ParseException {
        DetailEvent detailEvent = new DetailEvent();
        detailEvent.setPerformer(record.get("Performer"));
        detailEvent.setStartTime(DateHelper.getTimestampFromString(record.get("StartDate") + " " + record.get("StartTime")));
        detailEvent.setEndTime(DateHelper.getTimestampFromString(record.get("EndDate") + " " + record.get("EndTime")));
        detailEvent.setEvent(selected);
        getFacade().persistDetailEvent(detailEvent);
    }

    private void socialEventCsv(CSVRecord record) throws Exception {
        switch (record.get("Social Network")) {
            case "Facebook":
                selected.setFacebookLink(record.get("Link"));
                break;
            case "Twitter":
                selected.setTwitterLink(record.get("Link"));
                break;
            case "Instagram":
                selected.setInstagramLink(record.get("Link"));
                break;
            case "YouTube":
                selected.setYoutubeLink(record.get("Link"));
                break;
            default:
                throw new Exception("Wrong formated csv file, social network table");
        }
    }

    public List<Event> getFilteredItems() {
        return filteredItems;
    }

    public void setItems(List<Event> items) {
        this.items = items;
    }

    public boolean filterByPrice(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim();
        if (filterText == null || filterText.equals("")) {
            return true;
        }

        if (value == null) {
            return false;
        }

        return ((Comparable) value).compareTo(Integer.valueOf(filterText)) > 0;
    }

    public void setFilteredItems(List<Event> filteredItems) {
        this.filteredItems = filteredItems;
    }

    public Event getSelected() {
        return selected;
    }

    public void setSelected(Event selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private EventFacade getFacade() {
        return ejbFacade;
    }

    public Event prepareCreate() {
        selected = new Event();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("EventCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("EventUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("EventDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Event> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        if (!initialized && session.getAttribute("user") != null
                && session.getAttribute("user").getClass().getSimpleName().equals("RegisteredUser")) {
            items = getFacade().getRecentEvents();
            initialized = true;
        }
        return items;
    }

    public void setDefaultItems() {
        items = getFacade().findAll();
    }

    public void setTopRatedItems() {
        items = getFacade().getTopRatedEvents();
    }

    public void setRecentEvents() {
        items = getFacade().getRecentEvents();
    }

    private void persist(PersistAction persistAction, String successMessage) {
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

    public Event getEvent(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Event> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Event> getItemsAvailableSelectOne() {
        return getFacade().findAll();

    }

    @FacesConverter(forClass = Event.class)
    public static class EventControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EventController controller = (EventController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "eventController");
            return controller.getEvent(getKey(value));
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
            if (object instanceof Event) {
                Event o = (Event) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Event.class.getName()});
                return null;
            }
        }

    }

}
