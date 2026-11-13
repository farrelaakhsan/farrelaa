package com.mycompany.farrelaa.dao;

import com.mycompany.farrelaa.Barang;
import java.util.List;

/** DAO (Data Access Object) untuk data barang. */
public interface BarangDao {

    List<Barang> findAll();

    List<Barang> cari(String kata);

    // true jika kode sudah dipakai barang lain (idKecuali = id barang yang sedang diubah, boleh null)
    boolean kodeSudahDipakai(String kode, Integer idKecuali);

    void simpan(Barang barang);

    void ubah(Integer id, Barang baru);

    void hapus(Integer id);
}
