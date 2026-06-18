package service;

import exception.ValidationException;
import exception.VendorException;
import model.CateringVendor;
import model.DecorationVendor;
import model.PhotographyVendor;
import model.Vendor;
import storage.VendorStorage;

import java.util.ArrayList;
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
        validateVendorInput(name, contact, address, type, price);

        Vendor vendor = createVendorByType(UUID.randomUUID().toString(),
                name.trim(), contact.trim(), address.trim(), type, price);
        vendors.add(vendor);
        vendorStorage.saveVendors(vendors);
    }

    public void updateVendor(String id, String name, String contact, String address, String type, double price)
            throws ValidationException, VendorException {
        validateVendorInput(name, contact, address, type, price);

        Vendor existingVendor = getVendorById(id);
        if (existingVendor == null) {
            throw new VendorException("Vendor tidak ditemukan.");
        }

        Vendor updatedVendor = createVendorByType(id, name.trim(), contact.trim(), address.trim(), type, price);
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

        vendor.getEventIds().add(eventId);
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

    // Validasi vendor input

    private void validateVendorInput(String name, String contact, String address, String type, double price)
            throws ValidationException {
        validateRequired(name, "Nama vendor");
        validateRequired(contact, "Kontak");
        validateRequired(address, "Alamat");
        validateType(type);

        if (!contact.matches("\\d{8,15}")) {
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
                && !"Photography".equalsIgnoreCase(type)) {
            throw new ValidationException("Tipe vendor harus Catering, Decoration, atau Photography.");
        }
    }

    private Vendor createVendorByType(String id, String name, String contact, String address, String type, double price) {
        if ("Catering".equalsIgnoreCase(type)) {
            return new CateringVendor(id, name, contact, address, price);
        }
        if ("Decoration".equalsIgnoreCase(type)) {
            return new DecorationVendor(id, name, contact, address, price);
        }
        return new PhotographyVendor(id, name, contact, address, price);
    }
}