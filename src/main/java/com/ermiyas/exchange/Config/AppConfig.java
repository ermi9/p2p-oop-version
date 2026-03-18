package com.ermiyas.exchange.Config;

import com.ermiyas.exchange.application.service.*;
import com.ermiyas.exchange.domain.logic.SettlementStrategy;
import com.ermiyas.exchange.domain.logic.SettlementStrategyFactory;
import com.ermiyas.exchange.domain.logic.ThreeWaySettlementStrategy;
import com.ermiyas.exchange.domain.repository.bet.BetRepository;
import com.ermiyas.exchange.domain.repository.bet.BetRepositoryImpl;
import com.ermiyas.exchange.domain.repository.event.EventRepository;
import com.ermiyas.exchange.domain.repository.event.EventRepositoryImpl;
import com.ermiyas.exchange.domain.repository.offer.OfferRepository;
import com.ermiyas.exchange.domain.repository.offer.OfferRepositoryImpl;
import com.ermiyas.exchange.domain.repository.user.UserRepository;
import com.ermiyas.exchange.domain.repository.user.UserRepositoryImpl;
import com.ermiyas.exchange.domain.repository.wallet.WalletRepository;
import com.ermiyas.exchange.domain.repository.wallet.WalletRepositoryImpl;
import com.ermiyas.exchange.domain.vo.CommissionPolicy;
import com.ermiyas.exchange.domain.vo.StandardPercentagePolicy;
import com.ermiyas.exchange.infrastructure.sports.*;
import com.ermiyas.exchange.infrastructure.storage.ExchangeDataStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class AppConfig {
    @Bean
    public ExchangeDataStore exchangeDataStore() { return new ExchangeDataStore(); }

    @Bean
    public UserRepository userRepository(ExchangeDataStore dataStore) { return new UserRepositoryImpl(dataStore); }
    @Bean
    public WalletRepository walletRepository(ExchangeDataStore dataStore) { return new WalletRepositoryImpl(dataStore); }
    @Bean
    public EventRepository eventRepository(ExchangeDataStore dataStore) { return new EventRepositoryImpl(dataStore); }
    @Bean
    public OfferRepository offerRepository(ExchangeDataStore dataStore) { return new OfferRepositoryImpl(dataStore); }
    @Bean
    public BetRepository betRepository(ExchangeDataStore dataStore) { return new BetRepositoryImpl(dataStore); }

    @Bean
    public SettlementStrategy threeWaySettlementStrategy() { return new ThreeWaySettlementStrategy(); }
    @Bean
    public SettlementStrategyFactory settlementStrategyFactory(List<SettlementStrategy> strategies) { return new SettlementStrategyFactory(strategies); }
    @Bean
    public CommissionPolicy commissionPolicy() { return new StandardPercentagePolicy(new BigDecimal("0.05")); }

    @Bean
    public CommissionService commissionService() { return new CommissionService(); }
    @Bean
    public UserService userService(UserRepository userRepository, WalletRepository walletRepository) { return new UserService(userRepository, walletRepository); }
    @Bean
    public WalletService walletService(WalletRepository walletRepository, UserRepository userRepository) { return new WalletService(walletRepository, userRepository); }
    @Bean
    public OfferService offerService(OfferRepository offerRepository, EventRepository eventRepository, UserRepository userRepository, WalletRepository walletRepository) { return new OfferService(offerRepository, eventRepository, userRepository, walletRepository); }
    @Bean
    public TradeService tradeService(OfferRepository offerRepository, WalletRepository walletRepository, BetRepository betRepository) { return new TradeService(offerRepository, walletRepository, betRepository); }
    @Bean
    public MarketQueryService marketQueryService(EventRepository eventRepository, OfferRepository offerRepository, BetRepository betRepository) { return new MarketQueryService(eventRepository, offerRepository, betRepository); }
    @Bean
    public AdminSyncService adminSyncService(EventRepository eventRepository, List<SportsDataProvider> providers, SettlementStrategyFactory strategyFactory) { return new AdminSyncService(eventRepository, providers, strategyFactory); }
    @Bean
    public AdminSettlementService adminSettlementService(EventRepository eventRepository, BetRepository betRepository, OfferRepository offerRepository, WalletRepository walletRepository, CommissionPolicy defaultPolicy) { return new AdminSettlementService(eventRepository, betRepository, offerRepository, walletRepository, defaultPolicy); }

    @Bean
    public FixtureNameMatcher fixtureNameMatcher() { return new FixtureNameMatcher(); }
    @Bean
    public H2HMarketStrategy h2hMarketStrategy(FixtureNameMatcher matcher) { return new H2HMarketStrategy(matcher); }
    
    @Bean
    public RestTemplate restTemplate() { return new RestTemplate(); }
    
    @Bean
    public SportsDataProvider sportsDataProvider(RestTemplate restTemplate, H2HMarketStrategy h2hMarketStrategy) {
return new TheOddsApiClient(restTemplate, List.of(h2hMarketStrategy));    }
}