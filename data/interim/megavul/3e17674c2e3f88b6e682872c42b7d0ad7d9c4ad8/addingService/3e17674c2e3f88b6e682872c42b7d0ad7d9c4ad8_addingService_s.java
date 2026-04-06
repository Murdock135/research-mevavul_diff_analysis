class addingService {
@Override
    public ApplicationConfigurationFactory addingService(
            ServiceReference<ApplicationConfigurationFactory> reference) {
        ApplicationConfigurationFactory factory = super.addingService(
                reference);
        AppConfigFactoryTracker tracker = servletContext
                .getAttribute(AppConfigFactoryTracker.class);
        if (tracker != null) {
            stop();
            servletContext.removeAttribute(AppConfigFactoryTracker.class);
        }
        initializeLookup();
        return factory;
    }
}
