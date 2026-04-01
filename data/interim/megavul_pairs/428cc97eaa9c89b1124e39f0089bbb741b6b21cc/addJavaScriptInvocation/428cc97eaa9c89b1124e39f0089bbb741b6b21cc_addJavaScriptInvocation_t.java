class addJavaScriptInvocation {
public void addJavaScriptInvocation(
            PendingJavaScriptInvocation invocation) {
        session.checkHasLock();
        pendingJsInvocations.add(invocation);

        invocation.getOwner()
                .addDetachListener(() -> pendingJsInvocations
                        .removeIf(pendingInvocation -> pendingInvocation
                                .equals(invocation)));
    }
}
