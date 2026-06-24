package storage;

import model.Client;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientStorage {
    private static final String SELECT_ALL_SQL = "SELECT id, name, email, phone, address FROM clients ORDER BY name";
    private static final String SELECT_BY_ID_SQL = "SELECT id, name, email, phone, address FROM clients WHERE id = ?";
    private static final String SELECT_BY_EMAIL_SQL = "SELECT id, name, email, phone, address FROM clients WHERE LOWER(email) = LOWER(?)";
    private static final String SEARCH_SQL = "SELECT id, name, email, phone, address FROM clients "
            + "WHERE LOWER(name) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?) OR LOWER(phone) LIKE LOWER(?) "
            + "ORDER BY name";
    private static final String INSERT_SQL = "INSERT INTO clients (id, name, email, phone, address) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE clients SET name = ?, email = ?, phone = ?, address = ?, "
            + "updated_at = CURRENT_TIMESTAMP WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM clients WHERE id = ?";

    public List<Client> loadClients() {
        List<Client> clients = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SQL);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                clients.add(toClient(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Gagal membaca data client dari PostgreSQL: " + e.getMessage());
        }

        return clients;
    }

    public Client findClientById(String id) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            statement.setString(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return toClient(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal mencari client berdasarkan ID: " + e.getMessage());
        }

        return null;
    }

    public Client findClientByEmail(String email) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SELECT_BY_EMAIL_SQL)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return toClient(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal mencari client berdasarkan email: " + e.getMessage());
        }

        return null;
    }

    public List<Client> searchClients(String keyword) {
        List<Client> clients = new ArrayList<>();
        String pattern = "%" + keyword + "%";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SEARCH_SQL)) {
            statement.setString(1, pattern);
            statement.setString(2, pattern);
            statement.setString(3, pattern);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    clients.add(toClient(resultSet));
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal mencari data client dari PostgreSQL: " + e.getMessage());
        }

        return clients;
    }

    public boolean addClient(Client client) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setString(1, client.getId());
            statement.setString(2, client.getName());
            statement.setString(3, client.getEmail());
            statement.setString(4, client.getPhone());
            statement.setString(5, client.getAddress());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Gagal menambahkan client ke PostgreSQL: " + e.getMessage());
            return false;
        }
    }

    public boolean updateClient(Client client) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, client.getName());
            statement.setString(2, client.getEmail());
            statement.setString(3, client.getPhone());
            statement.setString(4, client.getAddress());
            statement.setString(5, client.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Gagal mengubah client di PostgreSQL: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteClient(String id) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setString(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Gagal menghapus client dari PostgreSQL: " + e.getMessage());
            return false;
        }
    }

    public void saveClients(List<Client> clients) {
        throw new UnsupportedOperationException("ClientStorage PostgreSQL tidak mendukung save full list.");
    }

    private Client toClient(ResultSet resultSet) throws SQLException {
        return new Client(
                resultSet.getString("id"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getString("phone"),
                resultSet.getString("address"));
    }
}
