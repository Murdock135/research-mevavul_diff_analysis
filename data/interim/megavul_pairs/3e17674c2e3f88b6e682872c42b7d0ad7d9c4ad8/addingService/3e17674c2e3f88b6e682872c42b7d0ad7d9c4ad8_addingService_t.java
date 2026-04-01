class addingService {
@Override
    public ServletInitializationRequirements addingService(
            ServiceReference<ServletInitializationRequirements> reference) {
        ServletInitializationRequirements requirements = super.addingService(
                reference);
        AppConfigFactoryTracker tracker = servletContext
                .getAttribute(AppConfigFactoryTracker.class);
        if (tracker != null) {
            stop();
            servletContext.removeAttribute(AppConfigFactoryTracker.class);
        }
        initializeLookup();
        return requirements;
    }
}
