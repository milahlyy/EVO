package model;

public class Staff extends User {
    public Staff() {
        setRole("Staff");
    }

    public Staff(String id, String username, String password, String fullName) {
        super(id, username, password, fullName, "Staff");
    }

    @Override
    public String getRoleLabel() {
        return "Staff";
    }

    @Override
    public boolean canManageUsers() {
        return false;
    }
}
