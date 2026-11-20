package com.mycompany.farrelaa.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Implementasi LaporanDao. File laporan dibaca dari src/main/resources/reports/*.jrxml,
 * lalu diisi lewat koneksi JDBC milik Hibernate (session.doWork). SessionFactory diisi Spring lewat setter.
 */
public class LaporanDaoImpl implements LaporanDao {

    private static final Locale ID = Locale.forLanguageTag("id-ID");
    private static final DateTimeFormatter TGL = DateTimeFormatter.ofPattern("dd MMMM yyyy", ID);

    // Laporan yang sudah dikompilasi disimpan supaya cetak berikutnya cepat
    private static final Map<String, JasperReport> CACHE = new HashMap<>();

    private SessionFactory sessionFactory;

    // Getter dan setter (Spring memanggil setter ini: setter injection)
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public JasperPrint faktur(int idTransaksi) {
        Map<String, Object> param = new HashMap<>();
        param.put("ID_TRANSAKSI", idTransaksi);
        return isi("faktur", param);
    }

    @Override
    public JasperPrint penjualan(LocalDate dari, LocalDate sampai) {
        return isi("laporan_penjualan", paramPeriode(dari, sampai));
    }

    @Override
    public JasperPrint stok(LocalDate dari, LocalDate sampai) {
        return isi("laporan_stok", paramPeriode(dari, sampai));
    }

    // Parameter periode: SAMPAI dikirim sebagai awal hari berikutnya (tanggal akhir ikut terhitung)
    private Map<String, Object> paramPeriode(LocalDate dari, LocalDate sampai) {
        Map<String, Object> param = new HashMap<>();
        param.put("DARI", Timestamp.valueOf(dari.atStartOfDay()));
        param.put("SAMPAI", Timestamp.valueOf(sampai.plusDays(1).atStartOfDay()));
        param.put("PERIODE", dari.format(TGL) + " s.d. " + sampai.format(TGL));
        return param;
    }

    // Baca dan kompilasi file .jrxml (hanya sekali per laporan)
    private synchronized JasperReport muat(String nama) throws JRException {
        JasperReport laporan = CACHE.get(nama);
        if (laporan == null) {
            try (InputStream in = LaporanDaoImpl.class.getResourceAsStream("/reports/" + nama + ".jrxml")) {
                if (in == null) {
                    throw new JRException("File laporan tidak ditemukan: reports/" + nama + ".jrxml");
                }
                laporan = JasperCompileManager.compileReport(in);
            } catch (IOException e) {
                throw new JRException(e);
            }
            CACHE.put(nama, laporan);
        }
        return laporan;
    }

    // Isi laporan dengan data dari database memakai koneksi milik session Hibernate
    private JasperPrint isi(String nama, Map<String, Object> param) {
        param.put(JRParameter.REPORT_LOCALE, ID);
        JasperPrint[] hasil = new JasperPrint[1];
        try (Session s = sessionFactory.openSession()) {
            JasperReport laporan = muat(nama);
            s.doWork(koneksi -> {
                try {
                    hasil[0] = JasperFillManager.fillReport(laporan, param, koneksi);
                } catch (JRException e) {
                    throw new IllegalStateException("Gagal mengisi laporan: " + e.getMessage(), e);
                }
            });
        } catch (JRException e) {
            throw new IllegalStateException("Gagal membuat laporan: " + e.getMessage(), e);
        }
        return hasil[0];
    }
}
