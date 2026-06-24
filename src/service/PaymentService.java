package service;

import exception.PaymentException;
import exception.EventException;
import model.CashPayment;
import model.EWalletPayment;
import model.Event;
import model.Payment;
import model.TransferPayment;
import storage.PaymentStorage;
import util.IdGenerator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentService {
    private final PaymentStorage storage;
    private final EventService eventService;
    private List<Payment> paymentList;

    public PaymentService() {
        this.storage = new PaymentStorage();
        this.eventService = new EventService();
        this.paymentList = storage.loadPayments();
    }

    public List<Payment> getAllPayments() {
        return paymentList;
    }

    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    public Payment getPaymentById(String id) {
        for (Payment payment : paymentList) {
            if (payment.getId().equals(id)) {
                return payment;
            }
        }
        return null;
    }

    public void addPayment(String eventId, double amount, String method, String detailOne, String detailTwo)
            throws PaymentException {
        validatePaymentInput(eventId, amount, method, detailOne, detailTwo);

        String id = generateNextPaymentId();
        Payment payment;

        if ("Cash".equalsIgnoreCase(method)) {
            payment = new CashPayment(id, eventId.trim(), amount, new Date(), "Pending", detailOne.trim());
        } else if ("Transfer".equalsIgnoreCase(method)) {
            payment = new TransferPayment(id, eventId.trim(), amount, new Date(), "Pending",
                    detailOne.trim(), detailTwo.trim());
        } else {
            payment = new EWalletPayment(id, eventId.trim(), amount, new Date(), "Pending",
                    detailOne.trim(), detailTwo.trim());
        }

        paymentList.add(payment);
        storage.savePayments(paymentList);
    }

    public String generateNextPaymentId() {
        List<String> ids = new ArrayList<>();
        for (Payment payment : paymentList) {
            ids.add(payment.getId());
        }
        return IdGenerator.generateNextId("PAY", ids);
    }

    public void processPayment(String paymentId) throws PaymentException {
        Payment payment = getPaymentById(paymentId);

        if (payment == null) {
            throw new PaymentException("Payment tidak ditemukan.");
        }

        if ("Paid".equalsIgnoreCase(payment.getStatus())) {
            throw new PaymentException("Payment ini sudah diproses.");
        }

        payment.processPayment();
        storage.savePayments(paymentList);
    }

    public void deletePayment(String paymentId) throws PaymentException {
        Payment payment = getPaymentById(paymentId);

        if (payment == null) {
            throw new PaymentException("Payment tidak ditemukan.");
        }

        paymentList.remove(payment);
        storage.savePayments(paymentList);
    }

    public String generateInvoice(String paymentId) throws PaymentException {
        Payment payment = getPaymentById(paymentId);

        if (payment == null) {
            throw new PaymentException("Payment tidak ditemukan.");
        }

        return payment.generateInvoice();
    }

    public double calculateTotalRevenue() {
        double total = 0;

        for (Payment payment : paymentList) {
            if ("Paid".equalsIgnoreCase(payment.getStatus())) {
                total += payment.getAmount();
            }
        }

        return total;
    }

    public double calculateEventBudget(String eventId) throws PaymentException {
        try {
            return eventService.calculateTotalBudget(eventId);
        } catch (EventException e) {
            throw new PaymentException(e.getMessage());
        }
    }

    public double calculatePaidAmountByEvent(String eventId) {
        double total = 0;
        for (Payment payment : paymentList) {
            if (payment.getEventId().equals(eventId) && "Paid".equalsIgnoreCase(payment.getStatus())) {
                total += payment.getAmount();
            }
        }
        return total;
    }

    public double calculatePendingAmountByEvent(String eventId) {
        double total = 0;
        for (Payment payment : paymentList) {
            if (payment.getEventId().equals(eventId) && "Pending".equalsIgnoreCase(payment.getStatus())) {
                total += payment.getAmount();
            }
        }
        return total;
    }

    public double calculateRemainingAmountByEvent(String eventId) throws PaymentException {
        double remaining = calculateEventBudget(eventId)
                - calculatePaidAmountByEvent(eventId)
                - calculatePendingAmountByEvent(eventId);
        return Math.max(remaining, 0);
    }

    public int countPaidPayments() {
        int total = 0;

        for (Payment payment : paymentList) {
            if ("Paid".equalsIgnoreCase(payment.getStatus())) {
                total++;
            }
        }

        return total;
    }

    public int countPendingPayments() {
        int total = 0;

        for (Payment payment : paymentList) {
            if ("Pending".equalsIgnoreCase(payment.getStatus())) {
                total++;
            }
        }

        return total;
    }

    private void validatePaymentInput(String eventId, double amount, String method, String detailOne, String detailTwo)
            throws PaymentException {
        if (eventId == null || eventId.trim().isEmpty()) {
            throw new PaymentException("Event ID tidak boleh kosong.");
        }

        Event event = eventService.getEventById(eventId.trim());
        if (event == null) {
            throw new PaymentException("Event dengan ID '" + eventId + "' tidak ditemukan.");
        }

        if (amount <= 0) {
            throw new PaymentException("Jumlah pembayaran harus lebih dari 0.");
        }

        double remainingAmount = calculateRemainingAmountByEvent(eventId.trim());
        if (amount > remainingAmount) {
            throw new PaymentException("Jumlah pembayaran melebihi sisa tagihan event. Sisa: Rp "
                    + String.format("%,.0f", remainingAmount));
        }

        if (method == null || method.trim().isEmpty()) {
            throw new PaymentException("Metode pembayaran wajib dipilih.");
        }

        if (detailOne == null || detailOne.trim().isEmpty()) {
            throw new PaymentException("Detail pembayaran pertama wajib diisi.");
        }

        if (!"Cash".equalsIgnoreCase(method)
                && !"Transfer".equalsIgnoreCase(method)
                && !"E-Wallet".equalsIgnoreCase(method)) {
            throw new PaymentException("Metode pembayaran tidak valid.");
        }

        if (!"Cash".equalsIgnoreCase(method)
                && (detailTwo == null || detailTwo.trim().isEmpty())) {
            throw new PaymentException("Detail pembayaran kedua wajib diisi.");
        }
    }
}
