package model;

public class CateringVendor extends Vendor {

    public CateringVendor() {
    }

    public CateringVendor(String id, String name, String contact, String address, double price) {
        super(id, name, contact, address, "Catering", price);
    }

    @Override
    public String getServiceDescription() {
        return "Catering penyedia makanan & minuman untuk acara";
    }
}