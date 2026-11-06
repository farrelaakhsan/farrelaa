package com.mycompany.farrelaa;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Session;

/** Fitur 3: Laporan penjualan dan stok per periode. */
public class LaporanForm extends JFrame {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private final JTextField txtDari = new JTextField(LocalDate.now().minusDays(30).toString(), 10);
    private final JTextField txtSampai = new JTextField(LocalDate.now().toString(), 10);
    private final JLabel lblTotal = new JLabel("Total penjualan: Rp 0");

    private final DefaultTableModel mJual = buatModel("No. Transaksi", "Tanggal", "Total", "Bayar", "Kembalian");
    private final DefaultTableModel mStok = buatModel("Kode", "Nama", "Stok Saat Ini", "Terjual (periode)");

    public LaporanForm() {
        super("Laporan");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JButton btnTampil = new JButton("Tampilkan");
        btnTampil.addActionListener(e -> tampilkan());

        JPanel atas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        atas.add(new JLabel("Dari (yyyy-MM-dd)"));
        atas.add(txtDari);
        atas.add(new JLabel("Sampai"));
        atas.add(txtSampai);
        atas.add(btnTampil);

        JPanel panelJual = new JPanel(new BorderLayout());
        panelJual.add(new JScrollPane(new JTable(mJual)), BorderLayout.CENTER);
        panelJual.add(lblTotal, BorderLayout.SOUTH);

        JTabbedPane tab = new JTabbedPane();
        tab.addTab("Penjualan", panelJual);
        tab.addTab("Stok", new JScrollPane(new JTable(mStok)));

        setLayout(new BorderLayout(5, 5));
        add(atas, BorderLayout.NORTH);
        add(tab, BorderLayout.CENTER);
        setSize(800, 450);
        setLocationRelativeTo(null);
        tampilkan();
    }

    private static DefaultTableModel buatModel(String... kolom) {
        return new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
    }

    private void tampilkan() {
        LocalDate dari;
        LocalDate sampai;
        try {
            dari = LocalDate.parse(txtDari.getText().trim());
            sampai = LocalDate.parse(txtSampai.getText().trim());
        } catch (DateTimeParseException e) {
            Util.info(this, "Format tanggal harus yyyy-MM-dd, contoh 2026-11-06");
            return;
        }
        if (sampai.isBefore(dari)) {
            Util.info(this, "Tanggal 'Sampai' tidak boleh sebelum 'Dari'!");
            return;
        }
        LocalDateTime a = dari.atStartOfDay();
        LocalDateTime b = sampai.plusDays(1).atStartOfDay();

        mJual.setRowCount(0);
        mStok.setRowCount(0);
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            // Laporan penjualan
            List<Transaksi> list = s.createQuery(
                    "from Transaksi where tanggal >= :a and tanggal < :b order by tanggal", Transaksi.class)
                    .setParameter("a", a).setParameter("b", b).list();
            long jumlah = 0;
            for (Transaksi t : list) {
                mJual.addRow(new Object[]{t.getId(), t.getTanggal().format(FMT),
                    Util.rp(t.getTotal()), Util.rp(t.getBayar()), Util.rp(t.getKembalian())});
                jumlah += t.getTotal();
            }
            lblTotal.setText("Total penjualan: " + Util.rp(jumlah) + " (" + list.size() + " transaksi)");

            // Laporan stok: stok sekarang + jumlah terjual pada periode
            Map<Integer, Long> terjual = new HashMap<>();
            List<Object[]> rows = s.createQuery(
                    "select d.barang.id, sum(d.jumlah) from DetailTransaksi d "
                    + "where d.transaksi.tanggal >= :a and d.transaksi.tanggal < :b group by d.barang.id",
                    Object[].class)
                    .setParameter("a", a).setParameter("b", b).list();
            for (Object[] r : rows) {
                terjual.put((Integer) r[0], ((Number) r[1]).longValue());
            }
            for (Barang br : s.createQuery("from Barang order by kode", Barang.class).list()) {
                mStok.addRow(new Object[]{br.getKode(), br.getNama(), br.getStok(),
                    terjual.getOrDefault(br.getId(), 0L)});
            }
        } catch (Exception e) {
            Util.error(this, e);
        }
    }
}
