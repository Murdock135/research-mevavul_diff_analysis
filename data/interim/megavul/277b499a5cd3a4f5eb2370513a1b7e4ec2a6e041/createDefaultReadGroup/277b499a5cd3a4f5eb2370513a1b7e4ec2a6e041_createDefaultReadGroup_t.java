class createDefaultReadGroup {
@Override
    public Group createDefaultReadGroup(Context context, Collection collection, String typeOfGroupString,
                                        int defaultRead)
        throws SQLException, AuthorizeException {
        Group role = groupService.create(context);
        groupService.setName(role, getDefaultReadGroupName(collection, typeOfGroupString));

        // Remove existing privileges from the anonymous group.
        authorizeService.removePoliciesActionFilter(context, collection, defaultRead);

        // Grant our new role the default privileges.
        authorizeService.addPolicy(context, collection, defaultRead, role);
        groupService.update(context, role);
        return role;
    }
}
