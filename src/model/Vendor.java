package model;

import java.util.ArrayList;
import java.util.List;

public class Vendor {
    private String id;
    private String name;      
    private String contact;  
    private String address;   
    private String type;      
    private double price;    
    private List<String> eventIds = new ArrayList<>(); 

    public Vendor() {
    }

    public Vendor(String id, String name, String contact, String address, String type, double price) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.address = address;
        this.type = type;
        this.price = price;
    }

    public String getServiceDescription() {
        return "Layanan vendor umum";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public List<String> getEventIds() {
        if (eventIds == null) {
            eventIds = new ArrayList<>();
        }
        return eventIds;
    }

    public void setEventIds(List<String> eventIds) {
        this.eventIds = eventIds;
    }
}