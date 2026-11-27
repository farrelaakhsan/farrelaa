package com.mycompany.farrelaa;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Tema tampilan (warna, font, tabel, tombol) untuk form fitur utama.
 * Cara pakai di form: Tema.terapkan(this, "Judul Form"); dipanggil setelah initComponents().
 * Dipisah dari kode designer supaya tidak tertimpa NetBeans.
 */
public class Tema {

    static final Color HIJAU = new Color(0x1F6F5C);
    static final Color HIJAU_TUA = new Color(0x14493D);
    static final Color LATAR = new Color(0xF4F8F6);
    static final Color GARIS = new Color(0xC9D6D1);
    static final Color ZEBRA = new Color(0xEAF3F0);
    static final Color AMBER = new Color(0xF2A93B);
    static final Color MERAH = new Color(0xC62828);
    static final Color ABU = new Color(0x6B7B76);
    static final Color TEKS = new Color(0x222222);

    private static final Font FONT = new Font("Arial", Font.PLAIN, 13);
    private static final Font FONT_TEBAL = new Font("Arial", Font.BOLD, 13);

    // Pasang header (judul + nama toko) lalu percantik semua komponen di form
    public static void terapkan(JFrame frame, String judul) {
        Container lama = frame.getContentPane();
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(LATAR);
        root.add(header(judul), BorderLayout.NORTH);
        root.add(lama, BorderLayout.CENTER);
        frame.setContentPane(root);
        gaya(lama);
    }

    // Tebalkan label penting (misal total dan kembalian). Panggil setelah terapkan().
    public static void sorot(JLabel... labels) {
        for (JLabel l : labels) {
            l.setFont(new Font("Arial", Font.BOLD, 14));
            l.setForeground(HIJAU_TUA);
        }
    }

    private static JPanel header(String judul) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(HIJAU);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 4, 0, AMBER),
                BorderFactory.createEmptyBorder(12, 18, 10, 18)));
        JLabel kiri = new JLabel(judul);
        kiri.setFont(new Font("Arial", Font.BOLD, 20));
        kiri.setForeground(Color.WHITE);
        JLabel kanan = new JLabel(Toko.NAMA);
        kanan.setFont(FONT);
        kanan.setForeground(Color.WHITE);
        p.add(kiri, BorderLayout.WEST);
        p.add(kanan, BorderLayout.EAST);
        return p;
    }

    // Telusuri komponen secara rekursif dan beri gaya sesuai jenisnya
    private static void gaya(Component c) {
        if (c instanceof JButton) {
            gayaTombol((JButton) c);
        } else if (c instanceof JTable) {
            gayaTabel((JTable) c);
        } else if (c instanceof JTextField) {
            JTextField t = (JTextField) c;
            t.setFont(FONT);
            t.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GARIS), BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        } else if (c instanceof JSpinner) {
            c.setFont(FONT); // isi spinner tidak ditelusuri (ada tombol dan field bawaan)
            return;
        } else if (c instanceof JComboBox) {
            c.setFont(FONT);
            c.setBackground(Color.WHITE);
            return;
        } else if (c instanceof JLabel) {
            c.setFont(FONT);
            c.setForeground(TEKS);
        } else if (c instanceof JScrollPane) {
            JScrollPane sp = (JScrollPane) c;
            if (!(sp.getBorder() instanceof TitledBorder)) {
                sp.setBorder(BorderFactory.createLineBorder(GARIS));
            }
            sp.getViewport().setBackground(Color.WHITE);
            gaya(sp.getViewport().getView()); // hanya isi tabel; scrollbar tidak disentuh
            return;
        } else if (c instanceof JPanel) {
            c.setBackground(LATAR);
        }
        if (c instanceof Container) {
            for (Component anak : ((Container) c).getComponents()) {
                gaya(anak);
            }
        }
    }

    // Warna tombol menurut tulisannya: Hapus merah, Bersihkan/Kosongkan abu, Cetak kuning, lainnya hijau
    private static void gayaTombol(JButton b) {
        String t = b.getText().toLowerCase();
        Color latar = HIJAU;
        Color teks = Color.WHITE;
        if (t.contains("hapus")) {
            latar = MERAH;
        } else if (t.contains("bersihkan") || t.contains("kosongkan") || t.contains("batal")) {
            latar = ABU;
        } else if (t.contains("cetak")) {
            latar = AMBER;
            teks = new Color(0x3A2A00);
        } else if (t.contains("simpan")) {
            latar = HIJAU_TUA;
        }
        b.setFont(FONT_TEBAL);
        b.setBackground(latar);
        b.setForeground(teks);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // Tabel: header hijau, baris selang-seling, garis tipis
    private static void gayaTabel(JTable t) {
        t.setRowHeight(26);
        t.setFont(FONT);
        t.setGridColor(GARIS);
        t.setShowVerticalLines(false);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setSelectionBackground(new Color(0xBFE3D6));
        t.setSelectionForeground(Color.BLACK);
        t.setFillsViewportHeight(true);

        DefaultTableCellRenderer isi = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tb, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tb, v, sel, false, row, col);
                if (!sel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : ZEBRA);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        };
        t.setDefaultRenderer(Object.class, isi);
        t.setDefaultRenderer(Number.class, isi);

        JTableHeader h = t.getTableHeader();
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(h.getPreferredSize().width, 30));
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tb, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tb, v, false, false, row, col);
                setOpaque(true);
                setBackground(HIJAU);
                setForeground(Color.WHITE);
                setFont(FONT_TEBAL);
                setHorizontalAlignment(LEFT);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
    }
}
