package com.mycompany.farrelaa;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Session;
import org.hibernate.Transaction;

/** Fitur 1: Kelola Data Barang (tambah, ubah, hapus, cari) memakai Hibernate. */
public class BarangForm extends JFrame {

    private final JTextField txtKode = new JTextField(10);
    private final JTextField txtNama = new JTextField(18);
    private final JTextField txtHarga = new JTextField(9);
    private final JTextField txtStok = new JTextField(5);
    private final JTextField txtCari = new JTextField(14);

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Kode", "Nama", "Harga", "Stok"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    private final JTable tabel = new JTable(model);
    private Integer idTerpilih = null; // id barang yang dipilih di tabel

    public BarangForm() {
        super("Kelola Data Barang");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        susunTampilan();
        muatData(null);
        setSize(900, 450);
        setLocationRelativeTo(null);
    }

    private void susunTampilan() {
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Kode"));
        form.add(txtKode);
        form.add(new JLabel("Nama"));
        form.add(txtNama);
        form.add(new JLabel("Harga"));
        form.add(txtHarga);
        form.add(new JLabel("Stok"));
        form.add(txtStok);

        JButton btnTambah = new JButton("Tambah");
        JButton btnUbah = new JButton("Ubah");
        JButton btnHapus = new JButton("Hapus");
        JButton btnBersih = new JButton("Bersihkan");
        JButton btnCari = new JButton("Cari");

        btnTambah.addActionListener(e -> tambah());
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> hapus());
        btnBersih.addActionListener(e -> bersihkan());
        btnCari.addActionListener(e -> muatData(txtCari.getText()));
        txtCari.addActionListener(e -> muatData(txtCari.getText()));

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tombol.add(btnTambah);
        tombol.add(btnUbah);
        tombol.add(btnHapus);
        tombol.add(btnBersih);
        tombol.add(new JLabel("   Cari (kode/nama)"));
        tombol.add(txtCari);
        tombol.add(btnCari);

        JPanel atas = new JPanel(new GridLayout(2, 1));
        atas.add(form);
        atas.add(tombol);

        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                isiFormDariTabel();
            }
        });

        setLayout(new BorderLayout(5, 5));
        add(atas, BorderLayout.NORTH);
        add(new JScrollPane(tabel), BorderLayout.CENTER);
    }

    // Ambil data dari database (semua, atau yang cocok dengan kata kunci)
    private void muatData(String kata) {
        model.setRowCount(0);
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            List<Barang> list;
            if (kata == null || kata.isBlank()) {
                list = s.createQuery("from Barang order by kode", Barang.class).list();
            } else {
                list = s.createQuery(
                        "from Barang where kode like :k or nama like :k order by kode", Barang.class)
                        .setParameter("k", "%" + kata.trim() + "%").list();
            }
            for (Barang b : list) {
                model.addRow(new Object[]{b.getId(), b.getKode(), b.getNama(), b.getHarga(), b.getStok()});
            }
        } catch (Exception e) {
            Util.error(this, e);
        }
    }

    private void isiFormDariTabel() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            return;
        }
        idTerpilih = (Integer) model.getValueAt(baris, 0);
        txtKode.setText(String.valueOf(model.getValueAt(baris, 1)));
        txtNama.setText(String.valueOf(model.getValueAt(baris, 2)));
        txtHarga.setText(String.valueOf(model.getValueAt(baris, 3)));
        txtStok.setText(String.valueOf(model.getValueAt(baris, 4)));
    }

    private void bersihkan() {
        idTerpilih = null;
        txtKode.setText("");
        txtNama.setText("");
        txtHarga.setText("");
        txtStok.setText("");
        tabel.clearSelection();
    }

    // Baca dan validasi isian form; kembalikan null jika tidak valid
    private Barang bacaForm() {
        String kode = txtKode.getText().trim();
        String nama = txtNama.getText().trim();
        if (kode.isEmpty() || nama.isEmpty()) {
            Util.info(this, "Kode dan nama barang wajib diisi!");
            return null;
        }
        try {
            long harga = Long.parseLong(txtHarga.getText().trim());
            int stok = Integer.parseInt(txtStok.getText().trim());
            if (harga < 0 || stok < 0) {
                Util.info(this, "Harga dan stok tidak boleh negatif!");
                return null;
            }
            Barang b = new Barang();
            b.setKode(kode);
            b.setNama(nama);
            b.setHarga(harga);
            b.setStok(stok);
            return b;
        } catch (NumberFormatException e) {
            Util.info(this, "Harga dan stok harus berupa angka bulat!");
            return null;
        }
    }

    private void tambah() {
        Barang b = bacaForm();
        if (b == null) {
            return;
        }
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Long ada = s.createQuery("select count(x) from Barang x where x.kode = :k", Long.class)
                    .setParameter("k", b.getKode()).uniqueResult();
            if (ada != null && ada > 0) {
                Util.info(this, "Kode barang sudah dipakai!");
                return;
            }
            tx = s.beginTransaction();
            s.persist(b);
            tx.commit();
            Util.info(this, "Barang berhasil ditambahkan.");
            bersihkan();
            muatData(null);
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            Util.error(this, e);
        }
    }

    private void ubah() {
        if (idTerpilih == null) {
            Util.info(this, "Pilih barang di tabel dulu!");
            return;
        }
        Barang baru = bacaForm();
        if (baru == null) {
            return;
        }
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Long ada = s.createQuery(
                    "select count(x) from Barang x where x.kode = :k and x.id <> :id", Long.class)
                    .setParameter("k", baru.getKode()).setParameter("id", idTerpilih).uniqueResult();
            if (ada != null && ada > 0) {
                Util.info(this, "Kode barang sudah dipakai barang lain!");
                return;
            }
            tx = s.beginTransaction();
            Barang b = s.get(Barang.class, idTerpilih);
            b.setKode(baru.getKode());
            b.setNama(baru.getNama());
            b.setHarga(baru.getHarga());
            b.setStok(baru.getStok());
            tx.commit();
            Util.info(this, "Barang berhasil diubah.");
            bersihkan();
            muatData(null);
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            Util.error(this, e);
        }
    }

    private void hapus() {
        if (idTerpilih == null) {
            Util.info(this, "Pilih barang di tabel dulu!");
            return;
        }
        int pilih = JOptionPane.showConfirmDialog(this, "Hapus barang ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (pilih != JOptionPane.YES_OPTION) {
            return;
        }
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            s.remove(s.get(Barang.class, idTerpilih));
            tx.commit();
            Util.info(this, "Barang berhasil dihapus.");
            bersihkan();
            muatData(null);
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            Util.info(this, "Gagal menghapus. Barang mungkin sudah dipakai di transaksi.");
        }
    }
}
