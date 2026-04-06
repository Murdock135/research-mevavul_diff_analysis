class checkConnection {
public void checkConnection(UrlArgument repoUrl) {
        final String ref = fullUpstreamRef();
        final CommandLine commandLine = git().withArgs("ls-remote").withArg(repoUrl).withArg(ref);
        final ConsoleResult result = commandLine.runOrBomb(new NamedProcessTag(repoUrl.forDisplay()));

        if (!hasExactlyOneMatchingBranch(result)) {
            throw new CommandLineException(format("The ref %s could not be found.", ref));
        }
    }
}
