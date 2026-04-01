class internalGetMessageById {
protected void internalGetMessageById(AsyncResponse asyncResponse, long ledgerId, long entryId,
                                              boolean authoritative) {
        try {
            // will redirect if the topic not owned by current broker
            validateTopicOwnership(topicName, authoritative);
            validateTopicOperation(topicName, TopicOperation.PEEK_MESSAGES);

            if (topicName.isGlobal()) {
                validateGlobalNamespaceOwnership(namespaceName);
            }
            PersistentTopic topic = (PersistentTopic) getTopicReference(topicName);
            ManagedLedgerImpl ledger = (ManagedLedgerImpl) topic.getManagedLedger();
            if (null == ledger.getLedgerInfo(ledgerId).get()) {
                log.error("[{}] Failed to get message with ledgerId {} entryId {} from {}, "
                                + "ledgerId not belong to this topic.",
                        clientAppId(), ledgerId, entryId, topicName);
                asyncResponse.resume(new RestException(Status.NOT_FOUND, "Message not found"));
            }
            ledger.asyncReadEntry(new PositionImpl(ledgerId, entryId), new AsyncCallbacks.ReadEntryCallback() {
                @Override
                public void readEntryFailed(ManagedLedgerException exception, Object ctx) {
                    asyncResponse.resume(new RestException(exception));
                }

                @Override
                public void readEntryComplete(Entry entry, Object ctx) {
                    try {
                        asyncResponse.resume(generateResponseWithEntry(entry));
                    } catch (IOException exception) {
                        asyncResponse.resume(new RestException(exception));
                    } finally {
                        if (entry != null) {
                            entry.release();
                        }
                    }
                }
            }, null);
        } catch (NullPointerException npe) {
            asyncResponse.resume(new RestException(Status.NOT_FOUND, "Message not found"));
        } catch (Exception exception) {
            log.error("[{}] Failed to get message with ledgerId {} entryId {} from {}",
                    clientAppId(), ledgerId, entryId, topicName, exception);
            asyncResponse.resume(new RestException(exception));
        }
    }
}
