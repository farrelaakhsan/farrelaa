package com.mycompany.farrelaa.dao;

import com.mycompany.farrelaa.Barang;
import com.mycompany.farrelaa.DetailTransaksi;
import com.mycompany.farrelaa.Transaksi;
import com.mycompany.farrelaa.Util;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/** Implementasi TransaksiDao memakai Hibernate. SessionFactory diisi Spring lewat setter. */
public class TransaksiDaoImpl implements TransaksiDao {

    private SessionFactory sessionFactory;

    // Getter dan setter (Spring memanggil setter ini: setter injection)
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Transaksi simpan(List<ItemKeranjang> items, long bayar) {
        Transaction tx = null;
        try (Session s = sessionFactory.openSession()) {
            tx = s.beginTransaction();
            Transaksi t = new Transaksi();
            t.setTanggal(LocalDateTime.now());

            long total = 0;
            for (ItemKeranjang item : items) {
                // Ambil ulang dari database supaya stok dan harga terbaru
                Barang b = s.get(Barang.class, item.getIdBarang());
                if (b == null || b.getStok() < item.getJumlah()) {
                    tx.rollback();
                    throw new IllegalStateException("Stok tidak cukup untuk: "
                            + (b == null ? item.getNama() : b.getNama()));
                }
                b.setStok(b.getStok() - item.getJumlah()); // stok berkurang otomatis

                DetailTransaksi d = new DetailTransaksi();
                d.setTransaksi(t);
                d.setBarang(b);
                d.setJumlah(item.getJumlah());
                d.setHarga(b.getHarga());
                d.setSubtotal(b.getHarga() * item.getJumlah());
                t.getDetail().add(d);
                total += d.getSubtotal();
            }

            if (bayar < total) {
                tx.rollback();
                throw new IllegalStateException("Uang bayar kurang! Total: " + Util.rp(total));
            }
            t.setTotal(total);
            t.setBayar(bayar);
            t.setKembalian(bayar - total);

            s.persist(t);
            tx.commit();
            return t;
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public List<Transaksi> findByPeriode(LocalDateTime dari, LocalDateTime sampai) {
        try (Session s = sessionFactory.openSession()) {
            return s.createQuery(
                    "from Transaksi where tanggal >= :a and tanggal < :b order by tanggal", Transaksi.class)
                    .setParameter("a", dari).setParameter("b", sampai).list();
        }
    }

    @Override
    public Map<Integer, Long> terjualPerBarang(LocalDateTime dari, LocalDateTime sampai) {
        Map<Integer, Long> hasil = new HashMap<>();
        try (Session s = sessionFactory.openSession()) {
            List<Object[]> rows = s.createQuery(
                    "select d.barang.id, sum(d.jumlah) from DetailTransaksi d "
                    + "where d.transaksi.tanggal >= :a and d.transaksi.tanggal < :b group by d.barang.id",
                    Object[].class)
                    .setParameter("a", dari).setParameter("b", sampai).list();
            for (Object[] r : rows) {
                hasil.put((Integer) r[0], ((Number) r[1]).longValue());
            }
        }
        return hasil;
    }
}
