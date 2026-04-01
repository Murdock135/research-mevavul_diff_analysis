class loadPassword {
public static String loadPassword(KieServerConfig config) {
        String passwordKey;
        KeyStoreHelper keyStoreHelper = new KeyStoreHelper();

        try {
            passwordKey = keyStoreHelper.getPasswordKey();
        } catch (RuntimeException re) {
            logger.warn("Unable to load key store. Using password from configuration");
            passwordKey = config.getConfigItemValue(KieServerConstants.CFG_KIE_CONTROLLER_PASSWORD, "kieserver1!");
        }

        return passwordKey;
    }
}
