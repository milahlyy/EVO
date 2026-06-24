package service;

import exception.ValidationException;
import model.Client;
import storage.ClientStorage;

import java.util.List;
import java.util.UUID;

public class ClientService {
    private final ClientStorage clientStorage;

    public ClientService() {
        this.clientStorage = new ClientStorage();
    }

    public List<Client> getAllClients() {
        return clientStorage.loadClients();
    }

    public Client getClientById(String id) {
        return clientStorage.findClientById(id);
    }

    public List<Client> searchClients(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllClients();
        }

        return clientStorage.searchClients(keyword.trim());
    }

    public void addClient(String name, String email, String phone, String address) throws ValidationException {
        validateClientInput(null, name, email, phone, address);

        Client client = new Client(
                UUID.randomUUID().toString(),
                name.trim(),
                email.trim(),
                phone.trim(),
                address.trim());

        if (!clientStorage.addClient(client)) {
            throw new ValidationException("Client gagal ditambahkan.");
        }
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

        if (!clientStorage.updateClient(existingClient)) {
            throw new ValidationException("Client gagal diedit.");
        }
    }

    public void deleteClient(String id) throws ValidationException {
        validateRequired(id, "Client");

        Client client = getClientById(id);
        if (client == null) {
            throw new ValidationException("Client tidak ditemukan.");
        }

        if (!clientStorage.deleteClient(id)) {
            throw new ValidationException("Client gagal dihapus.");
        }
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

        Client client = clientStorage.findClientByEmail(email.trim());
        boolean sameClient = currentClientId != null && client != null && client.getId().equals(currentClientId);
        if (client != null && !sameClient) {
            throw new ValidationException("Email client sudah digunakan.");
        }
    }

    private void validateRequired(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " wajib diisi.");
        }
    }
}
