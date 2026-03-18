package com.ermiyas.exchange.api.dto;

import com.ermiyas.exchange.domain.model.Outcome;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ExchangeDtos {
    public static class CreateOfferRequest {
        private Long makerId;
        private Long eventId;
        private Outcome outcome;
        private BigDecimal odds;
        private BigDecimal stake;
        public Long getMakerId() { return makerId; }
        public void setMakerId(Long makerId) { this.makerId = makerId; }
        public Long getEventId() { return eventId; }
        public void setEventId(Long eventId) { this.eventId = eventId; }
        public Outcome getOutcome() { return outcome; }
        public void setOutcome(Outcome outcome) { this.outcome = outcome; }
        public BigDecimal getOdds() { return odds; }
        public void setOdds(BigDecimal odds) { this.odds = odds; }
        public BigDecimal getStake() { return stake; }
        public void setStake(BigDecimal stake) { this.stake = stake; }
    }

    public static class MatchBetRequest {
        private Long takerId;
        private Long offerId;
        private BigDecimal amountToMatch;
        public Long getTakerId() { return takerId; }
        public void setTakerId(Long takerId) { this.takerId = takerId; }
        public Long getOfferId() { return offerId; }
        public void setOfferId(Long offerId) { this.offerId = offerId; }
        public BigDecimal getAmountToMatch() { return amountToMatch; }
        public void setAmountToMatch(BigDecimal amountToMatch) { this.amountToMatch = amountToMatch; }
    }

    public static class WalletActionRequest {
        private BigDecimal amount;
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }

    public static class AdminSettleRequest {
        private List<String> externalIds;
        public List<String> getExternalIds() { return externalIds; }
        public void setExternalIds(List<String> externalIds) { this.externalIds = externalIds; }
    }

    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class DashboardStatsResponse {
        private long totalUsers;
        private long activeFixtures;
        private BigDecimal lockedStake;
        public long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
        public long getActiveFixtures() { return activeFixtures; }
        public void setActiveFixtures(long activeFixtures) { this.activeFixtures = activeFixtures; }
        public BigDecimal getLockedStake() { return lockedStake; }
        public void setLockedStake(BigDecimal lockedStake) { this.lockedStake = lockedStake; }
    }

    public static class LoginRequest {
        private String username;
        private String password;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class ErrorResponse {
        private String message;
        private String errorCode;
        private long timestamp;
        public ErrorResponse() { }
        public ErrorResponse(String message, String errorCode, long timestamp) { this.message = message; this.errorCode = errorCode; this.timestamp = timestamp; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    public static class EventSummaryResponse {
        private Long id;
        private String externalId;
        private String homeTeam;
        private String awayTeam;
        private LocalDateTime startTime;
        private String leagueName;
        private Double homeOdds;
        private Double awayOdds;
        private Double drawOdds;
        private String homeSource;
        private String awaySource;
        private String drawSource;
        private String status;
        private Integer offerCount;
        private Integer finalHomeScore;
        private Integer finalAwayScore;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getExternalId() { return externalId; }
        public void setExternalId(String externalId) { this.externalId = externalId; }
        public String getHomeTeam() { return homeTeam; }
        public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }
        public String getAwayTeam() { return awayTeam; }
        public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public String getLeagueName() { return leagueName; }
        public void setLeagueName(String leagueName) { this.leagueName = leagueName; }
        public Double getHomeOdds() { return homeOdds; }
        public void setHomeOdds(Double homeOdds) { this.homeOdds = homeOdds; }
        public Double getAwayOdds() { return awayOdds; }
        public void setAwayOdds(Double awayOdds) { this.awayOdds = awayOdds; }
        public Double getDrawOdds() { return drawOdds; }
        public void setDrawOdds(Double drawOdds) { this.drawOdds = drawOdds; }
        public String getHomeSource() { return homeSource; }
        public void setHomeSource(String homeSource) { this.homeSource = homeSource; }
        public String getAwaySource() { return awaySource; }
        public void setAwaySource(String awaySource) { this.awaySource = awaySource; }
        public String getDrawSource() { return drawSource; }
        public void setDrawSource(String drawSource) { this.drawSource = drawSource; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Integer getOfferCount() { return offerCount; }
        public void setOfferCount(Integer offerCount) { this.offerCount = offerCount; }
        public Integer getFinalHomeScore() { return finalHomeScore; }
        public void setFinalHomeScore(Integer finalHomeScore) { this.finalHomeScore = finalHomeScore; }
        public Integer getFinalAwayScore() { return finalAwayScore; }
        public void setFinalAwayScore(Integer finalAwayScore) { this.finalAwayScore = finalAwayScore; }
    }

    public static class UserResponse {
        private Long id;
        private String username;
        private String role;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class EventResponse {
        private Long id;
        private String homeTeam;
        private String awayTeam;
        private LocalDateTime startTime;
        private String leagueName;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getHomeTeam() { return homeTeam; }
        public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }
        public String getAwayTeam() { return awayTeam; }
        public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public String getLeagueName() { return leagueName; }
        public void setLeagueName(String leagueName) { this.leagueName = leagueName; }
    }

    public static class OfferResponse {
        private Long id;
        private UserResponse maker;
        private EventResponse event;
        private String outcome;
        private BigDecimal odds;
        private BigDecimal remainingStake;
        private String status;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public UserResponse getMaker() { return maker; }
        public void setMaker(UserResponse maker) { this.maker = maker; }
        public EventResponse getEvent() { return event; }
        public void setEvent(EventResponse event) { this.event = event; }
        public String getOutcome() { return outcome; }
        public void setOutcome(String outcome) { this.outcome = outcome; }
        public BigDecimal getOdds() { return odds; }
        public void setOdds(BigDecimal odds) { this.odds = odds; }
        public BigDecimal getRemainingStake() { return remainingStake; }
        public void setRemainingStake(BigDecimal remainingStake) { this.remainingStake = remainingStake; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class MatchedBetResponse {
        private Long id;
        private OfferResponse offer;
        private UserResponse taker;
        private BigDecimal takerLiability;
        private BigDecimal makerStake;
        private BigDecimal odds;
        private String status;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public OfferResponse getOffer() { return offer; }
        public void setOffer(OfferResponse offer) { this.offer = offer; }
        public UserResponse getTaker() { return taker; }
        public void setTaker(UserResponse taker) { this.taker = taker; }
        public BigDecimal getTakerLiability() { return takerLiability; }
        public void setTakerLiability(BigDecimal takerLiability) { this.takerLiability = takerLiability; }
        public BigDecimal getMakerStake() { return makerStake; }
        public void setMakerStake(BigDecimal makerStake) { this.makerStake = makerStake; }
        public BigDecimal getOdds() { return odds; }
        public void setOdds(BigDecimal odds) { this.odds = odds; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
