package com.ermiyas.exchange.infrastructure.sports;

public class FixtureNameMatcher {
    public boolean namesMatch(String a, String b) {
        if (a == null || b == null) return false;
        String na = normalize(a);
        String nb = normalize(b);
        return na.equals(nb) || na.contains(nb) || nb.contains(na);
    }

    private String normalize(String value) {
        return value.toLowerCase().replace("fc", "").replace("cf", "").replaceAll("[^a-z0-9]", "").trim();
    }
}
