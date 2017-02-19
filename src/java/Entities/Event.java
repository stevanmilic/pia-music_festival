/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
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
    private int maxTicketsPerDay;
    
    @Column(name="max_reservations", nullable = false)
    private int maxReservations;
    
    @Column(name="place", nullable = false)
    private String place;
    
    @Column(name="visit_count")
    private int visitCount = 0;
    
    @Column(name="rating")
    private int rating = 0;
    
    @Column(name="link_facebook")
    private String facebookLink;
    
    @Column(name="link_twitter")
    private String twitterLink;
    
    @Column(name="link_instagram")
    private String instagramLink;
    
    @Column(name="link_youtube")
    private String youtubeLink;
    
    

    public String getFacebookLink() {
        return facebookLink;
    }

    public String getTwitterLink() {
        return twitterLink;
    }

    public String getInstagramLink() {
        return instagramLink;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public void setTwitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
    }

    public void setInstagramLink(String instagramLink) {
        this.instagramLink = instagramLink;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }
    
    @OneToMany(mappedBy="event", cascade=CascadeType.REMOVE)
    private List<CommentEvent> comments;
    
    @OneToMany(mappedBy="event", cascade=CascadeType.REMOVE)
    private List<DetailEvent> details;
    
    @OneToMany(mappedBy = "event", cascade=CascadeType.REMOVE)
    private List<ImageEvent> images;
    
    @OneToMany(mappedBy = "event", cascade=CascadeType.REMOVE)
    private List<VideoEvent> videos;
    
    @OneToMany(mappedBy = "event", cascade=CascadeType.REMOVE)
    private List<Ticket> tickets;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getMaxTicketsPerDay() {
        return maxTicketsPerDay;
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

    public void setMaxTicketsPerDay(int maxTickets) {
        this.maxTicketsPerDay = maxTickets;
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
