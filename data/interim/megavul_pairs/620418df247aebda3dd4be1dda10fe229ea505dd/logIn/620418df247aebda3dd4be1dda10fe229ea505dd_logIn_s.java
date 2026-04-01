class logIn {
public void logIn(Response response) throws UnauthorizedException {
        // Check user against login whitelist, if it exists
        if (GribbitServer.loginWhitelistChecker == null || GribbitServer.loginWhitelistChecker.allowUserToLogin(id)) {

            // Create new session token
            sessionTok = new Token(TokenType.SESSION, Cookie.SESSION_COOKIE_MAX_AGE_SECONDS);
            csrfTok = CSRF.generateRandomCSRFToken();
            save();
            if (sessionTokHasExpired()) {
                // Shouldn't happen, since we just created session tok, but just in case
                clearSessionTok();
                throw new UnauthorizedException("Couldn't create auth session");
            }

            // Save login cookies in result
            response.setCookie(new Cookie(Cookie.SESSION_COOKIE_NAME, "/", sessionTok.token,
                    Cookie.SESSION_COOKIE_MAX_AGE_SECONDS));
            response.setCookie(new Cookie(Cookie.EMAIL_COOKIE_NAME, "/", id, Cookie.SESSION_COOKIE_MAX_AGE_SECONDS));

        } else {
            // User is not authorized
            throw new UnauthorizedException("User is not whitelisted for login: " + id);
        }
    }
}
