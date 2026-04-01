class loadUserSessionsStream {
public Stream<UserSessionModel> loadUserSessionsStream(Integer firstResult, Integer maxResults, boolean offline,
                                                           String lastUserSessionId) {
        String offlineStr = offlineToString(offline);

        TypedQuery<PersistentUserSessionEntity> query = paginateQuery(em.createNamedQuery("findUserSessionsOrderedById", PersistentUserSessionEntity.class)
            .setParameter("offline", offlineStr)
            .setParameter("lastSessionId", lastUserSessionId), firstResult, maxResults);

        return loadUserSessionsWithClientSessions(query, offlineStr, false);
    }
}
