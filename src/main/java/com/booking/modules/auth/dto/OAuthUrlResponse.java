package com.booking.modules.auth.dto;

public class OAuthUrlResponse {
    private String provider;
    private String authorizationUrl;
    private String clientId;
    private String state;

    public OAuthUrlResponse() {}

    public OAuthUrlResponse(String provider, String authorizationUrl, String clientId, String state) {
        this.provider = provider;
        this.authorizationUrl = authorizationUrl;
        this.clientId = clientId;
        this.state = state;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getAuthorizationUrl() {
        return authorizationUrl;
    }

    public void setAuthorizationUrl(String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public static OAuthUrlResponseBuilder builder() {
        return new OAuthUrlResponseBuilder();
    }

    public static class OAuthUrlResponseBuilder {
        private String provider;
        private String authorizationUrl;
        private String clientId;
        private String state;

        public OAuthUrlResponseBuilder provider(String provider) {
            this.provider = provider;
            return this;
        }

        public OAuthUrlResponseBuilder authorizationUrl(String authorizationUrl) {
            this.authorizationUrl = authorizationUrl;
            return this;
        }

        public OAuthUrlResponseBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public OAuthUrlResponseBuilder state(String state) {
            this.state = state;
            return this;
        }

        public OAuthUrlResponse build() {
            return new OAuthUrlResponse(provider, authorizationUrl, clientId, state);
        }
    }
}
