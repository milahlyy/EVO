package service;

import exception.EventException;
import model.Event;
import storage.EventStorage;

import java.util.List;

public class EventService {
    private final EventStorage storage;
    private List<Event> eventList;

    public EventService() {
        this.storage = new EventStorage();
        this.eventList = storage.loadEvents(); 
    }

    // 1. Ngambil semua data acara
    public List<Event> getAllEvents() {
        return eventList;
    }

    // 2. Mencari satu acara spesifik
    public Event getEventById(String id) {
        for (Event event : eventList) {
            if (event.getId().equals(id)) {
                return event; //ketemu
            }
        }
        return null; //tidak ketemu
    }

    // 3. Nambah acara baru
    public void addEvent(Event newEvent) throws EventException {
        validateEvent(newEvent); //cek kelengkapan data dulu

        //cek apakah ID sudah dipakai (tidak boleh ada duplikat)
        if (getEventById(newEvent.getId()) != null) {
            throw new EventException("Gagal! Event dengan ID '" + newEvent.getId() + "' sudah terdaftar.");
        }

        eventList.add(newEvent);
        storage.saveEvents(eventList); //simpan ke file JSON
    }

    // 4. Ngubah data acara lama
    public void updateEvent(Event updatedEvent) throws EventException {
        validateEvent(updatedEvent); //cek kelengkapan data lagi

        for (int i = 0; i < eventList.size(); i++) {
            if (eventList.get(i).getId().equals(updatedEvent.getId())) {
                eventList.set(i, updatedEvent); //timpa data lama dengan yang baru
                storage.saveEvents(eventList); //simpan perubahan ke JSON
                return;
            }
        }
        //jika ID tidak ditemukan di daftar
        throw new EventException("Gagal Update. Event dengan ID '" + updatedEvent.getId() + "' tidak ditemukan.");
    }

    // 5. Menghapus acara
    public void deleteEvent(String id) throws EventException {
        Event eventToDelete = getEventById(id);
        
        if (eventToDelete == null) {
            throw new EventException("Gagal Hapus. Event dengan ID '" + id + "' tidak ditemukan.");
        }

        eventList.remove(eventToDelete);
        storage.saveEvents(eventList); //simpan status terbaru ke JSON
    }

    //fungsi validasi
    private void validateEvent(Event event) throws EventException {
        if (event.getId() == null || event.getId().trim().isEmpty()) {
            throw new EventException("ID Event tidak boleh kosong.");
        }
        if (event.getName() == null || event.getName().trim().isEmpty()) {
            throw new EventException("Nama Event tidak boleh kosong.");
        }
        if (event.getClientId() == null || event.getClientId().trim().isEmpty()) {
            throw new EventException("Client ID tidak boleh kosong! Event harus terikat dengan Klien.");
        }
        //jika semua lolos, proses berlanjut
    }
}