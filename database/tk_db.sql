-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.30 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.1.0.6537
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for tk_db
CREATE DATABASE IF NOT EXISTS `tk_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `tk_db`;

-- Dumping structure for table tk_db.barang
DROP TABLE IF EXISTS `barang`;
CREATE TABLE IF NOT EXISTS `barang` (
  `id` int NOT NULL AUTO_INCREMENT,
  `harga` bigint NOT NULL,
  `kode_barang` varchar(20) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `stok` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKtk8j5i6x0etut6r194v1cavqa` (`kode_barang`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table tk_db.barang: ~6 rows (approximately)
INSERT INTO `barang` (`id`, `harga`, `kode_barang`, `nama`, `stok`) VALUES
	(1, 4000, 'BR001', 'Sabun Nuvo', 8),
	(2, 4000, 'BR002', 'Teh Pucuk Harum', 20),
	(3, 5000, 'BR003', 'Sprite', 10),
	(4, 3500, 'BR004', 'Indomie Goreng', 23),
	(5, 3000, 'BR005', 'Aqua 600 ml', 19),
	(6, 3000, 'BR006', 'Le Minarale 600 ml', 15),
	(7, 2500, 'BR007', 'Torabika Cappuccino', 17);

-- Dumping structure for table tk_db.detail_transaksi
DROP TABLE IF EXISTS `detail_transaksi`;
CREATE TABLE IF NOT EXISTS `detail_transaksi` (
  `id` int NOT NULL AUTO_INCREMENT,
  `harga` bigint NOT NULL,
  `jumlah` int NOT NULL,
  `subtotal` bigint NOT NULL,
  `id_barang` int NOT NULL,
  `id_transaksi` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcw9qt9tk2x2mhyqnfy2ok7kl4` (`id_barang`),
  KEY `FKb75o6ojbg0fiwps16mqsvhueh` (`id_transaksi`),
  CONSTRAINT `FKb75o6ojbg0fiwps16mqsvhueh` FOREIGN KEY (`id_transaksi`) REFERENCES `transaksi` (`id`),
  CONSTRAINT `FKcw9qt9tk2x2mhyqnfy2ok7kl4` FOREIGN KEY (`id_barang`) REFERENCES `barang` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table tk_db.detail_transaksi: ~14 rows (approximately)
INSERT INTO `detail_transaksi` (`id`, `harga`, `jumlah`, `subtotal`, `id_barang`, `id_transaksi`) VALUES
	(1, 4000, 1, 4000, 1, 1),
	(2, 4000, 1, 4000, 1, 2),
	(3, 4000, 1, 4000, 2, 2),
	(4, 5000, 1, 5000, 3, 3),
	(5, 4000, 1, 4000, 1, 3),
	(6, 5000, 3, 15000, 3, 4),
	(7, 3500, 7, 24500, 4, 4),
	(8, 4000, 6, 24000, 2, 4),
	(9, 4000, 1, 4000, 1, 5),
	(10, 4000, 1, 4000, 2, 5),
	(11, 5000, 1, 5000, 3, 5),
	(12, 3500, 2, 7000, 4, 5),
	(13, 3000, 1, 3000, 5, 5),
	(14, 2500, 5, 12500, 7, 5);

-- Dumping structure for table tk_db.transaksi
DROP TABLE IF EXISTS `transaksi`;
CREATE TABLE IF NOT EXISTS `transaksi` (
  `id` int NOT NULL AUTO_INCREMENT,
  `bayar` bigint NOT NULL,
  `kembalian` bigint NOT NULL,
  `tanggal` datetime(6) NOT NULL,
  `total` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table tk_db.transaksi: ~0 rows (approximately)
INSERT INTO `transaksi` (`id`, `bayar`, `kembalian`, `tanggal`, `total`) VALUES
	(1, 5000, 1000, '2026-11-06 15:04:32.513855', 4000),
	(2, 20000, 12000, '2026-11-12 06:37:54.310051', 8000),
	(3, 10000, 1000, '2026-11-13 08:39:51.600688', 9000),
	(4, 100000, 36500, '2026-11-19 08:36:53.060027', 63500),
	(5, 50000, 14500, '2026-11-20 09:07:42.552142', 35500);

-- Dumping structure for table tk_db.users
DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table tk_db.users: ~0 rows (approximately)
INSERT INTO `users` (`id`, `username`, `password`) VALUES
	(1, 'admin', 'admin123');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
