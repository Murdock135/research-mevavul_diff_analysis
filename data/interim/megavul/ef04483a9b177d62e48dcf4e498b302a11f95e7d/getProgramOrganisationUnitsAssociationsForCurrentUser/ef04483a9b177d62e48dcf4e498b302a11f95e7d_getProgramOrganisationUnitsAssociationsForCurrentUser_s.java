class getProgramOrganisationUnitsAssociationsForCurrentUser {
@Override
    public SetValuedMap<String, String> getProgramOrganisationUnitsAssociationsForCurrentUser(
        Set<String> programUids )
    {
        return jdbcOrgUnitAssociationsStore.getOrganisationUnitsAssociationsForCurrentUser( programUids );
    }
}
