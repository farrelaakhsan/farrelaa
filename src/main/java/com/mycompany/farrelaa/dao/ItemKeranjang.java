package com.mycompany.farrelaa.dao;

/** Satu baris keranjang belanja: barang yang dibeli dan jumlahnya. */
public class ItemKeranjang {

    private Integer idBarang;
    private String nama;
    private int jumlah;

    public Integer getIdBarang() { return idBarang; }
    public void setIdBarang(Integer idBarang) { this.idBarang = idBarang; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }
}
