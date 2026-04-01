class sessionClosed {
public void sessionClosed(boolean deleteSubscriptions) {
        Iterator<Subscription> iterator = subscriptions.values().iterator();

        while (iterator.hasNext()) {
            Subscription s = iterator.next();
            s.setStateListener(null);

            if (deleteSubscriptions) {
                server.getSubscriptions().remove(s.getId());
                server.getEventBus().post(new SubscriptionDeletedEvent(s));

                List<BaseMonitoredItem<?>> deletedItems = s.deleteSubscription();

                /*
                 * Notify AddressSpaces the items for this subscription are deleted.
                 */

                byMonitoredItemType(
                    deletedItems,
                    dataItems -> server.getAddressSpaceManager().onDataItemsDeleted(dataItems),
                    eventItems -> server.getAddressSpaceManager().onEventItemsDeleted(eventItems)
                );

                monitoredItemCount.getAndUpdate(count -> count - deletedItems.size());
                server.getMonitoredItemCount().getAndUpdate(count -> count - deletedItems.size());
            }

            iterator.remove();
        }

        if (deleteSubscriptions) {
            while (publishQueue.isNotEmpty()) {
                ServiceRequest publishService = publishQueue.poll();
                if (publishService != null) {
                    publishService.setServiceFault(StatusCodes.Bad_SessionClosed);
                }
            }
        }
    }
}
