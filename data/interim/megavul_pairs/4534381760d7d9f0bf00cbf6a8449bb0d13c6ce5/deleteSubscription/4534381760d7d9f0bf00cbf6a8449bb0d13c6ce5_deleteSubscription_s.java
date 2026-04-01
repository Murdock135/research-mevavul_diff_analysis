class deleteSubscription {
public void deleteSubscription(ServiceRequest service) throws UaException {
        DeleteSubscriptionsRequest request = (DeleteSubscriptionsRequest) service.getRequest();

        List<UInteger> subscriptionIds = l(request.getSubscriptionIds());

        if (subscriptionIds.isEmpty()) {
            throw new UaException(StatusCodes.Bad_NothingToDo);
        }

        StatusCode[] results = new StatusCode[subscriptionIds.size()];

        for (int i = 0; i < subscriptionIds.size(); i++) {
            UInteger subscriptionId = subscriptionIds.get(i);
            Subscription subscription = subscriptions.remove(subscriptionId);

            if (subscription != null) {
                server.getSubscriptions().remove(subscription.getId());
                server.getEventBus().post(new SubscriptionDeletedEvent(subscription));

                List<BaseMonitoredItem<?>> deletedItems = subscription.deleteSubscription();

                /*
                 * Notify AddressSpaces of the items we just deleted.
                 */

                byMonitoredItemType(
                    deletedItems,
                    dataItems -> server.getAddressSpaceManager().onDataItemsDeleted(dataItems),
                    eventItems -> server.getAddressSpaceManager().onEventItemsDeleted(eventItems)
                );

                results[i] = StatusCode.GOOD;

                server.getMonitoredItemCount().getAndUpdate(count -> count - deletedItems.size());
            } else {
                results[i] = new StatusCode(StatusCodes.Bad_SubscriptionIdInvalid);
            }
        }

        ResponseHeader header = service.createResponseHeader();

        DeleteSubscriptionsResponse response = new DeleteSubscriptionsResponse(
            header,
            results,
            new DiagnosticInfo[0]
        );

        service.setResponse(response);

        while (subscriptions.isEmpty() && publishQueue.isNotEmpty()) {
            ServiceRequest publishService = publishQueue.poll();
            if (publishService != null) {
                publishService.setServiceFault(StatusCodes.Bad_NoSubscription);
            }
        }
    }
}
