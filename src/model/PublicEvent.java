package model;

import java.util.Date;

public class PublicEvent extends Event {
    public PublicEvent(String id, String name, String clientId, Date eventDate, String status,
                       String eventType, int expectedAttendance, String eventConcept,
                       String specialRequest, double basePrice) {
        super(id, name, clientId, eventDate, status, eventType, expectedAttendance,
                eventConcept, specialRequest, basePrice);
    }

    @Override
    public double calculateBudget() {
        return getBasePrice() + (getExpectedAttendance() * 30000) + 2500000;
    }

    @Override
    public String getEventCategory() {
        return "Public";
    }
}
