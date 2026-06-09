package com.ermiyas.exchange.ui;

import com.ermiyas.exchange.Exchange;
import com.ermiyas.exchange.domain.model.Money;
import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.domain.model.user.Tradeable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WalletPanel extends JPanel {

    private final Exchange  exchange;
    private final Tradeable account;
    private       JLabel    totalLabel;
    private       JLabel    reservedLabel;
    private       JLabel    availLabel;

    public WalletPanel(Exchange exchange, Tradeable account) {
        this.exchange = exchange;
        this.account  = account;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        build();
    }

    // 

    private void build() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill   = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 8, 6, 8);

        totalLabel    = new JLabel();
        reservedLabel = new JLabel();
        availLabel    = new JLabel();
        refreshLabels();

        c.gridy = 0; c.gridx = 0; add(new JLabel("Total Balance:"),      c);
        c.gridx = 1;               add(totalLabel,    c);
        c.gridy = 1; c.gridx = 0; add(new JLabel("Reserved (in bets):"), c);
        c.gridx = 1;               add(reservedLabel, c);
        c.gridy = 2; c.gridx = 0; add(new JLabel("Available:"),           c);
        c.gridx = 1;               add(availLabel,    c);

        c.gridy = 3; c.gridx = 0; c.gridwidth = 2;
        add(new JSeparator(), c);

        JTextField amountField = new JTextField("100.00", 12);
        c.gridy = 4; c.gridwidth = 1; c.gridx = 0; add(new JLabel("Amount ($):"), c);
        c.gridx = 1; add(amountField, c);

        JButton depositBtn  = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JPanel  btnRow      = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.add(depositBtn);
        btnRow.add(withdrawBtn);
        c.gridy = 5; c.gridx = 0; c.gridwidth = 2;
        add(btnRow, c);

        depositBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Money amount = Money.of(amountField.getText().trim());
                    exchange.deposit(account, amount);
                    refreshLabels();
                    JOptionPane.showMessageDialog(WalletPanel.this,
                            "Deposited " + amount, "Info", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(WalletPanel.this,
                            ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        withdrawBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Money amount = Money.of(amountField.getText().trim());
                    exchange.withdraw(account, amount);
                    refreshLabels();
                    JOptionPane.showMessageDialog(WalletPanel.this,
                            "Withdrew " + amount, "Info", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(WalletPanel.this,
                            ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // 

    private void refreshLabels() {
        Wallet w = account.getWallet();
        totalLabel.setText(w.getTotalBalance().toString());
        reservedLabel.setText(w.getReservedBalance().toString());
        availLabel.setText(w.availableBalance().toString());
    }
}
