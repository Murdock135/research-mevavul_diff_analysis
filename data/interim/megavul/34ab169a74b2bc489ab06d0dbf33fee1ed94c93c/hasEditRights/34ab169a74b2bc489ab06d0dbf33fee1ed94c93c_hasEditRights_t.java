class hasEditRights {
private static boolean hasEditRights(SecurityContext securityContext) {
        if (securityContext.isUserInRole(Authentication.ROLE_ADMIN)) {
            return true;
        } else {
            return false;
        }
    }
}
