package com.mycompany.farrelaa.dao;

import com.mycompany.farrelaa.Transaksi;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** DAO (Data Access Object) untuk transaksi penjualan dan laporan. */
public interface TransaksiDao {

    /**
     * Simpan transaksi: stok barang berkurang otomatis.
     * Melempar IllegalStateException (dengan pesan) jika stok atau uang bayar kurang.
     */
    Transaksi simpan(List<ItemKeranjang> items, long bayar);

    // Transaksi pada rentang waktu [dari, sampai)
    List<Transaksi> findByPeriode(LocalDateTime dari, LocalDateTime sampai);

    // Jumlah terjual per barang (id barang -> jumlah) pada rentang waktu [dari, sampai)
    Map<Integer, Long> terjualPerBarang(LocalDateTime dari, LocalDateTime sampai);
}
