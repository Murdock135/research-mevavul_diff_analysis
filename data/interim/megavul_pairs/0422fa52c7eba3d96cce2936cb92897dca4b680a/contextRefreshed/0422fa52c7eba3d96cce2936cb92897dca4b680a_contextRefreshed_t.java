class contextRefreshed {
@Override
	public void contextRefreshed() {
		contextLastRefreshedTime = System.currentTimeMillis();

		// START HACK
		// since we're not using a Listener anymore, these are not set at startup
		try {
			Class<?> webConstants1x = Context.loadClass("org.openmrs.web.WebConstants");
			String webappName = (String) webConstants1x.getField("WEBAPP_NAME").get(null);
			WebConstants.CONTEXT_PATH = webappName;
			WebConstants.WEBAPP_NAME = webappName;
		} catch (Exception ex) {
			log.error("Failed to get CONTEXT_PATH from WebConstants during UI Framework startup");
		}
		// END HACK
		
		PageFactory pageFactory = getComponent(PageFactory.class);
		FragmentFactory fragmentFactory = getComponent(FragmentFactory.class);
		ResourceFactory resourceFactory = getComponent(ResourceFactory.class);

		// Register a standard resource provider that can load file-based resources
		resourceFactory.addResourceProvider(ConfigurationResourceProvider.RESOURCE_KEY, new ConfigurationResourceProvider());

		List<UiContextRefreshedCallback> callbacks = Context.getRegisteredComponents(UiContextRefreshedCallback.class);
		for (UiContextRefreshedCallback callback : callbacks) {
			try {
				callback.afterContextRefreshed(pageFactory, fragmentFactory, resourceFactory);
			}
			catch (Exception ex) {
				log.error("Error in UiContextRefreshedCallback: " + callback, ex);
			}
		}
	}
}
