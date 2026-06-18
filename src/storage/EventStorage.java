package storage;

import com.google.gson.*;
import model.CorporateEvent;
import model.Event;
import model.PersonalEvent;
import model.PublicEvent;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventStorage extends JsonStorage {
    private static final String EVENT_FILE = "data/events.json";

    private final Gson eventGson;

    public EventStorage() {
        super();

        JsonDeserializer<Event> deserializer = (json, typeOfT, context) -> {
            JsonObject jsonObject = json.getAsJsonObject();
            String category = getString(jsonObject, "eventCategory", "");
            if (category.isEmpty()) {
                category = getString(jsonObject, "category", "");
            }
            if (category.isEmpty()) {
                category = inferCategoryFromLegacyEvent(jsonObject);
            }

            if ("Personal".equalsIgnoreCase(category)) {
                return createEvent(jsonObject, context, "Personal");
            } else if ("Corporate".equalsIgnoreCase(category)) {
                return createEvent(jsonObject, context, "Corporate");
            } else if ("Public".equalsIgnoreCase(category)) {
                return createEvent(jsonObject, context, "Public");
            }

            throw new JsonParseException("Kategori Event tidak dikenali di dalam file JSON");
        };

        JsonSerializer<Event> serializer = (event, typeOfSrc, context) -> {
            JsonObject jsonObject = context.serialize(event).getAsJsonObject();
            jsonObject.addProperty("eventCategory", event.getEventCategory());
            return jsonObject;
        };

        this.eventGson = new GsonBuilder()
                .registerTypeAdapter(Event.class, deserializer)
                .registerTypeAdapter(Event.class, serializer)
                .setPrettyPrinting()
                .create();
    }

    public List<Event> loadEvents() {
        ensureFileExists(EVENT_FILE); 
        try (FileReader reader = new FileReader(EVENT_FILE)) {
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
            eventGson.toJson(events.toArray(new Event[0]), Event[].class, writer);
        } catch (IOException e) {
            System.out.println("Gagal menyimpan file JSON Event: " + e.getMessage());
        }
    }

    private Event createEvent(JsonObject jsonObject, JsonDeserializationContext context, String category) {
        String id = getString(jsonObject, "id", "");
        String name = getString(jsonObject, "name", "");
        String clientId = getString(jsonObject, "clientId", "");
        Date eventDate = getDate(jsonObject, context);
        String status = getString(jsonObject, "status", "Planned");
        String eventType = getString(jsonObject, "eventType", inferLegacyEventType(jsonObject));
        int expectedAttendance = getInt(jsonObject, "expectedAttendance", inferLegacyAttendance(jsonObject));
        String eventConcept = getString(jsonObject, "eventConcept", inferLegacyConcept(jsonObject));
        String specialRequest = getString(jsonObject, "specialRequest", "");
        double basePrice = getDouble(jsonObject, "basePrice", 0);

        if ("Corporate".equalsIgnoreCase(category)) {
            return new CorporateEvent(id, name, clientId, eventDate, status, eventType, expectedAttendance,
                    eventConcept, specialRequest, basePrice);
        }
        if ("Public".equalsIgnoreCase(category)) {
            return new PublicEvent(id, name, clientId, eventDate, status, eventType, expectedAttendance,
                    eventConcept, specialRequest, basePrice);
        }
        return new PersonalEvent(id, name, clientId, eventDate, status, eventType, expectedAttendance,
                eventConcept, specialRequest, basePrice);
    }

    private Date getDate(JsonObject jsonObject, JsonDeserializationContext context) {
        if (!jsonObject.has("eventDate") || jsonObject.get("eventDate").isJsonNull()) {
            return null;
        }
        return context.deserialize(jsonObject.get("eventDate"), Date.class);
    }

    private String inferCategoryFromLegacyEvent(JsonObject jsonObject) {
        String eventType = getString(jsonObject, "eventType", "");
        if ("Seminar".equalsIgnoreCase(eventType) || jsonObject.has("participantCount")) {
            return "Corporate";
        }
        if ("Public".equalsIgnoreCase(eventType)) {
            return "Public";
        }
        return "Personal";
    }

    private String inferLegacyEventType(JsonObject jsonObject) {
        if (jsonObject.has("participantCount")) {
            return "Seminar";
        }
        if (jsonObject.has("cateringCapacity")) {
            return "Wedding";
        }
        if (jsonObject.has("theme")) {
            return "Birthday";
        }
        return "General";
    }

    private int inferLegacyAttendance(JsonObject jsonObject) {
        if (jsonObject.has("expectedAttendance")) {
            return getInt(jsonObject, "expectedAttendance", 0);
        }
        if (jsonObject.has("guestCount")) {
            return getInt(jsonObject, "guestCount", 0);
        }
        if (jsonObject.has("participantCount")) {
            return getInt(jsonObject, "participantCount", 0);
        }
        if (jsonObject.has("cateringCapacity")) {
            return getInt(jsonObject, "cateringCapacity", 0);
        }
        return 0;
    }

    private String inferLegacyConcept(JsonObject jsonObject) {
        if (jsonObject.has("eventConcept")) {
            return getString(jsonObject, "eventConcept", "");
        }
        if (jsonObject.has("theme")) {
            return getString(jsonObject, "theme", "");
        }
        if (jsonObject.has("topic")) {
            return getString(jsonObject, "topic", "");
        }
        if (jsonObject.has("decorationConcept")) {
            return getString(jsonObject, "decorationConcept", "");
        }
        return "Migrated " + inferLegacyEventType(jsonObject) + " event";
    }

    private String getString(JsonObject jsonObject, String fieldName, String defaultValue) {
        if (!jsonObject.has(fieldName) || jsonObject.get(fieldName).isJsonNull()) {
            return defaultValue;
        }
        return jsonObject.get(fieldName).getAsString();
    }

    private int getInt(JsonObject jsonObject, String fieldName, int defaultValue) {
        if (!jsonObject.has(fieldName) || jsonObject.get(fieldName).isJsonNull()) {
            return defaultValue;
        }
        return jsonObject.get(fieldName).getAsInt();
    }

    private double getDouble(JsonObject jsonObject, String fieldName, double defaultValue) {
        if (!jsonObject.has(fieldName) || jsonObject.get(fieldName).isJsonNull()) {
            return defaultValue;
        }
        return jsonObject.get(fieldName).getAsDouble();
    }
}
