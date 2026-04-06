class loadPassword {
public static String loadPassword() {
        String passwordKey;
        KeyStoreHelper keyStoreHelper = new KeyStoreHelper();

        try {
            passwordKey = keyStoreHelper.getPasswordKey();
        } catch (RuntimeException re) {
            logger.warn("Unable to load key store. Using password from configuration");
            passwordKey = System.getProperty(KieServerConstants.CFG_KIE_PASSWORD, "kieserver1!");
        }

        return passwordKey;
    }
}
