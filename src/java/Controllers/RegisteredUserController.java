package Controllers;

import Entities.RegisteredUser;
import Controllers.util.JsfUtil;
import Controllers.util.JsfUtil.PersistAction;
import Entities.Event;
import Entities.Ticket;
import Utils.DateHelper;
import Utils.RegisteredUserFacade;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

@Named("registeredUserController")
@SessionScoped
public class RegisteredUserController implements Serializable {

    @EJB
    private Utils.RegisteredUserFacade ejbFacade;
    private List<RegisteredUser> items = null;
    private RegisteredUser selected;
    private String confirmPassword;

    HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

    public void onDeleteEventAddMessages(Event event) {
        addMessages(event, event.getName() + " event has been canceled.",
                event.getName() + " event has been canceled."
                + "\nYou can get your money at the Box office");
    }

    public void onEditEventAddMessages(Event event) {
        addMessages(event, event.getName() + " details has been changed, check your booking.",
                event.getName() + " details has been changed, check with info with box office.");
    }

    private void addMessages(Event event, String messageForBooked, String messageForSold) {
        Date now = new Date();
        if (event.getEndDate().after(now)) {
            for (Ticket ticket : getFacade().getTicketsByEvent(event)) {
                if (ticket.getType().equals(Ticket.TYPE_ONE_DAY)
                        && ticket.getDayEvent().before(now)) {
                    continue;
                }
                if (ticket.getStatus().equals(Ticket.STATUS_BOOKED)) {
                    addMessage(ticket.getRegisteredUser(), messageForBooked);
                } else if (ticket.getStatus().equals(Ticket.STATUS_SOLD)) {
                    addMessage(ticket.getRegisteredUser(), messageForSold);
                }
            }
        }
    }

    public void addMessage(RegisteredUser registeredUser, String message) {
        registeredUser.getMessages().add(message);
        getFacade().edit(registeredUser);
    }

    public void displayAndRemoveMessages() {
        if (session.getAttribute("user") != null
                && session.getAttribute("user").getClass().getSimpleName().equals("RegisteredUser")) {
            RegisteredUser registeredUser = (RegisteredUser) session.getAttribute("user");
            JsfUtil.addWarningMessage(registeredUser.getMessages());
            registeredUser.getMessages().clear();
            getFacade().edit(registeredUser);
        }
    }

    public void updateTicketsStatus() {
        if (session.getAttribute("user") != null
                && session.getAttribute("user").getClass().getSimpleName().equals("RegisteredUser")) {
            RegisteredUser registeredUser = (RegisteredUser) session.getAttribute("user");
            if (isDisabled(registeredUser)) {
                registeredUser.setActivated(false);
                getFacade().edit(registeredUser);
                JsfUtil.addWarningMessage("Reached three disabled booking, "
                        + "this account is deactivated.");
                session.invalidate();
            }
        } else if (session.getAttribute("user") != null
                && session.getAttribute("user").getClass().getSimpleName().equals("AdminUser")) {
            for (RegisteredUser registeredUser : getFacade().findAll()) {
                if (registeredUser.isActivated() && isDisabled(registeredUser)) {
                    registeredUser.setActivated(false);
                    getFacade().edit(registeredUser);
                    JsfUtil.addSuccessMessage("User(" + registeredUser.getUserName() + ")" 
                    + "is deactivated!");
                }
            }
        }

    }

    public boolean isDisabled(RegisteredUser registeredUser) {
        int ticketsDisabled = 0;
        for (Ticket ticket : getFacade().getTicketsByRegisteredUser(registeredUser)) {
            if (ticket.getStatus().equals(Ticket.STATUS_BOOKED)) {
                long difference = DateHelper.getDateDiff(ticket.getTicketId().getTimestamp(),
                        new Date(), TicketController.DEADLINE_TIMEUNIT);
                if (difference >= TicketController.DEADLINE_FOR_BOOKING) {
                    getFacade().setTicketStatus(ticket, Ticket.STATUS_DISABLED);
                    ticketsDisabled++;
                }
            } else if (ticket.getStatus().equals(Ticket.STATUS_DISABLED)) {
                ticketsDisabled++;
            }
            if (ticketsDisabled >= 3) {
                return true;
            }
        }
        return false;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public RegisteredUserController() {
    }

    public RegisteredUser getSelected() {
        return selected;
    }

    public void setSelected(RegisteredUser selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private RegisteredUserFacade getFacade() {
        return ejbFacade;
    }

    public RegisteredUser prepareCreate() {
        selected = new RegisteredUser();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("RegisteredUserCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("RegisteredUserUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("RegisteredUserDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<RegisteredUser> getItems() {
        //if (items == null) {
            items = getFacade().findAll();
        //}
        return items;
    }

    public void setDefaultItems() {
        items = getFacade().findAll();
    }

    public void setLastItems() {
        items = getFacade().getLastLoggedInUsers();
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

    public RegisteredUser getRegisteredUser(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<RegisteredUser> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<RegisteredUser> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = RegisteredUser.class)
    public static class RegisteredUserControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RegisteredUserController controller = (RegisteredUserController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "registeredUserController");
            return controller.getRegisteredUser(getKey(value));
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
            if (object instanceof RegisteredUser) {
                RegisteredUser o = (RegisteredUser) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), RegisteredUser.class.getName()});
                return null;
            }
        }

    }

}
