package com.ermiyas.exchange;

import com.ermiyas.exchange.domain.exception.*;
import com.ermiyas.exchange.domain.model.*;
import com.ermiyas.exchange.domain.model.user.*;
import com.ermiyas.exchange.domain.repository.GenericRepository;
import com.ermiyas.exchange.domain.settlement.SettlementStrategy;
import com.ermiyas.exchange.domain.settlement.ThreeWaySettlementStrategy;

import java.time.LocalDateTime;
import java.util.*;

public class Exchange {

    private final GenericRepository<User>  userRepo  = new GenericRepository<>();
    private final GenericRepository<Event> eventRepo = new GenericRepository<>();
    private final GenericRepository<Offer> offerRepo = new GenericRepository<>();
    private final GenericRepository<Bet>   betRepo   = new GenericRepository<>();

    private final Map<Long, Wallet> wallets = new HashMap<>();
    private long walletSeq = 1;

    private final CommissionPolicy commissionPolicy;
    private final Map<MarketType, SettlementStrategy> strategies = new HashMap<>();

    public Exchange(CommissionPolicy commissionPolicy) {
        this.commissionPolicy = commissionPolicy;
        registerStrategy(MarketType.THREE_WAY, new ThreeWaySettlementStrategy());
    }

    public void registerStrategy(MarketType type, SettlementStrategy strategy) {
        strategies.put(type, strategy);
    }


    // User management


    public StandardUser register(String username, String email, String password) throws ExchangeException {
        if (usernameTaken(username))
            throw new IdentityConflictException("Username '" + username + "' is already taken");
        long uid     = userRepo.nextId();
        StandardUser user   = new StandardUser(uid, username, email, Password.create(password));
        Wallet       wallet = new Wallet(walletSeq++, Money.zero());
        user.setWallet(wallet);
        userRepo.save(uid, user);
        wallets.put(uid, wallet);
        return user;
    }

    public AdminUser registerAdmin(String username, String email, String password) throws ExchangeException {
        if (usernameTaken(username))
            throw new IdentityConflictException("Username '" + username + "' is already taken");
        long uid      = userRepo.nextId();
        AdminUser admin = new AdminUser(uid, username, email, Password.create(password));
        userRepo.save(uid, admin);
        return admin;
    }

    public User login(String username, String password) throws ExchangeException {
        User found = null;
        for (User u : userRepo.findAll()) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                found = u;
                break;
            }
        }
        if (found == null)
            throw new UserNotFoundException("Invalid username or password");
        if (!found.authenticate(password))
            throw new UserNotFoundException("Invalid username or password");
        return found;
    }

    public void deposit(Tradeable account, Money amount) {
        account.getWallet().deposit(amount);
    }

    public void withdraw(Tradeable account, Money amount) throws ExchangeException {
        account.getWallet().withdraw(amount);
    }

    public Wallet getWalletFor(Long userId) throws ExchangeException {
        Wallet w = wallets.get(userId);
        if (w == null) throw new UserNotFoundException("No wallet found for user #" + userId);
        return w;
    }


    // Offer operations


    public Long placeOffer(StandardUser maker, Long eventId, Outcome outcome, Odds odds, Money stake)
            throws ExchangeException {
        Event event = requireEvent(eventId);
        if (event.getStatus() != EventStatus.OPEN)
            throw new IllegalBetException("Market is " + event.getStatus() + " — no new offers accepted");
        maker.getWallet().reserve(stake);
        long oid    = offerRepo.nextId();
        Offer offer = new Offer(oid, maker, event, outcome, stake, odds);
        event.attachOffer(offer);
        offerRepo.save(oid, offer);
        return offer.getId();
    }

    public void cancelOffer(Long offerId, Long requestingUserId) throws ExchangeException {
        Offer offer = requireOffer(offerId);
        if (!Objects.equals(offer.getMaker().getId(), requestingUserId))
            throw new IllegalBetException("You can only cancel your own offers");
        Money toReturn = offer.getRemainingStake();
        offer.cancel();
        wallets.get(requestingUserId).unreserve(toReturn);
    }


    // Trade (matching)


    public void matchOffer(Long offerId, StandardUser taker, Money stakeToMatch) throws ExchangeException {
        Offer offer = requireOffer(offerId);
        if (offer.getEvent().getStatus() != EventStatus.OPEN)
            throw new IllegalBetException("Market is closed for this event");
        String ref = "BET-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        long bid   = betRepo.nextId();
        Bet  bet   = offer.fill(bid, stakeToMatch, taker, ref);
        taker.getWallet().reserve(bet.getTakerLiability());
        betRepo.save(bid, bet);
    }


    // Admin — settlement


    public void processResult(AdminUser admin, Long eventId, int homeScore, int awayScore)
            throws ExchangeException {
        Objects.requireNonNull(admin, "Admin reference required");
        Event event = requireEvent(eventId);
        event.processResult(homeScore, awayScore);
    }

    public void settleEvent(AdminUser admin, Long eventId) throws ExchangeException {
        Objects.requireNonNull(admin, "Admin reference required");
        Event event = requireEvent(eventId);
        if (event.getStatus() != EventStatus.COMPLETED)
            throw new IllegalBetException("Event must be COMPLETED before settlement");

        for (Offer offer : event.getOpenOffers()) {
            offer.cancel();
            Wallet makerWallet = wallets.get(offer.getMaker().getId());
            if (makerWallet != null) makerWallet.unreserve(offer.getRemainingStake());
        }

        for (Bet bet : event.getMatchedBets()) {
            Wallet makerWallet = wallets.get(bet.getMaker().getId());
            Wallet takerWallet = wallets.get(bet.getTaker().getId());
            bet.resolve(event.getResult(), makerWallet, takerWallet, commissionPolicy);
        }

        event.markSettled();
    }


    // Queries


    public List<Event> getAllEvents() { return eventRepo.findAll(); }
    public List<User>  getAllUsers()  { return userRepo.findAll(); }
    public CommissionPolicy getCommissionPolicy() { return commissionPolicy; }

    public List<Event> getOpenEvents() {
        List<Event> result = new ArrayList<>();
        for (Event e : eventRepo.findAll()) {
            if (e.getStatus() == EventStatus.OPEN) {
                result.add(e);
            }
        }
        result.sort(new Comparator<Event>() {
            @Override
            public int compare(Event a, Event b) {
                return a.getKickOff().compareTo(b.getKickOff());
            }
        });
        return result;
    }

    public List<Offer> getOpenOffersForEvent(Long eventId) {
        List<Offer> result = new ArrayList<>();
        for (Offer o : offerRepo.findAll()) {
            if (Objects.equals(o.getEvent().getId(), eventId)
                    && (o.getStatus() == OfferStatus.OPEN || o.getStatus() == OfferStatus.PARTIALLY_TAKEN)) {
                result.add(o);
            }
        }
        return result;
    }

    public List<Offer> getOffersForUser(Long userId) {
        List<Offer> result = new ArrayList<>();
        for (Offer o : offerRepo.findAll()) {
            if (Objects.equals(o.getMaker().getId(), userId)) {
                result.add(o);
            }
        }
        return result;
    }

    public List<Bet> getBetsForUser(Long userId) {
        List<Bet> result = new ArrayList<>();
        for (Bet b : betRepo.findAll()) {
            if (Objects.equals(b.getMaker().getId(), userId)
                    || Objects.equals(b.getTaker().getId(), userId)) {
                result.add(b);
            }
        }
        return result;
    }


    // Event management


    public Event addEvent(String homeTeam, String awayTeam, LocalDateTime kickOff,
                          League league, MarketType marketType) {
        SettlementStrategy strategy = strategies.get(marketType);
        if (strategy == null)
            throw new IllegalArgumentException("No strategy registered for market type: " + marketType);
        long eid    = eventRepo.nextId();
        Event event = new Event(eid, homeTeam, awayTeam, kickOff, league, marketType, strategy);
        eventRepo.save(eid, event);
        return event;
    }

    public Event addEvent(AdminUser admin, String homeTeam, String awayTeam, LocalDateTime kickOff,
                          League league, MarketType marketType) {
        Objects.requireNonNull(admin, "Admin reference required");
        return addEvent(homeTeam, awayTeam, kickOff, league, marketType);
    }


    // Private helpers


    private boolean usernameTaken(String username) {
        for (User u : userRepo.findAll()) {
            if (u.getUsername().equalsIgnoreCase(username)) return true;
        }
        return false;
    }

    private Event requireEvent(Long id) throws ExchangeException {
        return eventRepo.findById(id)
                .orElseThrow(() -> new IllegalBetException("Event #" + id + " not found"));
    }

    private Offer requireOffer(Long id) throws ExchangeException {
        return offerRepo.findById(id)
                .orElseThrow(() -> new IllegalBetException("Offer #" + id + " not found"));
    }
}
