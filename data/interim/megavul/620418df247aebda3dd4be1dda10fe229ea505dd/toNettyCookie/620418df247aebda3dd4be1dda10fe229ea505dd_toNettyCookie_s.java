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
            nettyCookie.setSecure(true);  // TODO
        }
        return nettyCookie;
    }
}
