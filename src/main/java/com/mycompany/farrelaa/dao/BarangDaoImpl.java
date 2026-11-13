package com.mycompany.farrelaa.dao;

import com.mycompany.farrelaa.Barang;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/** Implementasi BarangDao memakai Hibernate. SessionFactory diisi Spring lewat setter. */
public class BarangDaoImpl implements BarangDao {

    private SessionFactory sessionFactory;

    // Getter dan setter (Spring memanggil setter ini: setter injection)
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Barang> findAll() {
        try (Session s = sessionFactory.openSession()) {
            return s.createQuery("from Barang order by kode", Barang.class).list();
        }
    }

    @Override
    public List<Barang> cari(String kata) {
        if (kata == null || kata.isBlank()) {
            return findAll();
        }
        try (Session s = sessionFactory.openSession()) {
            return s.createQuery(
                    "from Barang where kode like :k or nama like :k order by kode", Barang.class)
                    .setParameter("k", "%" + kata.trim() + "%").list();
        }
    }

    @Override
    public boolean kodeSudahDipakai(String kode, Integer idKecuali) {
        try (Session s = sessionFactory.openSession()) {
            Long ada;
            if (idKecuali == null) {
                ada = s.createQuery("select count(x) from Barang x where x.kode = :k", Long.class)
                        .setParameter("k", kode).uniqueResult();
            } else {
                ada = s.createQuery(
                        "select count(x) from Barang x where x.kode = :k and x.id <> :id", Long.class)
                        .setParameter("k", kode).setParameter("id", idKecuali).uniqueResult();
            }
            return ada != null && ada > 0;
        }
    }

    @Override
    public void simpan(Barang barang) {
        Transaction tx = null;
        try (Session s = sessionFactory.openSession()) {
            tx = s.beginTransaction();
            s.persist(barang);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public void ubah(Integer id, Barang baru) {
        Transaction tx = null;
        try (Session s = sessionFactory.openSession()) {
            tx = s.beginTransaction();
            Barang b = s.get(Barang.class, id);
            b.setKode(baru.getKode());
            b.setNama(baru.getNama());
            b.setHarga(baru.getHarga());
            b.setStok(baru.getStok());
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public void hapus(Integer id) {
        Transaction tx = null;
        try (Session s = sessionFactory.openSession()) {
            tx = s.beginTransaction();
            s.remove(s.get(Barang.class, id));
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }
}
