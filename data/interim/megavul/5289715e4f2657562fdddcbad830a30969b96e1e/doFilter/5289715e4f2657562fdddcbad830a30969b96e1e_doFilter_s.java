class doFilter {
@Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getServletPath();
        LOG.debug("Handling request for path {}", path);

        if (configuration.getRealm() == null || configuration.getRealm().equals("") || !configuration.isEnabled()) {
            LOG.debug("No authentication needed for path {}", path);
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            Subject subject = (Subject) session.getAttribute("subject");
            if (subject != null) {
                LOG.debug("Session subject {}", subject);
                executeAs(request, response, chain, subject);
                return;
            }
        }

        boolean doAuthenticate = path.startsWith("/auth") ||
                path.startsWith("/jolokia") ||
                path.startsWith("/upload");

        if (doAuthenticate) {
            LOG.debug("Doing authentication and authorization for path {}", path);
            switch (Authenticator.authenticate(configuration.getRealm(), configuration.getRole(), configuration.getRolePrincipalClasses(),
                    configuration.getConfiguration(), httpRequest, new PrivilegedCallback() {
                public void execute(Subject subject) throws Exception {
                    executeAs(request, response, chain, subject);
                }
            })) {
                case AUTHORIZED:
                    // request was executed using the authenticated subject, nothing more to do
                    break;
                case NOT_AUTHORIZED:
                    Helpers.doForbidden((HttpServletResponse) response);
                    break;
                case NO_CREDENTIALS:
                    //doAuthPrompt((HttpServletResponse)response);
                    Helpers.doForbidden((HttpServletResponse) response);
                    break;
            }
        } else {
            LOG.debug("No authentication needed for path {}", path);
            chain.doFilter(request, response);
        }
    }
}
