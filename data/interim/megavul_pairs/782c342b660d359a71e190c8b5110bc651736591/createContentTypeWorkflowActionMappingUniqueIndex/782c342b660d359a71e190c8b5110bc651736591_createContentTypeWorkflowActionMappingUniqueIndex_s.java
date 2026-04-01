class createContentTypeWorkflowActionMappingUniqueIndex {
private void createContentTypeWorkflowActionMappingUniqueIndex() throws SQLException {

        Logger.info(this, "Creates the table content_type_workflow_action_mapping unique index.");

        try {

            new DotConnect().executeStatement(getCreateContentTypeWorkflowActionMappingUniqueIndexSQL());
        } catch (SQLException e) {
            Logger.error(this, "The index for the table 'content_type_workflow_action_mapping' could not be created.", e);
            throw  e;
        }
    }
}
