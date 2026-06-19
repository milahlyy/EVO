package model;

import java.util.Date;

public class CashPayment extends Payment {
    private String receivedBy;

    public CashPayment(String id, String eventId, double amount, Date paymentDate, String status,
                       String receivedBy) {
        super(id, eventId, amount, paymentDate, status);
        this.receivedBy = receivedBy;
    }

    @Override
    public void processPayment() {
        setStatus("Paid");
        setPaymentDate(new Date());
    }

    @Override
    public String generateInvoice() {
        return "Invoice " + getId()
                + "\nEvent ID: " + getEventId()
                + "\nMetode: Cash"
                + "\nJumlah: Rp " + String.format("%,.0f", getAmount())
                + "\nDiterima Oleh: " + receivedBy
                + "\nStatus: " + getStatus();
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }
}