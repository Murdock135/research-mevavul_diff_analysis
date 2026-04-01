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

            Class scc = Class.forName("javax.servlet.SessionCookieConfig");
            Method setSecure = scc.getMethod("setSecure", boolean.class);
            boolean v = fixNull(jenkinsUrl).startsWith("https");
            setSecure.invoke(sessionCookieConfig, v);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof IllegalStateException) {
                // servlet 3.0 spec seems to prohibit this from getting set at runtime,
                // though Winstone is happy to accept i. see JENKINS-25019
                return;
            }
            LOGGER.log(Level.WARNING, "Failed to set secure cookie flag", e);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to set secure cookie flag", e);
        }
    }
}
