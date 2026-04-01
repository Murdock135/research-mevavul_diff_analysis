class getLocalePrefix {
private String getLocalePrefix(FacesContext context) {

        String localePrefix = null;
        
        localePrefix = context.getExternalContext().getRequestParameterMap().get("loc");
        
        if(localePrefix != null && !nameContainsForbiddenSequence(localePrefix)){
            return localePrefix;
        } else {
            localePrefix = null; 
        }
        
        String appBundleName = context.getApplication().getMessageBundle();
        if (null != appBundleName) {
        	
            Locale locale = null;
            if (context.getViewRoot() != null) {
                locale = context.getViewRoot().getLocale();
            } else {
                locale = context.getApplication().getViewHandler().calculateLocale(context);
            }
            
                try {
                    ResourceBundle appBundle =
                          ResourceBundle.getBundle(appBundleName,
                                                   locale,
                                                   Util.getCurrentLoader(ResourceManager.class));
                    localePrefix =
                          appBundle
                                .getString(ResourceHandler.LOCALE_PREFIX);
                } catch (MissingResourceException mre) { 
                    if (LOGGER.isLoggable(Level.FINEST)) {
                        LOGGER.log(Level.FINEST, "Ignoring missing resource", mre);
                    }
                }
        }
        return localePrefix;

    }
}
