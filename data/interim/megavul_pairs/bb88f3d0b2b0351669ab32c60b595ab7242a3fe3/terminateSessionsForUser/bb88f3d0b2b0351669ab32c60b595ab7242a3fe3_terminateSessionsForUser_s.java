class terminateSessionsForUser {
private void terminateSessionsForUser(User user) {
        try {
            final Set<String> sessionIds = getSessionIDsForUser(user);

            for (final String sessionId : sessionIds) {
                getActiveSessionForID(sessionId).ifPresent(session -> {
                    LOG.info("Terminating session for user <{}/{}>", user.getName(), user.getId());
                    session.stop();
                });
            }
        } catch (Exception e) {
            LOG.error("Couldn't terminate session for user <{}/{}>", user.getName(), user.getId(), e);
        }
    }
}
