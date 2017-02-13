/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 *
 * @author stevan
 */
@Entity
@Table(name="admin")
@PrimaryKeyJoinColumn(name="ID")
public class AdminUser extends User {
    
}
