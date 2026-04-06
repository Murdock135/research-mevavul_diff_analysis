class backup {
@Override
    public void backup(File targetDir, DataSource dataSource, DbProperties dbProperties) throws InterruptedException, TimeoutException, IOException {
        ProcessResult processResult = createProcessExecutor(targetDir, dbProperties).execute();

        if (processResult.getExitValue() == 0) {
            log.info("MySQL backup finished successfully.");
        } else {
            log.warn("There was an error backing up the database using `mysqldump`. The `mysqldump` process exited with status code {}.", processResult.getExitValue());
            throw new RuntimeException("There was an error backing up the database using `mysqldump`. The `mysqldump` process exited with status code " + processResult.getExitValue() +
                    ". Please see the server logs for more errors");
        }
    }
}
