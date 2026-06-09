package gui;

import exception.ValidationException;
import model.User;
import service.UserService;
import util.SessionManager;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

public class UserManagementFrame extends JFrame {
    private final UserService userService;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField fullNameField;
    private JComboBox<String> roleComboBox;

    public UserManagementFrame(UserService userService) {
        this.userService = userService;

        if (!SessionManager.isAdmin()) {
            JOptionPane.showMessageDialog(this, "Hanya admin yang boleh mengakses User Management.",
                    "Akses ditolak", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        setTitle("EVO User Management");
        setSize(760, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadUserTable();
    }

    private void initComponents() {
        tableModel = new DefaultTableModel(new Object[] {"ID", "Username", "Nama Lengkap", "Role"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.getSelectionModel().addListSelectionListener(event -> fillFormFromSelectedRow());
        add(new JScrollPane(userTable), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        usernameField = new JTextField(16);
        passwordField = new JPasswordField(16);
        fullNameField = new JTextField(16);
        roleComboBox = new JComboBox<>(new String[] {"Admin", "Staff"});

        addField(formPanel, gbc, 0, "Username", usernameField);
        addField(formPanel, gbc, 1, "Password", passwordField);
        addField(formPanel, gbc, 2, "Nama Lengkap", fullNameField);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Role"), gbc);

        gbc.gridx = 1;
        formPanel.add(roleComboBox, gbc);

        JButton addButton = new JButton("Tambah");
        addButton.addActionListener(event -> addUser());

        JButton updateButton = new JButton("Edit");
        updateButton.addActionListener(event -> updateUser());

        JButton deleteButton = new JButton("Hapus");
        deleteButton.addActionListener(event -> deleteUser());

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(event -> clearForm());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void loadUserTable() {
        tableModel.setRowCount(0);
        List<User> users = userService.getAllUsers();

        for (User user : users) {
            tableModel.addRow(new Object[] {
                    user.getId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getRoleLabel()
            });
        }
    }

    private void fillFormFromSelectedRow() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            return;
        }

        String id = tableModel.getValueAt(row, 0).toString();
        User user = userService.getUserById(id);
        if (user == null) {
            return;
        }

        usernameField.setText(user.getUsername());
        passwordField.setText(user.getPassword());
        fullNameField.setText(user.getFullName());
        roleComboBox.setSelectedItem(user.getRoleLabel());
    }

    private void addUser() {
        try {
            userService.addUser(getUsername(), getPassword(), getFullName(), getRole());
            JOptionPane.showMessageDialog(this, "User berhasil ditambahkan.");
            loadUserTable();
            clearForm();
        } catch (ValidationException e) {
            showValidationError(e);
        }
    }

    private void updateUser() {
        String selectedId = getSelectedUserId();
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Pilih user yang ingin diedit.", "Validasi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            userService.updateUser(selectedId, getUsername(), getPassword(), getFullName(), getRole());
            JOptionPane.showMessageDialog(this, "User berhasil diedit.");
            loadUserTable();
            clearForm();
        } catch (ValidationException e) {
            showValidationError(e);
        }
    }

    private void deleteUser() {
        String selectedId = getSelectedUserId();
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Pilih user yang ingin dihapus.", "Validasi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this, "Hapus user ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            userService.deleteUser(selectedId);
            JOptionPane.showMessageDialog(this, "User berhasil dihapus.");
            loadUserTable();
            clearForm();
        } catch (ValidationException e) {
            showValidationError(e);
        }
    }

    private String getSelectedUserId() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            return null;
        }

        return tableModel.getValueAt(row, 0).toString();
    }

    private String getUsername() {
        return usernameField.getText();
    }

    private String getPassword() {
        return new String(passwordField.getPassword());
    }

    private String getFullName() {
        return fullNameField.getText();
    }

    private String getRole() {
        return roleComboBox.getSelectedItem().toString();
    }

    private void clearForm() {
        userTable.clearSelection();
        usernameField.setText("");
        passwordField.setText("");
        fullNameField.setText("");
        roleComboBox.setSelectedIndex(0);
    }

    private void showValidationError(ValidationException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Validasi gagal", JOptionPane.ERROR_MESSAGE);
    }
}
