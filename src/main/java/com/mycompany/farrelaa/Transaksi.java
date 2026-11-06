package com.mycompany.farrelaa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Entity: tabel transaksi (header penjualan). */
@Entity
@Table(name = "transaksi")
public class Transaksi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDateTime tanggal;

    private long total;
    private long bayar;
    private long kembalian;

    @OneToMany(mappedBy = "transaksi", cascade = CascadeType.ALL)
    private List<DetailTransaksi> detail = new ArrayList<>();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public LocalDateTime getTanggal() { return tanggal; }
    public void setTanggal(LocalDateTime tanggal) { this.tanggal = tanggal; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public long getBayar() { return bayar; }
    public void setBayar(long bayar) { this.bayar = bayar; }
    public long getKembalian() { return kembalian; }
    public void setKembalian(long kembalian) { this.kembalian = kembalian; }
    public List<DetailTransaksi> getDetail() { return detail; }
    public void setDetail(List<DetailTransaksi> detail) { this.detail = detail; }
}
