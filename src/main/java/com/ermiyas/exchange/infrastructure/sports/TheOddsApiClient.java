package com.ermiyas.exchange.infrastructure.sports;

import com.ermiyas.exchange.domain.model.Event;
import com.ermiyas.exchange.domain.vo.Odds;
import com.ermiyas.exchange.infrastructure.sports.dto.TheOddsApiFixtureDto;
import com.ermiyas.exchange.infrastructure.sports.dto.TheOddsApiOddsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.logging.Logger;

@Component
public class TheOddsApiClient implements SportsDataProvider {

    private static final Logger logger = Logger.getLogger(TheOddsApiClient.class.getName());
    
    private final RestTemplate restTemplate;
    private final List<MarketStrategy> marketStrategies;
    
    @Value("${api.theodds.key}")
    private String apiKey;

    @Value("${api.theodds.base-url}")
    private String baseUrl;
    
    public TheOddsApiClient(RestTemplate restTemplate, List<MarketStrategy> marketStrategies) {
        this.restTemplate = restTemplate;
        this.marketStrategies = marketStrategies;
    }

    @Override
    public boolean supports(SportRequest request) {
        return request.getLeague() != null && getStrategy(request) != null;
    }

    @Override
    public List<Event> fetchUpcomingFixtures(SportRequest request) {
        String leagueKey = request.getLeague().getApiKey();
        String url = String.format("%s/sports/%s/events?apiKey=%s", baseUrl, leagueKey, apiKey);
        
        logger.info("[FIXTURES] Fetching from: " + url);
        
        TheOddsApiFixtureDto[] response = restTemplate.getForObject(url, TheOddsApiFixtureDto[].class);
        List<Event> events = new ArrayList<>();

        if (response != null) {
            logger.info("[FIXTURES] Received " + response.length + " fixtures from API");
            
            for (TheOddsApiFixtureDto dto : response) {
                // ✓ VALIDATION: Check all required fields are present
                if (dto.getId() == null || dto.getId().trim().isEmpty()) {
                    logger.warning("[FIXTURES] Skipping fixture: missing ID");
                    continue;
                }
                if (dto.getHomeTeam() == null || dto.getHomeTeam().trim().isEmpty()) {
                    logger.warning("[FIXTURES] Skipping fixture " + dto.getId() + ": missing home team");
                    continue;
                }
                if (dto.getAwayTeam() == null || dto.getAwayTeam().trim().isEmpty()) {
                    logger.warning("[FIXTURES] Skipping fixture " + dto.getId() + ": missing away team");
                    continue;
                }
                if (dto.getStartTime() == null) {
                    logger.warning("[FIXTURES] Skipping fixture " + dto.getId() + ": missing or unparseable start time");
                    continue;
                }
                
                //  Ensure start time is reasonable (not in 1970)
                if (dto.getStartTime().getYear() < 2020) {
                    logger.warning("[FIXTURES] Skipping fixture " + dto.getId() + ": unrealistic start time " + dto.getStartTime());
                    continue;
                }
                
                //  validations passed - create event
                Event event = new Event(
                    0L,
                    dto.getId(),
                    dto.getHomeTeam().trim(),
                    dto.getAwayTeam().trim(),
                    dto.getStartTime(),
                    request.getLeague(),
                    request.getMarketType()
                );
                        
                events.add(event);
                logger.fine("[FIXTURES] Added fixture: " + event.getHomeTeam() + " vs " + event.getAwayTeam() 
                    + " at " + event.getStartTime());
            }
            
            logger.info("[FIXTURES] Successfully processed " + events.size() + " valid fixtures out of " + response.length);
        } else {
            logger.warning("[FIXTURES] API returned null response");
        }
        
        return events;
    }

    @Override
    public Map<String, BestOddsResult> fetchBestOddsWithSources(SportRequest request) {
        String leagueKey = request.getLeague().getApiKey();
        MarketStrategy strategy = getStrategy(request);
        
        if (strategy == null) {
            logger.warning("[ODDS] No strategy found for market type: " + request.getMarketType());
            return Collections.emptyMap();
        }

        String url = String.format("%s/sports/%s/odds?apiKey=%s&regions=eu&markets=%s", 
                baseUrl, leagueKey, apiKey, strategy.getMarketKey());
        
        logger.info("[ODDS] Fetching from: " + url);
        
        TheOddsApiOddsDto[] response = restTemplate.getForObject(url, TheOddsApiOddsDto[].class);
        Map<String, BestOddsResult> resultsMap = new HashMap<>();

        if (response != null) {
            logger.info("[ODDS] Received " + response.length + " odds records from API");
            
            for (TheOddsApiOddsDto dto : response) {
                if (dto.getId() == null) {
                    logger.warning("[ODDS] Skipping odds: missing fixture ID");
                    continue;
                }
                
                BestOddsResult result = strategy.calculateBestOddsWithSources(dto);
                if (result != null) {
                    resultsMap.put(dto.getId(), result);
                }
            }
            
            logger.info("[ODDS] Successfully processed " + resultsMap.size() + " odds records");
        } else {
            logger.warning("[ODDS] API returned null response");
        }
        
        return resultsMap;
    }

    @Override
    public Map<String, List<Odds>> fetchBestOdds(SportRequest request) {
        Map<String, BestOddsResult> sourceData = fetchBestOddsWithSources(request);
        Map<String, List<Odds>> OddsMap = new HashMap<>();
        
        for (Map.Entry<String, BestOddsResult> entry : sourceData.entrySet()) {
            BestOddsResult res = entry.getValue();
            List<Odds> oddsList = new ArrayList<>();
            oddsList.add(res.getHomeOdds());
            oddsList.add(res.getAwayOdds());
            oddsList.add(res.getDrawOdds());
            OddsMap.put(entry.getKey(), oddsList);
        }
        return OddsMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Integer[]> fetchScores(SportRequest request) {
        String leagueKey = request.getLeague().getApiKey();
        String url = String.format("%s/sports/%s/scores?apiKey=%s&daysFrom=1", baseUrl, leagueKey, apiKey);
        
        logger.info("[SCORES] Fetching from: " + url);
        
        Map<String, Object>[] response = restTemplate.getForObject(url, Map[].class);
        Map<String, Integer[]> scoreMap = new HashMap<>();

        if (response != null) {
            logger.info("[SCORES] Received " + response.length + " score records from API");
            
            for (Map<String, Object> event : response) {
                Object idObj = event.get("id");
                if (idObj == null) {
                    logger.warning("[SCORES] Skipping score: missing fixture ID");
                    continue;
                }
                
                List<Map<String, Object>> scores = (List<Map<String, Object>>) event.get("scores");
                if (scores != null && scores.size() >= 2) {
                    try {
                        Integer h = Integer.parseInt(scores.get(0).get("score").toString());
                        Integer a = Integer.parseInt(scores.get(1).get("score").toString());
                        scoreMap.put(idObj.toString(), new Integer[]{h, a});
                    } catch (NumberFormatException e) {
                        logger.warning("[SCORES] Could not parse score for fixture " + idObj + ": " + e.getMessage());
                    }
                }
            }
            
            logger.info("[SCORES] Successfully processed " + scoreMap.size() + " score records");
        } else {
            logger.warning("[SCORES] API returned null response");
        }
        
        return scoreMap;
    }

    private MarketStrategy getStrategy(SportRequest request) {
        for (MarketStrategy strategy : marketStrategies) {
            if (strategy.supports(request.getMarketType())) {
                return strategy;
            }
        }
        return null;
    }

}