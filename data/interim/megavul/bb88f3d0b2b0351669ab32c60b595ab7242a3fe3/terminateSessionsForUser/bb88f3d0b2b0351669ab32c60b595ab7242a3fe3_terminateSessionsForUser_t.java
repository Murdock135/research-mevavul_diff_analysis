class terminateSessionsForUser {
private void terminateSessionsForUser(User user) {
        try {
            for (final Session session : getActiveSessionsForUser(user)) {
                LOG.info("Terminating session for user <{}/{}>", user.getName(), user.getId());
                session.stop();
            }
        } catch (Exception e) {
            LOG.error("Couldn't terminate session for user <{}/{}>", user.getName(), user.getId(), e);
        }
    }
}
