package com.mycompany.farrelaa;

import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Menu utama setelah login berhasil. */
public class MenuUtama extends JFrame {

    public MenuUtama() {
        super("Menu Utama - Toko Kelontong");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel judul = new JLabel("MENU UTAMA", SwingConstants.CENTER);
        JButton btnBarang = new JButton("Kelola Data Barang");
        JButton btnTransaksi = new JButton("Transaksi Penjualan");
        JButton btnLaporan = new JButton("Laporan");
        JButton btnKeluar = new JButton("Keluar");

        btnBarang.addActionListener(e -> new BarangForm().setVisible(true));
        btnTransaksi.addActionListener(e -> new TransaksiForm().setVisible(true));
        btnLaporan.addActionListener(e -> new LaporanForm().setVisible(true));
        btnKeluar.addActionListener(e -> {
            dispose();
            Main.main(new String[0]); // kembali ke form Login
        });

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        panel.add(judul);
        panel.add(btnBarang);
        panel.add(btnTransaksi);
        panel.add(btnLaporan);
        panel.add(btnKeluar);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
    }
}
