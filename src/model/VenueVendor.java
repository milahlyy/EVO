package model;

public class VenueVendor extends Vendor {
    private int capacity;
    private String facilities;

    public VenueVendor() {
        setType("Venue");
    }

    public VenueVendor(String id, String name, String contact, String address, double price,
                       int capacity, String facilities) {
        super(id, name, contact, address, "Venue", price);
        this.capacity = capacity;
        this.facilities = facilities;
    }

    @Override
    public String getServiceDescription() {
        return "Venue penyedia lokasi acara dengan kapasitas dan fasilitas";
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }
}
