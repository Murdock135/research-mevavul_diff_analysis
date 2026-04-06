class createContentTypeWorkflowActionMappingTable {
private void createContentTypeWorkflowActionMappingTable() throws SQLException {

        Logger.info(this, "Creates the table workflow_action_mappings.");

        try {

            new DotConnect().executeStatement(getCreateContentTypeWorkflowActionMappingTableSQL());
        } catch (SQLException e) {
            Logger.error(this, "The table 'workflow_action_mappings' could not be created.", e);
            throw  e;
        }
    }
}
