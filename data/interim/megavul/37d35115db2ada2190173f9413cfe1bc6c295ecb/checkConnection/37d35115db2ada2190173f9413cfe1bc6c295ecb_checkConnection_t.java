class checkConnection {
public void checkConnection(UrlArgument repositoryURL) {
        CommandLine hg = createCommandLine("hg")
                .withArgs("id", "--id", "--")
                .withArg(repositoryURL)
                .withNonArgSecrets(secrets)
                .withEncoding("UTF-8");
        execute(hg, new NamedProcessTag(repositoryURL.forDisplay()));
    }
}
