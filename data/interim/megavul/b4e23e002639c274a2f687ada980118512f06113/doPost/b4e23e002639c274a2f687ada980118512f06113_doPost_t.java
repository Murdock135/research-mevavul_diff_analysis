class doPost {
@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        final PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);

        if (session != null) {
            Subject subject = (Subject) session.getAttribute("subject");
            if (subject == null) {
                LOG.warn("No security subject stored in existing session, invalidating");
                session.invalidate();
                Helpers.doForbidden(resp);
                return;
            }
            sendResponse(session, subject, out);
            return;
        }

        AccessControlContext acc = AccessController.getContext();
        Subject subject = Subject.getSubject(acc);

        if (subject == null) {
            Helpers.doForbidden(resp);
            return;
        }
        Set<Principal> principals = subject.getPrincipals();

        String username = null;

        if (principals != null) {
            for (Principal principal : principals) {
                if (principal.getClass().getSimpleName().equals("UserPrincipal")) {
                    username = principal.getName();
                    LOG.debug("Authorizing user {}", username);
                }
            }
        }

        session = req.getSession(true);
        session.setAttribute("subject", subject);
        session.setAttribute("user", username);
        session.setAttribute("org.osgi.service.http.authentication.remote.user", username);
        session.setAttribute("org.osgi.service.http.authentication.type", HttpServletRequest.BASIC_AUTH);
        session.setAttribute("loginTime", GregorianCalendar.getInstance().getTimeInMillis());
        if (timeout != null) {
            session.setMaxInactiveInterval(timeout);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Http session timeout for user {} is {} sec.", username, session.getMaxInactiveInterval());
        }

        sendResponse(session, subject, out);
    }
}
