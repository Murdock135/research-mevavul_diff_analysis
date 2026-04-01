class backup {
@Override
    public void backup(File targetDir, DataSource dataSource, DbProperties dbProperties) throws InterruptedException, TimeoutException, IOException {
        try {
            ProcessResult processResult = createProcessExecutor(targetDir, dbProperties).execute();

            if (processResult.getExitValue() == 0) {
                log.info("MySQL backup finished successfully.");
            } else {
                throwBackupError(COMMAND, processResult.getExitValue());
            }
        } catch (ProcessInitException e) {
            throwBackupError(COMMAND, e.getErrorCode(), e.getCause());
        }
    }
}
