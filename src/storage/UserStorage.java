package storage;

import model.Admin;
import model.Staff;
import model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserStorage extends JsonStorage {
    private static final String USER_FILE = "data/users.json";

    public List<User> loadUsers() {
        User[] users = readJson(USER_FILE, User[].class);
        List<User> result = new ArrayList<>();

        if (users == null) {
            return result;
        }

        for (User user : Arrays.asList(users)) {
            result.add(toUserSubclass(user));
        }

        return result;
    }

    public void saveUsers(List<User> users) {
        writeJson(USER_FILE, users);
    }

    private User toUserSubclass(User user) {
        if (user == null) {
            return null;
        }

        if ("Admin".equalsIgnoreCase(user.getRole())) {
            return new Admin(user.getId(), user.getUsername(), user.getPassword(), user.getFullName());
        }

        return new Staff(user.getId(), user.getUsername(), user.getPassword(), user.getFullName());
    }
}
