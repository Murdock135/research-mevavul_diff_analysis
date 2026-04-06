class getProgramOrganisationUnitsAssociationsForCurrentUser {
@Override
    public SetValuedMap<String, String> getProgramOrganisationUnitsAssociationsForCurrentUser( Set<String> programUids )
    {
        idObjectManager.getAndValidateByUid( Program.class, programUids );

        return jdbcOrgUnitAssociationsStore.getOrganisationUnitsAssociationsForCurrentUser( programUids );
    }
}
