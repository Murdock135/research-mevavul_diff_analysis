class init {
public void init() throws ServletException {

        // Wraps the entire initialization in a try/catch to better handle
        // unexpected exceptions and errors to provide better feedback
        // to the developer
        try {
            initInternal();
            initOther();
            initServlet();

            getServletContext().setAttribute(Globals.ACTION_SERVLET_KEY, this);
            initModuleConfigFactory();
            // Initialize modules as needed
            ModuleConfig moduleConfig = initModuleConfig("", config);
            initModuleMessageResources(moduleConfig);
            initModuleDataSources(moduleConfig);
            initModulePlugIns(moduleConfig);
            moduleConfig.freeze();

            Enumeration names = getServletConfig().getInitParameterNames();
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                if (!name.startsWith("config/")) {
                    continue;
                }
                String prefix = name.substring(6);
                moduleConfig = initModuleConfig
                    (prefix, getServletConfig().getInitParameter(name));
                initModuleMessageResources(moduleConfig);
                initModuleDataSources(moduleConfig);
                initModulePlugIns(moduleConfig);
                moduleConfig.freeze();
            }

            this.initModulePrefixes(this.getServletContext());

            this.destroyConfigDigester();
        } catch (UnavailableException ex) {
            throw ex;
        } catch (Throwable t) {

            // The follow error message is not retrieved from internal message
            // resources as they may not have been able to have been
            // initialized
            log.error("Unable to initialize Struts ActionServlet due to an "
                + "unexpected exception or error thrown, so marking the "
                + "servlet as unavailable.  Most likely, this is due to an "
                + "incorrect or missing library dependency.", t);
            throw new UnavailableException(t.getMessage());
        }
    }
}
