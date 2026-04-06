class createProcessExecutor {
private ProcessExecutor createProcessExecutor(File targetDir, DbProperties dbProperties) {
        ConnectionUrl connectionUrlInstance = ConnectionUrl.getConnectionUrlInstance(dbProperties.url(), dbProperties.connectionProperties());

        LinkedHashMap<String, String> env = new LinkedHashMap<>();
        if (isNotBlank(dbProperties.password())) {
            env.put("MYSQL_PWD", dbProperties.password());
        }
        // override with any user specified environment
        env.putAll(dbProperties.extraBackupEnv());

        ArrayList<String> argv = new ArrayList<>();
        argv.add("mysqldump");


        String dbName = connectionUrlInstance.getDatabase();
        HostInfo mainHost = connectionUrlInstance.getMainHost();

        if (mainHost != null) {
            argv.add("--host=" + mainHost.getHost());
            argv.add("--port=" + mainHost.getPort());
        }
        if (isNotBlank(dbProperties.user())) {
            argv.add("--user=" + dbProperties.user());
        }

        // append any user specified args for mysqldump
        if (isNotBlank(dbProperties.extraBackupCommandArgs())) {
            Collections.addAll(argv, Commandline.translateCommandline(dbProperties.extraBackupCommandArgs()));
        }

        argv.add("--result-file=" + new File(targetDir, "db." + dbName).toString());
        argv.add(connectionUrlInstance.getDatabase());

        ProcessExecutor processExecutor = new ProcessExecutor();
        processExecutor.redirectOutputAlsoTo(Slf4jStream.of(getClass()).asDebug());
        processExecutor.redirectErrorAlsoTo(Slf4jStream.of(getClass()).asDebug());
        processExecutor.environment(env);
        processExecutor.command(argv);
        return processExecutor;
    }
}
