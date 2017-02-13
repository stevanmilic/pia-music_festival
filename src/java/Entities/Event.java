/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Type;

/**
 *
 * @author stevan
 */
@Entity
@Table(name="event")
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name="name", nullable = false)
    private String name;
    
    @Temporal(TemporalType.DATE)
    @Column(name="start_date", nullable = false)
    @Type(type="date")
    private Date startDate;
    
    @Temporal(TemporalType.DATE)
    @Column(name="end_date", nullable = false)
    @Type(type="date")
    private Date endDate;
    
    @Column(name="price_per_day", nullable = false)
    private int pricePerDay;
    
    @Column(name="price_for_whole", nullable = false)
    private int priceForWhole;
    
    @Column(name="max_tickets", nullable = false)
    private int maxTickets;
    
    @Column(name="max_reservations", nullable = false)
    private int maxReservations;
    
    @Column(name="visit_count")
    private int visitCount;
    
    @OneToMany(mappedBy="event")
    private List<CommentEvent> comments;
    
    @OneToMany(mappedBy="event")
    private List<DetailEvent> details;
    
    @OneToMany(mappedBy = "event")
    private List<ImageEvent> images;
    
    @OneToMany(mappedBy = "event")
    private List<VideoEvent> videos;
    
    @OneToMany(mappedBy = "event")
    private List<Ticket> tickets;

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }
    
    

    public int getMaxTickets() {
        return maxTickets;
    }

    public int getMaxReservations() {
        return maxReservations;
    }

    public List<CommentEvent> getComments() {
        return comments;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setMaxTickets(int maxTickets) {
        this.maxTickets = maxTickets;
    }

    public void setMaxReservations(int maxReservations) {
        this.maxReservations = maxReservations;
    }

    public void setComments(List<CommentEvent> comments) {
        this.comments = comments;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    
    public List<DetailEvent> getDetails() {
        return details;
    }

    public List<ImageEvent> getImages() {
        return images;
    }

    public List<VideoEvent> getVideos() {
        return videos;
    }

    public void setDetails(List<DetailEvent> details) {
        this.details = details;
    }

    public void setImages(List<ImageEvent> images) {
        this.images = images;
    }

    public void setVideos(List<VideoEvent> videos) {
        this.videos = videos;
    }
    

    public String getName() {
        return name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getPricePerDay() {
        return pricePerDay;
    }

    public int getPriceForWhole() {
        return priceForWhole;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setPricePerDay(int pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public void setPriceForWhole(int priceForWhole) {
        this.priceForWhole = priceForWhole;
    }
    
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Event)) {
            return false;
        }
        Event other = (Event) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Festival[ id=" + id + " ]";
    }
    
}
