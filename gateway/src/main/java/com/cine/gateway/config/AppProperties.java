package com.cine.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String premieresUrl;
    private String candyUrl;
    private String completeUrl;
    private String jwtSecret;
    private String googleClientId;

    public String getPremieresUrl() {
        return premieresUrl;
    }

    public void setPremieresUrl(String premieresUrl) {
        this.premieresUrl = premieresUrl;
    }

    public String getCandyUrl() {
        return candyUrl;
    }

    public void setCandyUrl(String candyUrl) {
        this.candyUrl = candyUrl;
    }

    public String getCompleteUrl() {
        return completeUrl;
    }

    public void setCompleteUrl(String completeUrl) {
        this.completeUrl = completeUrl;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public String getGoogleClientId() {
        return googleClientId;
    }

    public void setGoogleClientId(String googleClientId) {
        this.googleClientId = googleClientId;
    }
}
