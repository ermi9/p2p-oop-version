package com.ermiyas.exchange.ui;

import com.ermiyas.exchange.Exchange;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.model.Bet;
import com.ermiyas.exchange.domain.model.Money;
import com.ermiyas.exchange.domain.model.Offer;
import com.ermiyas.exchange.domain.model.user.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActivityPanel extends JPanel {

    private final Exchange         exchange;
    private final User             currentUser;
    private       DefaultTableModel offersModel;
    private       DefaultTableModel betsModel;

    public ActivityPanel(Exchange exchange, User currentUser) {
        this.exchange    = exchange;
        this.currentUser = currentUser;

        setLayout(new GridLayout(2, 1, 0, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildOffersSection());
        add(buildBetsSection());
    }

    // 

    private JPanel buildOffersSection() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBorder(BorderFactory.createTitledBorder("My Offers"));

        String[] cols = {"Offer#", "Event", "Outcome", "Odds", "Original", "Remaining", "Status"};
        offersModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(offersModel);
        table.setRowHeight(22);
        refreshOffers();

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton refreshBtn = new JButton("Refresh");
        south.add(refreshBtn);

        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshOffers();
            }
        });

        if ("PLAYER".equals(currentUser.getRoleName())) {
            JButton cancelBtn = new JButton("Cancel Selected");
            south.add(cancelBtn);

            cancelBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int row = table.getSelectedRow();
                    if (row < 0) {
                        JOptionPane.showMessageDialog(ActivityPanel.this,
                                "Select an offer to cancel.", "Warning", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    Long offerId = (Long) offersModel.getValueAt(row, 0);
                    try {
                        exchange.cancelOffer(offerId, currentUser.getId());
                        JOptionPane.showMessageDialog(ActivityPanel.this,
                                "Offer cancelled. Stake returned.", "Info", JOptionPane.INFORMATION_MESSAGE);
                        refreshOffers();
                    } catch (ExchangeException ex) {
                        JOptionPane.showMessageDialog(ActivityPanel.this,
                                ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildBetsSection() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBorder(BorderFactory.createTitledBorder("My Matched Bets"));

        String[] cols = {"Ref", "Event", "My Side", "Outcome Bet", "Odds", "My Stake", "Status"};
        betsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(betsModel);
        table.setRowHeight(22);
        refreshBets();

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshBets();
            }
        });

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        south.add(refreshBtn);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    // 

    private void refreshOffers() {
        offersModel.setRowCount(0);
        for (Offer o : exchange.getOffersForUser(currentUser.getId())) {
            offersModel.addRow(new Object[]{
                    o.getId(), o.getEvent().getDisplayName(),
                    o.getPredictedOutcome(), o.getOdds(),
                    o.getOriginalStake(), o.getRemainingStake(), o.getStatus()
            });
        }
    }

    private void refreshBets() {
        betsModel.setRowCount(0);
        for (Bet b : exchange.getBetsForUser(currentUser.getId())) {
            boolean isMaker = b.getMaker().getId().equals(currentUser.getId());
            String  side    = isMaker ? "MAKER" : "TAKER";
            Money   myStake = isMaker ? b.getMakerStake() : b.getTakerLiability();
            betsModel.addRow(new Object[]{
                    b.getReference(), b.getOffer().getEvent().getDisplayName(),
                    side, b.getOffer().getPredictedOutcome(),
                    b.getOdds(), myStake, b.getStatus()
            });
        }
    }
}
