package model;

public class PhotographyVendor extends Vendor {

    public PhotographyVendor() {
    }

    public PhotographyVendor(String id, String name, String contact, String address, double price) {
        super(id, name, contact, address, "Photography", price);
    }

    @Override
    public String getServiceDescription() {
        return "Fotografi untuk mendokumentasikan acara baik foto maupun video";
    }
}