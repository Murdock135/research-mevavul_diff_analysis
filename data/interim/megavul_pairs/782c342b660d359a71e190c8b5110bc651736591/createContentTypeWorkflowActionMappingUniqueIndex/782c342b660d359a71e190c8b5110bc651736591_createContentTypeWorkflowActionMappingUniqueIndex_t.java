class createContentTypeWorkflowActionMappingUniqueIndex {
private void createContentTypeWorkflowActionMappingUniqueIndex() throws SQLException {

        Logger.info(this, "Creates the table idx_workflow_action_mappings unique index.");

        try {

            new DotConnect().executeStatement(getCreateContentTypeWorkflowActionMappingUniqueIndexSQL());
        } catch (SQLException e) {
            Logger.error(this, "The index for the table 'idx_workflow_action_mappings' could not be created.", e);
            throw  e;
        }
    }
}
