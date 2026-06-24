package gui;

import exception.EventException;
import model.Client;
import model.CorporateEvent;
import model.Event;
import model.PersonalEvent;
import model.PublicEvent;
import model.Vendor;
import service.ClientService;
import service.EventService;

import javax.swing.BorderFactory;
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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EventManagementFrame extends JFrame {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final EventService eventService;
    private final ClientService clientService;

    private JTable eventTable;
    private JTable availableVendorTable;
    private JTable assignedVendorTable;
    private DefaultTableModel eventTableModel;
    private DefaultTableModel availableVendorModel;
    private DefaultTableModel assignedVendorModel;

    private JTextField idField;
    private JTextField nameField;
    private JTextField dateField;
    private JTextField eventTypeField;
    private JTextField expectedAttendanceField;
    private JTextField eventConceptField;
    private JTextField specialRequestField;
    private JTextField basePriceField;
    private JComboBox<ClientItem> clientComboBox;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> statusComboBox;
    private JComboBox<String> vendorFilterComboBox;

    public EventManagementFrame() {
        DATE_FORMAT.setLenient(false);
        this.eventService = new EventService();
        this.clientService = new ClientService();

        setTitle("EVO Event Management");
        setSize(1260, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadClientCombo();
        loadEventTable();
        loadVendorTables(null);
        clearForm();
    }

    private void initComponents() {
        eventTableModel = new DefaultTableModel(new Object[] {
                "ID", "Category", "Type", "Nama", "Client", "Tanggal", "Status",
                "Planning Fee", "Vendor", "Total"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        eventTable = new JTable(eventTableModel);
        eventTable.getSelectionModel().addListSelectionListener(event -> fillFormFromSelectedRow());
        add(new JScrollPane(eventTable), BorderLayout.CENTER);

        add(buildFormPanel(), BorderLayout.SOUTH);
        add(buildVendorPanel(), BorderLayout.EAST);
    }

    private JPanel buildFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Event Detail"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField = new JTextField(16);
        idField.setEditable(false);
        nameField = new JTextField(16);
        dateField = new JTextField(16);
        eventTypeField = new JTextField(16);
        expectedAttendanceField = new JTextField(16);
        eventConceptField = new JTextField(16);
        specialRequestField = new JTextField(16);
        basePriceField = new JTextField(16);
        clientComboBox = new JComboBox<>();
        categoryComboBox = new JComboBox<>(new String[] {"Personal", "Corporate", "Public"});
        statusComboBox = new JComboBox<>(new String[] {"Planned", "In Progress", "Completed", "Cancelled"});

        addField(formPanel, gbc, 0, 0, "ID Event", idField);
        addField(formPanel, gbc, 1, 0, "Nama Event", nameField);
        addField(formPanel, gbc, 2, 0, "Client", clientComboBox);
        addField(formPanel, gbc, 3, 0, "Tanggal (yyyy-MM-dd)", dateField);
        addField(formPanel, gbc, 4, 0, "Status", statusComboBox);

        addField(formPanel, gbc, 0, 2, "Event Category", categoryComboBox);
        addField(formPanel, gbc, 1, 2, "Event Type", eventTypeField);
        addField(formPanel, gbc, 2, 2, "Expected Attendance", expectedAttendanceField);
        addField(formPanel, gbc, 3, 2, "Event Concept", eventConceptField);
        addField(formPanel, gbc, 4, 2, "Special Request", specialRequestField);
        addField(formPanel, gbc, 0, 4, "Base Price", basePriceField);

        JButton addButton = new JButton("Tambah");
        addButton.addActionListener(event -> addEvent());
        JButton updateButton = new JButton("Edit");
        updateButton.addActionListener(event -> updateEvent());
        JButton deleteButton = new JButton("Hapus");
        deleteButton.addActionListener(event -> deleteEvent());
        JButton detailButton = new JButton("Detail");
        detailButton.addActionListener(event -> showDetail());
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(event -> clearForm());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(detailButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);
        return formPanel;
    }

    private JPanel buildVendorPanel() {
        JPanel vendorPanel = new JPanel(new BorderLayout(6, 6));
        vendorPanel.setBorder(BorderFactory.createTitledBorder("Vendor Assignment"));
        vendorPanel.setPreferredSize(new java.awt.Dimension(380, 0));

        vendorFilterComboBox = new JComboBox<>(new String[] {"All", "Venue", "Catering", "Decoration", "Photography"});
        vendorFilterComboBox.addActionListener(event -> loadVendorTables(getSelectedEventId()));

        availableVendorModel = new DefaultTableModel(new Object[] {"ID", "Tipe", "Nama", "Harga"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        availableVendorTable = new JTable(availableVendorModel);

        assignedVendorModel = new DefaultTableModel(new Object[] {"ID", "Tipe", "Nama", "Harga"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        assignedVendorTable = new JTable(assignedVendorModel);

        JPanel filterPanel = new JPanel(new BorderLayout(6, 6));
        filterPanel.add(new JLabel("Filter"), BorderLayout.WEST);
        filterPanel.add(vendorFilterComboBox, BorderLayout.CENTER);

        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 6, 6));
        tablesPanel.add(wrapTable("Available Vendors", availableVendorTable));
        tablesPanel.add(wrapTable("Assigned Vendors", assignedVendorTable));

        JButton assignButton = new JButton("Assign Selected Vendor");
        assignButton.addActionListener(event -> assignVendor());
        JButton removeButton = new JButton("Remove Selected Vendor");
        removeButton.addActionListener(event -> removeVendor());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(assignButton);
        buttonPanel.add(removeButton);

        vendorPanel.add(filterPanel, BorderLayout.NORTH);
        vendorPanel.add(tablesPanel, BorderLayout.CENTER);
        vendorPanel.add(buttonPanel, BorderLayout.SOUTH);
        return vendorPanel;
    }

    private JScrollPane wrapTable(String title, JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(title));
        return scrollPane;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, int col, Object label, Component field) {
        gbc.gridy = row;
        gbc.gridx = col;
        gbc.gridwidth = 1;
        if (label instanceof String) {
            panel.add(new JLabel((String) label), gbc);
        } else {
            panel.add((JLabel) label, gbc);
        }

        gbc.gridx = col + 1;
        panel.add(field, gbc);
    }

    private void loadClientCombo() {
        clientComboBox.removeAllItems();
        for (Client client : clientService.getAllClients()) {
            clientComboBox.addItem(new ClientItem(client));
        }
    }

    private void loadEventTable() {
        eventTableModel.setRowCount(0);
        for (Event event : eventService.getAllEvents()) {
            String eventId = event.getId();
            eventTableModel.addRow(new Object[] {
                    eventId,
                    event.getEventCategory(),
                    event.getEventType(),
                    event.getName(),
                    resolveClientName(event.getClientId()),
                    formatDate(event.getEventDate()),
                    event.getStatus(),
                    formatPrice(event.calculateBudget()),
                    formatPrice(eventService.calculateVendorTotal(eventId)),
                    formatPrice(safeTotalBudget(eventId))
            });
        }
    }

    private void loadVendorTables(String selectedEventId) {
        if (availableVendorModel == null || assignedVendorModel == null) {
            return;
        }

        availableVendorModel.setRowCount(0);
        assignedVendorModel.setRowCount(0);

        String filterType = vendorFilterComboBox == null ? "All" : vendorFilterComboBox.getSelectedItem().toString();
        for (Vendor vendor : eventService.getAllVendors()) {
            if (!"All".equals(filterType) && !filterType.equalsIgnoreCase(vendor.getType())) {
                continue;
            }

            Object[] row = new Object[] {
                    vendor.getId(), vendor.getType(), vendor.getName(), formatPrice(vendor.getPrice())
            };

            if (selectedEventId != null && vendor.getEventIds().contains(selectedEventId)) {
                assignedVendorModel.addRow(row);
            } else {
                availableVendorModel.addRow(row);
            }
        }
    }

    private void fillFormFromSelectedRow() {
        int row = eventTable.getSelectedRow();
        if (row < 0) {
            return;
        }

        Event event = eventService.getEventById(eventTableModel.getValueAt(row, 0).toString());
        if (event == null) {
            return;
        }

        idField.setText(event.getId());
        idField.setEditable(false);
        nameField.setText(event.getName());
        selectClient(event.getClientId());
        dateField.setText(formatDate(event.getEventDate()));
        statusComboBox.setSelectedItem(event.getStatus());
        categoryComboBox.setSelectedItem(event.getEventCategory());
        eventTypeField.setText(event.getEventType());
        expectedAttendanceField.setText(String.valueOf(event.getExpectedAttendance()));
        eventConceptField.setText(event.getEventConcept());
        specialRequestField.setText(event.getSpecialRequest());
        basePriceField.setText(formatRawPrice(event.getBasePrice()));
        loadVendorTables(event.getId());
    }

    private void addEvent() {
        try {
            Event event = buildEventFromForm(true);
            eventService.addEvent(event);
            JOptionPane.showMessageDialog(this, "Event berhasil ditambahkan.");
            loadEventTable();
            clearForm();
        } catch (EventException | NumberFormatException | ParseException e) {
            showError(e.getMessage());
        }
    }

    private void updateEvent() {
        if (getSelectedEventId() == null) {
            JOptionPane.showMessageDialog(this, "Pilih event yang ingin diedit.", "Validasi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Event event = buildEventFromForm(false);
            eventService.updateEvent(event);
            JOptionPane.showMessageDialog(this, "Event berhasil diedit.");
            loadEventTable();
            loadVendorTables(event.getId());
        } catch (EventException | NumberFormatException | ParseException e) {
            showError(e.getMessage());
        }
    }

    private void deleteEvent() {
        String selectedId = getSelectedEventId();
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Pilih event yang ingin dihapus.", "Validasi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this, "Hapus event " + selectedId + "?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            eventService.deleteEvent(selectedId);
            JOptionPane.showMessageDialog(this, "Event berhasil dihapus.");
            loadEventTable();
            clearForm();
        } catch (EventException e) {
            showError(e.getMessage());
        }
    }

    private void assignVendor() {
        String eventId = getSelectedEventId();
        String vendorId = getSelectedVendorId(availableVendorTable, availableVendorModel);
        if (eventId == null || vendorId == null) {
            JOptionPane.showMessageDialog(this, "Pilih event dan vendor yang ingin di-assign.", "Validasi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            eventService.assignVendorToEvent(eventId, vendorId);
            loadVendorTables(eventId);
            loadEventTable();
            selectEventRow(eventId);
        } catch (EventException e) {
            showError(e.getMessage());
        }
    }

    private void removeVendor() {
        String eventId = getSelectedEventId();
        String vendorId = getSelectedVendorId(assignedVendorTable, assignedVendorModel);
        if (eventId == null || vendorId == null) {
            JOptionPane.showMessageDialog(this, "Pilih event dan vendor assignment yang ingin dilepas.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            eventService.removeVendorFromEvent(eventId, vendorId);
            loadVendorTables(eventId);
            loadEventTable();
            selectEventRow(eventId);
        } catch (EventException e) {
            showError(e.getMessage());
        }
    }

    private void showDetail() {
        String eventId = getSelectedEventId();
        if (eventId == null) {
            JOptionPane.showMessageDialog(this, "Pilih event untuk melihat detail.", "Validasi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Event event = eventService.getEventById(eventId);
        if (event == null) {
            showError("Event tidak ditemukan.");
            return;
        }

        StringBuilder detail = new StringBuilder();
        detail.append("ID: ").append(event.getId()).append("\n");
        detail.append("Nama: ").append(event.getName()).append("\n");
        detail.append("Category: ").append(event.getEventCategory()).append("\n");
        detail.append("Type: ").append(event.getEventType()).append("\n");
        detail.append("Client: ").append(resolveClientName(event.getClientId())).append("\n");
        detail.append("Tanggal: ").append(formatDate(event.getEventDate())).append("\n");
        detail.append("Status: ").append(event.getStatus()).append("\n");
        detail.append("Expected Attendance: ").append(event.getExpectedAttendance()).append("\n");
        detail.append("Concept: ").append(event.getEventConcept()).append("\n");
        detail.append("Special Request: ").append(blankIfNull(event.getSpecialRequest())).append("\n");
        detail.append("Planning Fee: ").append(formatPrice(event.calculateBudget())).append("\n");
        detail.append("Vendor Total: ").append(formatPrice(eventService.calculateVendorTotal(eventId))).append("\n");
        detail.append("Final Budget: ").append(formatPrice(safeTotalBudget(eventId))).append("\n\n");
        detail.append("Assigned Vendors:\n");

        List<Vendor> assignedVendors = eventService.getAssignedVendors(eventId);
        if (assignedVendors.isEmpty()) {
            detail.append("- Belum ada vendor\n");
        } else {
            for (Vendor vendor : assignedVendors) {
                detail.append("- ").append(vendor.getType()).append(": ")
                        .append(vendor.getName()).append(" (")
                        .append(formatPrice(vendor.getPrice())).append(")\n");
            }
        }

        JOptionPane.showMessageDialog(this, detail.toString(), "Event Detail", JOptionPane.INFORMATION_MESSAGE);
    }

    private Event buildEventFromForm(boolean allowGeneratedId) throws ParseException {
        String id = idField.getText().trim();
        if (allowGeneratedId && id.isEmpty()) {
            id = eventService.generateNextEventId();
        }

        String name = nameField.getText().trim();
        ClientItem clientItem = (ClientItem) clientComboBox.getSelectedItem();
        String clientId = clientItem == null ? "" : clientItem.getId();
        Date eventDate = DATE_FORMAT.parse(dateField.getText().trim());
        String status = statusComboBox.getSelectedItem().toString();
        String category = categoryComboBox.getSelectedItem().toString();
        String eventType = eventTypeField.getText().trim();
        int expectedAttendance = parseInt(expectedAttendanceField.getText(), "Expected attendance");
        String eventConcept = eventConceptField.getText().trim();
        String specialRequest = specialRequestField.getText().trim();
        double basePrice = parseDouble(basePriceField.getText(), "Base price");

        if ("Corporate".equals(category)) {
            return new CorporateEvent(id, name, clientId, eventDate, status, eventType,
                    expectedAttendance, eventConcept, specialRequest, basePrice);
        }
        if ("Public".equals(category)) {
            return new PublicEvent(id, name, clientId, eventDate, status, eventType,
                    expectedAttendance, eventConcept, specialRequest, basePrice);
        }
        return new PersonalEvent(id, name, clientId, eventDate, status, eventType,
                expectedAttendance, eventConcept, specialRequest, basePrice);
    }

    private String getSelectedEventId() {
        int row = eventTable.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return eventTableModel.getValueAt(row, 0).toString();
    }

    private String getSelectedVendorId(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return model.getValueAt(row, 0).toString();
    }

    private void selectEventRow(String eventId) {
        for (int row = 0; row < eventTableModel.getRowCount(); row++) {
            if (eventTableModel.getValueAt(row, 0).equals(eventId)) {
                eventTable.setRowSelectionInterval(row, row);
                return;
            }
        }
    }

    private void selectClient(String clientId) {
        for (int i = 0; i < clientComboBox.getItemCount(); i++) {
            if (clientComboBox.getItemAt(i).getId().equals(clientId)) {
                clientComboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private String resolveClientName(String clientId) {
        Client client = clientService.getClientById(clientId);
        if (client == null) {
            return clientId;
        }
        return client.getName();
    }

    private double safeTotalBudget(String eventId) {
        try {
            return eventService.calculateTotalBudget(eventId);
        } catch (EventException e) {
            return 0;
        }
    }

    private int parseInt(String value, String fieldName) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new NumberFormatException(fieldName + " harus berupa angka.");
        }
    }

    private double parseDouble(String value, String fieldName) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new NumberFormatException(fieldName + " harus berupa angka.");
        }
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMAT.format(date);
    }

    private String formatPrice(double price) {
        return String.format("Rp %,.0f", price);
    }

    private String formatRawPrice(double price) {
        return String.format("%.0f", price);
    }

    private String blankIfNull(String value) {
        return value == null ? "" : value;
    }

    private void clearForm() {
        eventTable.clearSelection();
        idField.setText(eventService.generateNextEventId());
        idField.setEditable(false);
        nameField.setText("");
        dateField.setText(DATE_FORMAT.format(new Date()));
        eventTypeField.setText("");
        expectedAttendanceField.setText("0");
        eventConceptField.setText("");
        specialRequestField.setText("");
        basePriceField.setText("0");
        if (clientComboBox.getItemCount() > 0) {
            clientComboBox.setSelectedIndex(0);
        }
        categoryComboBox.setSelectedIndex(0);
        statusComboBox.setSelectedIndex(0);
        loadVendorTables(null);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Gagal", JOptionPane.ERROR_MESSAGE);
    }

    private static class ClientItem {
        private final Client client;

        ClientItem(Client client) {
            this.client = client;
        }

        String getId() {
            return client.getId();
        }

        @Override
        public String toString() {
            return client.getName() + " (" + client.getId() + ")";
        }
    }
}
