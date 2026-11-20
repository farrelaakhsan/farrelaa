package com.mycompany.farrelaa;

import com.mycompany.farrelaa.dao.LaporanDao;
import java.awt.Component;
import java.io.File;
import java.time.LocalDate;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/** Penghubung form dengan Jasper Report: ambil laporan dari LaporanDao, lalu pratinjau atau simpan ke PDF. */
public class LaporanHelper {

    public static void cetakFaktur(Component parent, int idTransaksi) {
        try {
            JasperPrint print = AppContext.getBean(LaporanDao.class).faktur(idTransaksi);
            keluarkan(parent, print, "Faktur-INV-" + String.format("%06d", idTransaksi));
        } catch (Exception e) {
            Util.error(parent, e);
        }
    }

    public static void cetakPenjualan(Component parent, LocalDate dari, LocalDate sampai) {
        try {
            JasperPrint print = AppContext.getBean(LaporanDao.class).penjualan(dari, sampai);
            keluarkan(parent, print, "Laporan-Penjualan-" + dari + "_" + sampai);
        } catch (Exception e) {
            Util.error(parent, e);
        }
    }

    public static void cetakStok(Component parent, LocalDate dari, LocalDate sampai) {
        try {
            JasperPrint print = AppContext.getBean(LaporanDao.class).stok(dari, sampai);
            keluarkan(parent, print, "Laporan-Stok-" + dari + "_" + sampai);
        } catch (Exception e) {
            Util.error(parent, e);
        }
    }

    // Tanya: pratinjau di layar atau simpan sebagai file PDF
    private static void keluarkan(Component parent, JasperPrint print, String namaFile) throws Exception {
        String[] pilihan = {"Pratinjau", "Simpan PDF", "Batal"};
        int p = JOptionPane.showOptionDialog(parent, "Laporan siap. Mau diapakan?", "Cetak Laporan",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, pilihan, pilihan[0]);
        if (p == 0) {
            JasperViewer viewer = new JasperViewer(print, false); // false: menutup viewer tidak menutup aplikasi
            viewer.setTitle("Pratinjau Laporan");
            viewer.setVisible(true);
        } else if (p == 1) {
            simpanPdf(parent, print, namaFile);
        }
    }

    // Simpan laporan ke file PDF di lokasi pilihan user
    private static void simpanPdf(Component parent, JasperPrint print, String namaFile) throws Exception {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Simpan PDF");
        fc.setFileFilter(new FileNameExtensionFilter("File PDF (*.pdf)", "pdf"));
        fc.setSelectedFile(new File(namaFile + ".pdf"));
        if (fc.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = fc.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new File(file.getParentFile(), file.getName() + ".pdf");
        }
        if (file.exists() && JOptionPane.showConfirmDialog(parent, "File sudah ada. Timpa?", "Simpan PDF",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        JasperExportManager.exportReportToPdfFile(print, file.getAbsolutePath());
        Util.info(parent, "PDF tersimpan:\n" + file.getAbsolutePath());
    }
}
