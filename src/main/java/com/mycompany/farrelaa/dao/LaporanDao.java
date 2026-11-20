package com.mycompany.farrelaa.dao;

import java.time.LocalDate;
import net.sf.jasperreports.engine.JasperPrint;

/** DAO untuk Jasper Report: mengisi laporan (.jrxml di resources/reports) dengan data dari database. */
public interface LaporanDao {

    // Faktur/resi satu transaksi
    JasperPrint faktur(int idTransaksi);

    // Laporan penjualan pada rentang tanggal [dari, sampai] (keduanya termasuk)
    JasperPrint penjualan(LocalDate dari, LocalDate sampai);

    // Laporan stok barang + jumlah terjual pada rentang tanggal [dari, sampai]
    JasperPrint stok(LocalDate dari, LocalDate sampai);
}
