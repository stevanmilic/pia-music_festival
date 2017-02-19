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
import javax.servlet.http.HttpSession;

@Named("ticketController")
@SessionScoped
public class TicketController implements Serializable {

    static final int DEADLINE_FOR_BOOKING = 2;
    static final TimeUnit DEADLINE_TIMEUNIT = TimeUnit.DAYS;

    @EJB
    private Utils.TicketFacade ejbFacade;
    private List<Ticket> items = null;
    private Ticket selected;

    HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

    public TicketController() {
    }

    public boolean isEditDisabled() {
        if (selected == null) {
            return true;
        }
        return !selected.getStatus().equals(Ticket.STATUS_BOOKED);
    }

    public void updateTickets() {
        for (Ticket ticket : items) {
            if (ticket.getStatus().equals(Ticket.STATUS_BOOKED) && isDisabled(ticket)) {
                JsfUtil.addWarningMessage(ticket.toString() + " has been Disabled!");
            }
        }
    }

    public boolean isDisabled(Ticket ticket) {
        if (ticket.getStatus().equals(Ticket.STATUS_DISABLED)) {
            return true;
        }
        long difference = DateHelper.getDateDiff(ticket.getTicketId().getTimestamp(),
                new Date(), DEADLINE_TIMEUNIT);
        if (difference >= DEADLINE_FOR_BOOKING) {
            ticket.setStatus(Ticket.STATUS_DISABLED);
            getFacade().edit(ticket);
            return true;
        }
        return false;
    }

    public boolean isMaxBooked() {
        long reservedCount;

        if (selected.getType().equals(Ticket.TYPE_ONE_DAY)) {
            reservedCount = 1;
        } else {
            reservedCount = DateHelper.getDateDiff(selected.getEvent().getStartDate(), selected.getEvent().getEndDate(), TimeUnit.DAYS);
        }

        for (Ticket ticket : getFacade().findAll()) {
            if (Objects.equals(ticket.getRegisteredUser().getId(), selected.getRegisteredUser().getId())
                    && Objects.equals(ticket.getEvent().getId(), selected.getEvent().getId())
                    && ticket.getStatus().equals(Ticket.STATUS_BOOKED)) {
                if (ticket.getType().equals(Ticket.TYPE_ONE_DAY)) {
                    reservedCount++;
                } else {
                    reservedCount += DateHelper.getDateDiff(selected.getEvent().getStartDate(), selected.getEvent().getEndDate(), TimeUnit.DAYS);
                }
                if (reservedCount > selected.getEvent().getMaxReservations()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isMaxTicketsSoldByDate(Date date) {
        int ticketsSold = 0;
        for (Ticket ticket : getFacade().findAll()) {
            if (Objects.equals(ticket.getEvent().getId(), selected.getEvent().getId())
                    && ticket.getStatus().equals(Ticket.STATUS_SOLD)
                    && ((ticket.getType().equals(Ticket.TYPE_ONE_DAY) && ticket.getDayEvent().compareTo(date) == 0)
                    || ticket.getType().equals(Ticket.TYPE_WHOLE_TRIP))) {
                ticketsSold++;
                if (ticketsSold >= (selected.getEvent().getMaxTicketsPerDay())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isMaxTicketsSoldByTrip() {
        Calendar start = Calendar.getInstance();
        start.setTime(selected.getEvent().getStartDate());
        Calendar end = Calendar.getInstance();
        end.setTime(selected.getEvent().getEndDate());

        for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
            if (isMaxTicketsSoldByDate(date)) {
                return true;
            }
        }

        return false;
    }

    public Ticket getSelected() {
        return selected;
    }

    public void setSelected(Ticket selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {

    }

    protected void initializeEmbeddableKey(long eventId, long userId) {
        TicketId ticketId = new TicketId();
        ticketId.setEventId(eventId);
        ticketId.setUserId(userId);
        selected.setTicketId(ticketId);
    }

    private TicketFacade getFacade() {
        return ejbFacade;
    }

    public Ticket prepareCreate(RegisteredUser registeredUser, Event event) {
        selected = new Ticket();
        selected.setStatus(Ticket.STATUS_BOOKED);
        selected.setRegisteredUser(registeredUser);
        selected.setEvent(event);
        initializeEmbeddableKey(event.getId(), registeredUser.getId());
        return selected;
    }

    public void create() {
        //TO DO: implement validator for this ifs....
        if (isMaxBooked()) {
            JsfUtil.addErrorMessage("Maximum tickets booked reached(" + selected.getEvent().getMaxReservations() + ")");
        } else {
            if (selected.getType().equals(Ticket.TYPE_ONE_DAY)) {
                if (isMaxTicketsSoldByDate(selected.getDayEvent())) {
                    JsfUtil.addErrorMessage("Tickets have been sold for this Date(" + DateHelper.getFormatedDate(selected.getDayEvent()) + ")");
                } else {
                    persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("TicketCreated"));

                    JsfUtil.addSuccessMessage("Successfully bought ticket for one day(" + selected.getEvent().getName() + ")");
                    JsfUtil.addSuccessMessage("Amount to pay: " + selected.getEvent().getPricePerDay());
                }
            } else {
                if (isMaxTicketsSoldByTrip()) {
                    JsfUtil.addErrorMessage("Tickets have been sold on this Date("
                            + DateHelper.getFormatedDate(selected.getDayEvent()) + ")."
                            + "\nTry buying One day Ticket");
                } else {
                    persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("TicketCreated"));
                    JsfUtil.addSuccessMessage("Successfully bought tickets for whole trip(" + selected.getEvent().getName() + ")");
                    JsfUtil.addSuccessMessage("Amount to pay: " + selected.getEvent().getPriceForWhole());
                }
            }
        }
    }

    public void update() {
        if (isDisabled(selected)) {
            JsfUtil.addErrorMessage("Ticket is disabled due to the Booking Deadline.");
        } else {
            persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("TicketUpdated"));
        }
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("TicketDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Ticket> getItems() {
        if (session.getAttribute("user") != null
                && session.getAttribute("user").getClass().getSimpleName().equals("RegisteredUser")) {
            items = getFacade().getTicketsByRegisteredUser((RegisteredUser) session.getAttribute("user"));
        } else {
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
