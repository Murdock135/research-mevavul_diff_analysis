class dumpPendingJavaScriptInvocations {
public List<PendingJavaScriptInvocation> dumpPendingJavaScriptInvocations() {
        pendingTitleUpdateCanceler = null;

        if (pendingJsInvocations.isEmpty()) {
            return Collections.emptyList();
        }

        List<PendingJavaScriptInvocation> currentList = getPendingJavaScriptInvocations()
                .peek(PendingJavaScriptInvocation::setSentToBrowser)
                .collect(Collectors.toList());

        pendingJsInvocations = new ArrayList<>();

        return currentList;
    }
}
