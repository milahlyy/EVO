package service;

import exception.ValidationException;
import exception.VendorException;
import model.CateringVendor;
import model.DecorationVendor;
import model.Event;
import model.PhotographyVendor;
import model.VenueVendor;
import model.Vendor;
import storage.VendorStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class VendorService {
    private final VendorStorage vendorStorage;
    private final List<Vendor> vendors;

    public VendorService() {
        this.vendorStorage = new VendorStorage();
        this.vendors = new ArrayList<>(vendorStorage.loadVendors());
    }

    public List<Vendor> getAllVendors() {
        return new ArrayList<>(vendors);
    }

    public Vendor getVendorById(String id) {
        for (Vendor vendor : vendors) {
            if (vendor.getId().equals(id)) {
                return vendor;
            }
        }
        return null;
    }

    // CRUD Vendor

    public void addVendor(String name, String contact, String address, String type, double price)
            throws ValidationException {
        addVendor(name, contact, address, type, price, 0, "");
    }

    public void addVendor(String name, String contact, String address, String type, double price,
                          int capacity, String facilities) throws ValidationException {
        validateVendorInput(name, contact, address, type, price);
        validateVenueInput(type, capacity, facilities);

        Vendor vendor = createVendorByType(UUID.randomUUID().toString(),
                name.trim(), contact.trim(), address.trim(), type, price, capacity, facilities);
        vendors.add(vendor);
        vendorStorage.saveVendors(vendors);
    }

    public void updateVendor(String id, String name, String contact, String address, String type, double price)
            throws ValidationException, VendorException {
        updateVendor(id, name, contact, address, type, price, 0, "");
    }

    public void updateVendor(String id, String name, String contact, String address, String type, double price,
                             int capacity, String facilities) throws ValidationException, VendorException {
        validateVendorInput(name, contact, address, type, price);
        validateVenueInput(type, capacity, facilities);

        Vendor existingVendor = getVendorById(id);
        if (existingVendor == null) {
            throw new VendorException("Vendor tidak ditemukan.");
        }

        Vendor updatedVendor = createVendorByType(id, name.trim(), contact.trim(), address.trim(), type, price,
                capacity, facilities);
        updatedVendor.setEventIds(existingVendor.getEventIds()); // jaga assign yang udah ada
        int index = vendors.indexOf(existingVendor);
        vendors.set(index, updatedVendor);
        vendorStorage.saveVendors(vendors);
    }

    public void deleteVendor(String id) throws VendorException {
        Vendor vendor = getVendorById(id);
        if (vendor == null) {
            throw new VendorException("Vendor tidak ditemukan.");
        }

        vendors.remove(vendor);
        vendorStorage.saveVendors(vendors);
    }

    // Memasukkan vendor ke event

    public void assignVendorToEvent(String vendorId, String eventId)
            throws ValidationException, VendorException {
        assignVendorToEvent(vendorId, eventId, null);
    }

    public void assignVendorToEvent(String vendorId, String eventId, List<Event> events)
            throws ValidationException, VendorException {
        if (eventId == null || eventId.trim().isEmpty()) {
            throw new ValidationException("Event wajib dipilih.");
        }

        Vendor vendor = getVendorById(vendorId);
        if (vendor == null) {
            throw new VendorException("Vendor tidak ditemukan.");
        }

        if (vendor.getEventIds().contains(eventId)) {
            throw new ValidationException("Vendor ini sudah di-assign ke event tersebut.");
        }

        if (events != null) {
            Event targetEvent = findEventById(events, eventId);
            if (targetEvent == null) {
                throw new ValidationException("Event tidak ditemukan.");
            }
            validateVenueAssignment(vendor, targetEvent, events);
        }

        vendor.getEventIds().add(eventId.trim());
        vendorStorage.saveVendors(vendors);
    }

    public void unassignVendorFromEvent(String vendorId, String eventId) throws VendorException {
        Vendor vendor = getVendorById(vendorId);
        if (vendor == null) {
            throw new VendorException("Vendor tidak ditemukan.");
        }
        vendor.getEventIds().remove(eventId);
        vendorStorage.saveVendors(vendors);
    }

    public void removeEventAssignments(String eventId) {
        for (Vendor vendor : vendors) {
            vendor.getEventIds().remove(eventId);
        }
        vendorStorage.saveVendors(vendors);
    }

    // Validasi vendor input

    private void validateVendorInput(String name, String contact, String address, String type, double price)
            throws ValidationException {
        validateRequired(name, "Nama vendor");
        validateRequired(contact, "Kontak");
        validateRequired(address, "Alamat");
        validateType(type);

        if (!contact.trim().matches("\\d{8,15}")) {
            throw new ValidationException("Kontak harus 8-15 digit angka.");
        }
        if (price < 0) {
            throw new ValidationException("Harga jasa tidak boleh negatif.");
        }
    }

    private void validateRequired(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " wajib diisi.");
        }
    }

    private void validateType(String type) throws ValidationException {
        if (!"Catering".equalsIgnoreCase(type)
                && !"Decoration".equalsIgnoreCase(type)
                && !"Photography".equalsIgnoreCase(type)
                && !"Venue".equalsIgnoreCase(type)) {
            throw new ValidationException("Tipe vendor harus Venue, Catering, Decoration, atau Photography.");
        }
    }

    private void validateVenueInput(String type, int capacity, String facilities) throws ValidationException {
        if (!"Venue".equalsIgnoreCase(type)) {
            return;
        }
        if (capacity <= 0) {
            throw new ValidationException("Kapasitas venue harus lebih dari 0.");
        }
        validateRequired(facilities, "Fasilitas venue");
    }

    private Vendor createVendorByType(String id, String name, String contact, String address, String type, double price,
                                      int capacity, String facilities) {
        if ("Catering".equalsIgnoreCase(type)) {
            return new CateringVendor(id, name, contact, address, price);
        }
        if ("Decoration".equalsIgnoreCase(type)) {
            return new DecorationVendor(id, name, contact, address, price);
        }
        if ("Venue".equalsIgnoreCase(type)) {
            return new VenueVendor(id, name, contact, address, price, capacity, facilities.trim());
        }
        return new PhotographyVendor(id, name, contact, address, price);
    }

    private Event findEventById(List<Event> events, String eventId) {
        for (Event event : events) {
            if (event.getId().equals(eventId)) {
                return event;
            }
        }
        return null;
    }

    private void validateVenueAssignment(Vendor vendor, Event targetEvent, List<Event> events)
            throws ValidationException {
        if (!"Venue".equalsIgnoreCase(vendor.getType())) {
            return;
        }

        for (Vendor existingVendor : vendors) {
            if ("Venue".equalsIgnoreCase(existingVendor.getType())
                    && existingVendor.getEventIds().contains(targetEvent.getId())) {
                throw new ValidationException("Event ini sudah memiliki venue.");
            }
        }

        for (String assignedEventId : vendor.getEventIds()) {
            Event assignedEvent = findEventById(events, assignedEventId);
            if (assignedEvent != null && sameDay(assignedEvent.getEventDate(), targetEvent.getEventDate())) {
                throw new ValidationException("Venue ini sudah dipakai event lain pada tanggal yang sama.");
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
