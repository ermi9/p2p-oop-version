package com.ermiyas.exchange.ui;

import com.ermiyas.exchange.Exchange;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.model.user.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginWindow extends JFrame {

    private final Exchange   exchange;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     cards      = new JPanel(cardLayout);

    public LoginWindow(Exchange exchange) {
        this.exchange = exchange;
        setTitle("P2P Betting Exchange");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 360);
        setLocationRelativeTo(null);
        setResizable(false);

        cards.add(buildLoginPanel(),    "LOGIN");
        cards.add(buildRegisterPanel(), "REGISTER");
        add(cards);
        cardLayout.show(cards, "LOGIN");
    }

    // -------------------------------------------------------------------------

    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        GridBagConstraints c = defaultConstraints();

        JTextField     usernameField = new JTextField(14);
        JPasswordField passwordField = new JPasswordField(14);

        addTitle(panel, c, 0, "P2P Betting Exchange", 20);
        addSubtitle(panel, c, 1, "Sign in to your account");
        addRow(panel, c, 2, "Username:", usernameField);
        addRow(panel, c, 3, "Password:", passwordField);

        JButton loginBtn    = new JButton("Login");
        JButton toRegister  = new JButton("Create an account");
        loginBtn.setPreferredSize(new Dimension(200, 30));
        styleLink(toRegister);

        addWide(panel, c, 4, loginBtn);
        addWide(panel, c, 5, toRegister);

        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    User user = exchange.login(
                            usernameField.getText().trim(),
                            new String(passwordField.getPassword()));
                    dispose();
                    new MainWindow(exchange, user).setVisible(true);
                } catch (ExchangeException ex) {
                    JOptionPane.showMessageDialog(LoginWindow.this,
                            ex.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        toRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cards, "REGISTER");
            }
        });

        return panel;
    }

    // -------------------------------------------------------------------------

    private JPanel buildRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        GridBagConstraints c = defaultConstraints();

        JTextField     usernameField = new JTextField(14);
        JTextField     emailField    = new JTextField(14);
        JPasswordField passwordField = new JPasswordField(14);

        addTitle(panel, c, 0, "Create Account", 20);
        addRow(panel, c, 1, "Username:", usernameField);
        addRow(panel, c, 2, "Email:",    emailField);
        addRow(panel, c, 3, "Password:", passwordField);

        JButton registerBtn = new JButton("Register");
        JButton toLogin     = new JButton("Back to Login");
        registerBtn.setPreferredSize(new Dimension(200, 30));
        styleLink(toLogin);

        addWide(panel, c, 4, registerBtn);
        addWide(panel, c, 5, toLogin);

        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    exchange.register(
                            usernameField.getText().trim(),
                            emailField.getText().trim(),
                            new String(passwordField.getPassword()));
                    JOptionPane.showMessageDialog(LoginWindow.this,
                            "Account created! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(cards, "LOGIN");
                } catch (ExchangeException ex) {
                    JOptionPane.showMessageDialog(LoginWindow.this,
                            ex.getMessage(), "Registration Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        toLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cards, "LOGIN");
            }
        });

        return panel;
    }

    // -------------------------------------------------------------------------
    // Layout helpers
    // -------------------------------------------------------------------------

    private GridBagConstraints defaultConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill   = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 6, 6, 6);
        return c;
    }

    private void addTitle(JPanel p, GridBagConstraints c, int row, String text, int fontSize) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        c.gridy = row; c.gridx = 0; c.gridwidth = 2;
        p.add(label, c);
        c.gridwidth = 1;
    }

    private void addSubtitle(JPanel p, GridBagConstraints c, int row, String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(Color.GRAY);
        c.gridy = row; c.gridx = 0; c.gridwidth = 2;
        p.add(label, c);
        c.gridwidth = 1;
    }

    private void addRow(JPanel p, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridy = row; c.gridx = 0; p.add(new JLabel(label), c);
        c.gridx = 1;               p.add(field, c);
    }

    private void addWide(JPanel p, GridBagConstraints c, int row, JComponent comp) {
        c.gridy = row; c.gridx = 0; c.gridwidth = 2;
        p.add(comp, c);
        c.gridwidth = 1;
    }

    private void styleLink(JButton btn) {
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setForeground(new Color(0, 100, 200));
    }
}
