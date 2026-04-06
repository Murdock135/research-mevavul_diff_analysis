class pull {
private boolean pull(ConsoleOutputStreamConsumer outputStreamConsumer) {
        CommandLine hg = hg("pull", branchArg(), "--config", String.format("paths.default=%s", url));
        return execute(hg, outputStreamConsumer) == 0;
    }
}
