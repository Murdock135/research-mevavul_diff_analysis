class requestForbidden {
private boolean requestForbidden(HttpServletRequest request) {
        if (!xsrfProtectionEnabled) {
            return false;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String csrfTokenInSession = (String) session
                .getAttribute(VaadinService.getCsrfTokenAttributeName());
        if (csrfTokenInSession == null) {
            if (getLogger().isInfoEnabled()) {
                getLogger().info(
                        "Unable to verify CSRF token for endpoint request, got null token in session");
            }

            return true;
        }

        if (!csrfTokenInSession.equals(request.getHeader("X-CSRF-Token"))) {
            if (getLogger().isInfoEnabled()) {
                getLogger().info("Invalid CSRF token in endpoint request");
            }

            return true;
        }

        return false;
    }
}
