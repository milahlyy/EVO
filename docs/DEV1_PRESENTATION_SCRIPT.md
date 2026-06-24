# Script Presentasi Developer 1 - Authentication dan User Management

## 1. Opening

Halo, saya Developer 1. Bagian yang saya kerjakan adalah Authentication dan User Management.

Selain itu, saya juga ikut menyiapkan fondasi awal project, seperti struktur package, dokumen PRD, dependency Gson, folder data, dan aturan arsitektur project.

Program ini bernama EVO atau Event Vendor Operations. EVO adalah aplikasi desktop Java Swing untuk membantu operasional event organizer, seperti mengelola user, client, event, vendor, dan payment.

Arsitektur utama project ini adalah:

```text
GUI -> Service -> Storage -> Data
```

Artinya:

- GUI hanya menangani tampilan dan input user.
- Service menangani validasi dan business logic.
- Storage menangani penyimpanan data.
- Model merepresentasikan object data.

Untuk bagian saya, alurnya adalah:

```text
LoginFrame -> UserService -> UserStorage -> data/users.json
```

## 2. Demo Login

Sekarang saya demo bagian login.

Jalankan aplikasi dari:

```text
src/main/Main.java
```

Atau lewat terminal:

```powershell
javac -cp "lib/*" -d out (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
java -cp "out;lib/*" main.Main
```

Saat aplikasi dibuka, tampilan pertama adalah `LoginFrame`.

Di sini user memasukkan username dan password. Jika `data/users.json` masih kosong, sistem otomatis membuat default admin:

```text
username: admin
password: admin123
```

Sekarang saya login menggunakan akun admin default.

Setelah login berhasil, sistem menyimpan user aktif ke `SessionManager`, lalu membuka dashboard sesuai role.

Kalau user adalah Admin, sistem membuka `AdminDashboardFrame`.

Kalau user adalah Staff, sistem membuka `StaffDashboardFrame`.

Bagian pentingnya ada di kode `LoginFrame`:

```java
User user = userService.login(username, password);
SessionManager.login(user);
openDashboard(user);
```

Kode ini menunjukkan bahwa GUI tidak mengecek data user langsung ke file. GUI hanya memanggil service.

## 3. Demo Login Gagal

Sekarang saya coba login dengan username atau password salah.

Hasilnya muncul dialog error.

Ini terjadi karena `UserService.login()` melempar `ValidationException` saat username atau password tidak cocok.

Contoh logic-nya:

```java
throw new ValidationException("Username atau password salah.");
```

Lalu di GUI, error itu ditangkap dengan `try-catch`:

```java
try {
    User user = userService.login(username, password);
} catch (ValidationException e) {
    JOptionPane.showMessageDialog(this, e.getMessage(), "Login gagal", JOptionPane.ERROR_MESSAGE);
}
```

Jadi aplikasi tidak crash. User mendapat pesan error yang jelas.

## 4. Demo Admin Dashboard

Setelah login sebagai Admin, kita masuk ke Admin Dashboard.

Admin bisa membuka:

- User Management
- Client Management
- Event Management
- Vendor Management
- Payment Management
- Logout

Yang paling penting untuk bagian saya adalah User Management.

Admin punya akses ini karena class `Admin` override method `canManageUsers()` menjadi `true`.

```java
@Override
public boolean canManageUsers() {
    return true;
}
```

Di `LoginFrame`, dashboard dipilih dengan:

```java
if (user.canManageUsers()) {
    dashboardFrame = new AdminDashboardFrame(userService);
} else {
    dashboardFrame = new StaffDashboardFrame();
}
```

Ini adalah contoh polymorphism. Method yang dipanggil sama, yaitu `canManageUsers()`, tetapi hasilnya berbeda tergantung object asli user tersebut Admin atau Staff.

## 5. Demo Staff Dashboard

Sekarang jika login sebagai Staff, user akan masuk ke Staff Dashboard.

Staff bisa membuka fitur operasional seperti client, vendor, event, dan payment. Tetapi Staff tidak punya menu User Management.

Alasannya ada di class `Staff`:

```java
@Override
public boolean canManageUsers() {
    return false;
}
```

Jadi perbedaan role tidak hanya ditulis sebagai text, tetapi juga punya behavior di object.

## 6. Demo User Management

Sekarang saya buka User Management dari Admin Dashboard.

Di screen ini Admin bisa:

- melihat daftar user
- menambah user
- mengedit user
- menghapus user
- memilih role Admin atau Staff

Class GUI-nya adalah:

```text
src/gui/UserManagementFrame.java
```

Pada awal constructor, ada proteksi:

```java
if (!SessionManager.isAdmin()) {
    JOptionPane.showMessageDialog(this, "Hanya admin yang boleh mengakses User Management.",
            "Akses ditolak", JOptionPane.ERROR_MESSAGE);
    dispose();
    return;
}
```

Artinya, walaupun ada cara membuka frame ini secara langsung, sistem tetap mengecek session. Kalau user yang login bukan Admin, akses ditolak.

## 7. Demo Tambah User

Sekarang saya tambah user baru.

Misalnya:

```text
Username: staff1
Password: staff123
Nama lengkap: Staff Satu
Role: Staff
```

Saat tombol Tambah diklik, GUI memanggil:

```java
userService.addUser(getUsername(), getPassword(), getFullName(), getRole());
```

Proses detailnya ada di `UserService.addUser()`:

```java
validateUserInput(null, username, password, fullName, role);

User user = createUserByRole(UUID.randomUUID().toString(), username.trim(), password, fullName.trim(), role);
users.add(user);
userStorage.saveUsers(users);
```

Penjelasannya:

1. Input divalidasi dulu.
2. Sistem membuat ID unik menggunakan UUID.
3. Sistem membuat object Admin atau Staff sesuai role.
4. Object dimasukkan ke list user.
5. Data disimpan ke `data/users.json`.

## 8. Demo Validasi Tambah User

Sekarang saya coba input yang salah.

Contoh:

- username kosong
- password kurang dari 6 karakter
- username sudah dipakai

Jika input tidak valid, `UserService` melempar `ValidationException`.

Validasi ada di method:

```java
private void validateUserInput(String currentUserId, String username, String password, String fullName, String role)
```

Aturan validasinya:

- username wajib diisi
- password wajib diisi
- nama lengkap wajib diisi
- role harus Admin atau Staff
- username minimal 3 karakter
- password minimal 6 karakter
- username tidak boleh duplikat

Kenapa validasi ada di service, bukan GUI?

Karena GUI hanya bertanggung jawab pada tampilan. Business rule harus berada di service agar lebih rapi dan mudah diuji.

## 9. Demo Edit User

Untuk edit user, Admin memilih row di tabel.

Saat row dipilih, data user masuk ke form.

Kode yang menangani ini:

```java
private void fillFormFromSelectedRow()
```

Setelah data diedit dan tombol Edit diklik, GUI memanggil:

```java
userService.updateUser(selectedId, getUsername(), getPassword(), getFullName(), getRole());
```

Di service:

1. ID dicek.
2. Input divalidasi ulang.
3. User lama dicari berdasarkan ID.
4. User baru dibuat sesuai role.
5. Data lama diganti.
6. Semua user disimpan ulang ke JSON.

## 10. Demo Hapus User

Untuk hapus user, Admin memilih row lalu klik Hapus.

Sebelum menghapus, GUI menampilkan dialog konfirmasi:

```java
JOptionPane.showConfirmDialog(this, "Hapus user ini?", "Konfirmasi",
        JOptionPane.YES_NO_OPTION);
```

Kalau user memilih Yes, GUI memanggil:

```java
userService.deleteUser(selectedId);
```

Di service ada aturan penting:

```java
if (users.size() == 1) {
    throw new ValidationException("User terakhir tidak boleh dihapus.");
}
```

Tujuannya agar sistem tidak kehilangan semua akun login.

## 11. Penjelasan Package

Project ini memakai beberapa package agar kode tidak bercampur.

```text
model
service
storage
gui
util
exception
main
```

Penjelasannya:

- `model`: bentuk data dan behavior object.
- `service`: validasi dan business logic.
- `storage`: baca tulis data.
- `gui`: tampilan Java Swing.
- `util`: helper umum.
- `exception`: custom error.
- `main`: titik awal aplikasi.

Contoh sederhana:

```text
User.java = data user
UserService.java = proses dan aturan user
UserStorage.java = simpan dan baca user
LoginFrame.java = tampilan login
```

## 12. Penjelasan Model User, Admin, dan Staff

`User` adalah parent class.

Field yang dimiliki User:

```text
id
username
password
fullName
role
```

`Admin` dan `Staff` adalah subclass dari `User`.

Relasinya:

```text
User
├── Admin
└── Staff
```

Kenapa dipisah?

Karena Admin dan Staff punya hak akses berbeda.

Admin:

```java
canManageUsers() -> true
```

Staff:

```java
canManageUsers() -> false
```

Ini menunjukkan konsep inheritance dan polymorphism.

## 13. Penjelasan UserService

`UserService` adalah pusat logic untuk authentication dan user management.

Tanggung jawabnya:

- login
- mengambil semua user
- mencari user berdasarkan ID
- tambah user
- edit user
- hapus user
- membuat default admin
- validasi input
- membuat object Admin atau Staff sesuai role

`UserService` menyimpan data user dalam:

```java
private final List<User> users;
```

Saat service dibuat, data user diload dari storage:

```java
this.users = new ArrayList<>(userStorage.loadUsers());
createDefaultAdminIfNeeded();
```

Artinya saat aplikasi berjalan, user dibaca dari JSON lalu dipakai di memory. Setelah ada perubahan, list disimpan ulang ke JSON.

## 14. Penjelasan UserStorage

`UserStorage` adalah class yang mengurus data user di JSON.

File yang dipakai:

```text
data/users.json
```

Method utama:

```java
loadUsers()
saveUsers(List<User> users)
```

`UserStorage` extends `JsonStorage`, jadi dia mewarisi kemampuan baca dan tulis JSON.

Ada method penting:

```java
private User toUserSubclass(User user)
```

Fungsinya mengubah data dari JSON menjadi object yang benar:

- jika role Admin, menjadi object `Admin`
- jika role Staff, menjadi object `Staff`

Ini penting agar polymorphism tetap berjalan setelah data dibaca dari JSON.

## 15. Penjelasan JsonStorage

`JsonStorage` adalah reusable base class untuk storage JSON.

Method penting:

- `ensureFileExists()`: memastikan file ada.
- `readJson()`: membaca JSON menjadi object.
- `writeJson()`: menulis object ke JSON.

Class ini memakai Gson:

```java
this.gson = new GsonBuilder().setPrettyPrinting().create();
```

`setPrettyPrinting()` membuat JSON lebih mudah dibaca manusia.

File I/O memakai try-with-resources:

```java
try (FileReader reader = new FileReader(filePath)) {
    return gson.fromJson(reader, type);
}
```

Ini memastikan file otomatis ditutup setelah selesai.

## 16. Penjelasan ValidationException

`ValidationException` adalah custom exception untuk error validasi.

Kodenya:

```java
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
```

Kenapa Dev 1 memakai `ValidationException`, bukan `UserException`?

Karena error di Authentication dan User Management sebagian besar adalah validasi umum, seperti:

- username kosong
- password kosong
- username terlalu pendek
- password terlalu pendek
- username duplikat
- role tidak valid
- login gagal

Modul lain punya exception khusus seperti `VendorException`, `EventException`, dan `PaymentException` karena domain mereka punya aturan bisnis yang lebih spesifik.

## 17. Penjelasan SessionManager

`SessionManager` menyimpan user yang sedang login.

Field utama:

```java
private static User currentUser;
```

Method:

- `login(User user)`: menyimpan user aktif.
- `logout()`: menghapus user aktif.
- `getCurrentUser()`: mengambil user aktif.
- `isLoggedIn()`: mengecek apakah ada user login.
- `isAdmin()`: mengecek apakah user aktif admin.

Kenapa static?

Karena session berlaku untuk seluruh aplikasi. Dashboard dan screen lain perlu tahu siapa user yang sedang login.

Contoh penggunaan:

```java
String name = SessionManager.getCurrentUser().getFullName();
```

Ini dipakai di dashboard untuk menampilkan nama user yang sedang login.

## 18. Konsep OOP yang Ditunjukkan Dev 1

### Encapsulation

Field di `User` dibuat private dan diakses lewat getter/setter.

### Inheritance

`Admin` dan `Staff` mewarisi `User`.

```text
Admin extends User
Staff extends User
```

### Polymorphism

`Admin` dan `Staff` sama-sama bertipe `User`, tetapi method `canManageUsers()` memberi hasil berbeda.

### Abstraction

Terlihat dari pemisahan layer. GUI tidak tahu detail file JSON. GUI hanya memanggil service. Service tidak tahu detail tampilan. Storage hanya fokus persistence.

## 19. Kontribusi Developer 1

Sebagai Developer 1, kontribusi yang bisa saya jelaskan adalah:

- Membuat PRD dan requirement awal.
- Menyiapkan struktur project.
- Menyiapkan package `model`, `service`, `storage`, `gui`, `util`, `exception`, dan `main`.
- Menyiapkan dependency Gson di folder `lib`.
- Menyiapkan konfigurasi VS Code agar membaca jar.
- Menyiapkan folder `data` dan file JSON awal.
- Membuat model `User`, `Admin`, dan `Staff`.
- Membuat `UserService`.
- Membuat `UserStorage`.
- Membuat `ValidationException`.
- Membuat `SessionManager`.
- Membuat Login screen.
- Membuat Admin dan Staff dashboard.
- Membuat User Management screen.
- Membuat default admin agar aplikasi bisa dipakai pertama kali.
- Membuat validasi user dan login.

## 20. Closing

Kesimpulannya, bagian Developer 1 adalah fondasi akses aplikasi.

Tanpa authentication, user tidak bisa masuk ke sistem. Tanpa role, sistem tidak bisa membedakan hak akses Admin dan Staff. Tanpa User Management, Admin tidak bisa mengelola akun pengguna.

Bagian ini juga menunjukkan konsep OOP utama:

- Encapsulation melalui private field dan getter/setter.
- Inheritance melalui User, Admin, dan Staff.
- Polymorphism melalui method `canManageUsers()`.
- Exception handling melalui `ValidationException`.
- Layered architecture melalui pemisahan GUI, Service, Storage, dan Model.

Jadi fokus saya bukan hanya membuat login, tetapi juga membuat struktur akses dan fondasi user management untuk seluruh aplikasi EVO.
