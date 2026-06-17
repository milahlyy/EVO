package model;

import java.util.Date;

public class BirthdayEvent extends Event {
    private String packageType; 
    private int guestCount; //total tamu undangan anak/dewasa
    private String theme;

    //add-ons (opsi tambahan)
    private boolean withMagician;
    private boolean withCustomCake;

    public BirthdayEvent(String id, String name, String clientId, Date eventDate, double basePrice, 
                         String packageType, int guestCount, String theme, 
                         boolean withMagician, boolean withCustomCake) {
        super(id, name, clientId, eventDate, basePrice);
        this.packageType = packageType;
        this.guestCount = guestCount;
        this.theme = theme;
        this.withMagician = withMagician;
        this.withCustomCake = withCustomCake;
    }

    @Override
    public double calculateBudget() {
        double packagePrice = 0;
        int baseCapacity = 0;

        // 1. Tentukan harga paket ultah & kapasitas
        switch (packageType.toUpperCase()) {
            case "A": packagePrice = 15000000; baseCapacity = 100; break; //paket Sultan
            case "B": packagePrice = 10000000; baseCapacity = 75;  break; //paket Mewah
            case "C": packagePrice = 7500000;  baseCapacity = 50;  break; //paket Meriah
            case "D": packagePrice = 5000000;  baseCapacity = 30;  break; //paket ceria
            case "E": 
            default:  packagePrice = 2500000;  baseCapacity = 15;  break; //paket sederhana
        }

        // 2. Hitung biaya ekstra tamu
        double extraGuestCost = 0;
        if (guestCount > baseCapacity) {
            int extraGuests = guestCount - baseCapacity;
            extraGuestCost = extraGuests * 100000; //charge tambahan makanan/souvenir per anak
        }

        // 3. Hitung biaya add-ons
        double magicianCost = withMagician ? 3000000 : 0; 
        double cakeCost = withCustomCake ? 1500000 : 0; 

        return getBasePrice() + packagePrice + extraGuestCost + magicianCost + cakeCost;
    }

//getter dan setter
    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public boolean isWithMagician() {
        return withMagician;
    }

    public void setWithMagician(boolean withMagician) {
        this.withMagician = withMagician;
    }

    public boolean isWithCustomCake() {
        return withCustomCake;
    }

    public void setWithCustomCake(boolean withCustomCake) {
        this.withCustomCake = withCustomCake;
    }
}