class loadUserSessionsWithClientSessions {
private Stream<UserSessionModel> loadUserSessionsWithClientSessions(TypedQuery<PersistentUserSessionEntity> query, String offlineStr, boolean useExact) {

        List<PersistentUserSessionAdapter> userSessionAdapters = closing(query.getResultStream()
                .map(this::toAdapter)
                .filter(Objects::nonNull))
                .collect(Collectors.toList());

        Map<String, PersistentUserSessionAdapter> sessionsById = userSessionAdapters.stream()
                .collect(Collectors.toMap(UserSessionModel::getId, Function.identity()));

        Set<String> userSessionIds = sessionsById.keySet();

        Set<String> removedClientUUIDs = new HashSet<>();

        if (!sessionsById.isEmpty()) {
            TypedQuery<PersistentClientSessionEntity> queryClientSessions;
            if (useExact) {
                queryClientSessions = em.createNamedQuery("findClientSessionsOrderedByIdExact", PersistentClientSessionEntity.class);
                queryClientSessions.setParameter("offline", offlineStr);
                queryClientSessions.setParameter("userSessionIds", userSessionIds);
            } else {
                String fromUserSessionId = userSessionAdapters.get(0).getId();
                String toUserSessionId = userSessionAdapters.get(userSessionAdapters.size() - 1).getId();

                queryClientSessions = em.createNamedQuery("findClientSessionsOrderedByIdInterval", PersistentClientSessionEntity.class);
                queryClientSessions.setParameter("offline", offlineStr);
                queryClientSessions.setParameter("fromSessionId", fromUserSessionId);
                queryClientSessions.setParameter("toSessionId", toUserSessionId);
            }

            closing(queryClientSessions.getResultStream()).forEach(clientSession -> {
                PersistentUserSessionAdapter userSession = sessionsById.get(clientSession.getUserSessionId());
                // check if we have a user session for the client session
                if (userSession != null) {
                    boolean added = addClientSessionToAuthenticatedClientSessionsIfPresent(userSession, clientSession);
                    if (!added) {
                        // client was removed in the meantime
                        removedClientUUIDs.add(clientSession.getClientId());
                    }
                }
            });
        }

        for (String clientUUID : removedClientUUIDs) {
            onClientRemoved(clientUUID);
        }

        return userSessionAdapters.stream().map(UserSessionModel.class::cast);
    }
}
