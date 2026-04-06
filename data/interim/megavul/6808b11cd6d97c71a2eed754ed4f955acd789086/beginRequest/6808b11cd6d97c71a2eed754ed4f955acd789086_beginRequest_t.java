class beginRequest {
public static void beginRequest() {
        // if the previous request was not ended properly for some reason, make sure it is ended now
        endRequest();
        CACHE.set(new LinkedList<RequestScopedItem>());
    }
}
