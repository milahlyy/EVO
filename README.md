# EVO

EVO (Event Vendor Operations) adalah aplikasi desktop berbasis Java Swing untuk membantu operasional event organizer, seperti pengelolaan user, client, event, vendor, pembayaran, dan laporan.

Project ini dibuat untuk tugas kuliah Object-Oriented Programming, jadi fokus utamanya adalah penerapan konsep OOP, exception handling, GUI, dan penyimpanan data lokal menggunakan file JSON.

## Tech Stack

- Java
- Java Swing
- JSON File Storage
- Gson `.jar` untuk serialization dan deserialization JSON

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
|-- .vscode/
|   `-- settings.json
|-- data/
|   |-- users.json
|   |-- clients.json
|   |-- events.json
|   |-- vendors.json
|   `-- payments.json
|-- docs/
|   `-- PRD.md
|-- lib/
|   `-- gson-2.10.1.jar
|-- src/
|   |-- model/
|   |-- service/
|   |-- storage/
|   |-- gui/
|   |-- util/
|   |-- exception/
|   |-- main/
|   `-- test/
`-- README.md
```

## File Penyimpanan JSON

Data aplikasi disimpan di folder `data/`:

- `data/users.json`
- `data/clients.json`
- `data/events.json`
- `data/vendors.json`
- `data/payments.json`

Setiap file berisi array JSON.

Setiap module harus membaca data dari file JSON saat aplikasi berjalan dan menyimpan ulang data setelah operasi tambah, ubah, atau hapus.

## Dependency Gson

Project ini memakai Gson dari file `.jar` di folder `lib/`.

File yang digunakan:

```text
lib/gson-2.10.1.jar
```

Folder `lib/` harus ikut ada di project supaya semua anggota tim bisa langsung menjalankan aplikasi tanpa setup dependency tambahan.

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

## 1. Install Java

Pastikan Java sudah terinstall.

Cek versi Java:

```bash
java -version
javac -version
```

## 2. Install Extension VS Code

Jika memakai VS Code, install extension berikut:

- Extension Pack for Java

Extension ini sudah cukup untuk menjalankan project Java Swing dan membaca konfigurasi library dari `.vscode/settings.json`.

## 3. Clone atau Buka Project

Jika memakai Git:

```bash
git clone <url-repository>
cd EVO
```

Jika project sudah ada di komputer, langsung buka folder `EVO` menggunakan VS Code atau IDE Java lain.

## 4. Pastikan Gson Ada di Folder Lib

Pastikan file Gson ada di:

```text
lib/gson-2.10.1.jar
```

Jika file ini belum ada, download Gson `.jar`, lalu simpan ke folder `lib/`.

## 5. Pastikan VS Code Membaca JAR

Pastikan file berikut ada:

```text
.vscode/settings.json
```

Dengan isi:

```json
{
    "java.project.referencedLibraries": [
        "lib/**/*.jar"
    ]
}
```

## 6. Reload Java Language Server

Setelah menambahkan `.jar` atau mengubah settings:

1. Tekan `Ctrl + Shift + P`.
2. Cari `Java: Clean Java Language Server Workspace`.
3. Klik command tersebut.
4. Pilih reload/restart saat diminta.

Langkah ini membuat VS Code membaca ulang dependency dari folder `lib/`.

## 7. Test Gson

File test tersedia di:

```text
src/test/GsonTest.java
```

Isi test:

```java
package test;

import com.google.gson.Gson;

public class GsonTest {
    public static void main(String[] args) {
        Gson gson = new Gson();
        String json = gson.toJson("Hello EVO");
        System.out.println(json);
    }
}
```

Jalankan `GsonTest.java`. Jika output:

```text
"Hello EVO"
```

berarti Gson sudah berhasil terbaca.

## 8. Contoh Gson untuk EVO

Contoh model:

```java
public class Client {
    private String name;

    public Client(String name) {
        this.name = name;
    }
}
```

Contoh convert object ke JSON:

```java
import com.google.gson.Gson;

public class Test {
    public static void main(String[] args) {
        Client client = new Client("Mila");
        Gson gson = new Gson();
        String json = gson.toJson(client);
        System.out.println(json);
    }
}
```

Output:

```json
{"name":"Mila"}
```

## 9. Pastikan Folder Data Ada

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

## 10. Buat Package Utama

Pastikan package berikut tersedia di dalam `src/`:

```text
model
service
storage
gui
util
exception
main
test
```

## 11. Jalankan Aplikasi

Entry point aplikasi tersedia di:

```text
src/main/Main.java
```

### Cara 1 - Lewat VS Code

1. Buka folder project `EVO` di VS Code.
2. Pastikan extension Java sudah aktif.
3. Buka file `src/main/Main.java`.
4. Klik tombol `Run` di atas method `main`.
5. GUI `EVO Login` akan muncul.

### Cara 2 - Lewat Terminal PowerShell

Jalankan command berikut dari folder root project
Compile semua file Java ke folder `out/`:

```powershell
javac -cp "lib/gson-2.10.1.jar" -d out (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

Jalankan aplikasi:

```powershell
java -cp "out;lib/gson-2.10.1.jar" main.Main
```

### Akun Login Default

Jika `data/users.json` masih kosong, aplikasi otomatis membuat akun admin default saat pertama kali dibuka:

```text
username: admin
password: admin123
```

Akun ini bisa dipakai untuk masuk ke menu Admin dan membuka fitur `User Management`.

## 12. Alur Kerja Developer

Saat membuat fitur baru, ikuti urutan ini:

1. Buat atau update class di package `model`.
2. Buat validasi dan business logic di package `service`.
3. Buat baca/tulis JSON di package `storage`.
4. Buat tampilan di package `gui`.
5. Tangani error menggunakan custom exception di package `exception`.
6. Test fitur melalui GUI.
7. Pastikan file JSON berubah setelah create, update, atau delete.

## 13. Contoh Alur Fitur

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
- Gunakan Gson dari file `.jar` di folder `lib/`.
