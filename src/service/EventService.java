package service;

import exception.EventException;
import exception.ValidationException;
import exception.VendorException;
import model.CorporateEvent;
import model.Event;
import model.PersonalEvent;
import model.PublicEvent;
import model.Vendor;
import storage.EventStorage;
import util.IdGenerator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventService {
    private final EventStorage storage;
    private final ClientService clientService;
    private final VendorService vendorService;
    private List<Event> eventList;

    public EventService() {
        this.storage = new EventStorage();
        this.clientService = new ClientService();
        this.vendorService = new VendorService();
        this.eventList = storage.loadEvents(); 
    }

    public List<Event> getAllEvents() {
        return new ArrayList<>(eventList);
    }

    public Event getEventById(String id) {
        for (Event event : eventList) {
            if (event.getId().equals(id)) {
                return event;
            }
        }
        return null;
    }

    public String generateNextEventId() {
        List<String> ids = new ArrayList<>();
        for (Event event : eventList) {
            ids.add(event.getId());
        }
        return IdGenerator.generateNextId("EVT", ids);
    }

    public void addEvent(Event newEvent) throws EventException {
        validateEvent(newEvent);

        if (getEventById(newEvent.getId()) != null) {
            throw new EventException("Gagal! Event dengan ID '" + newEvent.getId() + "' sudah terdaftar.");
        }

        eventList.add(newEvent);
        storage.saveEvents(eventList);
    }

    public void updateEvent(Event updatedEvent) throws EventException {
        validateEvent(updatedEvent);
        validateExistingVenueAssignments(updatedEvent);

        for (int i = 0; i < eventList.size(); i++) {
            if (eventList.get(i).getId().equals(updatedEvent.getId())) {
                eventList.set(i, updatedEvent);
                storage.saveEvents(eventList);
                return;
            }
        }

        throw new EventException("Gagal Update. Event dengan ID '" + updatedEvent.getId() + "' tidak ditemukan.");
    }

    public void deleteEvent(String id) throws EventException {
        Event eventToDelete = getEventById(id);
        
        if (eventToDelete == null) {
            throw new EventException("Gagal Hapus. Event dengan ID '" + id + "' tidak ditemukan.");
        }

        eventList.remove(eventToDelete);
        vendorService.removeEventAssignments(id);
        storage.saveEvents(eventList);
    }

    public List<Vendor> getAllVendors() {
        return vendorService.getAllVendors();
    }

    public List<Vendor> getAssignedVendors(String eventId) {
        List<Vendor> assignedVendors = new ArrayList<>();
        for (Vendor vendor : vendorService.getAllVendors()) {
            if (vendor.getEventIds().contains(eventId)) {
                assignedVendors.add(vendor);
            }
        }
        return assignedVendors;
    }

    public void assignVendorToEvent(String eventId, String vendorId) throws EventException {
        if (getEventById(eventId) == null) {
            throw new EventException("Event tidak ditemukan.");
        }

        try {
            vendorService.assignVendorToEvent(vendorId, eventId, eventList);
        } catch (ValidationException | VendorException e) {
            throw new EventException(e.getMessage());
        }
    }

    public void removeVendorFromEvent(String eventId, String vendorId) throws EventException {
        if (getEventById(eventId) == null) {
            throw new EventException("Event tidak ditemukan.");
        }

        try {
            vendorService.unassignVendorFromEvent(vendorId, eventId);
        } catch (VendorException e) {
            throw new EventException(e.getMessage());
        }
    }

    public double calculatePlanningFee(String eventId) throws EventException {
        Event event = getEventById(eventId);
        if (event == null) {
            throw new EventException("Event tidak ditemukan.");
        }

        return event.calculateBudget();
    }

    public double calculateVendorTotal(String eventId) {
        double total = 0;
        for (Vendor vendor : getAssignedVendors(eventId)) {
            total += vendor.getPrice();
        }
        return total;
    }

    public double calculateTotalBudget(String eventId) throws EventException {
        return calculatePlanningFee(eventId) + calculateVendorTotal(eventId);
    }

    private void validateEvent(Event event) throws EventException {
        if (event == null) {
            throw new EventException("Data event tidak valid.");
        }
        if (event.getId() == null || event.getId().trim().isEmpty()) {
            throw new EventException("ID Event tidak boleh kosong.");
        }
        if (event.getName() == null || event.getName().trim().isEmpty()) {
            throw new EventException("Nama Event tidak boleh kosong.");
        }
        if (event.getClientId() == null || event.getClientId().trim().isEmpty()) {
            throw new EventException("Client ID tidak boleh kosong! Event harus terikat dengan Klien.");
        }
        if (clientService.getClientById(event.getClientId()) == null) {
            throw new EventException("Client tidak ditemukan. Pilih client yang sudah terdaftar.");
        }
        if (event.getEventDate() == null) {
            throw new EventException("Tanggal event wajib diisi.");
        }
        if (event.getStatus() == null || event.getStatus().trim().isEmpty()) {
            throw new EventException("Status event wajib diisi.");
        }
        if (event.getEventType() == null || event.getEventType().trim().isEmpty()) {
            throw new EventException("Event type wajib diisi.");
        }
        if (event.getEventConcept() == null || event.getEventConcept().trim().isEmpty()) {
            throw new EventException("Konsep event wajib diisi.");
        }
        if (event.getExpectedAttendance() < 0) {
            throw new EventException("Expected attendance tidak boleh negatif.");
        }
        if (event.getBasePrice() < 0) {
            throw new EventException("Base price tidak boleh negatif.");
        }

        if (!(event instanceof PersonalEvent)
                && !(event instanceof CorporateEvent)
                && !(event instanceof PublicEvent)) {
            throw new EventException("Kategori event tidak dikenali.");
        }
    }

    private void validateExistingVenueAssignments(Event updatedEvent) throws EventException {
        int venueCount = 0;
        for (Vendor vendor : vendorService.getAllVendors()) {
            if (!"Venue".equalsIgnoreCase(vendor.getType())
                    || !vendor.getEventIds().contains(updatedEvent.getId())) {
                continue;
            }

            venueCount++;
            if (venueCount > 1) {
                throw new EventException("Event ini memiliki lebih dari satu venue.");
            }

            for (String assignedEventId : vendor.getEventIds()) {
                if (assignedEventId.equals(updatedEvent.getId())) {
                    continue;
                }

                Event assignedEvent = getEventById(assignedEventId);
                if (assignedEvent != null && sameDay(assignedEvent.getEventDate(), updatedEvent.getEventDate())) {
                    throw new EventException("Venue event ini sudah dipakai event lain pada tanggal tersebut.");
                }
            }
        }
    }

    private boolean sameDay(Date firstDate, Date secondDate) {
        if (firstDate == null || secondDate == null) {
            return false;
        }

        Calendar first = Calendar.getInstance();
        Calendar second = Calendar.getInstance();
        first.setTime(firstDate);
        second.setTime(secondDate);

        return first.get(Calendar.YEAR) == second.get(Calendar.YEAR)
                && first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR);
    }
}
