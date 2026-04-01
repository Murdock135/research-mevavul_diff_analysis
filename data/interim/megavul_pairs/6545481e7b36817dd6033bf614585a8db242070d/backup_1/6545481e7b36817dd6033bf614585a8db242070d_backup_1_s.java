class backup_1 {
@Override
    public void backup(File targetDir, DataSource dataSource, DbProperties dbProperties) throws Exception {
        ProcessResult processResult = createProcessExecutor(targetDir, dbProperties).execute();

        if (processResult.getExitValue() == 0) {
            log.info("PostgreSQL backup finished successfully.");
        } else {
            log.warn("There was an error backing up the database using `pg_dump`. The `pg_dump` process exited with status code {}.", processResult.getExitValue());
            throw new RuntimeException("There was an error backing up the database using `pg_dump`. The `pg_dump` process exited with status code " + processResult.getExitValue() +
                    ". Please see the server logs for more errors.");

        }
    }
}
