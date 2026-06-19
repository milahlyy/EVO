package model;

import java.util.Date;

public abstract class Payment {
    private String id;
    private String eventId;
    private double amount;
    private Date paymentDate;
    private String status;

    public Payment(String id, String eventId, double amount, Date paymentDate, String status) {
        this.id = id;
        this.eventId = eventId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.status = status;
    }

    public abstract void processPayment();

    public abstract String generateInvoice();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}