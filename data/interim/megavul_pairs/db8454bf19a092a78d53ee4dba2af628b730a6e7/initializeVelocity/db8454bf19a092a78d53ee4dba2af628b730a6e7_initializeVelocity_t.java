class initializeVelocity {
private void initializeVelocity() {
		if (velocityEngine == null) {
			velocityEngine = new VelocityEngine();
			
			Properties props = new Properties();
			props.setProperty(RuntimeConstants.RUNTIME_LOG, "startup_wizard_vel.log");
			// Linux requires setting logging properties to initialize Velocity Context.
			props.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
			    "org.apache.velocity.runtime.log.CommonsLogLogChute");
			props.setProperty(CommonsLogLogChute.LOGCHUTE_COMMONS_LOG_NAME, "initial_wizard_velocity");
			
			// so the vm pages can import the header/footer
			props.setProperty(RuntimeConstants.RESOURCE_LOADER, "class");
			props.setProperty("class.resource.loader.description", "Velocity Classpath Resource Loader");
			props.setProperty("class.resource.loader.class",
			    "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			
			try {
				velocityEngine.init(props);
			}
			catch (Exception e) {
				log.error("velocity init failed, because: {}", e, e);
			}
		}
	}
}
