class endRequest {
public static void endRequest() {
        final List<RequestScopedItem> result = CACHE.get();
        CACHE.remove();
        if (result != null) {
            for (final RequestScopedItem item : result) {
                item.invalidate();
            }
        }
    }
}
