package com.ermiyas.exchange.infrastructure.sports.dto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TheOddsApiFixtureDto {
    
    private String id;
    
    @JsonProperty("home_team")
    private String homeTeam;
    
    @JsonProperty("away_team")
    private String awayTeam;
    
    @JsonProperty("commence_time")
    @JsonDeserialize(using = ISO8601Deserializer.class)
    private LocalDateTime startTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getHomeTeam() { return homeTeam; }
    public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }
    
    public String getAwayTeam() { return awayTeam; }
    public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public static class ISO8601Deserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
            String dateString = jsonParser.getText();
            ZonedDateTime zdt = ZonedDateTime.parse(dateString);
            return zdt.toLocalDateTime();
        }
    }
}