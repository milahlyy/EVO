package storage;

import com.google.gson.*;
import model.BirthdayEvent;
import model.Event;
import model.SeminarEvent;
import model.WeddingEvent;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventStorage extends JsonStorage {
    private static final String EVENT_FILE = "data/events.json";
    
    //buat Gson khusus agar tidak mengganggu settingan JsonStorage utama
    private final Gson eventGson;

    public EventStorage() {
        super(); //panggil konstruktor JsonStorage untuk memastikan file tersedia

        // 1. Buat custom deserializer
        JsonDeserializer<Event> deserializer = (json, typeOfT, context) -> {
            JsonObject jsonObject = json.getAsJsonObject();

            //deteksi tipe acara berdasarkan field 
            if (jsonObject.has("cateringCapacity")) {
                return context.deserialize(json, WeddingEvent.class);
            } else if (jsonObject.has("participantCount")) {
                return context.deserialize(json, SeminarEvent.class);
            } else if (jsonObject.has("guestCount")) {
                return context.deserialize(json, BirthdayEvent.class);
            }

            throw new JsonParseException("Tipe Event tidak dikenali di dalam file JSON");
        };

        // 2. Memasang pendeteksi ke dalam mesin Gson khusus Event
        this.eventGson = new GsonBuilder()
                .registerTypeAdapter(Event.class, deserializer)
                .setPrettyPrinting()
                .create();
    }

    public List<Event> loadEvents() {
        ensureFileExists(EVENT_FILE); 
        try (FileReader reader = new FileReader(EVENT_FILE)) {
            //menggunakan eventGson khusus
            Event[] events = eventGson.fromJson(reader, Event[].class);
            if (events == null) {
                return new ArrayList<>();
            }
            return new ArrayList<>(Arrays.asList(events));
        } catch (IOException e) {
            System.out.println("Gagal membaca file JSON Event: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void saveEvents(List<Event> events) {
        ensureFileExists(EVENT_FILE);
        try (FileWriter writer = new FileWriter(EVENT_FILE)) {
            eventGson.toJson(events, writer);
        } catch (IOException e) {
            System.out.println("Gagal menyimpan file JSON Event: " + e.getMessage());
        }
    }
}