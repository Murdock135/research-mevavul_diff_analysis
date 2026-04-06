class push {
@Post("/projects/{projectName}/repos/{repoName}/contents")
    @RequiresWritePermission
    public CompletableFuture<PushResultDto> push(
            @Param @Default("-1") String revision,
            Repository repository,
            Author author,
            CommitMessageDto commitMessage,
            @RequestConverter(ChangesRequestConverter.class) Iterable<Change<?>> changes) {

        final long commitTimeMillis = System.currentTimeMillis();
        return push(commitTimeMillis, author, repository, new Revision(revision), commitMessage, changes)
                .toCompletableFuture()
                .thenApply(rrev -> convert(rrev, commitTimeMillis));
    }
}
