class deleteMonitoredItems {
public void deleteMonitoredItems(ServiceRequest service) throws UaException {
        DeleteMonitoredItemsRequest request = (DeleteMonitoredItemsRequest) service.getRequest();

        UInteger subscriptionId = request.getSubscriptionId();
        Subscription subscription = subscriptions.get(subscriptionId);
        List<UInteger> itemsToDelete = l(request.getMonitoredItemIds());

        if (subscription == null) {
            throw new UaException(StatusCodes.Bad_SubscriptionIdInvalid);
        }
        if (itemsToDelete.isEmpty()) {
            throw new UaException(StatusCodes.Bad_NothingToDo);
        }

        StatusCode[] deleteResults = new StatusCode[itemsToDelete.size()];
        List<BaseMonitoredItem<?>> deletedItems = newArrayListWithCapacity(itemsToDelete.size());

        synchronized (subscription) {
            for (int i = 0; i < itemsToDelete.size(); i++) {
                UInteger itemId = itemsToDelete.get(i);
                BaseMonitoredItem<?> item = subscription.getMonitoredItems().get(itemId);

                if (item == null) {
                    deleteResults[i] = new StatusCode(StatusCodes.Bad_MonitoredItemIdInvalid);
                } else {
                    deletedItems.add(item);

                    deleteResults[i] = StatusCode.GOOD;

                    monitoredItemCount.decrementAndGet();
                    server.getMonitoredItemCount().decrementAndGet();
                }
            }

            subscription.removeMonitoredItems(deletedItems);
        }

        /*
         * Notify AddressSpaces of the items that have been deleted.
         */

        byMonitoredItemType(
            deletedItems,
            dataItems -> server.getAddressSpaceManager().onDataItemsDeleted(dataItems),
            eventItems -> server.getAddressSpaceManager().onEventItemsDeleted(eventItems)
        );

        /*
         * Build and return results.
         */
        ResponseHeader header = service.createResponseHeader();

        DeleteMonitoredItemsResponse response = new DeleteMonitoredItemsResponse(
            header,
            deleteResults,
            new DiagnosticInfo[0]
        );

        service.setResponse(response);
    }
}
