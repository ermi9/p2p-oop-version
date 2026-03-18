package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.domain.logic.SettlementStrategy;
import com.ermiyas.exchange.domain.logic.SettlementStrategyFactory;
import com.ermiyas.exchange.domain.model.Event;
import com.ermiyas.exchange.domain.model.League;
import com.ermiyas.exchange.domain.model.MarketType;
import com.ermiyas.exchange.domain.repository.event.EventRepository;
import com.ermiyas.exchange.infrastructure.sports.BestOddsResult;
import com.ermiyas.exchange.infrastructure.sports.SportsDataProvider;
import com.ermiyas.exchange.infrastructure.sports.SportsDataProvider.SportRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AdminSyncService {
    private final EventRepository eventRepository;
    private final List<SportsDataProvider> providers;
    private final SettlementStrategyFactory strategyFactory;

    public AdminSyncService(EventRepository eventRepository, List<SportsDataProvider> providers, SettlementStrategyFactory strategyFactory) {
        this.eventRepository = eventRepository;
        this.providers = providers;
        this.strategyFactory = strategyFactory;
    }

    public long getActiveFixtureCount() {
        return eventRepository.count();
    }

    public void syncAllFixtures() {
    System.out.println("[SYNC] Starting fixture sync...");
    int totalSynced = 0;
    
    for (League league : League.values()) {
        System.out.println("[SYNC] Processing: " + league);
        SportRequest request = new SportRequest(league, MarketType.THREE_WAY);
        
        for (SportsDataProvider provider : providers) {
            if (!provider.supports(request)) {
                System.out.println("[SYNC] Provider doesn't support " + league);
                continue;
            }
            
            System.out.println("[SYNC] Syncing " + league + " with provider");
            syncFixturesForProvider(provider, request);
            syncReferenceOdds(provider, request);
            syncScores(provider, request);
            totalSynced++;
        }
    }
    
    System.out.println("[SYNC] Finished! Total synced: " + totalSynced + " leagues");
    System.out.println("[SYNC] Total events in DB: " + eventRepository.count());
}

    private void syncFixturesForProvider(SportsDataProvider provider, SportRequest request) {
        List<Event> incoming = provider.fetchUpcomingFixtures(request);
        for (Event fetched : incoming) {
            Optional<Event> existing = eventRepository.getByExternalId(fetched.getExternalId());
            if (existing.isEmpty()) {
                Event event = new Event(eventRepository.nextId(), fetched.getExternalId(), fetched.getHomeTeam(), fetched.getAwayTeam(), fetched.getStartTime(), fetched.getLeague(), fetched.getMarketType());
                eventRepository.save(event);
            }
        }
    }

    private void syncReferenceOdds(SportsDataProvider provider, SportRequest request) {
        Map<String, BestOddsResult> bestOdds = provider.fetchBestOddsWithSources(request);
        for (Map.Entry<String, BestOddsResult> entry : bestOdds.entrySet()) {
            eventRepository.getByExternalId(entry.getKey()).ifPresent(event -> {
                BestOddsResult result = entry.getValue();
                event.updateReferenceOdds(result.getHomeOdds(), result.getHomeSource(), result.getAwayOdds(), result.getAwaySource(), result.getDrawOdds(), result.getDrawSource());
                eventRepository.save(event);
            });
        }
    }

    private void syncScores(SportsDataProvider provider, SportRequest request) {
        Map<String, Integer[]> scores = provider.fetchScores(request);
        for (Map.Entry<String, Integer[]> entry : scores.entrySet()) {
            eventRepository.getByExternalId(entry.getKey()).ifPresent(event -> {
                if (event.getStatus() == com.ermiyas.exchange.domain.model.EventStatus.OPEN) {
                    Integer[] result = entry.getValue();
                    SettlementStrategy strategy = strategyFactory.getStrategy(event.getMarketType());
                    try {
                        event.processResult(result[0], result[1], strategy);
                        eventRepository.save(event);
                    } catch (Exception ignored) {
                    }
                }
            });
        }
    }
}
