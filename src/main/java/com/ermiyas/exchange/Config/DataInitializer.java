package com.ermiyas.exchange.Config;

import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.model.*;
import com.ermiyas.exchange.domain.model.user.AdminUser;
import com.ermiyas.exchange.domain.model.user.StandardUser;
import com.ermiyas.exchange.domain.model.user.UserFactory;
import com.ermiyas.exchange.domain.repository.event.EventRepository;
import com.ermiyas.exchange.domain.repository.user.UserRepository;
import com.ermiyas.exchange.domain.repository.wallet.WalletRepository;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.vo.Odds;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner initData(UserRepository userRepository, WalletRepository walletRepository, EventRepository eventRepository) {
        return args -> {
            if (userRepository.count() > 0 || eventRepository.count() > 0) return;
            seedUsers(userRepository, walletRepository);
            seedEvents(eventRepository);
        };
    }

    private void seedUsers(UserRepository userRepository, WalletRepository walletRepository) throws ExchangeException {
        AdminUser admin = UserFactory.createAdmin(userRepository.nextId(), "admin1", "admin@exchange.com", "admin123456");
        userRepository.save(admin);

        StandardUser alice = UserFactory.createStandard(userRepository.nextId(), "alice", "alice@exchange.com", "password123");
        Wallet aliceWallet = new Wallet(walletRepository.nextId(), alice, Money.of("1000"));
        alice.setWallet(aliceWallet);
        walletRepository.save(aliceWallet);
        userRepository.save(alice);

        StandardUser bob = UserFactory.createStandard(userRepository.nextId(), "bob", "bob@exchange.com", "password123");
        Wallet bobWallet = new Wallet(walletRepository.nextId(), bob, Money.of("1000"));
        bob.setWallet(bobWallet);
        walletRepository.save(bobWallet);
        userRepository.save(bob);
    }

    private void seedEvents(EventRepository eventRepository) {
        createEvent(eventRepository, "seed-epl-1", "Arsenal FC", "Chelsea FC", League.PREMIER_LEAGUE, 1.95, 3.40, 4.20, 48);
        createEvent(eventRepository, "seed-seriea-1", "Inter Milan", "Juventus FC", League.SERIE_A, 2.10, 3.10, 3.60, 72);
        createEvent(eventRepository, "seed-laliga-1", "Real Madrid", "FC Barcelona", League.LA_LIGA, 2.05, 3.55, 3.10, 96);
    }

    private void createEvent(EventRepository eventRepository, String externalId, String home, String away, League league, double homeOdds, double drawOdds, double awayOdds, int hoursAhead) {
        Event event = new Event(eventRepository.nextId(), externalId, home, away, LocalDateTime.now().plusHours(hoursAhead), league, MarketType.THREE_WAY);
        event.updateReferenceOdds(Odds.of(homeOdds), "SeedBook", Odds.of(awayOdds), "SeedBook", Odds.of(drawOdds), "SeedBook");
        eventRepository.save(event);
    }
}
