package storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonStorage {
    protected final Gson gson;

    public JsonStorage() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    protected void ensureFileExists(String filePath) {
        try {
            File file = new File(filePath);
            File parent = file.getParentFile();

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            if (!file.exists()) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("[]");
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to prepare JSON file: " + e.getMessage());
        }
    }

    protected <T> T readJson(String filePath, Class<T> type) {
        ensureFileExists(filePath);

        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            System.out.println("Failed to read JSON file: " + e.getMessage());
            return null;
        }
    }

    protected void writeJson(String filePath, Object data) {
        ensureFileExists(filePath);

        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            System.out.println("Failed to write JSON file: " + e.getMessage());
        }
    }
}
