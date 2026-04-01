class isCsrfTokenValid {
public static boolean isCsrfTokenValid(UI ui, String requestToken) {

        if (ui.getSession().getService().getDeploymentConfiguration()
                .isXsrfProtectionEnabled()) {
            String uiToken = ui.getCsrfToken();

            if (uiToken == null || !MessageDigest.isEqual(
                    uiToken.getBytes(StandardCharsets.UTF_8),
                    requestToken.getBytes(StandardCharsets.UTF_8))) {
                return false;
            }
        }
        return true;
    }
}
