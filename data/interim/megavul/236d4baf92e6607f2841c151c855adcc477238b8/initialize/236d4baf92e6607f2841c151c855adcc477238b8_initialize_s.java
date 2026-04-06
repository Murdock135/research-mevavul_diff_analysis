class initialize {
private void initialize(DataSource dataSource) throws AmbiguousTableNameException {
        databaseTester = new DataSourceDatabaseTester(dataSource);
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        databaseTester.setTearDownOperation(DatabaseOperation.DELETE_ALL);
        DefaultDataSet dataSet = new DefaultDataSet();
        dataSet.addTable(new DefaultTable("agents"));

        dataSet.addTable(new DefaultTable("pipelines"));
        dataSet.addTable(new DefaultTable("pipelinestates"));
        dataSet.addTable(new DefaultTable("materials"));
        dataSet.addTable(new DefaultTable("modifications"));
        dataSet.addTable(new DefaultTable("pipelineMaterialRevisions"));
        dataSet.addTable(new DefaultTable("modifiedFiles"));

        dataSet.addTable(new DefaultTable("notificationfilters"));
        dataSet.addTable(new DefaultTable("users"));
        dataSet.addTable(new DefaultTable("artifactPropertiesGenerator"));
        dataSet.addTable(new DefaultTable("stages"));
        dataSet.addTable(new DefaultTable("pipelineLabelCounts"));
        dataSet.addTable(new DefaultTable("environmentVariables"));
        dataSet.addTable(new DefaultTable("properties"));
        dataSet.addTable(new DefaultTable("artifactPlans"));
        dataSet.addTable(new DefaultTable("buildStateTransitions"));
        dataSet.addTable(new DefaultTable("resources"));
        dataSet.addTable(new DefaultTable("builds"));

        dataSet.addTable(new DefaultTable("stageArtifactCleanupProhibited"));
        dataSet.addTable(new DefaultTable("serverBackups"));
        dataSet.addTable(new DefaultTable("jobAgentMetadata"));
        dataSet.addTable(new DefaultTable("DataSharingSettings"));
        dataSet.addTable(new DefaultTable("UsageDataReporting"));

        databaseTester.setDataSet(dataSet);
    }
}
