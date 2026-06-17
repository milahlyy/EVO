package gui;

import exception.EventException;
import model.BirthdayEvent;
import model.Event;
import model.SeminarEvent;
import model.WeddingEvent;
import service.EventService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class EventManagementFrame extends JFrame {
    private final EventService eventService;
    private JTable eventTable;
    private DefaultTableModel tableModel;
    
    private JTextField idField, nameField, clientIdField, capacityField;
    private JComboBox<String> typeCombo, packageCombo;
    
    private JLabel extraStringLabel, extraIntLabel;
    private JTextField extraStringField, extraIntField;
    private JCheckBox addon1, addon2, addon3;

    public EventManagementFrame() {
        this.eventService = new EventService();

        setTitle("EVO Event Management");
        setSize(1000, 650); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadEventTable(eventService.getAllEvents());
    }

    private void initComponents() {
        //bagian tengah tabel
        tableModel = new DefaultTableModel(new Object[] {
            "ID", "Tipe", "Nama Acara", "ID Client", "Paket", "Kapasitas", "Estimasi Budget"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        eventTable = new JTable(tableModel);
        add(new JScrollPane(eventTable), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField = new JTextField(15);
        nameField = new JTextField(15);
        clientIdField = new JTextField(15);
        capacityField = new JTextField(15);
        
        typeCombo = new JComboBox<>(new String[]{"Wedding", "Seminar", "Birthday"});
        packageCombo = new JComboBox<>(new String[]{"A", "B", "C", "D", "E"});

        extraStringLabel = new JLabel("Tema:");
        extraStringField = new JTextField(15);
        extraIntLabel = new JLabel("Jml Pembicara:");
        extraIntField = new JTextField(15);
        
        addon1 = new JCheckBox("Addon 1");
        addon2 = new JCheckBox("Addon 2");
        addon3 = new JCheckBox("Addon 3");

        //ubah tampilan form saat tipe acara diganti
        typeCombo.addActionListener(e -> updateDynamicFields());
        updateDynamicFields(); //panggil sekali saat awal jalan

        //kolom kiri
        addField(formPanel, gbc, 0, 0, "ID Event", idField);
        addField(formPanel, gbc, 1, 0, "Nama Acara", nameField);
        addField(formPanel, gbc, 2, 0, "ID Client", clientIdField);
        addField(formPanel, gbc, 3, 0, "Tipe Acara", typeCombo);

        //kolom kanan
        addField(formPanel, gbc, 0, 2, "Paket Pilihan", packageCombo);
        addField(formPanel, gbc, 1, 2, "Kapasitas Tamu", capacityField);
        addField(formPanel, gbc, 2, 2, extraStringLabel, extraStringField);
        addField(formPanel, gbc, 3, 2, extraIntLabel, extraIntField);

        //baris checkbox (add-ons)
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        JPanel addonPanel = new JPanel();
        addonPanel.add(addon1); addonPanel.add(addon2); addonPanel.add(addon3);
        formPanel.add(addonPanel, gbc);

        JButton addButton = new JButton("Tambah");
        addButton.addActionListener(e -> addEvent());
        JButton deleteButton = new JButton("Hapus");
        deleteButton.addActionListener(e -> deleteEvent());
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearForm());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridy = 5;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, int col, Object label, Component field) {
        gbc.gridy = row;
        gbc.gridx = col;
        gbc.gridwidth = 1;
        if (label instanceof String) panel.add(new JLabel((String) label), gbc);
        else panel.add((JLabel) label, gbc);

        gbc.gridx = col + 1;
        panel.add(field, gbc);
    }

    private void updateDynamicFields() {
        String type = typeCombo.getSelectedItem().toString();
        
        //reset state
        extraStringField.setEnabled(false);
        extraIntField.setEnabled(false);
        addon3.setVisible(false);

        if (type.equals("Wedding")) {
            addon1.setText("Dekorasi Ekstra");
            addon2.setText("Fotografer");
            addon3.setText("Live Band");
            addon3.setVisible(true);
        } else if (type.equals("Seminar")) {
            extraIntField.setEnabled(true);
            addon1.setText("Makan Siang");
            addon2.setText("Sertifikat");
        } else if (type.equals("Birthday")) {
            extraStringField.setEnabled(true);
            addon1.setText("Pesulap/Badut");
            addon2.setText("Kue Kustom");
        }
    }

    //fungsi crud & backend
    private void loadEventTable(List<Event> events) {
        tableModel.setRowCount(0);
        for (Event event : events) {
            String tipe = event.getClass().getSimpleName().replace("Event", "");
            String kapasitas = "0";
            String paket = "E";

            //ambil data spesifik berdasarkan kelasnya
            if (event instanceof WeddingEvent) {
                kapasitas = String.valueOf(((WeddingEvent) event).getCateringCapacity());
                paket = ((WeddingEvent) event).getPackageType();
            } else if (event instanceof SeminarEvent) {
                kapasitas = String.valueOf(((SeminarEvent) event).getParticipantCount());
                paket = ((SeminarEvent) event).getPackageType();
            } else if (event instanceof BirthdayEvent) {
                kapasitas = String.valueOf(((BirthdayEvent) event).getGuestCount());
                paket = ((BirthdayEvent) event).getPackageType();
            }

            tableModel.addRow(new Object[] {
                event.getId(), tipe, event.getName(), event.getClientId(), 
                paket, kapasitas, String.format("Rp %,.0f", event.calculateBudget())
            });
        }
    }

    private void addEvent() {
        try {
            String id = idField.getText();
            String name = nameField.getText();
            String clientId = clientIdField.getText();
            String type = typeCombo.getSelectedItem().toString();
            String paket = packageCombo.getSelectedItem().toString();
            int capacity = capacityField.getText().isEmpty() ? 0 : Integer.parseInt(capacityField.getText());
            
            Event newEvent = null;

            if (type.equals("Wedding")) {
                newEvent = new WeddingEvent(id, name, clientId, new Date(), 0, 
                        paket, capacity, addon1.isSelected(), addon2.isSelected(), addon3.isSelected());
            } else if (type.equals("Seminar")) {
                int speakers = extraIntField.getText().isEmpty() ? 0 : Integer.parseInt(extraIntField.getText());
                newEvent = new SeminarEvent(id, name, clientId, new Date(), 0, 
                        paket, capacity, speakers, addon1.isSelected(), addon2.isSelected());
            } else if (type.equals("Birthday")) {
                String theme = extraStringField.getText();
                newEvent = new BirthdayEvent(id, name, clientId, new Date(), 0, 
                        paket, capacity, theme, addon1.isSelected(), addon2.isSelected());
            }

            eventService.addEvent(newEvent);
            
            JOptionPane.showMessageDialog(this, "Acara berhasil ditambahkan!");
            loadEventTable(eventService.getAllEvents());
            clearForm();

        } catch (EventException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Gagal Menyimpan", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Kapasitas/Jml Pembicara harus berupa angka!", "Error Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEvent() {
        int row = eventTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih acara di tabel yang ingin dihapus.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = tableModel.getValueAt(row, 0).toString();
        int confirmation = JOptionPane.showConfirmDialog(this, "Hapus acara " + id + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                eventService.deleteEvent(id);
                loadEventTable(eventService.getAllEvents());
                clearForm();
            } catch (EventException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Gagal Hapus", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        clientIdField.setText("");
        capacityField.setText("");
        extraStringField.setText("");
        extraIntField.setText("");
        addon1.setSelected(false);
        addon2.setSelected(false);
        addon3.setSelected(false);
    }
}