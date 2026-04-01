class forceRun {
@Override
    @CloseDBIfOpened
    public boolean forceRun() {

        try {

            return !new DotDatabaseMetaData().tableExists(
                    DbConnectionFactory.getConnection(), "content_type_workflow_action_mapping");
        } catch (SQLException e) {

            return Boolean.FALSE;
        }
    }
}
