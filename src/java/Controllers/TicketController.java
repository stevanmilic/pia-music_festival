package Controllers;

import Entities.Ticket;
import Controllers.util.JsfUtil;
import Controllers.util.JsfUtil.PersistAction;
import Entities.Event;
import Entities.RegisteredUser;
import Entities.TicketId;
import Utils.DateHelper;
import Utils.TicketFacade;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
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

@Named("ticketController")
@SessionScoped
public class TicketController implements Serializable {

    @EJB
    private Utils.TicketFacade ejbFacade;
    private List<Ticket> items = null;
    private Ticket selected;

    public TicketController() {
    }

    public boolean isMaxBooked(RegisteredUser registeredUser, Event event, String type) {
        long reservedCount;

        if (type.equals(Ticket.TYPE_ONE_DAY)) {
            reservedCount = 1;
        } else {
            reservedCount = DateHelper.getDateDiff(event.getStartDate(), event.getEndDate(), TimeUnit.DAYS);
        }

        for (Ticket ticket : getItems()) {
            if (ticket.getRegisteredUser() == registeredUser && ticket.getEvent() == event
                    && ticket.getStatus().equals(Ticket.STATUS_BOOKED)) {
                if (ticket.getType().equals(Ticket.TYPE_ONE_DAY)) {
                    reservedCount++;
                } else {
                    reservedCount += DateHelper.getDateDiff(event.getStartDate(), event.getEndDate(), TimeUnit.DAYS);
                }
                if (reservedCount > event.getMaxReservations()) {
                    JsfUtil.addErrorMessage("Maximum tickets booked reached(" + event.getMaxReservations() + ")");
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isMaxTicketsSoldByDate(Event event, Date date) {
        int ticketsSold = 0;
        for (Ticket ticket : getItems()) {
            if (ticket.getEvent() == event && ticket.getStatus().equals(Ticket.STATUS_SOLD)
                    && ((ticket.getType().equals(Ticket.TYPE_ONE_DAY) && ticket.getDayEvent().compareTo(date) == 0)
                    || ticket.getType().equals(Ticket.TYPE_WHOLE_TRIP))) {
                ticketsSold++;
                if (ticketsSold >= (event.getMaxTicketsPerDay())) {
                    JsfUtil.addErrorMessage("Tickets have been sold for this Date(" + DateHelper.getFormatedDate(date) + ")");
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isMaxTicketsSoldByTrip(Event event) {
        Calendar start = Calendar.getInstance();
        start.setTime(event.getStartDate());
        Calendar end = Calendar.getInstance();
        end.setTime(event.getEndDate());

        for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
            if (isMaxTicketsSoldByDate(event, date)) {
                return true;
            }
        }

        return false;
    }

    public void bookOneDayTicket(RegisteredUser registeredUser, Event event, Date date) {
        Ticket ticket = new Ticket();
        TicketId ticketId = new TicketId();
        ticketId.setEventId(event.getId());
        ticketId.setUserId(registeredUser.getId());
        ticket.setTicketId(ticketId);
        ticket.setDayEvent(date);
        ticket.setType(Ticket.TYPE_ONE_DAY);
        ticket.setStatus(Ticket.STATUS_BOOKED);
        getFacade().create(ticket);
    }

    public void bookWholeTripTicket(RegisteredUser registeredUser, Event event) {
        if (!isMaxBooked(registeredUser, event, Ticket.TYPE_WHOLE_TRIP) && !isMaxTicketsSoldByTrip(event)) {
            Ticket ticket = new Ticket();
            TicketId ticketId = new TicketId();
            ticketId.setEventId(event.getId());
            ticketId.setUserId(registeredUser.getId());
            ticket.setTicketId(ticketId);
            ticket.setType(Ticket.TYPE_WHOLE_TRIP);
            ticket.setStatus(Ticket.STATUS_BOOKED);
            getFacade().create(ticket);
            JsfUtil.addSuccessMessage("Successfully bought tickets for whole trip(" + event.getName() + ")");
            JsfUtil.addSuccessMessage("Amount to pay: " + event.getPriceForWhole());
        }
    }

    public Ticket getSelected() {
        return selected;
    }

    public void setSelected(Ticket selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TicketFacade getFacade() {
        return ejbFacade;
    }

    public Ticket prepareCreate() {
        selected = new Ticket();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("TicketCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("TicketUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("TicketDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Ticket> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
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

    public Ticket getTicket(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Ticket> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Ticket> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Ticket.class)
    public static class TicketControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TicketController controller = (TicketController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "ticketController");
            return controller.getTicket(getKey(value));
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
            if (object instanceof Ticket) {
                Ticket o = (Ticket) object;
                return getStringKey(o.getTicketId().getEventId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Ticket.class.getName()});
                return null;
            }
        }

    }

}
