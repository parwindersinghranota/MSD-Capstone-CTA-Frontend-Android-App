package ca.on.conestogac.model;

import java.sql.Timestamp;

public class Message {
    private int id;
    private String messageFrom;
    private String messageTo;
    private int rideId;
    private Timestamp timeStamp;
    private String message;
    private String messageType;
    private int rideConfirm;

    public Message() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    public String getMessageTo() {
        return messageTo;
    }

    public void setMessageTo(String messageTo) {
        this.messageTo = messageTo;
    }

    public int getRideId() {
        return rideId;
    }

    public void setRideId(int rideId) {
        this.rideId = rideId;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public int getRideConfirm() {
        return rideConfirm;
    }

    public void setRideConfirm(int rideConfirm) {
        this.rideConfirm = rideConfirm;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", messageFrom='" + messageFrom + '\'' +
                ", messageTo='" + messageTo + '\'' +
                ", rideId=" + rideId +
                ", timeStamp=" + timeStamp +
                ", message='" + message + '\'' +
                ", messageType='" + messageType + '\'' +
                '}';
    }
}
