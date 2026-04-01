class endRequest {
public static void endRequest() {
        final List<RequestScopedItem> result = CACHE.get();
        if (result != null) {
            CACHE.remove();
            for (final RequestScopedItem item : result) {
                item.invalidate();
            }
        }
    }
}
