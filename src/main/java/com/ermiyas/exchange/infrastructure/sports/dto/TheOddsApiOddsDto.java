package com.ermiyas.exchange.infrastructure.sports.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TheOddsApiOddsDto {
    private String id;
    private String homeTeam;
    private String awayTeam;
    private List<Bookmaker> bookmakers;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getHomeTeam() { return homeTeam; }
    public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }
    public String getAwayTeam() { return awayTeam; }
    public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }
    public List<Bookmaker> getBookmakers() { return bookmakers; }
    public void setBookmakers(List<Bookmaker> bookmakers) { this.bookmakers = bookmakers; }

    public static class Bookmaker {
        private String title;
        private List<Market> markets;
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public List<Market> getMarkets() { return markets; }
        public void setMarkets(List<Market> markets) { this.markets = markets; }
    }

    public static class Market {
        private String key;
        private List<Outcome> outcomes;
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public List<Outcome> getOutcomes() { return outcomes; }
        public void setOutcomes(List<Outcome> outcomes) { this.outcomes = outcomes; }
    }

    public static class Outcome {
        private String name;
        private double price;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
    }
}
