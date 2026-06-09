package model;

public class Admin extends User {
    public Admin() {
        setRole("Admin");
    }

    public Admin(String id, String username, String password, String fullName) {
        super(id, username, password, fullName, "Admin");
    }

    @Override
    public String getRoleLabel() {
        return "Admin";
    }

    @Override
    public boolean canManageUsers() {
        return true;
    }
}
