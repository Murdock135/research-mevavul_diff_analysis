class hasEditRights {
private static boolean hasEditRights(SecurityContext securityContext) {
        if (securityContext.isUserInRole(Authentication.ROLE_ADMIN) || securityContext.isUserInRole(Authentication.ROLE_REST)) {
            return true;
        } else {
            return false;
        }
    }
}
