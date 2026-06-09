package com.ermiyas.exchange.ui;

import com.ermiyas.exchange.Exchange;
import com.ermiyas.exchange.domain.model.user.AdminUser;
import com.ermiyas.exchange.domain.model.user.Tradeable;
import com.ermiyas.exchange.domain.model.user.User;

import javax.swing.*;

public class MainWindow extends JFrame {

    public MainWindow(Exchange exchange, User currentUser) {
        setTitle("P2P Exchange  —  " + currentUser.getUsername() + "  [" + currentUser.getRoleName() + "]");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(960, 680);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Markets",     new MarketsPanel(exchange, currentUser));
        tabs.addTab("My Activity", new ActivityPanel(exchange, currentUser));

        if ("PLAYER".equals(currentUser.getRoleName())) {
            tabs.addTab("Wallet", new WalletPanel(exchange, (Tradeable) currentUser));//i have to find better way to do this, shouldn't be casted.
        }

        if ("ADMIN".equals(currentUser.getRoleName())) {
            tabs.addTab("Admin", new AdminPanel(exchange, (AdminUser) currentUser));
        }

        add(tabs);
    }
}
