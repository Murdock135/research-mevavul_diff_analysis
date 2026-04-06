class get {
@Override
    public FileResourceManager get() {
        String workDir = createTmpDir();
        boolean urlEncodePath = false;
        final ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
        PrintWriter printWriter = new PrintWriter(stream) {
            @Override
            public void flush() {
                super.flush();
                log.logFileResourceMessage(stream.toString());
            }
        };
        LoggerFacade logger = new PrintWriterLogger(printWriter, "", debug);
        return new FileResourceManager(storeDir, workDir, urlEncodePath, logger);
    }
}
