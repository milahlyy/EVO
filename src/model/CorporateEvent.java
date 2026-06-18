package model;

import java.util.Date;

public class CorporateEvent extends Event {
    public CorporateEvent(String id, String name, String clientId, Date eventDate, String status,
                          String eventType, int expectedAttendance, String eventConcept,
                          String specialRequest, double basePrice) {
        super(id, name, clientId, eventDate, status, eventType, expectedAttendance,
                eventConcept, specialRequest, basePrice);
    }

    @Override
    public double calculateBudget() {
        return getBasePrice() + (getExpectedAttendance() * 20000) + 1000000;
    }

    @Override
    public String getEventCategory() {
        return "Corporate";
    }
}
