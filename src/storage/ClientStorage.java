package storage;

import model.Client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientStorage extends JsonStorage {
    private static final String CLIENT_FILE = "data/clients.json";

    public List<Client> loadClients() {
        Client[] clients = readJson(CLIENT_FILE, Client[].class);

        if (clients == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(Arrays.asList(clients));
    }

    public void saveClients(List<Client> clients) {
        writeJson(CLIENT_FILE, clients);
    }
}