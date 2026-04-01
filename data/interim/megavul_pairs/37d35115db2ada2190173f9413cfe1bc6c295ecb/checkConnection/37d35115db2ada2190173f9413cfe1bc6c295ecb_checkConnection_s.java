class checkConnection {
public void checkConnection(UrlArgument repositoryURL) {
        execute(createCommandLine("hg").withArgs("id", "--id").withArg(repositoryURL).withNonArgSecrets(secrets).withEncoding("utf-8"), new NamedProcessTag(repositoryURL.forDisplay()));
    }
}
