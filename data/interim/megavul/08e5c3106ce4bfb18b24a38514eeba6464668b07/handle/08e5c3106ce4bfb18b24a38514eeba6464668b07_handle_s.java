class handle {
@Override
    public void handle(final RoutingContext ctx) {

        if (currentManagedContext.isActive()) {
            handleWithIdentity(ctx);
        } else {

            currentManagedContext.activate();
            ctx.response()
                    .endHandler(currentManagedContextTerminationHandler)
                    .exceptionHandler(currentManagedContextTerminationHandler)
                    .closeHandler(currentManagedContextTerminationHandler);

            try {
                handleWithIdentity(ctx);
            } catch (Throwable t) {
                currentManagedContext.terminate();
                throw t;
            }
        }
    }
}
