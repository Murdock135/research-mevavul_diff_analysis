class addSubscription {
public void addSubscription(Subscription subscription) {
        subscriptions.put(subscription.getId(), subscription);
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
    }
}
