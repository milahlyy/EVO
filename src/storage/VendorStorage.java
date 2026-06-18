package storage;

import model.CateringVendor;
import model.DecorationVendor;
import model.PhotographyVendor;
import model.Vendor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VendorStorage extends JsonStorage {
    private static final String VENDOR_FILE = "data/vendors.json";

    public List<Vendor> loadVendors() {
        Vendor[] vendors = readJson(VENDOR_FILE, Vendor[].class);
        List<Vendor> result = new ArrayList<>();

        if (vendors == null) {
            return result;
        }

        for (Vendor vendor : Arrays.asList(vendors)) {
            result.add(toVendorSubclass(vendor));
        }

        return result;
    }

    public void saveVendors(List<Vendor> vendors) {
        writeJson(VENDOR_FILE, vendors);
    }

    private Vendor toVendorSubclass(Vendor vendor) {
        if (vendor == null) {
            return null;
        }

        Vendor result;
        String type = vendor.getType();
        if ("Catering".equalsIgnoreCase(type)) {
            result = new CateringVendor(vendor.getId(), vendor.getName(), vendor.getContact(),
                    vendor.getAddress(), vendor.getPrice());
        } else if ("Decoration".equalsIgnoreCase(type)) {
            result = new DecorationVendor(vendor.getId(), vendor.getName(), vendor.getContact(),
                    vendor.getAddress(), vendor.getPrice());
        } else if ("Photography".equalsIgnoreCase(type)) {
            result = new PhotographyVendor(vendor.getId(), vendor.getName(), vendor.getContact(),
                    vendor.getAddress(), vendor.getPrice());
        } else {
            result = vendor;
        }

        result.setEventIds(vendor.getEventIds()); // jaga data assign vendor
        return result;
    }
}