package ca.on.conestogac.model;

import java.io.Serializable;

public class RidePost implements Serializable {

    private int id;
    private String provider;
    private String date;
    private String time;
    private String description;
    private boolean active;

    public RidePost() {
    }

    public RidePost(String provider, String date, String time, String description, boolean active) {
        this.provider = provider;
        this.date = date;
        this.time = time;
        this.description = description;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "RidePost{" +
                "id=" + id +
                ", provider='" + provider + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", description='" + description + '\'' +
                ", active=" + active +
                '}';
    }
}
