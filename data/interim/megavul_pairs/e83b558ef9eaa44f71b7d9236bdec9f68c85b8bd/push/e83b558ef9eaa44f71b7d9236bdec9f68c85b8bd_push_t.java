class push {
@Override
    public void push(String projectName, String repositoryName, Revision baseRevision, Author author,
                     String summary, Comment detail, List<Change> changes, AsyncMethodCallback resultHandler) {
        final List<com.linecorp.centraldogma.common.Change<?>> convertedChanges =
                convert(changes, Converter::convert);
        try {
            checkMirrorLocalRepo(repositoryName, convertedChanges);
        } catch (Exception e) {
            resultHandler.onError(e);
            return;
        }
        // TODO(trustin): Change Repository.commit() to return a Commit.
        handle(executor.execute(Command.push(convert(author), projectName, repositoryName,
                                             convert(baseRevision), summary, detail.getContent(),
                                             convert(detail.getMarkup()), convertedChanges))
                       .thenCompose(commitResult -> {
                           final com.linecorp.centraldogma.common.Revision newRev = commitResult.revision();
                           return projectManager.get(projectName).repos().get(repositoryName)
                                                .history(newRev, newRev, "/**");
                       })
                       .thenApply(commits -> convert(commits.get(0))),
               resultHandler);
    }
}
