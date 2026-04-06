class clone {
public int clone(ConsoleOutputStreamConsumer outputStreamConsumer, UrlArgument repositoryUrl) {
        CommandLine hg = createCommandLine("hg").withArgs("clone").withArg("-b").withArg(branch).withArg(repositoryUrl)
                .withArg(workingDir.getAbsolutePath()).withNonArgSecrets(secrets).withEncoding("utf-8");
        return execute(hg, outputStreamConsumer);
    }
}
