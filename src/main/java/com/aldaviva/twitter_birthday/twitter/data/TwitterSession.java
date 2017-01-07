package com.aldaviva.twitter_birthday.twitter.data;

public class TwitterSession {

    private String authenticationToken;
    private String sessionId;
    private String authenticityToken;

    /**
     * This is an identifier for the logged-in user.
     * It comes from logging in.
     * It appears in the {@code auth_token} cookie.
     */
    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    /**
     * This is an identifier for the logged-in user's web session.
     * It comes from a Set-Cookie header when loading pages.
     * It appears in the {@code _twitter_sess} cookie.
     */
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * This is a Cross-Site Request Forgery prevention token.
     * It comes from a big JSON blob inside an HTML page's {@code <input>} element, and goes in POSTED forms.
     * @return
     */
    public String getAuthenticityToken() {
        return authenticityToken;
    }

    public void setAuthenticityToken(String authenticityToken) {
        this.authenticityToken = authenticityToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TwitterSession that = (TwitterSession) o;

        if (authenticationToken != null ? !authenticationToken.equals(that.authenticationToken) : that.authenticationToken != null)
            return false;
        if (sessionId != null ? !sessionId.equals(that.sessionId) : that.sessionId != null) return false;
        return authenticityToken != null ? authenticityToken.equals(that.authenticityToken) : that.authenticityToken == null;
    }

    @Override
    public int hashCode() {
        int result = authenticationToken != null ? authenticationToken.hashCode() : 0;
        result = 31 * result + (sessionId != null ? sessionId.hashCode() : 0);
        result = 31 * result + (authenticityToken != null ? authenticityToken.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TwitterSession{" +
                "authenticationToken='" + authenticationToken + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", authenticityToken='" + authenticityToken + '\'' +
                '}';
    }
}
