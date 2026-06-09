package com.ermiyas.exchange.ui;

import com.ermiyas.exchange.Exchange;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.model.Event;
import com.ermiyas.exchange.domain.model.EventStatus;
import com.ermiyas.exchange.domain.model.Money;
import com.ermiyas.exchange.domain.model.Odds;
import com.ermiyas.exchange.domain.model.MarketType;
import com.ermiyas.exchange.domain.model.Offer;
import com.ermiyas.exchange.domain.model.OfferStatus;
import com.ermiyas.exchange.domain.model.Outcome;
import com.ermiyas.exchange.domain.model.user.StandardUser;
import com.ermiyas.exchange.domain.model.user.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MarketsPanel extends JPanel {

    private final Exchange exchange;
    private final User     currentUser;
    private JPanel         cardsArea;

    public MarketsPanel(Exchange exchange, User currentUser) {
        this.exchange    = exchange;
        this.currentUser = currentUser;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JLabel title  = new JLabel("Markets");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        JButton refreshBtn = new JButton("Refresh");
        header.add(title);
        header.add(refreshBtn);
        add(header, BorderLayout.NORTH);

        cardsArea = new JPanel();
        cardsArea.setLayout(new BoxLayout(cardsArea, BoxLayout.Y_AXIS));
        cardsArea.setBackground(new Color(245, 245, 245));

        JScrollPane scroll = new JScrollPane(cardsArea);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });

        refresh();
    }

    public void refresh() {
        cardsArea.removeAll();
        for (Event event : exchange.getAllEvents()) {
            cardsArea.add(buildCard(event));
            cardsArea.add(Box.createRigidArea(new Dimension(0, 8)));
        }
        cardsArea.revalidate();
        cardsArea.repaint();
    }

    // 

    private JPanel buildCard(Event event) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210), 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Left: event info
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel matchLabel = new JLabel(event.getDisplayName());
        matchLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        matchLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel metaLabel = new JLabel(
                event.getLeague().getDisplayName()
                + "   |   " + event.getMarketType()
                + "   |   " + event.getKickOffLabel());
        metaLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        metaLabel.setForeground(Color.GRAY);
        metaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        int openCount = 0;
        for (Offer o : event.getOffers()) {
            if (o.getStatus() == OfferStatus.OPEN || o.getStatus() == OfferStatus.PARTIALLY_TAKEN) {
                openCount++;
            }
        }
        JLabel offersLabel = new JLabel(openCount + " open offer" + (openCount != 1 ? "s" : ""));
        offersLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        offersLabel.setForeground(new Color(80, 80, 80));
        offersLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(matchLabel);
        info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(metaLabel);
        info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(offersLabel);

        // for positionning status + action buttons
        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.setOpaque(false);

        Color statusColor = event.getStatus() == EventStatus.OPEN
                ? new Color(0, 140, 70) : new Color(120, 120, 120);
        JLabel statusLabel = new JLabel(event.getStatus().toString());
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        statusLabel.setForeground(statusColor);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton viewBtn = new JButton("View Offers");
        viewBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showOffersDialog(event);
            }
        });

        actions.add(statusLabel);
        actions.add(Box.createRigidArea(new Dimension(0, 6)));
        actions.add(viewBtn);

        if ("PLAYER".equals(currentUser.getRoleName())) {
            final StandardUser player = (StandardUser) currentUser;

            JButton placeBtn = new JButton("Place Offer");
            placeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            placeBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (event.getStatus() != EventStatus.OPEN) {
                        JOptionPane.showMessageDialog(MarketsPanel.this,
                                "Event is not open.", "Warning", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    showPlaceOfferDialog(event, player);
                }
            });

            JButton matchBtn = new JButton("Match Offer");
            matchBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            matchBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showMatchOfferDialog(event, player);
                }
            });

            actions.add(Box.createRigidArea(new Dimension(0, 4)));
            actions.add(placeBtn);
            actions.add(Box.createRigidArea(new Dimension(0, 4)));
            actions.add(matchBtn);
        }

        card.add(info,    BorderLayout.CENTER);
        card.add(actions, BorderLayout.EAST);
        return card;
    }

    // 

    private void showOffersDialog(Event event) {
        List<Offer> offers = exchange.getOpenOffersForEvent(event.getId());
        if (offers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No open offers for this event.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] cols = {"Offer#", "Maker", "Outcome", "Odds", "Remaining Stake", "Status"};
        DefaultTableModel m = new DefaultTableModel(cols, 0);
        for (Offer o : offers) {
            m.addRow(new Object[]{
                    o.getId(), o.getMaker().getUsername(),
                    o.getPredictedOutcome(), o.getOdds(),
                    o.getRemainingStake(), o.getStatus()
            });
        }
        JTable t = new JTable(m);
        t.setRowHeight(22);
        JOptionPane.showMessageDialog(this, new JScrollPane(t), "Open Offers", JOptionPane.PLAIN_MESSAGE);
    }

    private void showPlaceOfferDialog(Event event, StandardUser maker) {
        JComboBox<Outcome> outcomeBox = new JComboBox<>(Outcome.values());
        JTextField oddsField  = new JTextField("2.0",   8);
        JTextField stakeField = new JTextField("50.00", 8);

        JPanel form = buildForm(
                "Outcome:",      outcomeBox,
                "Odds (>=1.0):", oddsField,
                "Stake ($):",    stakeField);

        int res = JOptionPane.showConfirmDialog(this, form, "Place Offer", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            Outcome outcome = (Outcome) outcomeBox.getSelectedItem();
            Odds    odds    = Odds.of(oddsField.getText().trim());
            Money   stake   = Money.of(stakeField.getText().trim());
            exchange.placeOffer(maker, event.getId(), outcome, odds, stake);
            JOptionPane.showMessageDialog(this, "Offer placed.", "Info", JOptionPane.INFORMATION_MESSAGE);
            refresh();
        } catch (ExchangeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMatchOfferDialog(Event event, StandardUser taker) {
        List<Offer> offers = exchange.getOpenOffersForEvent(event.getId());
        if (offers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No open offers on this event.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] headers = {"Offer#", "Maker", "Outcome", "Odds", "Remaining Stake"};
        DefaultTableModel m = new DefaultTableModel(headers, 0);
        for (Offer o : offers) {
            m.addRow(new Object[]{
                    o.getId(), o.getMaker().getUsername(),
                    o.getPredictedOutcome(), o.getOdds(), o.getRemainingStake()
            });
        }
        JTable     offerTable = new JTable(m);
        JTextField stakeField = new JTextField("20.00", 8);

        JLabel positionLabel = new JLabel("Select an offer above to see your winning position.");
        positionLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        positionLabel.setForeground(new Color(0, 100, 180));

        offerTable.getSelectionModel().addListSelectionListener(ignored -> {
            int sel = offerTable.getSelectedRow();
            if (sel >= 0) {
                Outcome makerOutcome = (Outcome) m.getValueAt(sel, 2);
                positionLabel.setText(takerPositionText(makerOutcome, event.getMarketType()));
            }
        });

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.add(positionLabel);
        southPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        JPanel stakeRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stakeRow.add(new JLabel("Stake to match ($):"));
        stakeRow.add(stakeField);
        southPanel.add(stakeRow);

        JPanel form = new JPanel(new BorderLayout(4, 4));
        form.add(new JScrollPane(offerTable), BorderLayout.CENTER);
        form.add(southPanel, BorderLayout.SOUTH);
        form.setPreferredSize(new Dimension(500, 290));

        int res = JOptionPane.showConfirmDialog(this, form, "Match Offer", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        int row = offerTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an offer to match.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Long  offerId = (Long) m.getValueAt(row, 0);
            Money stake   = Money.of(stakeField.getText().trim());
            exchange.matchOffer(offerId, taker, stake);
            JOptionPane.showMessageDialog(this, "Bet placed!", "Info", JOptionPane.INFORMATION_MESSAGE);
            refresh();
        } catch (ExchangeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String takerPositionText(Outcome makerOutcome, MarketType marketType) {
        if (marketType == MarketType.HEAD_TO_HEAD) {
            return makerOutcome == Outcome.HOME_WIN
                    ? "You win if: Away Win"
                    : "You win if: Home Win";
        }
        if (makerOutcome == Outcome.HOME_WIN) return "You win if: Away Win or Draw";
        if (makerOutcome == Outcome.AWAY_WIN) return "You win if: Home Win or Draw";
        return "You win if: Home Win or Away Win";
    }

    // -------------------------------------------------------------------------

    private JPanel buildForm(Object... pairs) {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill   = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 8, 5, 8);
        for (int i = 0; i < pairs.length; i += 2) {
            c.gridy = i / 2; c.gridx = 0;
            form.add(new JLabel(pairs[i].toString()), c);
            c.gridx = 1;
            form.add((Component) pairs[i + 1], c);
        }
        return form;
    }
}
