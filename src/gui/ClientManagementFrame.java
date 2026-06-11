package gui;

import exception.ValidationException;
import model.Client;
import service.ClientService;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

public class ClientManagementFrame extends JFrame {
    private final ClientService clientService;
    private JTable clientTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField searchField;

    public ClientManagementFrame() {
        this.clientService = new ClientService();

        setTitle("EVO Client Management");
        setSize(820, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadClientTable(clientService.getAllClients());
    }

    private void initComponents() {
        tableModel = new DefaultTableModel(new Object[] {"ID", "Nama", "Email", "Telepon", "Alamat"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        clientTable = new JTable(tableModel);
        clientTable.getSelectionModel().addListSelectionListener(event -> fillFormFromSelectedRow());
        add(new JScrollPane(clientTable), BorderLayout.CENTER);

        JPanel searchPanel = new JPanel();
        searchField = new JTextField(24);

        JButton searchButton = new JButton("Cari");
        searchButton.addActionListener(event -> searchClients());

        JButton showAllButton = new JButton("Tampilkan Semua");
        showAllButton.addActionListener(event -> loadClientTable(clientService.getAllClients()));

        searchPanel.add(new JLabel("Cari"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);

        add(searchPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(18);
        emailField = new JTextField(18);
        phoneField = new JTextField(18);
        addressField = new JTextField(18);

        addField(formPanel, gbc, 0, "Nama", nameField);
        addField(formPanel, gbc, 1, "Email", emailField);
        addField(formPanel, gbc, 2, "Telepon", phoneField);
        addField(formPanel, gbc, 3, "Alamat", addressField);

        JButton addButton = new JButton("Tambah");
        addButton.addActionListener(event -> addClient());

        JButton updateButton = new JButton("Edit");
        updateButton.addActionListener(event -> updateClient());

        JButton deleteButton = new JButton("Hapus");
        deleteButton.addActionListener(event -> deleteClient());

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

    private void loadClientTable(List<Client> clients) {
        tableModel.setRowCount(0);

        for (Client client : clients) {
            tableModel.addRow(new Object[] {
                    client.getId(),
                    client.getName(),
                    client.getEmail(),
                    client.getPhone(),
                    client.getAddress()
            });
        }
    }

    private void fillFormFromSelectedRow() {
        int row = clientTable.getSelectedRow();
        if (row < 0) {
            return;
        }

        nameField.setText(tableModel.getValueAt(row, 1).toString());
        emailField.setText(tableModel.getValueAt(row, 2).toString());
        phoneField.setText(tableModel.getValueAt(row, 3).toString());
        addressField.setText(tableModel.getValueAt(row, 4).toString());
    }

    private void searchClients() {
        loadClientTable(clientService.searchClients(searchField.getText()));
    }

    private void addClient() {
        try {
            clientService.addClient(getNameInput(), getEmailInput(), getPhoneInput(), getAddressInput());
            JOptionPane.showMessageDialog(this, "Client berhasil ditambahkan.");
            loadClientTable(clientService.getAllClients());
            clearForm();
        } catch (ValidationException e) {
            showValidationError(e);
        }
    }

    private void updateClient() {
        String selectedId = getSelectedClientId();
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Pilih client yang ingin diedit.", "Validasi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            clientService.updateClient(selectedId, getNameInput(), getEmailInput(), getPhoneInput(), getAddressInput());
            JOptionPane.showMessageDialog(this, "Client berhasil diedit.");
            loadClientTable(clientService.getAllClients());
            clearForm();
        } catch (ValidationException e) {
            showValidationError(e);
        }
    }

    private void deleteClient() {
        String selectedId = getSelectedClientId();
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Pilih client yang ingin dihapus.", "Validasi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this, "Hapus client ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            clientService.deleteClient(selectedId);
            JOptionPane.showMessageDialog(this, "Client berhasil dihapus.");
            loadClientTable(clientService.getAllClients());
            clearForm();
        } catch (ValidationException e) {
            showValidationError(e);
        }
    }

    private String getSelectedClientId() {
        int row = clientTable.getSelectedRow();
        if (row < 0) {
            return null;
        }

        return tableModel.getValueAt(row, 0).toString();
    }

    private String getNameInput() {
        return nameField.getText();
    }

    private String getEmailInput() {
        return emailField.getText();
    }

    private String getPhoneInput() {
        return phoneField.getText();
    }

    private String getAddressInput() {
        return addressField.getText();
    }

    private void clearForm() {
        clientTable.clearSelection();
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
    }

    private void showValidationError(ValidationException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Validasi gagal", JOptionPane.ERROR_MESSAGE);
    }
}