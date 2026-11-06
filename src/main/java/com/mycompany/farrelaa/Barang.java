package com.mycompany.farrelaa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Entity: tabel barang. */
@Entity
@Table(name = "barang")
public class Barang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "kode_barang", nullable = false, unique = true, length = 20)
    private String kode;

    @Column(nullable = false, length = 100)
    private String nama;

    @Column(nullable = false)
    private long harga;

    @Column(nullable = false)
    private int stok;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getKode() { return kode; }
    public void setKode(String kode) { this.kode = kode; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public long getHarga() { return harga; }
    public void setHarga(long harga) { this.harga = harga; }
    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }

    @Override
    public String toString() {
        return kode + " - " + nama + " (stok " + stok + ")";
    }
}
