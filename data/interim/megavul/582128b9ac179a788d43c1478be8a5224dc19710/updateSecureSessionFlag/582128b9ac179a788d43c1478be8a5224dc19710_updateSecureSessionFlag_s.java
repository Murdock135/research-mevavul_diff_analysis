class updateSecureSessionFlag {
private void updateSecureSessionFlag() {
        try {
            ServletContext context = Jenkins.getInstance().servletContext;
            Method m;
            try {
                m = context.getClass().getMethod("getSessionCookieConfig");
            } catch (NoSuchMethodException x) { // 3.0+
                LOGGER.log(Level.FINE, "Failed to set secure cookie flag", x);
                return;
            }
            Object sessionCookieConfig = m.invoke(context);

            // not exposing session cookie to JavaScript to mitigate damage caused by XSS
            Class scc = Class.forName("javax.servlet.SessionCookieConfig");
            Method setHttpOnly = scc.getMethod("setHttpOnly",boolean.class);
            setHttpOnly.invoke(sessionCookieConfig,true);

            Method setSecure = scc.getMethod("setSecure",boolean.class);
            boolean v = fixNull(jenkinsUrl).startsWith("https");
            setSecure.invoke(sessionCookieConfig,v);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to set secure cookie flag", e);
        }
    }
}
