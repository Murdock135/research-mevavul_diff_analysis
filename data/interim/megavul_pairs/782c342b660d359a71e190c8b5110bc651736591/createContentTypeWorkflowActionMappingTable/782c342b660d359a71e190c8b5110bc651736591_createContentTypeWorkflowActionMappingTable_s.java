class createContentTypeWorkflowActionMappingTable {
private void createContentTypeWorkflowActionMappingTable() throws SQLException {

        Logger.info(this, "Creates the table content_type_workflow_action_mapping.");

        try {

            new DotConnect().executeStatement(getCreateContentTypeWorkflowActionMappingTableSQL());
        } catch (SQLException e) {
            Logger.error(this, "The table 'content_type_workflow_action_mapping' could not be created.", e);
            throw  e;
        }
    }
}
