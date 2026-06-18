package model;

import java.util.Date;

public class SeminarEvent extends Event {
    private String packageType; 
    private int participantCount; //total peserta yang hadir
    private int speakerCount;

    //add-ons (opsi tambahan)
    private boolean withLunch;
    private boolean withCertificate;

    public SeminarEvent(String id, String name, String clientId, Date eventDate, double basePrice, 
                        String packageType, int participantCount, int speakerCount, 
                        boolean withLunch, boolean withCertificate) {
        super(id, name, clientId, eventDate, basePrice);
        this.packageType = packageType;
        this.participantCount = participantCount;
        this.speakerCount = speakerCount;
        this.withLunch = withLunch;
        this.withCertificate = withCertificate;
    }

    @Override
    public double calculateBudget() {
        double packagePrice = 0;
        int baseCapacity = 0;

        // 1. Tentukan harga sewa ruangan & kapasitas
        switch (packageType.toUpperCase()) {
            case "A": packagePrice = 30000000; baseCapacity = 300; break; //auditorium Besar
            case "B": packagePrice = 20000000; baseCapacity = 200; break; //aula madya
            case "C": packagePrice = 10000000; baseCapacity = 100; break; //ruang Serbaguna
            case "D": packagePrice = 5000000;  baseCapacity = 50;  break; //ruang rapat eksekutif
            case "E": 
            default:  packagePrice = 2000000;  baseCapacity = 20;  break; //ruang meeting standar
        }

        // 2. Hitung biaya ekstra peserta (jika melebihi kapasitas ruangan dasar)
        double extraParticipantCost = 0;
        if (participantCount > baseCapacity) {
            int extraGuests = participantCount - baseCapacity;
            extraParticipantCost = extraGuests * 50000; //biaya sewa kursi & snack tambahan per orang
        }

        // 3. Hitung biaya lainnya & add-ons
        double speakerFee = speakerCount * 2000000; //honor pembicara
        double lunchCost = withLunch ? (participantCount * 75000) : 0; //makan siang semua peserta
        double certificateCost = withCertificate ? (participantCount * 15000) : 0; //cetak sertifikat

        return getBasePrice() + packagePrice + extraParticipantCost + speakerFee + lunchCost + certificateCost;
    }

//getter dan setter
    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }

    public int getSpeakerCount() {
        return speakerCount;
    }

    public void setSpeakerCount(int speakerCount) {
        this.speakerCount = speakerCount;
    }

    public boolean isWithLunch() {
        return withLunch;
    }

    public void setWithLunch(boolean withLunch) {
        this.withLunch = withLunch;
    }

    public boolean isWithCertificate() {
        return withCertificate;
    }

    public void setWithCertificate(boolean withCertificate) {
        this.withCertificate = withCertificate;
    }
}