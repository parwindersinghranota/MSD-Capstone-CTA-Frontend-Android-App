package ca.on.conestogac.model;


import java.sql.Timestamp;


public class Feedback {
    private int id;
    private String user;
    private String driver;
    private String message;
    private double rating;
    private Timestamp dateTime;

    public Feedback() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", user='" + user + '\'' +
                ", driver='" + driver + '\'' +
                ", message='" + message + '\'' +
                ", rating=" + rating +
                ", dateTime=" + dateTime +
                '}';
    }
}

