package model;

public class DecorationVendor extends Vendor {

    public DecorationVendor() {
    }

    public DecorationVendor(String id, String name, String contact, String address, double price) {
        super(id, name, contact, address, "Decoration", price);
    }

    @Override
    public String getServiceDescription() {
        return "Dekorasi untuk menata panggung, ruangan, dan ornamen acara";
    }
}