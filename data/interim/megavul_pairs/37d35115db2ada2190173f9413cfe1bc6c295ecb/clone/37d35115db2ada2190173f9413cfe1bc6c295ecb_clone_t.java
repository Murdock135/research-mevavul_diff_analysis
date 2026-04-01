class clone {
public int clone(ConsoleOutputStreamConsumer outputStreamConsumer, UrlArgument repositoryUrl) {
        CommandLine hg = createCommandLine("hg")
                .withArgs("clone")
                .withArg(branchArg())
                .withArg("--")
                .withArg(repositoryUrl)
                .withArg(workingDir.getAbsolutePath())
                .withNonArgSecrets(secrets)
                .withEncoding("UTF-8");
        return execute(hg, outputStreamConsumer);
    }
}
