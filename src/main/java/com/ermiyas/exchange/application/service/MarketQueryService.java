package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.api.dto.ExchangeDtos;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.UserNotFoundException;
import com.ermiyas.exchange.domain.model.*;
import com.ermiyas.exchange.domain.repository.bet.BetRepository;
import com.ermiyas.exchange.domain.repository.event.EventRepository;
import com.ermiyas.exchange.domain.repository.offer.OfferRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class MarketQueryService {
    
    private static final Logger logger = Logger.getLogger(MarketQueryService.class.getName());
    
    private final EventRepository eventRepository;
    private final OfferRepository offerRepository;
    private final BetRepository betRepository;

    public MarketQueryService(EventRepository eventRepository, OfferRepository offerRepository, BetRepository betRepository) {
        this.eventRepository = eventRepository;
        this.offerRepository = offerRepository;
        this.betRepository = betRepository;
    }

    /**
     * Get events grouped by league for display on markets page.
     * 
     * Returns:
     * 1. OPEN events that are in the future (startTime > now) - upcoming fixtures
     * 2. COMPLETED events that have active bets/offers - past results
     * 
     * ✓ FIXED: Re-added time-based filtering for OPEN events
     * ✓ FIXED: Now properly filters out past OPEN fixtures
     */
    public Map<String, List<ExchangeDtos.EventSummaryResponse>> getEventsByLeague() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> allEvents = eventRepository.findAll();
        Map<String, List<ExchangeDtos.EventSummaryResponse>> leagueMap = new HashMap<>();
        
        logger.info("[MARKETS] Querying " + allEvents.size() + " total events. Current time: " + now);
        
        for (Event event : allEvents) {
            // ✓ FIXED: Show OPEN events only if they are in the future
            // This ensures we display upcoming fixtures, not past ones
            boolean isFutureOpen = event.getStatus() == EventStatus.OPEN && 
                                  event.getStartTime() != null &&
                                  event.getStartTime().isAfter(now);
            
            // Show COMPLETED events that have bets/offers (past results with activity)
            boolean isCompletedWithBets = event.getStatus() == EventStatus.COMPLETED && 
                                         event.getOffers() != null && 
                                         !event.getOffers().isEmpty();
            
            if (isFutureOpen) {
                logger.fine("[MARKETS] Including OPEN future event: " + event.getHomeTeam() + 
                           " vs " + event.getAwayTeam() + " @ " + event.getStartTime());
            } else if (isCompletedWithBets) {
                logger.fine("[MARKETS] Including COMPLETED event with bets: " + event.getHomeTeam() + 
                           " vs " + event.getAwayTeam());
            }
            
            // ✓ Validation: Skip events with null startTime
            if (event.getStartTime() == null && event.getStatus() == EventStatus.OPEN) {
                logger.warning("[MARKETS] Skipping OPEN event with null startTime: " + event.getId());
                continue;
            }
            
            if (isFutureOpen || isCompletedWithBets) {
                String leagueName = event.getLeague() != null ? 
                    event.getLeague().getDisplayName() : 
                    "International Football";
                leagueMap.computeIfAbsent(leagueName, k -> new ArrayList<>())
                    .add(mapToSummary(event));
            }
        }
        
        int totalDisplayed = leagueMap.values().stream().mapToInt(List::size).sum();
        logger.info("[MARKETS] Displaying " + totalDisplayed + " events across " + 
                   leagueMap.size() + " leagues");
        
        return leagueMap;
    }

    public Map<String, Object> getFixtureDetailSnapshot(Long eventId) throws ExchangeException {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new UserNotFoundException("Fixture #" + eventId + " not found."));
            
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("id", event.getId());
        snapshot.put("homeTeam", event.getHomeTeam());
        snapshot.put("awayTeam", event.getAwayTeam());
        snapshot.put("startTime", event.getStartTime());
        snapshot.put("homeOdds", event.getRefHomeOdds() != null ? event.getRefHomeOdds().value() : null);
        snapshot.put("homeSource", event.getRefHomeSource());
        snapshot.put("awayOdds", event.getRefAwayOdds() != null ? event.getRefAwayOdds().value() : null);
        snapshot.put("awaySource", event.getRefAwaySource());
        snapshot.put("drawOdds", event.getRefDrawOdds() != null ? event.getRefDrawOdds().value() : null);
        snapshot.put("drawSource", event.getRefDrawSource());
        
        List<ExchangeDtos.OfferResponse> offerResponses = new ArrayList<>();
        for (Offer offer : offerRepository.findAllByEventId(eventId)) {
            offerResponses.add(mapToOfferResponse(offer));
        }
        snapshot.put("offers", offerResponses);
        return snapshot;
    }

    public List<ExchangeDtos.OfferResponse> getUserOpenOffers(Long userId) {
        List<ExchangeDtos.OfferResponse> results = new ArrayList<>();
        for (Offer o : offerRepository.findAll()) {
            if (o.getMaker() != null && o.getMaker().getId().equals(userId) && 
                (o.getStatus() == OfferStatus.OPEN || o.getStatus() == OfferStatus.PARTIALLY_TAKEN)) {
                results.add(mapToOfferResponse(o));
            }
        }
        return results;
    }

    public List<ExchangeDtos.MatchedBetResponse> getUserMatchedBets(Long userId) {
        List<ExchangeDtos.MatchedBetResponse> results = new ArrayList<>();
        for (Bet b : betRepository.findAll()) {
            if (b.getStatus() == BetStatus.MATCHED || b.getStatus() == BetStatus.SETTLED) {
                boolean isTaker = b.getTaker() != null && b.getTaker().getId().equals(userId);
                boolean isMaker = b.getOffer() != null && b.getOffer().getMaker() != null && 
                                 b.getOffer().getMaker().getId().equals(userId);
                if (isTaker || isMaker) {
                    results.add(mapToBetResponse(b));
                }
            }
        }
        return results;
    }

    private ExchangeDtos.MatchedBetResponse mapToBetResponse(Bet b) {
        ExchangeDtos.MatchedBetResponse response = new ExchangeDtos.MatchedBetResponse();
        response.setId(b.getId());
        response.setStatus(b.getStatus().name());
        response.setOffer(mapToOfferResponse(b.getOffer()));
        ExchangeDtos.UserResponse taker = new ExchangeDtos.UserResponse();
        taker.setId(b.getTaker().getId());
        taker.setUsername(b.getTaker().getUsername());
        response.setTaker(taker);
        response.setTakerLiability(b.getTakerLiability().value());
        response.setMakerStake(b.getMakerStake().value());
        response.setOdds(b.getOdds().value());
        return response;
    }

    private ExchangeDtos.EventSummaryResponse mapToSummary(Event event) {
        ExchangeDtos.EventSummaryResponse response = new ExchangeDtos.EventSummaryResponse();
        response.setId(event.getId());
        response.setExternalId(event.getExternalId());
        response.setHomeTeam(event.getHomeTeam());
        response.setAwayTeam(event.getAwayTeam());
        response.setStartTime(event.getStartTime());
        response.setLeagueName(event.getLeague() != null ? 
            event.getLeague().getDisplayName() : 
            "International Football");
        response.setHomeOdds(event.getRefHomeOdds() != null ? event.getRefHomeOdds().value().doubleValue() : null);
        response.setAwayOdds(event.getRefAwayOdds() != null ? event.getRefAwayOdds().value().doubleValue() : null);
        response.setDrawOdds(event.getRefDrawOdds() != null ? event.getRefDrawOdds().value().doubleValue() : null);
        response.setHomeSource(event.getRefHomeSource());
        response.setAwaySource(event.getRefAwaySource());
        response.setDrawSource(event.getRefDrawSource());
        response.setStatus(event.getStatus() != null ? event.getStatus().name() : null);
        response.setOfferCount(event.getOffers() != null ? event.getOffers().size() : 0);
        response.setFinalHomeScore(event.getFinalHomeScore());
        response.setFinalAwayScore(event.getFinalAwayScore());
        return response;
    }

    private ExchangeDtos.OfferResponse mapToOfferResponse(Offer o) {
        ExchangeDtos.OfferResponse response = new ExchangeDtos.OfferResponse();
        response.setId(o.getId());
        ExchangeDtos.UserResponse maker = new ExchangeDtos.UserResponse();
        maker.setId(o.getMaker().getId());
        maker.setUsername(o.getMaker().getUsername());
        maker.setRole(o.getMaker().getRoleName());
        response.setMaker(maker);
        response.setEvent(mapToEventResponse(o.getEvent()));
        response.setOutcome(o.getPredictedOutcome() != null ? o.getPredictedOutcome().name() : null);
        response.setOdds(o.getOdds() != null ? o.getOdds().value() : null);
        response.setRemainingStake(o.getRemainingStake() != null ? o.getRemainingStake().value() : null);
        response.setStatus(o.getStatus() != null ? o.getStatus().name() : null);
        return response;
    }

    private ExchangeDtos.EventResponse mapToEventResponse(Event e) {
        ExchangeDtos.EventResponse response = new ExchangeDtos.EventResponse();
        response.setId(e.getId());
        response.setHomeTeam(e.getHomeTeam());
        response.setAwayTeam(e.getAwayTeam());
        response.setStartTime(e.getStartTime());
        response.setLeagueName(e.getLeague() != null ? 
            e.getLeague().getDisplayName() : 
            "International Football");
        return response;
    }
}


