package gui;

import exception.PaymentException;
import model.CashPayment;
import model.EWalletPayment;
import model.Payment;
import model.TransferPayment;
import service.PaymentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PaymentManagementFrame extends JFrame {
    private final PaymentService paymentService;

    private JTable paymentTable;
    private DefaultTableModel tableModel;

    private JTextField eventIdField, amountField, detailOneField, detailTwoField;
    private JComboBox<String> methodCombo;
    private JLabel detailOneLabel, detailTwoLabel;
    private JLabel totalRevenueLabel, paidCountLabel, pendingCountLabel;

    public PaymentManagementFrame() {
        this.paymentService = new PaymentService();

        setTitle("EVO Payment Management");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadPaymentTable(paymentService.getAllPayments());
        updateReportSummary();
    }

    private void initComponents() {
        JPanel reportPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        totalRevenueLabel = new JLabel("Total Revenue: Rp 0", SwingConstants.CENTER);
        paidCountLabel = new JLabel("Paid: 0", SwingConstants.CENTER);
        pendingCountLabel = new JLabel("Pending: 0", SwingConstants.CENTER);

        reportPanel.add(totalRevenueLabel);
        reportPanel.add(paidCountLabel);
        reportPanel.add(pendingCountLabel);

        add(reportPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[] {
                "ID", "Event ID", "Metode", "Jumlah", "Status"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        paymentTable = new JTable(tableModel);
        add(new JScrollPane(paymentTable), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        eventIdField = new JTextField(15);
        amountField = new JTextField(15);
        methodCombo = new JComboBox<>(new String[] {"Cash", "Transfer", "E-Wallet"});

        detailOneLabel = new JLabel("Diterima Oleh");
        detailTwoLabel = new JLabel("-");
        detailOneField = new JTextField(15);
        detailTwoField = new JTextField(15);

        methodCombo.addActionListener(event -> updatePaymentDetailFields());
        updatePaymentDetailFields();

        addField(formPanel, gbc, 0, 0, "Event ID", eventIdField);
        addField(formPanel, gbc, 1, 0, "Jumlah", amountField);
        addField(formPanel, gbc, 2, 0, "Metode", methodCombo);

        addField(formPanel, gbc, 0, 2, detailOneLabel, detailOneField);
        addField(formPanel, gbc, 1, 2, detailTwoLabel, detailTwoField);

        JButton addButton = new JButton("Tambah");
        addButton.addActionListener(event -> addPayment());

        JButton processButton = new JButton("Proses");
        processButton.addActionListener(event -> processPayment());

        JButton invoiceButton = new JButton("Invoice");
        invoiceButton.addActionListener(event -> showInvoice());

        JButton deleteButton = new JButton("Hapus");
        deleteButton.addActionListener(event -> deletePayment());

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(event -> clearForm());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(processButton);
        buttonPanel.add(invoiceButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);
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

    private void updatePaymentDetailFields() {
        String method = methodCombo.getSelectedItem().toString();

        detailTwoField.setEnabled(true);

        if (method.equals("Cash")) {
            detailOneLabel.setText("Diterima Oleh");
            detailTwoLabel.setText("-");
            detailTwoField.setText("");
            detailTwoField.setEnabled(false);
        } else if (method.equals("Transfer")) {
            detailOneLabel.setText("Nama Bank");
            detailTwoLabel.setText("No Rekening");
        } else {
            detailOneLabel.setText("Provider");
            detailTwoLabel.setText("No HP");
        }
    }

    private void loadPaymentTable(List<Payment> payments) {
        tableModel.setRowCount(0);

        for (Payment payment : payments) {
            tableModel.addRow(new Object[] {
                    payment.getId(),
                    payment.getEventId(),
                    getPaymentMethod(payment),
                    String.format("Rp %,.0f", payment.getAmount()),
                    payment.getStatus()
            });
        }
    }

    private String getPaymentMethod(Payment payment) {
        if (payment instanceof CashPayment) {
            return "Cash";
        } else if (payment instanceof TransferPayment) {
            return "Transfer";
        } else if (payment instanceof EWalletPayment) {
            return "E-Wallet";
        }

        return "-";
    }

    private void addPayment() {
        try {
            String eventId = eventIdField.getText();
            double amount = Double.parseDouble(amountField.getText());
            String method = methodCombo.getSelectedItem().toString();
            String detailOne = detailOneField.getText();
            String detailTwo = detailTwoField.getText();

            paymentService.addPayment(eventId, amount, method, detailOne, detailTwo);

            JOptionPane.showMessageDialog(this, "Payment berhasil ditambahkan!");
            refreshData();
            clearForm();
        } catch (PaymentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Gagal Menyimpan", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah pembayaran harus berupa angka!", "Error Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processPayment() {
        int row = paymentTable.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih payment yang ingin diproses.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String paymentId = tableModel.getValueAt(row, 0).toString();

        try {
            paymentService.processPayment(paymentId);
            JOptionPane.showMessageDialog(this, "Payment berhasil diproses!");
            refreshData();
        } catch (PaymentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Gagal Memproses", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showInvoice() {
        int row = paymentTable.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih payment untuk membuat invoice.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String paymentId = tableModel.getValueAt(row, 0).toString();

        try {
            String invoice = paymentService.generateInvoice(paymentId);
            JTextArea invoiceArea = new JTextArea(invoice);
            invoiceArea.setEditable(false);
            invoiceArea.setRows(10);
            invoiceArea.setColumns(35);

            JOptionPane.showMessageDialog(this, new JScrollPane(invoiceArea), "Invoice", JOptionPane.INFORMATION_MESSAGE);
        } catch (PaymentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Gagal Membuat Invoice", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePayment() {
        int row = paymentTable.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih payment yang ingin dihapus.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String paymentId = tableModel.getValueAt(row, 0).toString();
        int confirmation = JOptionPane.showConfirmDialog(this, "Hapus payment " + paymentId + "?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                paymentService.deletePayment(paymentId);
                refreshData();
                clearForm();
            } catch (PaymentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Gagal Hapus", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshData() {
        loadPaymentTable(paymentService.getAllPayments());
        updateReportSummary();
    }

    private void updateReportSummary() {
        totalRevenueLabel.setText("Total Revenue: Rp "
                + String.format("%,.0f", paymentService.calculateTotalRevenue()));
        paidCountLabel.setText("Paid: " + paymentService.countPaidPayments());
        pendingCountLabel.setText("Pending: " + paymentService.countPendingPayments());
    }

    private void clearForm() {
        eventIdField.setText("");
        amountField.setText("");
        detailOneField.setText("");
        detailTwoField.setText("");
        methodCombo.setSelectedIndex(0);
    }
}