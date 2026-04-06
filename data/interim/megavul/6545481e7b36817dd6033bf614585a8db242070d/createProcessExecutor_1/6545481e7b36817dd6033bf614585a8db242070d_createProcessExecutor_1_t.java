class createProcessExecutor_1 {
ProcessExecutor createProcessExecutor(File targetDir, DbProperties dbProperties) {
        Properties connectionProperties = dbProperties.connectionProperties();
        Properties pgProperties = Driver.parseURL(dbProperties.url(), connectionProperties);

        Map<String, String> env = new LinkedHashMap<>();
        if (isNotBlank(dbProperties.password())) {
            env.put("PGPASSWORD", dbProperties.password());
        }

        // override with any user specified environment
        env.putAll(dbProperties.extraBackupEnv());

        List<String> argv = new ArrayList<>();
        argv.add(COMMAND);

        String dbName = pgProperties.getProperty("PGDBNAME");
        argv.add("--host=" + pgProperties.getProperty("PGHOST"));
        argv.add("--port=" + pgProperties.getProperty("PGPORT"));
        argv.add("--dbname=" + dbName);
        if (isNotBlank(dbProperties.user())) {
            argv.add("--username=" + dbProperties.user());
        }
        argv.add("--no-password");
        // append any user specified args for pg_dump
        if (isNotBlank(dbProperties.extraBackupCommandArgs())) {
            Collections.addAll(argv, Commandline.translateCommandline(dbProperties.extraBackupCommandArgs()));
        }
        argv.add("--file=" + new File(targetDir, "db." + dbName));
        ProcessExecutor processExecutor = new ProcessExecutor();
        processExecutor.redirectOutputAlsoTo(Slf4jStream.of(getClass()).asDebug());
        processExecutor.redirectErrorAlsoTo(Slf4jStream.of(getClass()).asDebug());
        processExecutor.environment(env);
        processExecutor.command(argv);
        return processExecutor;
    }
}
