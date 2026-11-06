package com.mycompany.farrelaa;

/**
 * Titik awal aplikasi: menampilkan LoginForm di dalam JFrame.
 *
 * @author Acer
 */
public class Main {

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            javax.swing.JFrame frame = new javax.swing.JFrame("Login");

            frame.setDefaultCloseOperation(
                    javax.swing.JFrame.EXIT_ON_CLOSE
            );

            frame.setContentPane(new LoginForm());
            frame.pack();
            frame.setLocationRelativeTo(null); // tampil di tengah layar
            frame.setVisible(true);
        });
    }
}
