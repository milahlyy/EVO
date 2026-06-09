# EVO

EVO (Event Vendor Operations) adalah aplikasi desktop berbasis Java Swing untuk membantu operasional event organizer, seperti pengelolaan user, client, event, vendor, pembayaran, dan laporan.

Project ini dibuat untuk tugas kuliah Object-Oriented Programming, jadi fokus utamanya adalah penerapan konsep OOP, exception handling, GUI, dan penyimpanan data lokal menggunakan file JSON.

## Tech Stack

- Java 17
- Java Swing
- JSON File Storage
- Gson untuk serialization dan deserialization JSON

## Fokus Project

- Encapsulation
- Inheritance
- Abstraction
- Polymorphism
- Exception handling
- GUI desktop
- Penyimpanan data lokal dengan JSON

## Arsitektur

```text
GUI -> Service -> Storage -> JSON Files
```

Penjelasan layer:

- GUI menangani tampilan, form, tombol, tabel, dan interaksi user.
- Service menangani validasi, perhitungan, dan business logic.
- Storage menangani baca/tulis file JSON menggunakan Gson.
- Model menyimpan struktur data dan behavior dari object.

GUI tidak boleh membaca atau menulis file JSON secara langsung. Semua akses data harus lewat service, lalu service memanggil storage.

## Struktur Project

```text
EVO/
|-- data/
|   |-- users.json
|   |-- clients.json
|   |-- events.json
|   |-- vendors.json
|   `-- payments.json
|-- docs/
|   `-- PRD.md
|-- src/
|   |-- model/
|   |-- service/
|   |-- storage/
|   |-- gui/
|   |-- util/
|   |-- exception/
|   `-- main/
`-- README.md
```

## File Penyimpanan JSON

Data aplikasi disimpan di folder `data/`:

- `data/users.json`
- `data/clients.json`
- `data/events.json`
- `data/vendors.json`
- `data/payments.json`

Setiap file berisi array JSON. Contoh isi awal:

```json
[]
```

Setiap module harus membaca data dari file JSON saat aplikasi berjalan dan menyimpan ulang data setelah operasi tambah, ubah, atau hapus.

## Storage Layer

Class storage yang harus dibuat:

- `JsonStorage.java`
- `UserStorage.java`
- `ClientStorage.java`
- `EventStorage.java`
- `VendorStorage.java`
- `PaymentStorage.java`

Aturan storage:

- Storage hanya menangani file I/O.
- Storage menggunakan Gson.
- Storage tidak boleh berisi business logic.
- Storage harus menangani error file dengan `try-catch`.

## OOP Requirements

## Encapsulation

- Semua field model dibuat `private`.
- Gunakan getter dan setter.

## Inheritance

- `User -> Admin, Staff`
- `Event -> WeddingEvent, SeminarEvent, BirthdayEvent`
- `Vendor -> CateringVendor, DecorationVendor, PhotographyVendor`
- `Payment -> CashPayment, TransferPayment, EWalletPayment`

## Abstraction

- `abstract Event`
- `abstract Payment`

## Polymorphism

- `calculateBudget()`
- `processPayment()`
- `generateInvoice()`

## Custom Exceptions

Custom exception yang digunakan:

- `ValidationException`
- `PaymentException`
- `EventException`

## Pembagian Tugas Tim

Setiap developer mengerjakan model, service, storage, GUI, dan exception yang sesuai dengan module masing-masing.

- Developer 1: Authentication dan User Management
- Developer 2: Client Management
- Developer 3: Event Management
- Developer 4: Vendor Management
- Developer 5: Payment dan Reporting

## Tutorial Setup untuk Developer

## 1. Install Java 17

Pastikan Java 17 sudah terinstall.

Cek versi Java:

```bash
java -version
javac -version
```

Output yang diharapkan harus menunjukkan versi 17.

## 2. Clone atau Buka Project

Jika memakai Git:

```bash
git clone <url-repository>
cd EVO
```

Jika project sudah ada di komputer, langsung buka folder `EVO` menggunakan IDE seperti IntelliJ IDEA, NetBeans, Eclipse, atau VS Code.

## 3. Siapkan Dependency Gson

Project membutuhkan Gson agar object Java bisa disimpan dan dibaca sebagai JSON.

Jika memakai IDE:

- Buat folder `lib/` jika belum ada.
- Masukkan file Gson `.jar` ke folder `lib/`.
- Tambahkan file `.jar` tersebut sebagai library project di IDE.

Jika memakai build tool, tambahkan dependency Gson sesuai format build tool yang dipakai.

## 4. Pastikan Folder Data Ada

Pastikan folder `data/` berisi file berikut:

```text
users.json
clients.json
events.json
vendors.json
payments.json
```

Jika file belum ada, buat file tersebut dan isi dengan:

```json
[]
```

## 5. Buat Package Utama

Pastikan package berikut tersedia di dalam `src/`:

```text
model
service
storage
gui
util
exception
main
```

## 6. Jalankan Aplikasi dari Main Class

Buat class entry point di package `main`, misalnya:

```text
src/main/Main.java
```

Contoh struktur minimal:

```java
package main;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Panggil frame utama aplikasi di sini
        });
    }
}
```

Jalankan `Main.java` dari IDE.

## 7. Alur Kerja Developer

Saat membuat fitur baru, ikuti urutan ini:

1. Buat atau update class di package `model`.
2. Buat validasi dan business logic di package `service`.
3. Buat baca/tulis JSON di package `storage`.
4. Buat tampilan di package `gui`.
5. Tangani error menggunakan custom exception di package `exception`.
6. Test fitur melalui GUI.
7. Pastikan file JSON berubah setelah create, update, atau delete.

## 8. Contoh Alur Fitur

Contoh saat menambah client:

```text
ClientForm -> ClientService -> ClientStorage -> data/clients.json
```

Urutan proses:

1. User mengisi form client.
2. GUI mengirim data ke `ClientService`.
3. `ClientService` melakukan validasi.
4. Jika valid, `ClientService` memanggil `ClientStorage`.
5. `ClientStorage` menyimpan data ke `data/clients.json`.
6. GUI menampilkan pesan berhasil.

## Development Rules

- Jangan gunakan persistence berbasis query relasional.
- Jangan gunakan API koneksi data eksternal.
- Jangan gunakan class koneksi untuk external data store.
- GUI tidak boleh mengakses file JSON secara langsung.
- Service layer menangani business logic.
- Storage layer menangani file I/O saja.
- Gunakan Gson untuk serialization dan deserialization.
