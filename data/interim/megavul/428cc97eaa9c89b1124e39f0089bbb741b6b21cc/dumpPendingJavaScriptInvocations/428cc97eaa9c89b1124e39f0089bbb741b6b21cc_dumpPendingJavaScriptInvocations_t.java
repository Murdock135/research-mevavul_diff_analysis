class dumpPendingJavaScriptInvocations {
public List<PendingJavaScriptInvocation> dumpPendingJavaScriptInvocations() {
        pendingTitleUpdateCanceler = null;

        if (pendingJsInvocations.isEmpty()) {
            return Collections.emptyList();
        }

        List<PendingJavaScriptInvocation> readyToSend = getPendingJavaScriptInvocations()
                .filter(invocation -> invocation.getOwner().isVisible())
                .peek(PendingJavaScriptInvocation::setSentToBrowser)
                .collect(Collectors.toList());

        pendingJsInvocations = getPendingJavaScriptInvocations()
                .filter(invocation -> !invocation.getOwner().isVisible())
                .collect(Collectors.toCollection(ArrayList::new));

        return readyToSend;
    }
}
