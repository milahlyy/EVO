package gui;

import exception.ValidationException;
import exception.VendorException;
import model.Vendor;
import service.VendorService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VendorManagementFrame extends JFrame {
    private final VendorService vendorService = new VendorService();

    private JTextField nameField;
    private JTextField contactField;
    private JTextField addressField;
    private JComboBox<String> typeCombo;
    private JTextField priceField;
    private JTable vendorTable;
    private DefaultTableModel tableModel;
    private String selectedId = null;

    public VendorManagementFrame() {
        setTitle("Manajemen Vendor");
        setSize(760, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        loadVendorTable();
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        nameField = new JTextField();
        contactField = new JTextField();
        addressField = new JTextField();
        typeCombo = new JComboBox<>(new String[]{"Catering", "Decoration", "Photography"});
        priceField = new JTextField();

        formPanel.add(new JLabel("Nama Vendor:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Kontak:"));
        formPanel.add(contactField);
        formPanel.add(new JLabel("Alamat:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Tipe:"));
        formPanel.add(typeCombo);
        formPanel.add(new JLabel("Harga Jasa:"));
        formPanel.add(priceField);

        JButton saveButton = new JButton("Simpan");
        JButton clearButton = new JButton("Bersihkan");
        formPanel.add(saveButton);
        formPanel.add(clearButton);

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Nama", "Kontak", "Alamat", "Tipe", "Harga Jasa", "Jml Event"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vendorTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(vendorTable);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton editButton = new JButton("Edit Terpilih");
        JButton deleteButton = new JButton("Hapus Terpilih");
        JButton assignButton = new JButton("Assign ke Event");
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(assignButton);

        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> saveVendor());
        clearButton.addActionListener(e -> clearForm());
        editButton.addActionListener(e -> loadSelectedToForm());
        deleteButton.addActionListener(e -> deleteSelected());
        assignButton.addActionListener(e -> assignSelected());
    }

    private void saveVendor() {
        try {
            String name = nameField.getText();
            String contact = contactField.getText();
            String address = addressField.getText();
            String type = (String) typeCombo.getSelectedItem();
            double price = parsePrice(priceField.getText());

            if (selectedId == null) {
                vendorService.addVendor(name, contact, address, type, price);
            } else {
                vendorService.updateVendor(selectedId, name, contact, address, type, price);
            }

            JOptionPane.showMessageDialog(this, "Vendor berhasil disimpan.");
            clearForm();
            loadVendorTable();
        } catch (ValidationException | VendorException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double parsePrice(String text) throws ValidationException {
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Harga jasa harus berupa angka.");
        }
    }

    private void loadVendorTable() {
        tableModel.setRowCount(0);
        for (Vendor vendor : vendorService.getAllVendors()) {
            tableModel.addRow(new Object[]{
                    vendor.getId(), vendor.getName(), vendor.getContact(),
                    vendor.getAddress(), vendor.getType(), vendor.getPrice(),
                    vendor.getEventIds().size()
            });
        }
    }

    private void loadSelectedToForm() {
        int row = vendorTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih vendor dulu di tabel.");
            return;
        }
        selectedId = (String) tableModel.getValueAt(row, 0);
        nameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        contactField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        addressField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        typeCombo.setSelectedItem(tableModel.getValueAt(row, 4));
        priceField.setText(String.valueOf(tableModel.getValueAt(row, 5)));
    }

    private void deleteSelected() {
        int row = vendorTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih vendor dulu di tabel.");
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus vendor ini?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                vendorService.deleteVendor(id);
                clearForm();
                loadVendorTable();
            } catch (VendorException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void assignSelected() {
        int row = vendorTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih vendor dulu di tabel.");
            return;
        }
        String vendorId = (String) tableModel.getValueAt(row, 0);
        String eventId = JOptionPane.showInputDialog(this, "Masukkan ID Event:");
        if (eventId == null) {
            return; // dibatalkan
        }
        try {
            vendorService.assignVendorToEvent(vendorId, eventId);
            JOptionPane.showMessageDialog(this, "Vendor berhasil di-assign ke event.");
            loadVendorTable();
        } catch (ValidationException | VendorException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        selectedId = null;
        nameField.setText("");
        contactField.setText("");
        addressField.setText("");
        typeCombo.setSelectedIndex(0);
        priceField.setText("");
    }
}