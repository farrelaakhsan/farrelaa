package com.mycompany.farrelaa;

import java.awt.Component;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JOptionPane;

/** Fungsi bantu: format rupiah dan pesan dialog. */
public class Util {

    public static String rp(long n) {
        return "Rp " + NumberFormat.getInstance(Locale.forLanguageTag("id-ID")).format(n);
    }

    public static void info(Component parent, String pesan) {
        JOptionPane.showMessageDialog(parent, pesan);
    }

    public static void error(Component parent, Exception e) {
        JOptionPane.showMessageDialog(parent, "Terjadi kesalahan: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
