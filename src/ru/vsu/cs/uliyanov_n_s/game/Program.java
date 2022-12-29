package ru.vsu.cs.uliyanov_n_s.game;

import java.util.Locale;

public class Program {

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ROOT);

        java.awt.EventQueue.invokeLater(() -> new MainForm().setVisible(true));
    }
}
