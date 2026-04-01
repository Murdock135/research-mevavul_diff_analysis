class isCsrfTokenValid {
public static boolean isCsrfTokenValid(UI ui, String requestToken) {

        if (ui.getSession().getService().getDeploymentConfiguration()
                .isXsrfProtectionEnabled()) {
            String uiToken = ui.getCsrfToken();

            if (uiToken == null || !uiToken.equals(requestToken)) {
                return false;
            }
        }
        return true;
    }
}
