package com.ermiyas.exchange.ui;

import com.ermiyas.exchange.Exchange;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.model.Event;
import com.ermiyas.exchange.domain.model.League;
import com.ermiyas.exchange.domain.model.MarketType;
import com.ermiyas.exchange.domain.model.user.AdminUser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AdminPanel extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Exchange          exchange;
    private final AdminUser         admin;
    private       DefaultTableModel tableModel;

    public AdminPanel(Exchange exchange, AdminUser admin) {
        this.exchange = exchange;
        this.admin    = admin;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel commissionLabel = new JLabel(
                "<html><b>Commission rate:</b> " + exchange.getCommissionPolicy().getRateAsPercent() + "</html>");
        add(commissionLabel, BorderLayout.NORTH);

        String[] cols = {"#", "Event", "League", "Market", "Kick-off", "Status", "Score"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(24);
        refreshTable();

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton refreshBtn  = new JButton("Refresh");
        JButton addEventBtn = new JButton("Add Event");
        JButton resultBtn   = new JButton("Enter Result");
        JButton settleBtn   = new JButton("Settle Event");
        south.add(refreshBtn);
        south.add(addEventBtn);
        south.add(resultBtn);
        south.add(settleBtn);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        //  listeners 

        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTable();
            }
        });

        addEventBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddEventDialog();
            }
        });

        resultBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(AdminPanel.this,
                            "Select an event first.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Long eventId = (Long) tableModel.getValueAt(row, 0);
                showEnterResultDialog(eventId);
            }
        });

        settleBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(AdminPanel.this,
                            "Select an event first.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Long eventId = (Long) tableModel.getValueAt(row, 0);
                try {
                    exchange.settleEvent(admin, eventId);
                    JOptionPane.showMessageDialog(AdminPanel.this,
                            "Event settled. Bets resolved.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    refreshTable();
                } catch (ExchangeException ex) {
                    JOptionPane.showMessageDialog(AdminPanel.this,
                            ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // 

    private void showAddEventDialog() {
        JTextField         homeField  = new JTextField(14);
        JTextField         awayField  = new JTextField(14);
        JTextField         kickField  = new JTextField("2026-06-20 20:00", 14);
        JComboBox<League>     leagueBox = new JComboBox<>(League.values());
        JComboBox<MarketType> marketBox = new JComboBox<>(MarketType.values());

        JPanel form = buildForm(
                "Home Team:",                   homeField,
                "Away Team:",                   awayField,
                "Kick-off (yyyy-MM-dd HH:mm):", kickField,
                "League:",                      leagueBox,
                "Market Type:",                 marketBox);

        int res = JOptionPane.showConfirmDialog(this, form, "Add Event", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        String home   = homeField.getText().trim();
        String away   = awayField.getText().trim();
        String kickIn = kickField.getText().trim();

        if (home.isEmpty() || away.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Team names cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDateTime kickOff;
        try {
            kickOff = LocalDateTime.parse(kickIn, DATE_FMT);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date. Use format: yyyy-MM-dd HH:mm  (e.g. 2026-06-20 20:00)",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        League     league = (League)     leagueBox.getSelectedItem();
        MarketType market = (MarketType) marketBox.getSelectedItem();

        exchange.addEvent(admin, home, away, kickOff, league, market);
        JOptionPane.showMessageDialog(this, "Event added.", "Info", JOptionPane.INFORMATION_MESSAGE);
        refreshTable();
    }

    private void showEnterResultDialog(Long eventId) {
        JTextField homeScore = new JTextField("1", 4);
        JTextField awayScore = new JTextField("0", 4);
        JPanel form = buildForm("Home Score:", homeScore, "Away Score:", awayScore);

        int res = JOptionPane.showConfirmDialog(this, form, "Enter Result", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            int home = Integer.parseInt(homeScore.getText().trim());
            int away = Integer.parseInt(awayScore.getText().trim());
            exchange.processResult(admin, eventId, home, away);
            JOptionPane.showMessageDialog(this,
                    "Result recorded. Now settle the event to pay out bets.", "Info", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Event ev : exchange.getAllEvents()) {
            String score = (ev.getHomeScore() != null)
                    ? ev.getHomeScore() + " - " + ev.getAwayScore() : "-";
            tableModel.addRow(new Object[]{
                    ev.getId(), ev.getDisplayName(), ev.getLeague().getDisplayName(),
                    ev.getMarketType(), ev.getKickOffLabel(), ev.getStatus(), score
            });
        }
    }

    // 

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
