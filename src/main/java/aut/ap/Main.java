package aut.ap;

import aut.ap.cli.CLI;
import aut.ap.framework.SingletonSessionFactory;
import aut.ap.gui.GUI;

import javax.swing.*;
import java.awt.*;

public class Main {
    private static JFrame frame;

    public static void main(String[] args) {
        System.setProperty("ide.native.clipboard.support", "false");
        createAndShowModeSelector();
    }

    private static void createAndShowModeSelector() {
        frame = new JFrame("Select Mode");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel titleLabel = new JLabel("Choose your preferred mode:", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(titleLabel, BorderLayout.NORTH);

        JButton guiButton = createModeButton("GUI Mode", () -> {
            frame.setVisible(false);
            try {
                GUI.start();
            } catch (Exception ex) {
                showError("GUI Error: " + ex.getMessage());
                frame.setVisible(true);
            }
        });

        JButton cliButton = createModeButton("Command Line Mode", () -> {
            frame.setVisible(false);
            try {
                CLI.start();
            } catch (Exception ex) {
                showError("CLI Error: " + ex.getMessage());
                frame.setVisible(true);
            }
        });

        JButton exitButton = createModeButton("Exit", () -> {
            frame.dispose();
            SingletonSessionFactory.close();
            System.exit(0);
        });

        mainPanel.add(guiButton);
        mainPanel.add(cliButton);
        mainPanel.add(exitButton);

        frame.add(mainPanel, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JButton createModeButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(200, 50));
        button.addActionListener(e -> action.run());
        return button;
    }

    private static void showError(String message) {
        JOptionPane.showMessageDialog(frame,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}