package com.ermiyas.exchange.ui;

import com.ermiyas.exchange.Exchange;
import com.ermiyas.exchange.domain.model.*;
import com.ermiyas.exchange.domain.model.user.StandardUser;
import com.ermiyas.exchange.domain.settlement.HeadToHeadSettlementStrategy;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class ExchangeApp {

    public static void main(String[] args) {
        Exchange exchange = new Exchange(CommissionPolicy.fivePercent());
        exchange.registerStrategy(new HeadToHeadSettlementStrategy());
        StandardUser[] players = seedDemoAccounts(exchange);
        seedDemoEvents(exchange, players[0], players[1]);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginWindow login = new LoginWindow(exchange);
                login.setVisible(true);
            }
        });
    }

    private static StandardUser[] seedDemoAccounts(Exchange exchange) {
        try {
            exchange.registerAdmin("admin", "admin@exchange.com", "admin123");

            StandardUser ermi = exchange.register("ermi", "ermi@exchange.com", "ermi123");
            StandardUser nate   = exchange.register("nate",   "nate@exchange.com",   "nate123");

            ermi.getWallet().deposit(Money.of("1000.00"));
            nate.getWallet().deposit(Money.of("1000.00"));

            return new StandardUser[]{ermi, nate};
        } catch (Exception e) {
            throw new RuntimeException("Seed data failed: " + e.getMessage(), e);
        }
    }

    private static void seedDemoEvents(Exchange exchange, StandardUser alice, StandardUser bob) {
        try {
            LocalDate today = LocalDate.now();

            exchange.addEvent("Arsenal",         "Chelsea",           today.plusDays(1).atTime(LocalTime.of(15, 0)),  League.PREMIER_LEAGUE,   MarketType.THREE_WAY);
            exchange.addEvent("Real Madrid",     "Barcelona",         today.plusDays(1).atTime(LocalTime.of(20, 0)),  League.LA_LIGA,          MarketType.THREE_WAY);
            exchange.addEvent("Bayern Munich",   "Borussia Dortmund", today.plusDays(2).atTime(LocalTime.of(17, 30)), League.BUNDESLIGA,       MarketType.HEAD_TO_HEAD);
            exchange.addEvent("Man City",        "Liverpool",         today.plusDays(3).atTime(LocalTime.of(16, 30)), League.PREMIER_LEAGUE,   MarketType.THREE_WAY);
            exchange.addEvent("PSG",             "Olympique Lyon",    today.plusDays(4).atTime(LocalTime.of(20, 45)), League.LIGUE_1,          MarketType.THREE_WAY);
            exchange.addEvent("Juventus",        "Inter Milan",       today.plusDays(5).atTime(LocalTime.of(20, 45)), League.SERIE_A,          MarketType.THREE_WAY);
            exchange.addEvent("Atletico Madrid", "Sevilla",           today.plusDays(6).atTime(LocalTime.of(21, 0)),  League.LA_LIGA,          MarketType.HEAD_TO_HEAD);
            exchange.addEvent("Real Madrid",     "Man City",          today.plusDays(7).atTime(LocalTime.of(20, 0)),  League.CHAMPIONS_LEAGUE, MarketType.THREE_WAY);

            exchange.placeOffer(alice, 1L, Outcome.HOME_WIN, Odds.of("2.10"), Money.of("50.00"));
            exchange.placeOffer(alice, 2L, Outcome.AWAY_WIN, Odds.of("3.50"), Money.of("30.00"));
            exchange.placeOffer(bob,   1L, Outcome.DRAW,     Odds.of("3.20"), Money.of("40.00"));
            exchange.placeOffer(bob,   4L, Outcome.HOME_WIN, Odds.of("1.90"), Money.of("60.00"));
        } catch (Exception e) {
            throw new RuntimeException("Seed events failed: " + e.getMessage(), e);
        }
    }
}
