package model;

import java.util.Date;

public abstract class Event {
    private String id;
    private String name;
    private String clientId; //seharusnya sinkron ama tipe data ID di Client.java
    private Date eventDate;
    private double basePrice;

    public Event(String id, String name, String clientId, Date eventDate, double basePrice) {
        this.id = id;
        this.name = name;
        this.clientId = clientId;
        this.eventDate = eventDate;
        this.basePrice = basePrice;
    }

    //abstract method: wajib diimplementasikan untuk setiap jenis event
    public abstract double calculateBudget();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
}