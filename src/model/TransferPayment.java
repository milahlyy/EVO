package model;

import java.util.Date;

public class TransferPayment extends Payment {
    private String bankName;
    private String accountNumber;

    public TransferPayment(String id, String eventId, double amount, Date paymentDate, String status,
                           String bankName, String accountNumber) {
        super(id, eventId, amount, paymentDate, status);
        this.bankName = bankName;
        this.accountNumber = accountNumber;
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
                + "\nMetode: Transfer"
                + "\nJumlah: Rp " + String.format("%,.0f", getAmount())
                + "\nBank: " + bankName
                + "\nNo Rekening: " + accountNumber
                + "\nStatus: " + getStatus();
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}