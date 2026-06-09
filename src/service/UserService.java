package service;

import exception.ValidationException;
import model.Admin;
import model.Staff;
import model.User;
import storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserService {
    private final UserStorage userStorage;
    private final List<User> users;

    public UserService() {
        this.userStorage = new UserStorage();
        this.users = new ArrayList<>(userStorage.loadUsers());
        createDefaultAdminIfNeeded();
    }

    public User login(String username, String password) throws ValidationException {
        validateRequired(username, "Username");
        validateRequired(password, "Password");

        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username.trim())
                    && user.getPassword().equals(password)) {
                return user;
            }
        }

        throw new ValidationException("Username atau password salah.");
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public User getUserById(String id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }

        return null;
    }

    public void addUser(String username, String password, String fullName, String role) throws ValidationException {
        validateUserInput(null, username, password, fullName, role);

        User user = createUserByRole(UUID.randomUUID().toString(), username.trim(), password, fullName.trim(), role);
        users.add(user);
        userStorage.saveUsers(users);
    }

    public void updateUser(String id, String username, String password, String fullName, String role)
            throws ValidationException {
        validateRequired(id, "User");
        validateUserInput(id, username, password, fullName, role);

        User existingUser = getUserById(id);
        if (existingUser == null) {
            throw new ValidationException("User tidak ditemukan.");
        }

        User updatedUser = createUserByRole(id, username.trim(), password, fullName.trim(), role);
        int index = users.indexOf(existingUser);
        users.set(index, updatedUser);
        userStorage.saveUsers(users);
    }

    public void deleteUser(String id) throws ValidationException {
        validateRequired(id, "User");

        User user = getUserById(id);
        if (user == null) {
            throw new ValidationException("User tidak ditemukan.");
        }

        if (users.size() == 1) {
            throw new ValidationException("User terakhir tidak boleh dihapus.");
        }

        users.remove(user);
        userStorage.saveUsers(users);
    }

    private void createDefaultAdminIfNeeded() {
        if (!users.isEmpty()) {
            return;
        }

        users.add(new Admin(UUID.randomUUID().toString(), "admin", "admin123", "Default Admin"));
        userStorage.saveUsers(users);
    }

    private void validateUserInput(String currentUserId, String username, String password, String fullName, String role)
            throws ValidationException {
        validateRequired(username, "Username");
        validateRequired(password, "Password");
        validateRequired(fullName, "Nama lengkap");
        validateRole(role);

        if (username.trim().length() < 3) {
            throw new ValidationException("Username minimal 3 karakter.");
        }

        if (password.length() < 6) {
            throw new ValidationException("Password minimal 6 karakter.");
        }

        for (User user : users) {
            boolean sameUser = currentUserId != null && user.getId().equals(currentUserId);
            if (!sameUser && user.getUsername().equalsIgnoreCase(username.trim())) {
                throw new ValidationException("Username sudah digunakan.");
            }
        }
    }

    private void validateRequired(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " wajib diisi.");
        }
    }

    private void validateRole(String role) throws ValidationException {
        if (!"Admin".equalsIgnoreCase(role) && !"Staff".equalsIgnoreCase(role)) {
            throw new ValidationException("Role harus Admin atau Staff.");
        }
    }

    private User createUserByRole(String id, String username, String password, String fullName, String role) {
        if ("Admin".equalsIgnoreCase(role)) {
            return new Admin(id, username, password, fullName);
        }

        return new Staff(id, username, password, fullName);
    }
}
