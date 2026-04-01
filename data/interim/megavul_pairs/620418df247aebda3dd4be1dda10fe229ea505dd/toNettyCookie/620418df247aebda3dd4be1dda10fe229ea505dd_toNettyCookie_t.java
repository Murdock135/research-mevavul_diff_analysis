class toNettyCookie {
public io.netty.handler.codec.http.Cookie toNettyCookie() {
        io.netty.handler.codec.http.Cookie nettyCookie = new DefaultCookie(name, WebUtils.escapeCookieValue(value));
        if (path != null && !path.isEmpty()) {
            nettyCookie.setPath(path);
        }
        nettyCookie.setMaxAge(maxAgeSeconds);
        nettyCookie.setDiscard(discardAtEndOfBrowserSession);
        nettyCookie.setHttpOnly(true);  // TODO
        if (GribbitProperties.SSL) {
            // If SSL is enabled, force cookies to only be delivered over SSL, to prevent cookie hijacking
            // on public wifi networks
            nettyCookie.setSecure(true);
        }
        return nettyCookie;
    }
}
