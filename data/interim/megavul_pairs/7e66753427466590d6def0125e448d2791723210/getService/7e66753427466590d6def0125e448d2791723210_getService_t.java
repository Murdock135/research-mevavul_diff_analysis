class getService {
public static Service getService(Map environment)
    {
        Service service = null;
        InitialContext context = null;

        EngineConfiguration configProvider =
            (EngineConfiguration)environment.get(EngineConfiguration.PROPERTY_NAME);

        if (configProvider == null)
            configProvider = (EngineConfiguration)threadDefaultConfig.get();

        if (configProvider == null)
            configProvider = getDefaultEngineConfig();

        // First check to see if JNDI works
        // !!! Might we need to set up context parameters here?
        try {
            context = new InitialContext();
        } catch (NamingException e) {
        }
        
        if (context != null) {
            String name = (String)environment.get("jndiName");

	    if(name!=null && (name.toUpperCase().indexOf("LDAP")!=-1 || name.toUpperCase().indexOf("RMI")!=-1 || name.toUpperCase().indexOf("JMS")!=-1 || name.toUpperCase().indexOf("JMX")!=-1) || name.toUpperCase().indexOf("JRMP")!=-1 || name.toUpperCase().indexOf("JAVA")!=-1 || name.toUpperCase().indexOf("DNS")!=-1)  {
	        return null;
            }
            if (name == null) {
                name = "axisServiceName";
            }

            // We've got JNDI, so try to find an AxisClient at the
            // specified name.
            try {
                service = (Service)context.lookup(name);
            } catch (NamingException e) {
                service = new Service(configProvider);
                try {
                    context.bind(name, service);
                } catch (NamingException e1) {
                    // !!! Couldn't do it, what should we do here?
	            return null;
                }
            }
        } else {
            service = new Service(configProvider);
        }

        return service;
    }
}
