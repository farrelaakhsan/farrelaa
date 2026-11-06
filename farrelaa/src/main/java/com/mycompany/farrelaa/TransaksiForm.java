package com.mycompany.farrelaa;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Session;
import org.hibernate.Transaction;

/** Fitur 2: Transaksi Penjualan (keranjang, total, kembalian, stok berkurang otomatis). */
public class TransaksiForm extends JFrame {

    private final JComboBox<Barang> cmbBarang = new JComboBox<>();
    private final JSpinner spnJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
    private final JTextField txtBayar = new JTextField(10);
    private final JLabel lblTotal = new JLabel("Total: Rp 0");
    private final JLabel lblKembali = new JLabel("Kembalian: Rp 0");

    // Kolom keranjang: 0 id, 1 kode, 2 nama, 3 harga, 4 jumlah, 5 subtotal
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Kode", "Nama", "Harga", "Jumlah", "Subtotal"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    private final JTable tabel = new JTable(model);

    public TransaksiForm() {
        super("Transaksi Penjualan");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        susunTampilan();
        muatBarang();
        setSize(850, 480);
        setLocationRelativeTo(null);
    }

    private void susunTampilan() {
        JButton btnTambah = new JButton("Tambah ke Keranjang");
        btnTambah.addActionListener(e -> tambahKeKeranjang());

        JPanel atas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        atas.add(new JLabel("Barang"));
        atas.add(cmbBarang);
        atas.add(new JLabel("Jumlah"));
        atas.add(spnJumlah);
        atas.add(btnTambah);

        JButton btnHapusItem = new JButton("Hapus Item");
        JButton btnBatal = new JButton("Kosongkan");
        JButton btnSimpan = new JButton("Simpan Transaksi");
        btnHapusItem.addActionListener(e -> hapusItem());
        btnBatal.addActionListener(e -> kosongkan());
        btnSimpan.addActionListener(e -> simpan());

        txtBayar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { hitungKembali(); }
            @Override
            public void removeUpdate(DocumentEvent e) { hitungKembali(); }
            @Override
            public void changedUpdate(DocumentEvent e) { hitungKembali(); }
        });

        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bawah.add(btnHapusItem);
        bawah.add(btnBatal);
        bawah.add(lblTotal);
        bawah.add(new JLabel("   Bayar"));
        bawah.add(txtBayar);
        bawah.add(lblKembali);
        bawah.add(btnSimpan);

        setLayout(new BorderLayout(5, 5));
        add(atas, BorderLayout.NORTH);
        add(new JScrollPane(tabel), BorderLayout.CENTER);
        add(bawah, BorderLayout.SOUTH);
    }

    // Isi pilihan barang dari database
    private void muatBarang() {
        cmbBarang.removeAllItems();
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            List<Barang> list = s.createQuery("from Barang order by kode", Barang.class).list();
            for (Barang b : list) {
                cmbBarang.addItem(b);
            }
        } catch (Exception e) {
            Util.error(this, e);
        }
    }

    private void tambahKeKeranjang() {
        Barang b = (Barang) cmbBarang.getSelectedItem();
        if (b == null) {
            Util.info(this, "Belum ada barang. Tambah dulu di Kelola Data Barang.");
            return;
        }
        int qty = (Integer) spnJumlah.getValue();

        // Cek apakah barang sudah ada di keranjang
        int baris = -1;
        int sudah = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            if (((Integer) model.getValueAt(i, 0)).equals(b.getId())) {
                baris = i;
                sudah = (Integer) model.getValueAt(i, 4);
            }
        }
        if (sudah + qty > b.getStok()) {
            Util.info(this, "Stok tidak cukup! Stok " + b.getNama() + ": " + b.getStok());
            return;
        }
        int jumlah = sudah + qty;
        long subtotal = b.getHarga() * jumlah;
        if (baris >= 0) {
            model.setValueAt(jumlah, baris, 4);
            model.setValueAt(subtotal, baris, 5);
        } else {
            model.addRow(new Object[]{b.getId(), b.getKode(), b.getNama(), b.getHarga(), jumlah, subtotal});
        }
        spnJumlah.setValue(1);
        hitungTotal();
    }

    private void hapusItem() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            Util.info(this, "Pilih item di keranjang dulu!");
            return;
        }
        model.removeRow(baris);
        hitungTotal();
    }

    private void kosongkan() {
        model.setRowCount(0);
        txtBayar.setText("");
        hitungTotal();
    }

    private long totalKeranjang() {
        long total = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            total += ((Number) model.getValueAt(i, 5)).longValue();
        }
        return total;
    }

    private void hitungTotal() {
        lblTotal.setText("Total: " + Util.rp(totalKeranjang()));
        hitungKembali();
    }

    private void hitungKembali() {
        long kembali = 0;
        try {
            long bayar = Long.parseLong(txtBayar.getText().trim());
            kembali = Math.max(0, bayar - totalKeranjang());
        } catch (NumberFormatException e) {
            kembali = 0;
        }
        lblKembali.setText("Kembalian: " + Util.rp(kembali));
    }

    private void simpan() {
        if (model.getRowCount() == 0) {
            Util.info(this, "Keranjang masih kosong!");
            return;
        }
        long bayar;
        try {
            bayar = Long.parseLong(txtBayar.getText().trim());
        } catch (NumberFormatException e) {
            Util.info(this, "Isi uang bayar dengan angka!");
            return;
        }

        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            Transaksi t = new Transaksi();
            t.setTanggal(LocalDateTime.now());

            long total = 0;
            for (int i = 0; i < model.getRowCount(); i++) {
                int idBarang = (Integer) model.getValueAt(i, 0);
                int jumlah = (Integer) model.getValueAt(i, 4);

                // Ambil ulang dari database supaya stok dan harga terbaru
                Barang b = s.get(Barang.class, idBarang);
                if (b == null || b.getStok() < jumlah) {
                    tx.rollback();
                    Util.info(this, "Stok tidak cukup untuk: " + model.getValueAt(i, 2));
                    muatBarang();
                    return;
                }
                b.setStok(b.getStok() - jumlah); // stok berkurang otomatis

                DetailTransaksi d = new DetailTransaksi();
                d.setTransaksi(t);
                d.setBarang(b);
                d.setJumlah(jumlah);
                d.setHarga(b.getHarga());
                d.setSubtotal(b.getHarga() * jumlah);
                t.getDetail().add(d);
                total += d.getSubtotal();
            }

            if (bayar < total) {
                tx.rollback();
                Util.info(this, "Uang bayar kurang! Total: " + Util.rp(total));
                return;
            }
            t.setTotal(total);
            t.setBayar(bayar);
            t.setKembalian(bayar - total);

            s.persist(t);
            tx.commit();

            Util.info(this, "Transaksi tersimpan.\nTotal: " + Util.rp(total)
                    + "\nBayar: " + Util.rp(bayar)
                    + "\nKembalian: " + Util.rp(bayar - total));
            kosongkan();
            muatBarang();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            Util.error(this, e);
        }
    }
}
