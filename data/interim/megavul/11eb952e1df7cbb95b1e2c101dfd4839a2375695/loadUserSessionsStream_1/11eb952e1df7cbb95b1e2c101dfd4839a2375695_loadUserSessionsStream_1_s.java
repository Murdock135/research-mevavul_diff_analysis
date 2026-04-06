class loadUserSessionsStream_1 {
@Override
    public Stream<UserSessionModel> loadUserSessionsStream(RealmModel realm, UserModel user, boolean offline, Integer firstResult, Integer maxResults) {

        String offlineStr = offlineToString(offline);

        TypedQuery<PersistentUserSessionEntity> query = paginateQuery(
                em.createNamedQuery("findUserSessionsByUserId", PersistentUserSessionEntity.class),
                firstResult, maxResults);

        query.setParameter("offline", offlineStr);
        query.setParameter("realmId", realm.getId());
        query.setParameter("userId", user.getId());

        return loadUserSessionsWithClientSessions(query, offlineStr);
    }
}
