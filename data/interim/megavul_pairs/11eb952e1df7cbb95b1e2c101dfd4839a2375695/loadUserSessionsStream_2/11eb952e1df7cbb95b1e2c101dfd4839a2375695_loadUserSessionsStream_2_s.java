class loadUserSessionsStream_2 {
@Override
    public Stream<UserSessionModel> loadUserSessionsStream(RealmModel realm, ClientModel client, boolean offline, Integer firstResult, Integer maxResults) {

        String offlineStr = offlineToString(offline);
        TypedQuery<PersistentUserSessionEntity> query;
        StorageId clientStorageId = new StorageId(client.getId());
        if (clientStorageId.isLocal()) {
            query = paginateQuery(
                    em.createNamedQuery("findUserSessionsByClientId", PersistentUserSessionEntity.class),
                    firstResult, maxResults);
            query.setParameter("clientId", client.getId());
        } else {
            query = paginateQuery(
                    em.createNamedQuery("findUserSessionsByExternalClientId", PersistentUserSessionEntity.class),
                    firstResult, maxResults);
            query.setParameter("clientStorageProvider", clientStorageId.getProviderId());
            query.setParameter("externalClientId", clientStorageId.getExternalId());
        }

        query.setParameter("offline", offlineStr);
        query.setParameter("realmId", realm.getId());

        return loadUserSessionsWithClientSessions(query, offlineStr);
    }
}
