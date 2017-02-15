/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.util.JsfUtil;
import Entities.RegisteredUser;
import Entities.User;
import Entities.util.HibernateUtil;
import java.io.IOException;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author stevan
 */
@ManagedBean
@SessionScoped
public class AuthenticationController implements Serializable {

    HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

    @EJB
    private Utils.UserFacade ejbFacade;

    private String username;
    private String password;
    private String newPassword;
    private String confirmNewPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void login() {
        String errorMessage = "Username or Password are incorrect.";
        for (User user : ejbFacade.findAll()) {
            if (user.getUserName().equals(username) && user.getPassword().equals(password)) {
                if (user instanceof RegisteredUser && !((RegisteredUser) user).isActivated()) {
                    errorMessage = "This user is not approved.";
                    break;
                }
                user.setLastLogin(new Date());
                ejbFacade.edit(user);
                session.setAttribute("user", user);
                JsfUtil.addSuccessMessage("Successfull login!");
                break;
            }
        }
        if (errorMessage != null) {
            JsfUtil.addErrorMessage(errorMessage);
        } else {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/MusicFestivalParody/faces/event/List.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(AuthenticationController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void logout() {
        session.invalidate();
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/MusicFestivalParody/faces/event/List.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(AuthenticationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void changePassword() {
        String errorMessage = "Username or Password are incorrect.";
        for (User user : ejbFacade.findAll()) {
            if (user.getUserName().equals(username) && user.getPassword().equals(password)) {
                errorMessage = null;
                JsfUtil.addSuccessMessage("Successfully changed password!");
                user.setPassword(newPassword);
                ejbFacade.edit(user);
                break;
            }
        }
        if (errorMessage != null) {
            JsfUtil.addErrorMessage(errorMessage);
        }
    }

}
