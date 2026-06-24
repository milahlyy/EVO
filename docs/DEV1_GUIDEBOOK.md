# Developer 1 Guidebook - Authentication, User Management, and Project Foundation

## 1. Deskripsi Singkat Program

EVO atau Event Vendor Operations adalah aplikasi desktop berbasis Java Swing untuk membantu operasional event organizer. Aplikasi ini mengelola user, client, event, vendor, payment, dan report dalam satu sistem internal.

Tujuan utama EVO bukan hanya membuat aplikasi CRUD, tetapi juga menunjukkan konsep Object-Oriented Programming dalam Java, seperti encapsulation, inheritance, abstraction, polymorphism, exception handling, layered architecture, dan persistence.

Pada versi utama project, data disimpan secara lokal menggunakan file JSON di folder `data/`. Gson digunakan untuk mengubah object Java menjadi JSON dan JSON menjadi object Java.

Catatan branch:

- Versi utama tugas mengikuti PRD: Java Swing + JSON + Gson.
- Branch `learn/postgres-flyway` adalah branch belajar database. Di branch ini, modul Client sedang dicoba memakai PostgreSQL, Docker, Flyway, dan JDBC.
- Bagian Developer 1 tetap berfokus pada Authentication dan User Management.

## 2. Gambaran Besar Cara Kerja Sistem

Arsitektur utama project mengikuti alur:

```text
GUI -> Service -> Storage -> Data
```

Penjelasan layer:

- GUI menangani tampilan, form, tombol, tabel, dialog, dan input user.
- Service menangani validasi, business logic, pemilihan object, dan aturan sistem.
- Storage menangani penyimpanan data, seperti baca/tulis JSON atau akses database pada branch eksperimen.
- Model menyimpan struktur data dan behavior dari object.

Contoh alur login:

```text
LoginFrame -> UserService -> UserStorage -> data/users.json
```

Urutan proses login:

1. User mengetik username dan password di `LoginFrame`.
2. GUI memanggil `userService.login(username, password)`.
3. `UserService` memvalidasi input tidak kosong.
4. `UserService` mencari user yang cocok dari list user.
5. Jika cocok, object `User` dikembalikan.
6. `SessionManager.login(user)` menyimpan user yang sedang login.
7. Sistem membuka dashboard Admin atau Staff berdasarkan role.
8. Jika gagal, `ValidationException` dilempar dan GUI menampilkan pesan error.

Contoh alur tambah user:

```text
UserManagementFrame -> UserService -> UserStorage -> data/users.json
```

Urutan proses tambah user:

1. Admin mengisi form user.
2. GUI memanggil `userService.addUser(...)`.
3. `UserService` memvalidasi username, password, nama lengkap, dan role.
4. `UserService` membuat object `Admin` atau `Staff`.
5. Object user ditambahkan ke list.
6. `UserStorage.saveUsers(users)` menyimpan ulang data ke `users.json`.

## 3. Jobdesc Developer 1

Berdasarkan PRD, Developer 1 bertanggung jawab pada:

- Authentication.
- User Management.
- Role handling untuk Admin dan Staff.
- Session management.
- Model `User`, `Admin`, dan `Staff`.
- Service `UserService`.
- Storage `UserStorage`.
- Login screen.
- Admin dan Staff dashboard.
- User Management screen.
- Validation untuk fitur user.

Selain itu, Developer 1 juga dapat menjelaskan kontribusi fondasi project:

- Menyiapkan struktur package project.
- Menyiapkan folder utama seperti `src/`, `data/`, `docs/`, dan `lib/`.
- Menyiapkan package `model`, `service`, `storage`, `gui`, `util`, `exception`, dan `main`.
- Menyiapkan PRD sebagai dokumen requirement dan pembagian tugas.
- Menyiapkan aturan arsitektur `GUI -> Service -> Storage`.
- Menyiapkan dependency Gson melalui `lib/gson-2.10.1.jar`.
- Menyiapkan konfigurasi VS Code agar membaca semua jar di `lib/`.
- Menyiapkan file JSON awal seperti `users.json`, `clients.json`, `events.json`, `vendors.json`, dan `payments.json`.

Deliverables utama Developer 1:

```text
src/model/User.java
src/model/Admin.java
src/model/Staff.java
src/service/UserService.java
src/storage/UserStorage.java
src/util/SessionManager.java
src/exception/ValidationException.java
src/gui/LoginFrame.java
src/gui/AdminDashboardFrame.java
src/gui/StaffDashboardFrame.java
src/gui/UserManagementFrame.java
src/main/Main.java
docs/PRD.md
.vscode/settings.json
```

## 4. Konsep Java yang Digunakan

### Class dan Object

Class adalah blueprint atau rancangan. Object adalah instance nyata dari class.

Contoh:

- `User` adalah class.
- User dengan username `admin` adalah object.

Di EVO, hampir semua data penting direpresentasikan sebagai object, misalnya user, client, event, vendor, dan payment.

### Package

Package digunakan untuk mengelompokkan class berdasarkan tanggung jawab.

Contoh package:

- `model`: struktur data dan behavior object.
- `service`: business logic dan validasi.
- `storage`: persistence.
- `gui`: tampilan Swing.
- `util`: helper umum.
- `exception`: custom exception.
- `main`: entry point aplikasi.

Package membuat project lebih rapi dan mudah dibagi antar developer.

### Encapsulation

Encapsulation berarti data dalam class dilindungi dengan `private`, lalu diakses lewat getter dan setter.

Contoh di `User`:

```java
private String username;

public String getUsername() {
    return username;
}

public void setUsername(String username) {
    this.username = username;
}
```

Tujuannya agar data tidak diubah sembarangan dari luar class.

### Constructor

Constructor adalah method khusus yang dipanggil saat object dibuat.

Contoh:

```java
public User(String id, String username, String password, String fullName, String role) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.fullName = fullName;
    this.role = role;
}
```

Constructor membantu membuat object dengan data awal yang lengkap.

### Inheritance

Inheritance berarti satu class mewarisi field dan method dari class lain.

Di Dev 1:

```text
User -> Admin
User -> Staff
```

`Admin` dan `Staff` mewarisi data dasar dari `User`, seperti `id`, `username`, `password`, `fullName`, dan `role`.

### Method Overriding

Overriding berarti subclass mengubah behavior method dari parent class.

Contoh:

```java
@Override
public boolean canManageUsers() {
    return true;
}
```

Pada `Admin`, `canManageUsers()` bernilai `true`. Pada `Staff`, method yang sama bernilai `false`.

### Polymorphism

Polymorphism berarti object berbeda bisa diperlakukan sebagai tipe parent yang sama, tetapi behavior-nya tetap mengikuti class asli.

Contoh:

```java
User user = new Admin(...);
user.canManageUsers(); // true
```

Walaupun variabelnya bertipe `User`, behavior yang dipakai tetap behavior `Admin`.

Ini dipakai saat login:

```java
if (user.canManageUsers()) {
    dashboardFrame = new AdminDashboardFrame(userService);
} else {
    dashboardFrame = new StaffDashboardFrame();
}
```

Kode tidak perlu mengecek role secara manual terlalu banyak. Cukup panggil behavior object.

### Access Modifier

Access modifier mengatur visibility class, field, dan method.

- `private`: hanya bisa diakses dalam class itu sendiri.
- `public`: bisa diakses dari luar class.
- `protected`: bisa diakses class turunan atau package terkait.

Contoh:

- Field model dibuat `private`.
- Getter dan setter dibuat `public`.
- Helper validasi di service dibuat `private` karena hanya dipakai internal.

### Static

`static` berarti milik class, bukan milik object tertentu.

Contoh `SessionManager`:

```java
private static User currentUser;
```

Karena session hanya perlu satu user aktif untuk seluruh aplikasi, data session dibuat static.

### Final

`final` dipakai untuk nilai yang tidak ingin diganti setelah dibuat.

Contoh:

```java
private final UserStorage userStorage;
private final List<User> users;
```

Artinya referensi `userStorage` dan `users` tidak diarahkan ke object lain setelah constructor selesai.

### List dan ArrayList

`List` adalah interface untuk kumpulan data. `ArrayList` adalah implementasi konkret.

Contoh:

```java
private final List<User> users;
this.users = new ArrayList<>(userStorage.loadUsers());
```

User disimpan dalam list agar mudah ditambah, dicari, diedit, dan dihapus.

### UUID

`UUID` digunakan untuk membuat ID unik.

Contoh:

```java
UUID.randomUUID().toString()
```

Ini mengurangi risiko ID user duplikat.

### Exception Handling

Exception adalah mekanisme Java untuk menangani kondisi error.

Di project ini, input yang tidak valid dilempar sebagai custom exception.

Contoh:

```java
throw new ValidationException("Username wajib diisi.");
```

GUI menangkap exception dengan `try-catch`:

```java
try {
    userService.addUser(...);
} catch (ValidationException e) {
    showValidationError(e);
}
```

Tujuannya agar aplikasi tidak crash dan user mendapat pesan error yang jelas.

### Custom Exception

`ValidationException` adalah exception buatan sendiri untuk error validasi.

```java
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
```

Karena `ValidationException` extends `Exception`, ini termasuk checked exception. Method yang mungkin melempar exception ini harus menulis `throws ValidationException`.

### File I/O

File I/O adalah proses membaca dan menulis file.

Di `JsonStorage`, file dibaca dengan `FileReader` dan ditulis dengan `FileWriter`.

Contoh:

```java
try (FileReader reader = new FileReader(filePath)) {
    return gson.fromJson(reader, type);
}
```

Kode memakai try-with-resources agar file otomatis ditutup setelah selesai.

### Gson Serialization dan Deserialization

Serialization adalah mengubah object Java menjadi JSON.

Deserialization adalah mengubah JSON menjadi object Java.

Contoh:

```java
gson.toJson(data, writer);
gson.fromJson(reader, type);
```

Gson membantu agar storage tidak perlu menulis parsing JSON manual.

### Java Swing

Swing adalah library Java untuk membuat GUI desktop.

Contoh komponen yang dipakai:

- `JFrame`: window utama.
- `JPanel`: container layout.
- `JLabel`: teks.
- `JTextField`: input teks.
- `JPasswordField`: input password.
- `JButton`: tombol.
- `JTable`: tabel data.
- `JOptionPane`: dialog pesan.

Swing memakai event listener untuk menangani aksi user.

Contoh:

```java
loginButton.addActionListener(event -> handleLogin());
```

Saat tombol login diklik, method `handleLogin()` dijalankan.

### Event Dispatch Thread

Swing sebaiknya dijalankan lewat Event Dispatch Thread.

Contoh di `Main`:

```java
SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
```

Ini memastikan GUI dibuat dan dijalankan pada thread yang benar untuk Swing.

## 5. Penjelasan Code Developer 1

### `User.java`

`User` adalah parent class untuk semua user.

Field utama:

- `id`: identitas unik user.
- `username`: nama login.
- `password`: password login.
- `fullName`: nama lengkap.
- `role`: role user, misalnya Admin atau Staff.

Method penting:

- `getRoleLabel()`: mengembalikan label role.
- `canManageUsers()`: default `false`, artinya user biasa tidak bisa mengelola user.

`User` menjadi dasar untuk inheritance `Admin` dan `Staff`.

### `Admin.java`

`Admin` adalah subclass dari `User`.

Ciri utama:

- Role selalu `Admin`.
- `getRoleLabel()` mengembalikan `"Admin"`.
- `canManageUsers()` mengembalikan `true`.

Makna business logic:

Admin memiliki hak untuk membuka User Management.

### `Staff.java`

`Staff` adalah subclass dari `User`.

Ciri utama:

- Role selalu `Staff`.
- `getRoleLabel()` mengembalikan `"Staff"`.
- `canManageUsers()` mengembalikan `false`.

Makna business logic:

Staff bisa mengakses fitur operasional seperti client, event, vendor, dan payment, tetapi tidak boleh mengelola user.

### `ValidationException.java`

`ValidationException` digunakan untuk menandai input atau kondisi bisnis yang tidak valid.

Contoh kondisi yang memakai exception ini:

- Username kosong.
- Password kosong.
- Username kurang dari 3 karakter.
- Password kurang dari 6 karakter.
- Role bukan Admin atau Staff.
- Username sudah digunakan.
- Login gagal.

Dengan custom exception, error validasi lebih jelas dan bisa ditangani oleh GUI.

### `SessionManager.java`

`SessionManager` menyimpan user yang sedang login.

Field:

```java
private static User currentUser;
```

Method:

- `login(User user)`: menyimpan user aktif.
- `logout()`: menghapus session user.
- `getCurrentUser()`: mengambil user aktif.
- `isLoggedIn()`: mengecek apakah ada user login.
- `isAdmin()`: mengecek apakah user aktif punya hak admin.

Kenapa static?

Karena session berlaku global untuk aplikasi. Dashboard dan screen lain harus bisa tahu siapa user yang sedang login.

### `JsonStorage.java`

`JsonStorage` adalah reusable base storage untuk baca/tulis JSON.

Tanggung jawab:

- Membuat file jika belum ada.
- Membaca JSON dari file.
- Menulis object/list ke file JSON.
- Menangani `IOException` dengan try-catch.

Method penting:

- `ensureFileExists(String filePath)`: memastikan file data ada.
- `readJson(String filePath, Class<T> type)`: membaca JSON menjadi object.
- `writeJson(String filePath, Object data)`: menulis object menjadi JSON.

Konsep Java yang terlihat:

- Generics melalui `<T>`.
- File I/O.
- Try-with-resources.
- Inheritance karena `UserStorage` extends `JsonStorage`.

### `UserStorage.java`

`UserStorage` menangani persistence untuk user.

File yang dipakai:

```text
data/users.json
```

Method:

- `loadUsers()`: membaca semua user dari JSON.
- `saveUsers(List<User> users)`: menyimpan semua user ke JSON.
- `toUserSubclass(User user)`: mengubah object `User` biasa menjadi `Admin` atau `Staff`.

Kenapa perlu `toUserSubclass()`?

Saat Gson membaca JSON, data role memang ada, tetapi object hasil baca bisa menjadi `User` biasa. Agar polymorphism tetap jalan, storage membuat ulang object menjadi `Admin` atau `Staff` berdasarkan field `role`.

Contoh:

- Jika role `Admin`, dibuat object `Admin`.
- Selain itu, dibuat object `Staff`.

Ini penting karena `canManageUsers()` berbeda antara Admin dan Staff.

### `UserService.java`

`UserService` adalah pusat business logic untuk user.

Tanggung jawab:

- Load user dari storage saat aplikasi berjalan.
- Membuat default admin jika belum ada user.
- Login.
- Menampilkan semua user.
- Mencari user berdasarkan ID.
- Menambah user.
- Mengubah user.
- Menghapus user.
- Validasi input user.
- Menentukan apakah object yang dibuat adalah `Admin` atau `Staff`.

Method penting:

#### `login(String username, String password)`

Validasi:

- Username wajib diisi.
- Password wajib diisi.

Lalu mencocokkan username dan password dengan data user.

Jika cocok, return object user.

Jika tidak cocok, lempar:

```java
throw new ValidationException("Username atau password salah.");
```

#### `createDefaultAdminIfNeeded()`

Jika `users.json` masih kosong, sistem otomatis membuat admin default:

```text
username: admin
password: admin123
```

Tujuannya agar aplikasi bisa dipakai pertama kali tanpa harus membuat user manual dari luar.

#### `addUser(...)`

Alur:

1. Validasi input.
2. Buat ID unik dengan UUID.
3. Buat object `Admin` atau `Staff`.
4. Tambahkan ke list.
5. Simpan list ke JSON.

#### `updateUser(...)`

Alur:

1. Validasi ID.
2. Validasi input.
3. Cari user lama.
4. Buat object baru dengan ID yang sama dan data baru.
5. Replace object di list.
6. Simpan ke JSON.

#### `deleteUser(...)`

Alur:

1. Validasi ID.
2. Cari user.
3. Cegah penghapusan jika user tinggal satu.
4. Hapus dari list.
5. Simpan ke JSON.

Aturan `users.size() == 1` penting agar sistem tidak kehilangan semua akun login.

#### `validateUserInput(...)`

Validasi:

- Username wajib diisi.
- Password wajib diisi.
- Nama lengkap wajib diisi.
- Role harus Admin atau Staff.
- Username minimal 3 karakter.
- Password minimal 6 karakter.
- Username tidak boleh duplikat.

#### `createUserByRole(...)`

Factory sederhana untuk membuat subclass berdasarkan role.

```java
if ("Admin".equalsIgnoreCase(role)) {
    return new Admin(...);
}

return new Staff(...);
```

Ini menjaga agar service tidak membuat `User` generic, tetapi membuat object sesuai role.

### `LoginFrame.java`

`LoginFrame` adalah GUI pertama yang dilihat user.

Komponen utama:

- Input username.
- Input password.
- Tombol login.
- Hint default admin.

Method penting:

- `initComponents()`: membuat tampilan.
- `handleLogin()`: mengambil input dan memanggil `UserService.login`.
- `openDashboard(User user)`: membuka dashboard sesuai role.

Alur login:

1. User klik Login.
2. GUI ambil username dan password.
3. GUI memanggil service.
4. Jika berhasil, session dibuat.
5. Jika Admin, buka `AdminDashboardFrame`.
6. Jika Staff, buka `StaffDashboardFrame`.
7. Jika gagal, tampilkan error dialog.

### `AdminDashboardFrame.java`

Dashboard untuk admin.

Fitur utama:

- Menampilkan nama admin yang sedang login.
- Membuka User Management.
- Membuka Client Management.
- Membuka Event Management.
- Membuka Vendor Management.
- Membuka Payment Management.
- Logout.

Admin menerima object `UserService` dari `LoginFrame` agar User Management memakai service yang sama.

### `StaffDashboardFrame.java`

Dashboard untuk staff.

Fitur utama:

- Menampilkan nama staff yang sedang login.
- Membuka Client Management.
- Membuka Vendor Management.
- Membuka Event Management.
- Membuka Payment Management.
- Logout.

Staff tidak memiliki tombol User Management karena staff tidak boleh mengelola user.

### `UserManagementFrame.java`

Screen untuk CRUD user.

Hanya Admin yang boleh masuk. Di constructor ada guard:

```java
if (!SessionManager.isAdmin()) {
    JOptionPane.showMessageDialog(...);
    dispose();
    return;
}
```

Komponen utama:

- `JTable` untuk daftar user.
- Form username.
- Form password.
- Form nama lengkap.
- Combo box role Admin/Staff.
- Tombol Tambah.
- Tombol Edit.
- Tombol Hapus.
- Tombol Clear.

Alur tambah user:

1. Admin isi form.
2. Klik Tambah.
3. GUI panggil `userService.addUser(...)`.
4. Jika berhasil, tabel reload.
5. Jika gagal, dialog error muncul.

Alur edit user:

1. Admin pilih row di tabel.
2. Data masuk ke form.
3. Admin edit data.
4. Klik Edit.
5. GUI panggil `userService.updateUser(...)`.
6. Tabel reload.

Alur hapus user:

1. Admin pilih row.
2. Klik Hapus.
3. Sistem menampilkan konfirmasi.
4. Jika yes, GUI panggil `userService.deleteUser(...)`.
5. Service mencegah penghapusan user terakhir.

### `Main.java`

`Main` adalah entry point aplikasi.

Tugasnya sederhana:

```java
SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
```

Artinya aplikasi dimulai dari LoginFrame dan dijalankan melalui Event Dispatch Thread milik Swing.

## 6. Konsep OOP di Bagian Developer 1

### Encapsulation

Terlihat di `User`, karena semua field dibuat private dan diakses melalui getter/setter.

### Inheritance

Terlihat pada:

```text
Admin extends User
Staff extends User
```

Admin dan Staff memakai data dasar dari User.

### Polymorphism

Terlihat saat object `Admin` dan `Staff` diperlakukan sebagai `User`.

Contoh:

```java
User user = userService.login(username, password);
if (user.canManageUsers()) {
    ...
}
```

Method yang dipanggil sama, tetapi hasilnya berbeda berdasarkan object asli.

### Abstraction

Bagian Dev 1 tidak memakai abstract class secara langsung. Namun konsep abstraction tetap terlihat dari pemisahan tanggung jawab:

- GUI tidak tahu detail penyimpanan JSON.
- GUI hanya tahu memanggil service.
- Service tidak tahu detail tampilan.
- Storage hanya fokus persistence.

Abstraction yang lebih eksplisit di project ada pada `Event` dan `Payment` yang dibuat abstract.

## 7. Cara Menjelaskan Saat Presentasi

Versi singkat:

> Saya sebagai Developer 1 bertanggung jawab pada authentication dan user management. Saya membuat model User, Admin, dan Staff untuk menunjukkan inheritance dan polymorphism. Admin dan Staff memiliki behavior berbeda melalui method canManageUsers. Saya juga membuat UserService untuk validasi login dan CRUD user, UserStorage untuk persistence ke JSON, SessionManager untuk menyimpan user yang sedang login, serta GUI Login, Dashboard, dan User Management. Selain itu saya menyiapkan struktur project, package, dependency Gson, data folder, dan PRD sebagai fondasi kerja tim.

Versi teknis:

> Sistem memakai layered architecture GUI -> Service -> Storage. GUI hanya menangani input dan tampilan. Service menangani validasi dan business logic. Storage menangani baca/tulis data. Pada login, LoginFrame memanggil UserService. Jika valid, UserService mengembalikan object User yang bisa berupa Admin atau Staff. Object tersebut disimpan di SessionManager. Dashboard dipilih berdasarkan polymorphism melalui canManageUsers. Jika user adalah Admin, sistem membuka AdminDashboardFrame dan memperbolehkan User Management. Jika Staff, sistem membuka StaffDashboardFrame tanpa akses User Management.

## 8. Testing Checklist Developer 1

Gunakan checklist ini untuk memastikan bagian Dev 1 berjalan:

- Aplikasi bisa dibuka dari `Main`.
- Jika `users.json` kosong, default admin otomatis dibuat.
- Login dengan `admin / admin123` berhasil.
- Login dengan username/password salah menampilkan error.
- Admin masuk ke Admin Dashboard.
- Staff masuk ke Staff Dashboard.
- Admin bisa membuka User Management.
- Staff tidak bisa membuka User Management.
- Admin bisa tambah user.
- Admin bisa edit user.
- Admin bisa hapus user.
- User terakhir tidak bisa dihapus.
- Username duplikat ditolak.
- Username kurang dari 3 karakter ditolak.
- Password kurang dari 6 karakter ditolak.
- Role selain Admin/Staff ditolak.
- Data user tersimpan ke `data/users.json`.
- Setelah aplikasi restart, data user tetap ada.

## 9. Catatan Penting

Password saat ini disimpan sebagai plain text di JSON. Ini cukup untuk project OOP dasar, tetapi tidak aman untuk aplikasi production. Pada sistem production, password seharusnya di-hash, misalnya dengan BCrypt.

SessionManager saat ini menyimpan session secara static di memory. Ini cocok untuk aplikasi desktop single-user sederhana. Jika aplikasi berubah menjadi multi-user server-based, session perlu didesain ulang.

UserStorage menyimpan ulang seluruh list user setelah create, update, atau delete. Ini sesuai pola JSON file storage, tetapi berbeda dengan database yang biasanya melakukan insert, update, dan delete per record.

Branch `learn/postgres-flyway` adalah eksperimen database. Jangan jadikan branch ini sebagai acuan utama requirement JSON kecuali tim dan dosen setuju mengganti arsitektur.
