class setupFramework {
private static void setupFramework(boolean resolveExternalURI) throws BundleException {
		if (SpotlessEclipseFramework.setup(
				plugins -> {
					plugins.applyDefault();
					//The WST XML formatter
					plugins.add(new XMLCorePlugin());
					//XSDs/DTDs must be resolved by URI
					plugins.add(new URIResolverPlugin());
					//Support formatting based on DTD restrictions
					plugins.add(new DTDCorePlugin());
					//Support formatting based on XSD restrictions
					plugins.add(new XSDCorePlugin());
					if(!resolveExternalURI) {
						plugins.add(new PreventExternalURIResolverExtension());
					}
				})) {
			PREFERENCE_FACTORY = new XmlFormattingPreferencesFactory();
			//Register required EMF factories
			XSDSchemaBuildingTools.getXSDFactory();
		}
	}
}
