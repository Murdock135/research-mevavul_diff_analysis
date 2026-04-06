class backup_1 {
@Override
    public void backup(File targetDir, DataSource dataSource, DbProperties dbProperties) throws Exception {
        try {
            ProcessResult processResult = createProcessExecutor(targetDir, dbProperties).execute();

            if (processResult.getExitValue() == 0) {
                log.info("PostgreSQL backup finished successfully.");
            } else {
                throwBackupError(COMMAND, processResult.getExitValue());
            }
        } catch (ProcessInitException e) {
            throwBackupError(COMMAND, e.getErrorCode(), e.getCause());
        }
    }
}
