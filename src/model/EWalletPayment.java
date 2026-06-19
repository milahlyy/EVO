package model;

import java.util.Date;

public class EWalletPayment extends Payment {
    private String walletProvider;
    private String phoneNumber;

    public EWalletPayment(String id, String eventId, double amount, Date paymentDate, String status,
                          String walletProvider, String phoneNumber) {
        super(id, eventId, amount, paymentDate, status);
        this.walletProvider = walletProvider;
        this.phoneNumber = phoneNumber;
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
                + "\nMetode: E-Wallet"
                + "\nJumlah: Rp " + String.format("%,.0f", getAmount())
                + "\nProvider: " + walletProvider
                + "\nNo HP: " + phoneNumber
                + "\nStatus: " + getStatus();
    }

    public String getWalletProvider() {
        return walletProvider;
    }

    public void setWalletProvider(String walletProvider) {
        this.walletProvider = walletProvider;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}