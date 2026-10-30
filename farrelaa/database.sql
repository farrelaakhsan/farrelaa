-- Jalankan di HeidiSQL (Laragon) pada database tk_db
CREATE DATABASE IF NOT EXISTS tk_db;
USE tk_db;

CREATE TABLE IF NOT EXISTS pengguna (
    id_pengguna INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    role        ENUM('pemilik','kasir') NOT NULL
);

-- Akun contoh untuk uji login
INSERT IGNORE INTO pengguna (username, password, role) VALUES
    ('pemilik', 'pemilik123', 'pemilik'),
    ('kasir',   'kasir123',   'kasir');
