class handle {
@Override
    public void handle(final RoutingContext ctx) {

        ctx.response()
                .endHandler(currentManagedContextTerminationHandler)
                .exceptionHandler(currentManagedContextTerminationHandler)
                .closeHandler(currentManagedContextTerminationHandler);
        if (!currentManagedContext.isActive()) {
            currentManagedContext.activate();
        }
        try {
            handleWithIdentity(ctx);
        } catch (Throwable t) {
            currentManagedContext.terminate();
            throw t;
        }
    }
}
