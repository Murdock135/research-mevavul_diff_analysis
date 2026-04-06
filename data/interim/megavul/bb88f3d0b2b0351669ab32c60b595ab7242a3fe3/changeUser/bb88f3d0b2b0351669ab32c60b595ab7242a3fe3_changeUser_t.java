class changeUser {
@PUT
    @Path("{userId}")
    @ApiOperation("Modify user details.")
    @ApiResponses({
                          @ApiResponse(code = 400, message = "Attempted to modify a read only user account (e.g. built-in or LDAP users)."),
                          @ApiResponse(code = 400, message = "Missing or invalid user details.")
                  })
    @AuditEvent(type = AuditEventTypes.USER_UPDATE)
    public void changeUser(@ApiParam(name = "userId", value = "The ID of the user to modify.", required = true)
                           @PathParam("userId") String userId,
                           @ApiParam(name = "JSON body", value = "Updated user information.", required = true)
                           @Valid @NotNull ChangeUserRequest cr) throws ValidationException {

        final User user = loadUserById(userId);
        final String username = user.getName();
        checkPermission(USERS_EDIT, username);

        if (user.isReadOnly()) {
            throw new BadRequestException("Cannot modify readonly user " + username);
        }
        // We only allow setting a subset of the fields in ChangeUserRequest
        if (!user.isExternalUser()) {
            if (cr.email() != null) {
                user.setEmail(cr.email());
            }
            if (cr.firstName() != null && cr.lastName() != null) {
                user.setFirstLastFullNames(cr.firstName(), cr.lastName());
            }
        }
        final boolean permitted = isPermitted(USERS_PERMISSIONSEDIT, user.getName());
        if (permitted && cr.permissions() != null) {
            user.setPermissions(getEffectiveUserPermissions(user, cr.permissions()));
        }

        if (isPermitted(USERS_ROLESEDIT, user.getName())) {
            checkAdminRoleForServiceAccount(cr, user);
            setUserRoles(cr.roles(), user);
        }

        final String timezone = cr.timezone();
        if (timezone == null) {
            user.setTimeZone((String) null);
        } else {
            try {
                if (timezone.isEmpty()) {
                    user.setTimeZone((String) null);
                } else {
                    final DateTimeZone tz = DateTimeZone.forID(timezone);
                    user.setTimeZone(tz);
                }
            } catch (IllegalArgumentException e) {
                LOG.error("Invalid timezone '{}', ignoring it for user {}.", timezone, username);
            }
        }

        final Startpage startpage = cr.startpage();
        if (startpage != null) {
            user.setStartpage(startpage.type(), startpage.id());
        }

        if (isPermitted("*")) {
            final Long sessionTimeoutMs = cr.sessionTimeoutMs();
            if (Objects.nonNull(sessionTimeoutMs) && sessionTimeoutMs != 0 && (user.getSessionTimeoutMs() != sessionTimeoutMs)) {
                user.setSessionTimeoutMs(sessionTimeoutMs);
                terminateSessions(user);
            }
        }

        if (cr.isServiceAccount() != null) {
            user.setServiceAccount(cr.isServiceAccount());
        }

        userManagementService.update(user, cr);
    }
}
