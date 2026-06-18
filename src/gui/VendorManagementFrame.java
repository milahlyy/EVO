package gui;

import exception.ValidationException;
import exception.VendorException;
import model.Vendor;
import service.VendorService;

import javax.swing.JButton;
import javax.swing.JComboBox;
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

public class VendorManagementFrame extends JFrame {
    private final VendorService vendorService;
    private JTable vendorTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextField contactField;
    private JTextField addressField;
    private JComboBox<String> typeComboBox;
    private JTextField priceField;

    public VendorManagementFrame() {
        this.vendorService = new VendorService();
        setTitle("EVO Vendor Management");
        setSize(760, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        initComponents();
        loadVendorTable();
    }

    private void initComponents() {
        tableModel = new DefaultTableModel(
                new Object[] {"ID", "Nama", "Kontak", "Alamat", "Tipe", "Harga Jasa", "Jml Event"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vendorTable = new JTable(tableModel);
        vendorTable.getSelectionModel().addListSelectionListener(event -> fillFormFromSelectedRow());
        add(new JScrollPane(vendorTable), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(16);
        contactField = new JTextField(16);
        addressField = new JTextField(16);
        typeComboBox = new JComboBox<>(new String[] {"Catering", "Decoration", "Photography"});
        priceField = new JTextField(16);

        addField(formPanel, gbc, 0, "Nama Vendor", nameField);
        addField(formPanel, gbc, 1, "Kontak", contactField);
        addField(formPanel, gbc, 2, "Alamat", addressField);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Tipe"), gbc);
        gbc.gridx = 1;
        formPanel.add(typeComboBox, gbc);

        addField(formPanel, gbc, 4, "Harga Jasa", priceField);

        JButton addButton = new JButton("Tambah");
        addButton.addActionListener(event -> addVendor());

        JButton updateButton = new JButton("Edit");
        updateButton.addActionListener(event -> updateVendor());

        JButton deleteButton = new JButton("Hapus");
        deleteButton.addActionListener(event -> deleteVendor());

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(event -> clearForm());

        JButton assignButton = new JButton("Assign ke Event");
        assignButton.addActionListener(event -> assignVendor());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(assignButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
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

    private void loadVendorTable() {
        tableModel.setRowCount(0);
        List<Vendor> vendors = vendorService.getAllVendors();
        for (Vendor vendor : vendors) {
            tableModel.addRow(new Object[] {
                    vendor.getId(),
                    vendor.getName(),
                    vendor.getContact(),
                    vendor.getAddress(),
                    vendor.getType(),
                    formatPrice(vendor.getPrice()),
                    vendor.getEventIds().size()
            });
        }
    }

    private void fillFormFromSelectedRow() {
        int row = vendorTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        String id = tableModel.getValueAt(row, 0).toString();
        Vendor vendor = vendorService.getVendorById(id);
        if (vendor == null) {
            return;
        }
        nameField.setText(vendor.getName());
        contactField.setText(vendor.getContact());
        addressField.setText(vendor.getAddress());
        typeComboBox.setSelectedItem(vendor.getType());
        priceField.setText(formatPrice(vendor.getPrice()));
    }

    private void addVendor() {
        try {
            vendorService.addVendor(getVendorName(), getContact(), getAddress(), getVendorType(), getPrice());
            JOptionPane.showMessageDialog(this, "Vendor berhasil ditambahkan.");
            loadVendorTable();
            clearForm();
        } catch (ValidationException e) {
            showError(e);
        }
    }

    private void updateVendor() {
        String selectedId = getSelectedVendorId();
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Pilih vendor yang ingin diedit.", "Validasi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            vendorService.updateVendor(selectedId, getVendorName(), getContact(), getAddress(), getVendorType(), getPrice());
            JOptionPane.showMessageDialog(this, "Vendor berhasil diedit.");
            loadVendorTable();
            clearForm();
        } catch (ValidationException | VendorException e) {
            showError(e);
        }
    }

    private void deleteVendor() {
        String selectedId = getSelectedVendorId();
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Pilih vendor yang ingin dihapus.", "Validasi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmation = JOptionPane.showConfirmDialog(this, "Hapus vendor ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            vendorService.deleteVendor(selectedId);
            JOptionPane.showMessageDialog(this, "Vendor berhasil dihapus.");
            loadVendorTable();
            clearForm();
        } catch (VendorException e) {
            showError(e);
        }
    }

    private void assignVendor() {
        String selectedId = getSelectedVendorId();
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Pilih vendor yang ingin di-assign.", "Validasi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String eventId = JOptionPane.showInputDialog(this, "Masukkan ID Event:");
        if (eventId == null) {
            return; // dibatalkan
        }
        try {
            vendorService.assignVendorToEvent(selectedId, eventId);
            JOptionPane.showMessageDialog(this, "Vendor berhasil di-assign ke event.");
            loadVendorTable();
        } catch (ValidationException | VendorException e) {
            showError(e);
        }
    }

    private String getSelectedVendorId() {
        int row = vendorTable.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return tableModel.getValueAt(row, 0).toString();
    }

    private String getVendorName() {
        return nameField.getText();
    }

    private String getContact() {
        return contactField.getText();
    }

    private String getAddress() {
        return addressField.getText();
    }

    private String getVendorType() {
        return typeComboBox.getSelectedItem().toString();
    }

    private double getPrice() throws ValidationException {
        try {
            return Double.parseDouble(priceField.getText().trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Harga jasa harus berupa angka.");
        }
    }

    private String formatPrice(double price) {
        return String.format("%.0f", price); // hindari format 1.5E7
    }

    private void clearForm() {
        vendorTable.clearSelection();
        nameField.setText("");
        contactField.setText("");
        addressField.setText("");
        typeComboBox.setSelectedIndex(0);
        priceField.setText("");
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Gagal", JOptionPane.ERROR_MESSAGE);
    }
}