class forceRun {
@Override
    @CloseDBIfOpened
    public boolean forceRun() {

        try {

            return !new DotDatabaseMetaData().tableExists(
                    DbConnectionFactory.getConnection(), "workflow_action_mappings");
        } catch (SQLException e) {

            return Boolean.FALSE;
        }
    }
}
