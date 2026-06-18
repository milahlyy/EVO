package model;

import java.util.Date;

public class WeddingEvent extends Event {
    private String packageType; 
    private int cateringCapacity; //total tamu yang diundang
    
    //add-ons (opsi tambahan)
    private boolean withDecoration; //tambahan dekorasi
    private boolean withDocumentation; //tambahan opsi fotografer/video
    private boolean withLiveBand;      //tambahan opsi hiburan musik

    public WeddingEvent(String id, String name, String clientId, Date eventDate, double basePrice, 
                        String packageType, int cateringCapacity, boolean withDecoration, 
                        boolean withDocumentation, boolean withLiveBand) {
        super(id, name, clientId, eventDate, basePrice);
        this.packageType = packageType;
        this.cateringCapacity = cateringCapacity;
        this.withDecoration = withDecoration;
        this.withDocumentation = withDocumentation;
        this.withLiveBand = withLiveBand;
    }

    @Override
    public double calculateBudget() {
        double packagePrice = 0;
        int baseCapacity = 0;

        // 1. Tentukan harga paket & kapasitas bawaan
        switch (packageType.toUpperCase()) {
            case "A": 
                packagePrice = 100000000; 
                baseCapacity = 1000; //paket A udah gratis katering 1000 orang
                break;
            case "B": 
                packagePrice = 75000000; 
                baseCapacity = 750;  //paket B gratis 750 orang
                break;
            case "C": 
                packagePrice = 50000000; 
                baseCapacity = 500;  //paket C gratis 500 orang
                break;
            case "D": 
                packagePrice = 30000000; 
                baseCapacity = 300;  //paket D gratis 300 orang
                break;
            case "E": 
            default:  
                packagePrice = 15000000; 
                baseCapacity = 150;  //paket E gratis 150 orang
                break;
        }

        // 2. Hitung biaya katering tambahan 
        double extraCateringCost = 0;
        if (cateringCapacity > baseCapacity) {
            int extraGuests = cateringCapacity - baseCapacity;
            extraCateringCost = extraGuests * 150000; //charge Rp150.000 hanya untuk tamu lebihnya
        }

        // 3. Hitung biaya add-ons (jika dicentang/true, biayanya ditambahkan)
        double decorationCost = withDecoration ? 15000000 : 0; 
        double documentationCost = withDocumentation ? 8000000 : 0; 
        double liveBandCost = withLiveBand ? 10000000 : 0; 

        //total Keseluruhan
        return getBasePrice() + packagePrice + extraCateringCost + decorationCost + documentationCost + liveBandCost;
    }

    //getter dan setter
    public String getPackageType() { return packageType; }
    public void setPackageType(String packageType) { this.packageType = packageType; }

    public int getCateringCapacity() { return cateringCapacity; }
    public void setCateringCapacity(int cateringCapacity) { this.cateringCapacity = cateringCapacity; }

    public boolean isWithDecoration() { return withDecoration; }
    public void setWithDecoration(boolean withDecoration) { this.withDecoration = withDecoration; }

    public boolean isWithDocumentation() { return withDocumentation; }
    public void setWithDocumentation(boolean withDocumentation) { this.withDocumentation = withDocumentation; }

    public boolean isWithLiveBand() { return withLiveBand; }
    public void setWithLiveBand(boolean withLiveBand) { this.withLiveBand = withLiveBand; }
}