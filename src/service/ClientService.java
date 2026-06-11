package service;

import exception.ValidationException;
import model.Client;
import storage.ClientStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientService {
    private final ClientStorage clientStorage;
    private final List<Client> clients;

    public ClientService() {
        this.clientStorage = new ClientStorage();
        this.clients = new ArrayList<>(clientStorage.loadClients());
    }

    public List<Client> getAllClients() {
        return new ArrayList<>(clients);
    }

    public Client getClientById(String id) {
        for (Client client : clients) {
            if (client.getId().equals(id)) {
                return client;
            }
        }

        return null;
    }

    public List<Client> searchClients(String keyword) {
        List<Client> result = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllClients();
        }

        String lowerKeyword = keyword.trim().toLowerCase();

        for (Client client : clients) {
            if (client.getName().toLowerCase().contains(lowerKeyword)
                    || client.getEmail().toLowerCase().contains(lowerKeyword)
                    || client.getPhone().toLowerCase().contains(lowerKeyword)) {
                result.add(client);
            }
        }

        return result;
    }

    public void addClient(String name, String email, String phone, String address) throws ValidationException {
        validateClientInput(null, name, email, phone, address);

        Client client = new Client(
                UUID.randomUUID().toString(),
                name.trim(),
                email.trim(),
                phone.trim(),
                address.trim());

        clients.add(client);
        clientStorage.saveClients(clients);
    }

    public void updateClient(String id, String name, String email, String phone, String address)
            throws ValidationException {
        validateRequired(id, "Client");
        validateClientInput(id, name, email, phone, address);

        Client existingClient = getClientById(id);
        if (existingClient == null) {
            throw new ValidationException("Client tidak ditemukan.");
        }

        existingClient.setName(name.trim());
        existingClient.setEmail(email.trim());
        existingClient.setPhone(phone.trim());
        existingClient.setAddress(address.trim());

        clientStorage.saveClients(clients);
    }

    public void deleteClient(String id) throws ValidationException {
        validateRequired(id, "Client");

        Client client = getClientById(id);
        if (client == null) {
            throw new ValidationException("Client tidak ditemukan.");
        }

        clients.remove(client);
        clientStorage.saveClients(clients);
    }

    private void validateClientInput(String currentClientId, String name, String email, String phone, String address)
            throws ValidationException {
        validateRequired(name, "Nama client");
        validateRequired(email, "Email");
        validateRequired(phone, "Nomor telepon");
        validateRequired(address, "Alamat");

        if (!email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ValidationException("Format email tidak valid.");
        }

        if (!phone.trim().matches("^[0-9+\\- ]{8,15}$")) {
            throw new ValidationException("Nomor telepon harus 8 sampai 15 karakter dan hanya berisi angka, +, -, atau spasi.");
        }

        for (Client client : clients) {
            boolean sameClient = currentClientId != null && client.getId().equals(currentClientId);
            if (!sameClient && client.getEmail().equalsIgnoreCase(email.trim())) {
                throw new ValidationException("Email client sudah digunakan.");
            }
        }
    }

    private void validateRequired(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " wajib diisi.");
        }
    }
}