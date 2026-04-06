class removeSubscription {
public Subscription removeSubscription(UInteger subscriptionId) {
        Subscription subscription = subscriptions.remove(subscriptionId);
        server.getEventBus().post(new SubscriptionDeletedEvent(subscription));

        if (subscription != null) {
            subscription.setStateListener(null);

            monitoredItemCount.getAndUpdate(count -> count - subscription.getMonitoredItems().size());
        }

        return subscription;
    }
}
