# EVO PostgreSQL Lab

Branch ini adalah branch belajar database untuk mencoba PostgreSQL, Docker Compose, Flyway, dan JDBC.
Versi utama tugas kuliah tetap bisa menggunakan JSON di branch utama.

## Stack

- Java Swing application
- PostgreSQL via Docker Compose
- Flyway via Docker Compose
- PostgreSQL JDBC driver di folder `lib/`

## Database Defaults

```text
Database: evo_db
User: evo_user
Password: evo_pass
Host: localhost
Port: 55432
JDBC URL: jdbc:postgresql://localhost:55432/evo_db
```

Nilai koneksi Java bisa dioverride dengan environment variable:

```text
EVO_DB_URL
EVO_DB_USER
EVO_DB_PASSWORD
```

## Menjalankan PostgreSQL

```powershell
docker compose up -d postgres
```

## Menjalankan Flyway Migration

```powershell
docker compose run --rm flyway
```

Migration SQL ada di:

```text
db/migration/
```

## Compile Aplikasi

```powershell
javac -cp "lib/*" -d out (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

## Run Aplikasi

```powershell
java -cp "out;lib/*" main.Main
```

## Reset Database Lab

Command ini menghapus volume PostgreSQL lab dan semua data database:

```powershell
docker compose down -v
```

Setelah reset, jalankan ulang:

```powershell
docker compose up -d postgres
docker compose run --rm flyway
```

## Scope Saat Ini

Modul yang sudah memakai PostgreSQL:

- Client Management

Modul yang masih memakai JSON:

- User Management
- Event Management
- Vendor Management
- Payment Management
