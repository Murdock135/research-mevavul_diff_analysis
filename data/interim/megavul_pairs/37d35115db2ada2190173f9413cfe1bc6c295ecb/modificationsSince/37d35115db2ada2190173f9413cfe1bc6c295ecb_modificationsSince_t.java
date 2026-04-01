class modificationsSince {
public List<Modification> modificationsSince(Revision revision) {
        InMemoryStreamConsumer consumer = inMemoryConsumer();
        bombUnless(pull(consumer), "Failed to run hg pull command: " + consumer.getAllOutput());
        CommandLine hg = hg("log", "-r", "tip:" + revision.getRevision(), branchArg(), "--style", templatePath());
        return new HgModificationSplitter(execute(hg)).filterOutRevision(revision);
    }
}
