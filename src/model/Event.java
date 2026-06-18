package model;

import java.util.Date;

public abstract class Event {
    private String id;
    private String name;
    private String clientId;
    private Date eventDate;
    private String status;
    private String eventType;
    private int expectedAttendance;
    private String eventConcept;
    private String specialRequest;
    private double basePrice;

    public Event(String id, String name, String clientId, Date eventDate, String status,
                 String eventType, int expectedAttendance, String eventConcept,
                 String specialRequest, double basePrice) {
        this.id = id;
        this.name = name;
        this.clientId = clientId;
        this.eventDate = eventDate;
        this.status = status;
        this.eventType = eventType;
        this.expectedAttendance = expectedAttendance;
        this.eventConcept = eventConcept;
        this.specialRequest = specialRequest;
        this.basePrice = basePrice;
    }

    public abstract double calculateBudget();

    public abstract String getEventCategory();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public int getExpectedAttendance() { return expectedAttendance; }
    public void setExpectedAttendance(int expectedAttendance) { this.expectedAttendance = expectedAttendance; }

    public String getEventConcept() { return eventConcept; }
    public void setEventConcept(String eventConcept) { this.eventConcept = eventConcept; }

    public String getSpecialRequest() { return specialRequest; }
    public void setSpecialRequest(String specialRequest) { this.specialRequest = specialRequest; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
}
