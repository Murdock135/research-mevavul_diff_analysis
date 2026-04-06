class createSubscription {
public void createSubscription(ServiceRequest service) {
        CreateSubscriptionRequest request = (CreateSubscriptionRequest) service.getRequest();

        UInteger subscriptionId = nextSubscriptionId();

        Subscription subscription = new Subscription(
            this,
            subscriptionId,
            request.getRequestedPublishingInterval(),
            request.getRequestedMaxKeepAliveCount().longValue(),
            request.getRequestedLifetimeCount().longValue(),
            request.getMaxNotificationsPerPublish().longValue(),
            request.getPublishingEnabled(),
            request.getPriority().intValue()
        );

        subscriptions.put(subscriptionId, subscription);
        server.getSubscriptions().put(subscriptionId, subscription);
        server.getDiagnosticsSummary().getCumulatedSubscriptionCount().increment();
        server.getEventBus().post(new SubscriptionCreatedEvent(subscription));

        subscription.setStateListener((s, ps, cs) -> {
            if (cs == State.Closing) {
                subscriptions.remove(s.getId());
                server.getSubscriptions().remove(s.getId());
                server.getEventBus().post(new SubscriptionDeletedEvent(s));

                /*
                 * Notify AddressSpaces the items for this subscription are deleted.
                 */

                Map<UInteger, BaseMonitoredItem<?>> monitoredItems = s.getMonitoredItems();

                byMonitoredItemType(
                    monitoredItems.values(),
                    dataItems -> server.getAddressSpaceManager().onDataItemsDeleted(dataItems),
                    eventItems -> server.getAddressSpaceManager().onEventItemsDeleted(eventItems)
                );

                monitoredItemCount.getAndUpdate(count -> count - monitoredItems.size());
                server.getMonitoredItemCount().getAndUpdate(count -> count - monitoredItems.size());

                monitoredItems.clear();
            }
        });

        subscription.startPublishingTimer();

        ResponseHeader header = service.createResponseHeader();

        CreateSubscriptionResponse response = new CreateSubscriptionResponse(
            header, subscriptionId,
            subscription.getPublishingInterval(),
            uint(subscription.getLifetimeCount()),
            uint(subscription.getMaxKeepAliveCount())
        );

        service.setResponse(response);
    }
}
