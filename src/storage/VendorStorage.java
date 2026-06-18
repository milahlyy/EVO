package storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import model.CateringVendor;
import model.DecorationVendor;
import model.PhotographyVendor;
import model.VenueVendor;
import model.Vendor;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VendorStorage extends JsonStorage {
    private static final String VENDOR_FILE = "data/vendors.json";
    private final Gson vendorGson;

    public VendorStorage() {
        super();

        JsonDeserializer<Vendor> deserializer = (json, typeOfT, context) -> {
            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.has("type") ? jsonObject.get("type").getAsString() : "";

            if ("Catering".equalsIgnoreCase(type)) {
                return context.deserialize(json, CateringVendor.class);
            } else if ("Decoration".equalsIgnoreCase(type)) {
                return context.deserialize(json, DecorationVendor.class);
            } else if ("Photography".equalsIgnoreCase(type)) {
                return context.deserialize(json, PhotographyVendor.class);
            } else if ("Venue".equalsIgnoreCase(type)) {
                return context.deserialize(json, VenueVendor.class);
            }

            throw new JsonParseException("Tipe Vendor tidak dikenali di dalam file JSON");
        };

        this.vendorGson = new GsonBuilder()
                .registerTypeAdapter(Vendor.class, deserializer)
                .setPrettyPrinting()
                .create();
    }

    public List<Vendor> loadVendors() {
        ensureFileExists(VENDOR_FILE);
        try (FileReader reader = new FileReader(VENDOR_FILE)) {
            Vendor[] vendors = vendorGson.fromJson(reader, Vendor[].class);
            if (vendors == null) {
                return new ArrayList<>();
            }
            return new ArrayList<>(Arrays.asList(vendors));
        } catch (IOException e) {
            System.out.println("Gagal membaca file JSON Vendor: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void saveVendors(List<Vendor> vendors) {
        ensureFileExists(VENDOR_FILE);
        try (FileWriter writer = new FileWriter(VENDOR_FILE)) {
            vendorGson.toJson(vendors.toArray(new Vendor[0]), Vendor[].class, writer);
        } catch (IOException e) {
            System.out.println("Gagal menyimpan file JSON Vendor: " + e.getMessage());
        }
    }
}
